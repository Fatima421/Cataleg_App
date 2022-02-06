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
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.Movies.MoviesDetailsFragment;
import com.catrenat.wapps.Movies.RecyclerView.SearchListener;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private User user;

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

                // Creating the database instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesDetailsFragment(serie, selectedPlatform, user), "moviesDetailsFragment").addToBackStack(null).commit();
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
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
