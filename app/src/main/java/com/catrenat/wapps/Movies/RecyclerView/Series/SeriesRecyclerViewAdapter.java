package com.catrenat.wapps.Movies.RecyclerView.Series;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Movies.MoviesDetailsFragment;
import com.catrenat.wapps.Movies.RecyclerView.SearchListener;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeriesRecyclerViewAdapter extends RecyclerView.Adapter<SeriesRecyclerViewAdapter.SeriesViewHolder> {
    private List<Serie> series;
    private List<Serie> all_series;
    private Context context;
    private String selectedPlatform;

    public SeriesRecyclerViewAdapter(List<Serie> series, Context context, String selectedPlatform){
        this.series = series;
        this.context = context;
        this.selectedPlatform = selectedPlatform;
        all_series = new ArrayList<>();
        all_series.addAll(series);
    }

    @NonNull
    @Override
    public SeriesRecyclerViewAdapter.SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_series, parent, false);
        SeriesRecyclerViewAdapter.SeriesViewHolder holder = new SeriesRecyclerViewAdapter.SeriesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesRecyclerViewAdapter.SeriesViewHolder holder, int position) {
        // Serie in the current position
        Serie serie = series.get(position);

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!series.get(position).getImagePath().isEmpty()) {
            storageReference.child(series.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.serieImage);
                }
            });
        }
        holder.serieImage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                AppCompatActivity app = (AppCompatActivity) view.getContext();
                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesDetailsFragment(serie, selectedPlatform), "moviesDetailsFragment").addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public class SeriesViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView serieImage;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            serieImage = itemView.findViewById(R.id.serieImage);
        }
    }
}
