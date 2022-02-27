package com.catrenat.wapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Books.BooksFragment;
import com.catrenat.wapps.Favourites.GeneralFavFragment;
import com.catrenat.wapps.Information.InformationFragment;
import com.catrenat.wapps.LoginScreen.LoginScreen;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.Movies.MoviesFragment;
import com.catrenat.wapps.Music.MusicFragment;
import com.catrenat.wapps.Games.PlatformsListFragment;
import com.catrenat.wapps.Profile.ProfileScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static TextView headerUsername, headerBio, headerEmail;
    public static ImageView headerImage;
    private Button catalanBtn, englishBtn;
    private Vibrator vibe;
    private BottomNavigationView bottomNav;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView drawerNav;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Properties
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        drawerNav = findViewById(R.id.drawer_navigation);
        SharedPreferences prefs = this.getSharedPreferences("SharedP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String language = prefs.getString("language", null);
        if (language != null) {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
                config.setLocale(new Locale(language.toLowerCase()));
            } else {
                config.locale = new Locale(language.toLowerCase());
            }
            res.updateConfiguration(config, dm);
        }


        // Toolbar
        setSupportActionBar(toolbar);

        //NavigationDrawer Menu
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Drawer header TextViews
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = document.toObject(User.class);
                                // Header properties
                                headerUsername = findViewById(R.id.headerUsername);
                                headerBio = findViewById(R.id.headerBio);
                                headerEmail = findViewById(R.id.headerEmail);
                                headerImage = findViewById(R.id.headerImage);
                                catalanBtn = findViewById(R.id.catalanBtn);
                                englishBtn = findViewById(R.id.englishBtn);
                                
                                // Languages
                                catalanBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        setLocale("ca");
                                        editor.putString("language", "ca");
                                        editor.commit();
                                    }
                                });

                                englishBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        setLocale("en");
                                        editor.putString("language", "en");
                                        editor.commit();
                                    }
                                });

                                headerUsername.setText(user.getUsername());
                                headerBio.setText(user.getBio());
                                headerEmail.setText(user.getEmail());

                                // Header image download
                                StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
                                if(user.getImagePath() != null) {
                                    storageReference.child(user.getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Load image with glide
                                            Glide.with(MainActivity.this) // Context from getContext() in HomeFragment
                                                    .load(uri.toString())
                                                    .into(headerImage);
                                        }
                                    });
                                }
                            } else {
                                Log.d("FireStore", "No such document");
                            }
                        } else {
                            Log.d("FireStore", "get failed with ", task.getException());
                        }
                    }
                });

        // Drawer navigation selection fragment
        drawerNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        vibe.vibrate(3);
                        fragment = new GeneralFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        bottomNav.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_profile:
                        vibe.vibrate(3);
                        fragment = new ProfileScreen(user);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        bottomNav.setVisibility(View.GONE);
                        break;
                    case R.id.nav_favourites:
                        vibe.vibrate(3);
                        fragment = new GeneralFavFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        bottomNav.setVisibility(View.GONE);
                        break;
                    case R.id.nav_information:
                        vibe.vibrate(3);
                        fragment = new InformationFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        bottomNav.setVisibility(View.GONE);
                        break;
                    case R.id.nav_disconnect:
                        vibe.vibrate(3);
                        // Alert dialog to confirm logout action
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.alert_logout_title);
                        builder.setMessage(R.string.alert_logout_message);
                        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Log off the app and deactivates auto-login
                                SharedPreferences prefs= getSharedPreferences("SharedP", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("login", false);
                                editor.commit();
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Closes alert dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
                if (item.getItemId() != R.id.nav_disconnect) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
                return true;
            }

        });

        // Bottom navigation selection fragment
        bottomNav.setSelectedItemId(R.id.nav_general);
        bottomNav.setItemIconTintList(null);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){

                    case R.id.nav_music:
                        vibe.vibrate(3);
                        fragment = new MusicFragment();
                        break;

                    case R.id.nav_movies:
                        vibe.vibrate(3);
                        fragment = new MoviesFragment();
                        break;

                    case R.id.nav_general:
                        vibe.vibrate(3);
                        fragment = new GeneralFragment();
                        break;

                    case R.id.nav_games:
                        vibe.vibrate(3);
                        fragment = new PlatformsListFragment();
                        break;

                    case R.id.nav_books:
                        vibe.vibrate(3);
                        fragment = new BooksFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("").commit();

                return true;
            }
        });
    }

    // To change app language
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);
    }

    // To change the ripple color
    private void setRippleColor(View view, int color) {
        RippleDrawable drawable = (RippleDrawable) view.getBackground();
        ColorStateList stateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_pressed}},
                new int[]{color}
        );
        drawable.setColor(stateList);
        view.setBackground(drawable);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}