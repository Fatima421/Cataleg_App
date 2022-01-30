package com.catrenat.wapps.Favourites.GameFav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.catrenat.wapps.Favourites.GameFav.GameFavRecyclerView.GameFavRecyclerView;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;

import java.util.ArrayList;

public class GameFavFragment extends Fragment {
    // Properties
    private User user;
    private ArrayList<Game> gameArray = new ArrayList<>();
    private ArrayList<Game> favGameArray = new ArrayList<>();

    public GameFavFragment() {
        // Required empty public constructor
    }

    public GameFavFragment(User user, ArrayList<Game> gameArray) {
        this.user = user;
        this.gameArray = gameArray;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games_fav, container, false);

        // Elements from the view
        TextView emptyGameFavs = view.findViewById(R.id.emptyGameFavs);
        emptyGameFavs.setVisibility(View.INVISIBLE);
        TextView gameFavIntro = view.findViewById(R.id.gameFavIntro);
        gameFavIntro.setVisibility(View.INVISIBLE);

        // Check if user has music in favourites
        if (user != null) {
            if (user.getGames() != null) {
                if (user.getGames().isEmpty()) {
                    emptyGameFavs.setVisibility(View.VISIBLE);
                    gameFavIntro.setVisibility(View.INVISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    emptyGameFavs.setAnimation(animation);
                } else {
                    emptyGameFavs.setVisibility(View.INVISIBLE);
                    gameFavIntro.setVisibility(View.VISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    gameFavIntro.setAnimation(animation);
                    for (int i = 0; i < user.getGames().size(); i++) {
                        for (int j = 0; j < gameArray.size(); j++) {
                            if (user.getGames().get(i).equals(gameArray.get(j).getName())) {
                                favGameArray.add(gameArray.get(j));
                            }
                        }
                    }
                }
            } else {
                emptyGameFavs.setVisibility(View.VISIBLE);
                gameFavIntro.setVisibility(View.INVISIBLE);
                // Making the welcome text fade in
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                emptyGameFavs.setAnimation(animation);
            }
        }

        // Creating Recycler view
        // RecyclerView declared and init with array
        RecyclerView recyclerView = view.findViewById(R.id.gameFavRecyclerView);
        GameFavRecyclerView adapter = new GameFavRecyclerView(favGameArray, getContext());
        recyclerView.setAdapter(adapter);

        // Disables recyclerView nested scroll
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3) {
            @Override
            public boolean canScrollVertically() { return false; }
        });

        return view;
    }
}