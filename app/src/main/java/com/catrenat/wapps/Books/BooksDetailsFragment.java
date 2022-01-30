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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Books.RecyclerView.BookTagRecyclerViewAdapter;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Movies.RecyclerView.MovieTagRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BooksDetailsFragment extends Fragment {

    private BookTagRecyclerViewAdapter bookTagAdapter;
    boolean heartPressed = false;
    Book book = new Book();

    public BooksDetailsFragment() {
        // Required empty public constructor
    }

    public BooksDetailsFragment(Book book) {
        this.book = book;
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

        ImageView bookImage = view.findViewById(R.id.imageBook);
        TextView bookTitle = view.findViewById(R.id.bookTitle);
        TextView bookAuthor = view.findViewById(R.id.bookAuthor);
        TextView bookSinopsis = view.findViewById(R.id.bookDescription);
        bookSinopsis.setMovementMethod(new ScrollingMovementMethod());
        ImageView bookFavImg = view.findViewById(R.id.bookFav);
        ImageView bookShareImg = view.findViewById(R.id.bookShareImg);
        RecyclerView bookTagRecyclerView = view.findViewById(R.id.bookTagRecyclerView);

        // Setting book info to the values
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookSinopsis.setText(book.getDescription());
        Log.i("edwing", ""+book.getUrl());

        bookTagAdapter = new BookTagRecyclerViewAdapter(book.getGenres());
        bookTagRecyclerView.setAdapter(bookTagAdapter);
        bookTagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child(book.getImagePath());

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bookImage.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });

        // Movie favourite button
        bookFavImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = (!heartPressed) ? R.drawable.ic_music_filled_heart : R.drawable.ic_music_heart;
                heartPressed = current != R.drawable.ic_music_heart;
                bookFavImg.setImageResource(current);
            }
        });

        bookShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBookLink();
            }
        });

        return view;
    }

    // Share movie
    public void shareBookLink() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        Log.i("edwing", ""+book.getUrl());
        sendIntent.putExtra(Intent.EXTRA_TEXT, book.getUrl());
        sendIntent.setType("text/plain");
        getContext().startActivity(sendIntent);
    }
}
