package com.catrenat.wapps.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.MainActivity;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileScreen extends Fragment {
    // Properties
    private EditText nameProfileTxt, emailProfileTxt, passProfileTxt, passConfirmProfileTxt, bioProfileTxt;
    private TextInputLayout outerUsernameTxt, outerEmailTxt, outerPasswordTxt, outerConfirmPasswordTxt, outerBioTxt;
    private Button saveBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String username, bio, email, password, passwordRep;
    private User user;
    private ImageView profileImage;
    private Uri imageUri;
    private Bitmap bitmap;
    private String currentEmail, currentPass;
    private ProgressBar progressBar;

    public ProfileScreen() {
        // Required empty public constructor
    }

    public ProfileScreen(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        // Elements in the view
        outerUsernameTxt = view.findViewById(R.id.profileUsernameTxt);
        outerEmailTxt = view.findViewById(R.id.profileEmailTxt);
        outerPasswordTxt = view.findViewById(R.id.profilePasswordTxt);
        outerConfirmPasswordTxt = view.findViewById(R.id.profileConfirmPasswordTxt);
        outerBioTxt = view.findViewById(R.id.profileBioTxt);
        nameProfileTxt = view.findViewById(R.id.etProfileUsername);
        emailProfileTxt = view.findViewById(R.id.etProfileEmail);
        passProfileTxt = view.findViewById(R.id.etProfilePassword);
        passConfirmProfileTxt = view.findViewById(R.id.etProfileConfirmPassword);
        bioProfileTxt = view.findViewById(R.id.etProfileBio);
        saveBtn = view.findViewById(R.id.saveBtnProfile);
        profileImage = view.findViewById(R.id.profileImage);
        progressBar = view.findViewById(R.id.progressUpdateProfile);
        TextView changeImageText = view.findViewById(R.id.changeImageText);
        changeImageText.setVisibility(View.VISIBLE);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Gets current user email and pass
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
                                currentEmail = user.getEmail();
                                currentPass = user.getPassword();
                            } else {
                                Log.d("FireStore", "No such document");
                            }
                        } else {
                            Log.d("FireStore", "get failed with ", task.getException());
                        }
                    }
                });

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();

        // Load user image if it's on firebase
        if (user != null) {
            if (user.getImagePath() != null) {
                storageReference.child(user.getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(getContext())
                                .load(uri.toString())
                                .into(profileImage);
                        Log.i("IMAGEGLIDE", uri.toString());
                    }
                });
                changeImageText.setVisibility(View.INVISIBLE);
            }
        }

        // Save all the info in firebase if save button is clicked
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (saveUser()) {
                    if (!username.isEmpty()) {
                        modifyUserInFirebase(username, "username");
                        MainActivity.headerUsername.setText(username);
                    }
                    if (!email.isEmpty()) {
                        modifyEmailAuth(email);
                        MainActivity.headerEmail.setText(email);
                    }
                    if (!password.isEmpty()) {
                        modifyPasswordAuth(password);
                    }
                    if (!bio.isEmpty()) {
                        modifyUserInFirebase(bio, "bio");
                        MainActivity.headerBio.setText(bio);
                    }
                    if (imageUri != null) {
                        uploadImage();
                        modifyUserInFirebase("userImages/"+username, "imagePath");
                        if(user.getImagePath() != null) {
                            MainActivity.headerImage.setImageDrawable(profileImage.getDrawable());
                        }
                    }
                }
            }
        });

        // Add a profile image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });

        // To be able to change username icon color when focused
        nameProfileTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerUsernameTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // To be able to change email icon color when focused
        emailProfileTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerEmailTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // To be able to change password icon color when focused
        passProfileTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerPasswordTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // To be able to change confirm password icon color when focused
        passConfirmProfileTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerConfirmPasswordTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // To be able to change confirm password icon color when focused
        bioProfileTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int color = hasFocus ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey);
                outerBioTxt.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        return view;
    }

    public Boolean saveUser() {
        Boolean readyCheck = false;
        username = nameProfileTxt.getText().toString().trim();
        bio = bioProfileTxt.getText().toString().trim();
        email = emailProfileTxt.getText().toString().trim();
        password = passProfileTxt.getText().toString().trim();
        passwordRep = passConfirmProfileTxt.getText().toString().trim();

        // Bio conditions
        if(bio.length() > 18) {
            bioProfileTxt.setError(getString(R.string.bioMaxLength));
            bioProfileTxt.requestFocus();
            readyCheck = true;
        }

        // Email conditions
        if (email.isEmpty()) {
            readyCheck = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailProfileTxt.setError(getString(R.string.emailNotValid));
            emailProfileTxt.requestFocus();
            readyCheck = true;
        }

        // Password conditions
        if (password.isEmpty()) {
            readyCheck = true;
        } else if(password.length() < 6) {
            passProfileTxt.setError(getString(R.string.passwordMinLength));
            passProfileTxt.requestFocus();
            readyCheck = true;
        } else if(!password.equals(passwordRep)) {
            passConfirmProfileTxt.setError(getString(R.string.passwordNotEqual));
            passConfirmProfileTxt.requestFocus();
            readyCheck = true;
        }

        return readyCheck;
    }

    private void changeImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Declared the bitmap
        bitmap = null;

        if(requestCode == 10 && resultCode == RESULT_OK){

            //We assign the URI of the image
            imageUri = data.getData();

            try {
                //We assign the previously chosen image to our bitmap
                bitmap = MediaStore.Images.Media
                        .getBitmap(getContext().getContentResolver(), imageUri);


            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (requestCode == 20 && resultCode == RESULT_OK){

            bitmap = (Bitmap) data.getExtras().get("data");

        }

        if(bitmap != null){
            //We update our ImageView for the chosen image.
            profileImage.setImageBitmap(bitmap);
        }
    }

    private void uploadImage(){

        //We create the name of the file taking the category as a reference and adding the current date in the declared format.
        username = user.getUsername();
        String imagePath = "userImages/" + username;

        //We declare the fate of the images
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imagePath);

        //Let's put the image inside the storage
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profileImage.setImageURI(imageUri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void modifyUserInFirebase(String valueToUpdate, String field) {
        // Create a new user with a first and last name
        String document = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Update user data
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(document)
                .update(field, valueToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), getString(R.string.profileUpdated), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                        Toast.makeText(getContext(), getString(R.string.profileNotUpdated), Toast.LENGTH_SHORT).show();
                    }
                });

        progressBar.setVisibility(View.INVISIBLE);
    }

    private void modifyEmailAuth(String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.i("EMAIL", "OK");
                } else {
                    Log.i("EMAIL", "NO OK");
                }
            }
        });
        modifyUserInFirebase(email, "email");
    }

    private void modifyPasswordAuth(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.i("PASS", "OK");
                } else {
                    Log.i("PASS", "NO OK");
                }
            }
        });
        modifyUserInFirebase(password, "password");
    }
}