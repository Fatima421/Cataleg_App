package com.catrenat.wapps.Favourites.MovieFav.DocusRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Favourites.MovieFav.MovieFavRecyclerView.MovieFavRecyclerView;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DocusFavRecyclerView extends RecyclerView.Adapter<MovieFavRecyclerView.MovieFavViewHolder> {
    private Context context;
    ArrayList<Pelis> pelis;

    public DocusFavRecyclerView(Context context, ArrayList<Pelis> pelis) {
        this.context = context;
        this.pelis = pelis;
    }

    @NonNull
    @Override
    public DocusFavRecyclerView.MovieFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docus_fav, parent, false);
        MovieFavRecyclerView.MovieFavViewHolder holder = new MovieFavRecyclerView.MovieFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieFavRecyclerView.MovieFavViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if (!pelis.isEmpty()) {
            if(!pelis.get(position).getImagePath().isEmpty()) {
                storageReference.child(pelis.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(context) // Context from getContext() in HomeFragment
                                .load(uri.toString())
                                .into(holder.favMovieImage);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return pelis.size();
    }

    public class MovieFavViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView favMovieImage;

        public MovieFavViewHolder(@NonNull View itemView) {
            super(itemView);
            favMovieImage = itemView.findViewById(R.id.movieFavImage);
        }
    }
}