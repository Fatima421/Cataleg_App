package com.catrenat.wapps.LoginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.MainActivity;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.OnboardingActivity;
import com.catrenat.wapps.R;
import com.catrenat.wapps.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginScreen extends AppCompatActivity {
private EditText emailTxt, passwordTxt;
private TextView registerTxt;
private Button loginBtn;
private FirebaseAuth mAuth;
private CheckBox rememberBox;
private SharedPreferences prefs;
private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Properties
        TextInputLayout outerPasswordTxt = findViewById(R.id.loginPasswordTxt);
        TextInputLayout outerEmailTxt = findViewById(R.id.loginEmailTxt);
        passwordTxt = findViewById(R.id.etPassword);
        emailTxt = findViewById(R.id.etEmail);
        registerTxt = findViewById(R.id.registerTxt);
        loginBtn = findViewById(R.id.loginBtn);
        rememberBox = findViewById(R.id.remember_checkBox);

        // To be able to change lock icon color when focused
        passwordTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerPasswordTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // To be able to change email icon color when focused
        emailTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerEmailTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // Sign in button (Login authentication process)
        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // Register Activity launch
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Shared preference
        prefs = getSharedPreferences("SharedP", Context.MODE_PRIVATE);

        // If remember was check permits auto-login.
        if(prefs.getBoolean("login", false)) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
        overridePendingTransition(0, 0);
    }

    public void loginUser() {
        String email = emailTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();

        // Email conditions
        if(email.isEmpty()) {
            emailTxt.setError(getString(R.string.emailRequired));
            emailTxt.requestFocus();
            return;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError(getString(R.string.emailNotValid));
            emailTxt.requestFocus();
            return;
        }

        // Password conditions
        if(password.isEmpty()) {
            passwordTxt.setError(getString(R.string.passwordRequired));
            passwordTxt.requestFocus();
            return;
        } else if(password.length() < 6) {
            passwordTxt.setError(getString(R.string.passwordMinLength));
            passwordTxt.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginScreen.this, getString(R.string.loggedSuccessfully), Toast.LENGTH_SHORT).show();
                // If remember box isChecked auto-login is activated
                if(rememberBox.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("login", true).commit();
                }
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginScreen.this, getString(R.string.wrongEmailPass), Toast.LENGTH_SHORT).show();
            }
        });
        
    }
}

/*
db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        User user = document.toObject(User.class);
                                        if(user.getFirstTime()) {
                                            user.setFirstTime(false);
                                            db.collection("Users")
                                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .set(user);
                                            startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }

                                    } else {
                                        Log.d("FireStore", "No such document");
                                    }
                                } else {
                                    Log.d("FireStore", "get failed with ", task.getException());
                                }
                            }
                        });
 */