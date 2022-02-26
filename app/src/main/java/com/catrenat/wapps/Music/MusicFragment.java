package com.catrenat.wapps.Music;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.Music.RecyclerView.MusicRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MusicFragment extends Fragment {

    private static final String TAG = "music";
    // Properties
    private ArrayList<Music> musicArray = new ArrayList<>();
    private RecyclerView musicRecyclerView;
    private MusicRecyclerViewAdapter adapter;
    private SearchView searchView;
    YouTubePlayerView youTubePlayerView;
    private User user;
    private FirebaseFirestore db;
    BottomNavigationView bottomNav;

    public MusicFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        // Properties
        TextView musicIntroText = view.findViewById(R.id.musicIntroText);
        TextView musicSubtitleText = view.findViewById(R.id.musicSubtitle);
        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);

        // Making the welcome text fade in
        Animation animationTitle = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        musicIntroText.setAnimation(animationTitle);

        Animation animationSubtitle = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        musicSubtitleText.setAnimation(animationSubtitle);

        // Initializing the RecyclerView for the list
        musicRecyclerView = view.findViewById(R.id.musicRecyclerView);
        // To disable nested scroll
        musicRecyclerView.setLayoutManager(new LinearLayoutManager((getContext())) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            }
        );

        // Creating the database instance
        db = FirebaseFirestore.getInstance();

        // Get user from firebase
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
                            }
                            // Get data from firebase
                            db.collection("Music")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // Clearing the music list
                                                if (musicArray != null) {
                                                    musicArray.clear();
                                                }
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                    Music music = document.toObject(Music.class);
                                                    musicArray.add(music);
                                                }
                                                adapter = new MusicRecyclerViewAdapter(musicRecyclerView, musicArray, getContext(), youTubePlayerView, user);
                                                musicRecyclerView.setAdapter(adapter);
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        // SearchBar configuration
        MotionLayout motionLayout = view.findViewById(R.id.musicMotionLayout);
        searchView = view.findViewById(R.id.musicSearchBar);

        // Calls animation on motionLayout on searchBar icon click
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Click", "Se iso clic open");
                motionLayout.transitionToEnd();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("Click", "Se iso clic close");
                motionLayout.transitionToStart();
                return false;
            }
        });

        // Filters on search click and resets when no string or cancelled
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    if (adapter != null) {
                        adapter.filter(newText);
                    }
                }
                return false;
            }});

        return view;
    }
    // Resets search button to original position
    @Override
    public void onPause() {
        super.onPause();
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        }
    }
    // Resets search button to original position
    @Override
    public void onStop() {
        super.onStop();
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        }
    }
}