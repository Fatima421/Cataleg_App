package com.catrenat.wapps.Favourites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.R;

public class GeneralFavFragment extends Fragment {

    public GeneralFavFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general_fav, container, false);

        // View Elements
        TextView favsWelcomeTxt = view.findViewById(R.id.seeFavsText);
        ImageView musicFav = view.findViewById(R.id.musicFavSection);
        ImageView movieFav = view.findViewById(R.id.movieFavSection);
        ImageView gameFav = view.findViewById(R.id.gameFavSection);
        ImageView bookFav = view.findViewById(R.id.bookFavSection);

        // Making the welcome text fade in
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        favsWelcomeTxt.setAnimation(animation);

        // When music fav square clicked
        musicFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MusicFavFragment()).addToBackStack(null).commit();
            }
        });

        // When movies fav square clicked
        movieFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MovieFavFragment()).addToBackStack(null).commit();
            }
        });

        // When games fav square clicked
        gameFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GameFavFragment()).addToBackStack(null).commit();
            }
        });

        // When books fav square clicked
        bookFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BookFavFragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }

}