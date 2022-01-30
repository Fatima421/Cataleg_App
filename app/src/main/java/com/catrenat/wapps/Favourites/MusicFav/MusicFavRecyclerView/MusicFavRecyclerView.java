package com.catrenat.wapps.Favourites.MusicFav.MusicFavRecyclerView;

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
import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.Movies.MoviesDetailsFragment;
import com.catrenat.wapps.Movies.RecyclerView.Series.SeriesRecyclerViewAdapter;
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

public class MusicFavRecyclerView extends RecyclerView.Adapter<MusicFavRecyclerView.FavMusicViewHolder> {
    private Context context;
    ArrayList<Music> favMusicArray;

    public MusicFavRecyclerView(ArrayList<Music> favMusicArray, Context context){
       this.favMusicArray = favMusicArray;
       this.context = context;
    }

    @NonNull
    @Override
    public MusicFavRecyclerView.FavMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_fav, parent, false);
        MusicFavRecyclerView.FavMusicViewHolder holder = new MusicFavRecyclerView.FavMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicFavRecyclerView.FavMusicViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!favMusicArray.get(position).getSongImageUrl().isEmpty()) {
            storageReference.child(favMusicArray.get(position).getSongImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.favMusicImage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return favMusicArray.size();
    }

    public class FavMusicViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView favMusicImage;

        public FavMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            favMusicImage = itemView.findViewById(R.id.musicFavImage);
        }
    }
}
