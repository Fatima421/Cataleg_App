package com.catrenat.wapps.LoginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.catrenat.wapps.Main.MainActivity;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {
private EditText emailTxt, passwordTxt;
private CardView googleSignIn;
private TextView registerTxt;
private Button loginBtn;
private FirebaseAuth mAuth;
private CheckBox rememberBox;
private SharedPreferences prefs;
private FirebaseFirestore db;
private FirebaseAuth mFirebaseAuth;
private ProgressBar progressBar;
private GoogleSignInClient mGoogleSignInClient;
private static final String TAG = "SignInActivity";
private static final int RC_SIGN_IN = 101;


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
        googleSignIn = findViewById(R.id.loginWhiteBoxGoogle);
        progressBar = findViewById(R.id.progressLogin);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_manual))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginScreen.this, gso);

        // If Sign up with google button is clicked
        googleSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    // Sign in with Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // Checks if google sign in was succesful or not
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    // Asks for google credentials
    private void firebaseAuthWithGoogle(String idToken) {
        isLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    // Moves you to the next screen
    private void updateUI(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        Query query = usersRef.whereEqualTo("email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String email = documentSnapshot.getString("email");

                        if(email.equals(user.getEmail())){
                            isLoading(false);
                            LoginScreen.this.finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                }

                if(task.getResult().size() == 0 ){
                    createUser(user);
                }
            }
        });
    }


    public void createUser(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> modifiedUser = new HashMap<>();
        modifiedUser.put("email", user.getEmail());
        modifiedUser.put("username", user.getDisplayName());
        modifiedUser.put("password", "");

        // Add a new document with a generated ID
        db.collection("Users")
                .document(user.getUid())
                .set(modifiedUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + "");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        // Starts MainActivity
        isLoading(false);
        LoginScreen.this.finish();
        startActivity(new Intent(this, OnboardingActivity.class));
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
        } else {
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut();
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

        isLoading(true);
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
                isLoading(false);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginScreen.this, getString(R.string.wrongEmailPass), Toast.LENGTH_SHORT).show();
                isLoading(false);
            }
        });
    }

    private void isLoading(boolean isLoading) {
        if(isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            passwordTxt.setEnabled(false);
            emailTxt.setEnabled(false);
            registerTxt.setEnabled(false);
            loginBtn.setEnabled(false);
            rememberBox.setEnabled(false);
            googleSignIn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            passwordTxt.setEnabled(true);
            emailTxt.setEnabled(true);
            registerTxt.setEnabled(true);
            loginBtn.setEnabled(true);
            rememberBox.setEnabled(true);
            googleSignIn.setEnabled(true);
        }
    }

}