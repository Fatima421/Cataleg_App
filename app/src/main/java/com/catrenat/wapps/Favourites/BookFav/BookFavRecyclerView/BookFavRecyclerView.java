package com.catrenat.wapps.Favourites.BookFav.BookFavRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BookFavRecyclerView extends RecyclerView.Adapter<BookFavRecyclerView.BookFavViewHolder> {
    private Context context;
    ArrayList<Book> favBookArray;

    public BookFavRecyclerView(ArrayList<Book> favBookArray, Context context) {
        this.favBookArray = favBookArray;
        this.context = context;
    }

    @NonNull
    @Override
    public BookFavRecyclerView.BookFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_fav, parent, false);
        BookFavRecyclerView.BookFavViewHolder holder = new BookFavRecyclerView.BookFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookFavRecyclerView.BookFavViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if (!favBookArray.get(position).getImagePath().isEmpty()) {
            storageReference.child(favBookArray.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.bookFavImage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return favBookArray.size();
    }

    public class BookFavViewHolder extends RecyclerView.ViewHolder {
        // View Elements
        ImageView bookFavImage;

        public BookFavViewHolder(@NonNull View itemView) {
            super(itemView);
            bookFavImage = itemView.findViewById(R.id.bookFavImage);
        }
    }
}