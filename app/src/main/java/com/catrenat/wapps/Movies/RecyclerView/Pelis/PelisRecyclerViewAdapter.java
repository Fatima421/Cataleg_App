package com.catrenat.wapps.Movies.RecyclerView.Pelis;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.Movies.MoviesDetailsFragment;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PelisRecyclerViewAdapter extends RecyclerView.Adapter<PelisRecyclerViewAdapter.PelisViewHolder> {
    private List<Pelis> pelis;
    private Context context;
    private String selectedPlatform;
    private User user;

    public PelisRecyclerViewAdapter(List<Pelis> pelis, Context context, String selectedPlatform){
        this.pelis = pelis;
        this.context = context;
        this.selectedPlatform = selectedPlatform;
    }

    @NonNull
    @Override
    public PelisRecyclerViewAdapter.PelisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelis, parent, false);
        PelisRecyclerViewAdapter.PelisViewHolder holder = new PelisRecyclerViewAdapter.PelisViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PelisRecyclerViewAdapter.PelisViewHolder holder, int position) {
        // Peli in the current position
        Pelis peli = pelis.get(position);

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!pelis.get(position).getImagePath().isEmpty() && pelis.get(position).getImagePath() != null) {
            storageReference.child(pelis.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.pelisImage);
                }
            });
        }

        holder.pelisImage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesDetailsFragment(peli, selectedPlatform, user), "moviesDetailsFragment").addToBackStack(null).commit();
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
        return pelis.size();
    }

    public class PelisViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView pelisImage;

        public PelisViewHolder(@NonNull View itemView) {
            super(itemView);
            pelisImage = itemView.findViewById(R.id.pelisImage);
        }
    }
}
