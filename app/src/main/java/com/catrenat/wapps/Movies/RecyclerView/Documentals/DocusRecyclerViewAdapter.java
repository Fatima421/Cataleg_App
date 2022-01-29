package com.catrenat.wapps.Movies.RecyclerView.Documentals;

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

public class DocusRecyclerViewAdapter extends RecyclerView.Adapter<DocusRecyclerViewAdapter.DocusViewHolder> {
    private List<Documental> documentals;
    private Context context;
    private String selectedPlatform;
    private User user;

    public DocusRecyclerViewAdapter(List<Documental> documentals, Context context, String selectedPlatform){
        this.documentals = documentals;
        this.context = context;
        this.selectedPlatform = selectedPlatform;
    }

    @NonNull
    @Override
    public DocusRecyclerViewAdapter.DocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docus, parent, false);
        DocusRecyclerViewAdapter.DocusViewHolder holder = new DocusRecyclerViewAdapter.DocusViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DocusRecyclerViewAdapter.DocusViewHolder holder, int position) {
        // Documental in the current position
        Documental documental = documentals.get(position);

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!documentals.get(position).getImagePath().isEmpty() && documentals.get(position).getImagePath() != null) {
            storageReference.child(documentals.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.docusImage);
                }
            });
        }

        holder.docusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesDetailsFragment(documental, selectedPlatform, user), "moviesDetailsFragment").addToBackStack(null).commit();
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
        return documentals.size();
    }

    public class DocusViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView docusImage;

        public DocusViewHolder(@NonNull View itemView) {
            super(itemView);
            docusImage = itemView.findViewById(R.id.docusImage);
        }
    }
}
