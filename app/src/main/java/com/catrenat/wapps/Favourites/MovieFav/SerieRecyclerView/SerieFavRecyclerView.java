package com.catrenat.wapps.Favourites.MovieFav.SerieRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SerieFavRecyclerView extends RecyclerView.Adapter<SerieFavRecyclerView.SerieFavViewHolder> {
    private Context context;
    ArrayList<Serie> series;

    public SerieFavRecyclerView(Context context, ArrayList<Serie> series) {
        this.context = context;
        this.series = series;
    }

    @NonNull
    @Override
    public SerieFavRecyclerView.SerieFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie_fav, parent, false);
        SerieFavRecyclerView.SerieFavViewHolder holder = new SerieFavRecyclerView.SerieFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SerieFavRecyclerView.SerieFavViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if (!series.isEmpty()) {
            if(!series.get(position).getImagePath().isEmpty()) {
                storageReference.child(series.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(context) // Context from getContext() in HomeFragment
                                .load(uri.toString())
                                .into(holder.favSerieImage);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public class SerieFavViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView favSerieImage;

        public SerieFavViewHolder(@NonNull View itemView) {
            super(itemView);
            favSerieImage = itemView.findViewById(R.id.serieFavImage);
        }
    }
}
