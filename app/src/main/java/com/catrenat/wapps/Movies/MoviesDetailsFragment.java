package com.catrenat.wapps.Movies;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Movies.RecyclerView.MovieTagRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.youtube.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

import java.util.ArrayList;

public class MoviesDetailsFragment extends Fragment {
    // Properties
    private Serie serie;
    private ArrayList<String> genres;
    private String selectedPlatform;
    YouTubePlayerView youTubePlayerView;

    public MoviesDetailsFragment() {
        // Required empty public constructor
    }

    public MoviesDetailsFragment(Serie serie, String selectedPlatform) {
        this.serie = serie;
        this.genres = serie.getGenres();
        this.selectedPlatform = selectedPlatform;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies_details, container, false);

        // View Elements
        TextView movieTitle = view.findViewById(R.id.movieTitle);
        TextView seasonsAndEpisodes = view.findViewById(R.id.episodesAndSeasons);
        TextView movieSinopsis = view.findViewById(R.id.movieSinopsis);
        movieSinopsis.setMovementMethod(new ScrollingMovementMethod());
        TextView movieShareTxt = view.findViewById(R.id.movieShareText);
        ImageView movieShareImg = view.findViewById(R.id.movieShareImg);
        TextView moviePlatformTxt = view.findViewById(R.id.moviePlatformTxt);
        ImageView moviePlatformImg = view.findViewById(R.id.moviePlatformImg);
        RecyclerView movieTagRecyclerView = view.findViewById(R.id.movieTagRecyclerView);
        youTubePlayerView = view.findViewById(R.id.movieYoutubePlayer);

        // Setting values to the view elements
        movieTitle.setText(serie.getName());
        seasonsAndEpisodes.setText(serie.getSeasons() + " "+getString(R.string.seasons)+" "+ serie.getEpisodes() + " "+getString(R.string.episodes)+" ");
        movieSinopsis.setText(serie.getSinopsis());

        // Tag RecyclerView
        MovieTagRecyclerViewAdapter movieTagAdapter = new MovieTagRecyclerViewAdapter(serie.getGenres());
        movieTagRecyclerView.setAdapter(movieTagAdapter);
        movieTagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set moviePlatform Image
        if (selectedPlatform.equals(getString(R.string.netflix))) {
            moviePlatformImg.setImageResource(R.drawable.netflix);
        }

        // Movie trailer
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = serie.getYoutubeUrl();
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // using pre-made custom ui
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youTubePlayerView, youTubePlayer);
                defaultPlayerUiController.showFullscreenButton(false);
                youTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
            }
        };
        // disable iframe ui
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        youTubePlayerView.initialize(listener, options);


        // Share button
        movieShareTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMovieTrailer();
            }
        });

        movieShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMovieTrailer();
            }
        });
        
        // Platform link button
        moviePlatformTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlatform();
            }
        });

        moviePlatformImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlatform();
            }
        });

        return view;
    }

    // Share movie
    public void shareMovieTrailer() {
        String youtubeInitialText = "https://www.youtube.com/watch?v=";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, youtubeInitialText + serie.getYoutubeUrl());
        sendIntent.setType("text/plain");
        getContext().startActivity(sendIntent);
    }

    public void openPlatform() {

    }
}