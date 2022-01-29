package com.catrenat.wapps.Favourites.GameFav.GameFavRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Favourites.MusicFav.MusicFavRecyclerView.MusicFavRecyclerView;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GameFavRecyclerView extends RecyclerView.Adapter<GameFavRecyclerView.GameFavViewHolder> {
    private Context context;
    ArrayList<Game> favGameArray;

    public GameFavRecyclerView(ArrayList<Game> favMusicArray, Context context){
        this.favGameArray = favMusicArray;
        this.context = context;
    }

    @NonNull
    @Override
    public GameFavRecyclerView.GameFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_fav, parent, false);
        GameFavRecyclerView.GameFavViewHolder holder = new GameFavRecyclerView.GameFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameFavRecyclerView.GameFavViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!favGameArray.get(position).getImagePath().isEmpty()) {
            storageReference.child(favGameArray.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.gameFavImage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return favGameArray.size();
    }

    public class GameFavViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView gameFavImage;

        public GameFavViewHolder(@NonNull View itemView) {
            super(itemView);
            gameFavImage = itemView.findViewById(R.id.gameFavImage);
        }
    }
}