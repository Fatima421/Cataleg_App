package com.catrenat.wapps.Favourites.MovieFav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.catrenat.wapps.Favourites.MovieFav.MovieFavRecyclerView.MovieFavRecyclerView;
import com.catrenat.wapps.Favourites.MusicFav.MusicFavRecyclerView.MusicFavRecyclerView;
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;

import java.util.ArrayList;

public class MovieFavFragment extends Fragment {
    // Properties
    private User user;
    private ArrayList<Serie> seriesList = new ArrayList();
    private ArrayList<Serie> favSerieArray = new ArrayList<>();
    private ArrayList<Pelis> pelisList = new ArrayList();
    private ArrayList<Pelis> favPelisArray = new ArrayList<>();
    private ArrayList<Documental> documentalList = new ArrayList();
    private ArrayList<Documental> favDocusArray = new ArrayList<>();

    public MovieFavFragment() {
        // Required empty public constructor
    }

    public MovieFavFragment(User user, ArrayList<Serie> seriesList, ArrayList<Pelis> pelisLists, ArrayList<Documental> documentalList) {
        this.user = user;
        this.seriesList = seriesList;
        this.pelisList = pelisLists;
        this.documentalList = documentalList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_fav, container, false);
        // Elements from the view
        TextView emptyMovieText = view.findViewById(R.id.emptyMovieFav);
        emptyMovieText.setVisibility(View.INVISIBLE);
        TextView movieFavIntro = view.findViewById(R.id.movieFavIntro);
        movieFavIntro.setVisibility(View.INVISIBLE);

        // Check if user has music in favourites
        if (user != null) {
            if (user.getMovies() != null) {
                if (user.getMovies().isEmpty()) {
                    emptyMovieText.setVisibility(View.VISIBLE);
                    movieFavIntro.setVisibility(View.INVISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    emptyMovieText.setAnimation(animation);
                } else {
                    emptyMovieText.setVisibility(View.INVISIBLE);
                    movieFavIntro.setVisibility(View.VISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    movieFavIntro.setAnimation(animation);
                    for (int i = 0; i < user.getMovies().size(); i++) {
                        if (!seriesList.isEmpty()) {
                            for (int j = 0; j < seriesList.size(); j++) {
                                if (user.getMovies().get(i).equals(seriesList.get(j).getName())) {
                                    favSerieArray.add(seriesList.get(j));
                                }
                            }
                        }
                        if (!pelisList.isEmpty()) {
                            for (int j = 0; j < pelisList.size(); j++) {
                                if (user.getMovies().get(i).equals(pelisList.get(j).getName())) {
                                    favPelisArray.add(pelisList.get(j));
                                }
                            }
                        }
                        if (!documentalList.isEmpty()) {
                            for (int j = 0; j < documentalList.size(); j++) {
                                if (user.getMovies().get(i).equals(documentalList.get(j).getName())) {
                                    favDocusArray.add(documentalList.get(j));
                                }
                            }
                        }
                    }
                }
            }
        }
        // Creating Recycler view
        // RecyclerView declared and init with array
        RecyclerView recyclerView = view.findViewById(R.id.movieFavRecyclerView);
        MovieFavRecyclerView adapter = new MovieFavRecyclerView(getContext(), favSerieArray, favPelisArray, favDocusArray);
        recyclerView.setAdapter(adapter);

        // Disables recyclerView nested scroll
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3) {
            @Override
            public boolean canScrollVertically() { return false; }
        });

        return view;
    }
}