package com.catrenat.wapps.Books;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Books.RecyclerView.BookTagRecyclerViewAdapter;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.blurry.Blurry;

public class BooksDetailsFragment extends Fragment {

    private BookTagRecyclerViewAdapter bookTagAdapter;
    boolean heartPressed = false;
    private Book book = new Book();
    private User user;
    private FirebaseFirestore db;
    BottomNavigationView bottomNav;

    public BooksDetailsFragment() {
        // Required empty public constructor
    }

    public BooksDetailsFragment(Book book, User user) {
        this.book = book;
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
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        ImageView bookImageBackground = view.findViewById(R.id.imageBookBackground);
        ImageView bookImage = view.findViewById(R.id.imageBook);
        TextView bookTitle = view.findViewById(R.id.bookTitle);
        TextView bookAuthor = view.findViewById(R.id.bookAuthor);
        TextView bookSinopsis = view.findViewById(R.id.bookDescription);
        bookSinopsis.setMovementMethod(new ScrollingMovementMethod());
        ImageView bookFavImg = view.findViewById(R.id.bookFav);
        ImageView bookShareImg = view.findViewById(R.id.bookShareImg);
        RecyclerView bookTagRecyclerView = view.findViewById(R.id.bookTagRecyclerView);
        ImageView bookShop = view.findViewById(R.id.bookShop);
        TextView bookShareTxt = view.findViewById(R.id.bookShareText);
        TextView bookShopText = view.findViewById(R.id.bookShopTxt);
        TextView bookFavTxt = view.findViewById(R.id.bookFavouriteText);
        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);

        // Setting book info to the values
        bookTitle.setText(book.getTitle());
        bookAuthor.setText("Autor: " + book.getAuthor());
        bookSinopsis.setText(book.getDescription());

        bookTagAdapter = new BookTagRecyclerViewAdapter(book.getGenres());
        bookTagRecyclerView.setAdapter(bookTagAdapter);
        bookTagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        if (book.getImagePath() != null) {
            StorageReference photoReference= storageReference.child(book.getImagePath());
            final long ONE_MEGABYTE = 1024 * 1024;
            photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bookImage.setImageBitmap(bmp);
                    bookImageBackground.setImageBitmap(bmp);
                    if (getContext() != null) {
                        Blurry.with(getContext())
                                .radius(8)
                                .sampling(6)
                                .async()
                                .capture(view.findViewById(R.id.imageBookBackground))
                                .into(view.findViewById(R.id.imageBookBackground));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                }
            });
        }

        // To be able to open the book shop link
        bookShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book.getUrl() != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getUrl()));
                    startActivity(browserIntent);
                }
            }
        });

        bookShopText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book.getUrl() != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getUrl()));
                    startActivity(browserIntent);
                }
            }
        });

        // Book favourite button
        bookFavImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = (!heartPressed) ? R.drawable.ic_music_filled_heart : R.drawable.ic_music_heart;
                heartPressed = current != R.drawable.ic_music_heart;
                bookFavImg.setImageResource(current);
                if (book != null) {
                    if (heartPressed) {
                        addFavToFirebase(book.getTitle());
                    } else {
                        deleteFavFromFirebase(book.getTitle());
                    }
                }
            }
        });

        bookFavTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = (!heartPressed) ? R.drawable.ic_music_filled_heart : R.drawable.ic_music_heart;
                heartPressed = current != R.drawable.ic_music_heart;
                bookFavImg.setImageResource(current);
                if (book != null) {
                    if (heartPressed) {
                        addFavToFirebase(book.getTitle());
                    } else {
                        deleteFavFromFirebase(book.getTitle());
                    }
                }
            }
        });

        // Load favourite image
        if (user != null) {
            if (user.getBooks() != null) {
                for (int i = 0; i < user.getBooks().size(); i++) {
                    if (user.getBooks().get(i).equals(book.getTitle())) {
                        bookFavImg.setImageResource(R.drawable.ic_music_filled_heart);
                    }
                }
            }
        }

        // To be able to share a book
        bookShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBookLink();
            }
        });

        bookShareTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBookLink();
            }
        });

        return view;
    }

    // Share Book
    public void shareBookLink() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, book.getUrl());
        sendIntent.setType("text/plain");
        getContext().startActivity(sendIntent);
    }

    public void addFavToFirebase(String bookName) {
        // Create a new user with a first and last name
        String document = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(document)
                .update("books", FieldValue.arrayUnion(bookName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = document.toObject(User.class);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void deleteFavFromFirebase(String bookName) {
        // Create a new user with a first and last name
        String document = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(document)
                .update("books", FieldValue.arrayRemove(bookName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = document.toObject(User.class);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
