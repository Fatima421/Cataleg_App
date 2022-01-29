package com.catrenat.wapps.Games;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.catrenat.wapps.Games.RecyclerView.GameListRecyclerViewAdapter;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class GamesListFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Game> games;
    private String selectedPlatform;
    private GameListRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_games_list, container, false);

        // Properties
        MotionLayout motionLayout = root.findViewById(R.id.gameListLayout);

        // Gets data from bundle
        Bundle bundle = getArguments();
        selectedPlatform = (String) bundle.getSerializable("platform");

        // Data reading from firestore database
        db = FirebaseFirestore.getInstance();
        games = new ArrayList<>();
        db.collection("Games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            // RecyclerView array argument construction
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                Game game = document.toObject(Game.class);
                                Log.d("Platform", document.getId() + " => " + document.getData());
                                // Adds only games with current platform selected
                                for(String platform: game.getPlatforms()) {
                                    if(selectedPlatform.equals(platform)) {
                                        games.add(game);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        // RecyclerView declared and init with array
                        RecyclerView recyclerView = root.findViewById(R.id.gameListRecyclerView);
                        adapter = new GameListRecyclerViewAdapter(games, getContext());
                        recyclerView.setAdapter(adapter);

                        // Disables recyclerView nested scroll
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3) {
                            @Override
                            public boolean canScrollVertically() { return false; }
                        });
                    }
                });
        // Searcher that lets search items by name
        SearchView searchItem = root.findViewById(R.id.gamesSearchBar);
        searchItem.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Click", "Se iso clic open");
                motionLayout.transitionToEnd();
            }
        });
        searchItem.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("Click", "Se iso clic close");
                motionLayout.transitionToStart();
                return false;
            }
        });
        searchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }});

        return root;
    }
}