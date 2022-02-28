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
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DocusFavRecyclerView extends RecyclerView.Adapter<DocusFavRecyclerView.DocusFavViewHolder> {
    private Context context;
    ArrayList<Documental> documentals;

    public DocusFavRecyclerView(Context context, ArrayList<Documental> documentals) {
        this.context = context;
        this.documentals = documentals;
    }

    @NonNull
    @Override
    public DocusFavRecyclerView.DocusFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docus_fav, parent, false);
        DocusFavRecyclerView.DocusFavViewHolder holder = new DocusFavRecyclerView.DocusFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DocusFavRecyclerView.DocusFavViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if (!documentals.isEmpty()) {
            if(!documentals.get(position).getImagePath().isEmpty()) {
                storageReference.child(documentals.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(context) // Context from getContext() in HomeFragment
                                .load(uri.toString())
                                .into(holder.docFavImage);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return documentals.size();
    }

    public class DocusFavViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView docFavImage;

        public DocusFavViewHolder(@NonNull View itemView) {
            super(itemView);
            docFavImage = itemView.findViewById(R.id.docsFavImage);
        }
    }
}