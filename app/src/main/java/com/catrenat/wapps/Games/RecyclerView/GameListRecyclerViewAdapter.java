package com.catrenat.wapps.Games.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Games.DetailGameFragment;
import com.catrenat.wapps.Games.GamesListFragment;
import com.catrenat.wapps.Models.Game;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GameListRecyclerViewAdapter extends RecyclerView.Adapter<GameListRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Game> games;
    private ArrayList<Game> all_games;
    private Context context;
    private User user;
    private String selectedPlatform;

    public GameListRecyclerViewAdapter() {
    }

    public GameListRecyclerViewAdapter(ArrayList<Game> games, Context context, String selectedPlatform){
        this.games = games;
        this.context = context;
        all_games = new ArrayList<>();
        all_games.addAll(games);
        this.selectedPlatform = selectedPlatform;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_listgame, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gets image url from firebase storage
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(games.get(position).getImagePath() != null) {
            storageReference.child(games.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.gameImage);
                    Log.i("IMAGEGLIDE", uri.toString());
                }
            });
        }
        holder.gameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Preparation for fragment transaction
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
                                // Prepares and sets bundle for Detail fragment
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("platform", selectedPlatform);
                                DetailGameFragment detailFragment = new DetailGameFragment(games.get(position), user);
                                detailFragment.setArguments(bundle);

                                // Fragment transaction
                                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,  detailFragment).addToBackStack(null).commit();                                } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
            }
        });
    }

    // SearchBar filter
    public void filter(String string){
        String search = string.toLowerCase();
        if(search.length() == 0){
            games.clear();
            games.addAll(all_games);
        } else {
            games.clear();
            for(Game game: all_games) {
                if(game.getName().toLowerCase().contains(search)) {
                    games.add(game);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView gameImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.gameItemImage);

        }
    }
}
