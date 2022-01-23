package com.catrenat.wapps.Movies;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Movies.RecyclerView.MovieTagRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

import java.util.ArrayList;

public class MoviesDetailsFragment extends Fragment {
    // Properties
    private Serie serie;
    private Pelis peli;
    private ArrayList<String> genres;
    private String selectedPlatform;
    YouTubePlayerView youTubePlayerView;
    boolean heartPressed = false;
    MovieTagRecyclerViewAdapter movieTagAdapter;
    private Documental documental;

    public MoviesDetailsFragment() {
        // Required empty public constructor
    }

    public MoviesDetailsFragment(Serie serie, String selectedPlatform) {
        this.serie = serie;
        this.genres = serie.getGenres();
        this.selectedPlatform = selectedPlatform;
    }

    public MoviesDetailsFragment(Pelis peli, String selectedPlatform) {
        this.peli = peli;
        this.genres = peli.getGenres();
        this.selectedPlatform = selectedPlatform;
    }

    public MoviesDetailsFragment(Documental documental, String selectedPlatform) {
        this.documental = documental;
        this.genres = documental.getGenres();
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
        ImageView favouriteImg = view.findViewById(R.id.movieFav);
        RecyclerView movieTagRecyclerView = view.findViewById(R.id.movieTagRecyclerView);
        youTubePlayerView = view.findViewById(R.id.movieYoutubePlayer);
        seasonsAndEpisodes.setVisibility(View.VISIBLE);

        if (serie != null) {
            // Setting values to the view elements
            movieTitle.setText(serie.getName());
            if (serie.getSeasons().equals("1")) {
                seasonsAndEpisodes.setText(serie.getSeasons() + " "+getString(R.string.season)+" "+ serie.getEpisodes() + " "+getString(R.string.episodes)+" ");
            } else {
                seasonsAndEpisodes.setText(serie.getSeasons() + " "+getString(R.string.seasons)+" "+ serie.getEpisodes() + " "+getString(R.string.episodes)+" ");
            }
            movieSinopsis.setText(serie.getSinopsis());
            // Tag RecyclerView
            movieTagAdapter = new MovieTagRecyclerViewAdapter(serie.getGenres());

        } else if (peli != null) {
            // Setting values to the view elements
            movieTitle.setText(peli.getName());
            movieSinopsis.setText(peli.getSinopsis());
            seasonsAndEpisodes.setVisibility(View.GONE);
            // Tag RecyclerView
            movieTagAdapter = new MovieTagRecyclerViewAdapter(peli.getGenres());

        } else if (documental != null) {
            // Setting values to the view elements
            movieTitle.setText(documental.getName());
            if (documental.getSeasons().equals("1")) {
                seasonsAndEpisodes.setText( documental.getSeasons()+" "+getString(R.string.season)+" "+ documental.getEpisodes() + " "+getString(R.string.episodes)+" ");
            }
            if (documental.getEpisodes().equals("1")) {
                seasonsAndEpisodes.setText( documental.getSeasons()+" "+getString(R.string.seasons)+" "+ documental.getEpisodes() + " "+getString(R.string.episode)+" ");
            }
            if (documental.getSeasons().equals("1") && documental.getEpisodes().equals("1")) {
                seasonsAndEpisodes.setText( documental.getSeasons()+" "+getString(R.string.season)+" "+ documental.getEpisodes() + " "+getString(R.string.episode)+" ");
            } else {
                seasonsAndEpisodes.setText( documental.getSeasons()+" "+getString(R.string.seasons)+" "+ documental.getEpisodes() + " "+getString(R.string.episodes)+" ");
            }
            movieSinopsis.setText(documental.getSinopsis());
            // Tag RecyclerView
            movieTagAdapter = new MovieTagRecyclerViewAdapter(documental.getGenres());
        }

        movieTagRecyclerView.setAdapter(movieTagAdapter);
        movieTagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set moviePlatform Image
        if (selectedPlatform.equals(getString(R.string.netflix))) {
            moviePlatformImg.setImageResource(R.drawable.netflix);
        }

        if (selectedPlatform.equals(getString(R.string.tv3))) {
            moviePlatformImg.setImageResource(R.drawable.tv3);
        }

        if (selectedPlatform.equals(getString(R.string.disney))) {
            moviePlatformImg.setImageResource(R.drawable.disney);
        }

        if (selectedPlatform.equals(getString(R.string.super3))) {
            moviePlatformImg.setImageResource(R.drawable.super3);
        }

        if (selectedPlatform.equals(getString(R.string.primeVideo))) {
            moviePlatformImg.setImageResource(R.drawable.primevideo);
        }

        if (selectedPlatform.equals(getString(R.string.filmin))) {
            moviePlatformImg.setImageResource(R.drawable.filmin);
        }

        // Movie favourite button
        favouriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = (!heartPressed) ? R.drawable.ic_music_filled_heart : R.drawable.ic_music_heart;
                heartPressed = current != R.drawable.ic_music_heart;
                favouriteImg.setImageResource(current);
            }
        });

        // Movie trailer
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "";
                if (serie != null) {
                    videoId = serie.getYoutubeUrl();
                } else if (peli != null) {
                    videoId = peli.getYoutubeUrl();
                } else if (documental != null) {
                    videoId = documental.getYoutubeUrl();
                }
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
        if (serie != null) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, youtubeInitialText + serie.getYoutubeUrl());
        } else if (peli != null) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, youtubeInitialText + peli.getYoutubeUrl());
        } else if (documental != null) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, youtubeInitialText + documental.getYoutubeUrl());
        }
        sendIntent.setType("text/plain");
        getContext().startActivity(sendIntent);
    }

    public void openPlatform() {
        if (selectedPlatform.equals(getString(R.string.netflix))) {
            openNetflix();
        }

        if (selectedPlatform.equals(getString(R.string.tv3))) {
            openTV3();
        }

        if (selectedPlatform.equals(getString(R.string.disney))) {
            openDisney();
        }

        if (selectedPlatform.equals(getString(R.string.super3))) {
            openSuper3();
        }

        if (selectedPlatform.equals(getString(R.string.primeVideo))) {
            openPrimeVideo();
        }

        if (selectedPlatform.equals(getString(R.string.filmin))) {
            openFilmin();
        }
    }

    public void openNetflix() {
        String baseUrl = "http://www.netflix.com/";
        String netflixPackage = "com.netflix.mediaclient";
        String playStorePackage = "com.android.vending";

        // Creating intents to check whether the app exists or not in the device
        Intent playStoreExists = getContext().getPackageManager().getLaunchIntentForPackage(playStorePackage);
        try {
            // Open Netflix
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
            startActivity(intent);
        } catch (Exception e) {
            if (playStoreExists != null) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + netflixPackage)));
            }
        }
    }

    public void openTV3() {
        String baseUrl = "http://www.ccma.cat/tv3/alacarta/";
        String tv3packageName = "cat.tv3.eng.tresac";
        String playStorePackage = "com.android.vending";

        // Creating intents to check whether the app exists or not in the device
        Intent playStoreExists = getContext().getPackageManager().getLaunchIntentForPackage(playStorePackage);
        try {
            // Open TV3
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
            startActivity(intent);
        } catch (Exception e) {
            if (playStoreExists != null) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + tv3packageName)));
            }
        }
    }

    public void openDisney() {
        String baseUrl = "http://www.disneyplus.com/";
        String disneyPackage = "com.disney.disneyplus";
        String playStorePackage = "com.android.vending";

        // Creating intents to check whether the app exists or not in the device
        Intent playStoreExists = getContext().getPackageManager().getLaunchIntentForPackage(playStorePackage);
        try {
            // Open Disney
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
            startActivity(intent);
        } catch (Exception e) {
            if (playStoreExists != null) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + disneyPackage)));
            }
        }
    }

    public void openSuper3() {
        String baseUrl = "http://www.ccma.cat/tv3/super3/";
        String super3package = "es.jm.dolores.tv3";
        String playStorePackage = "com.android.vending";

        // Creating intents to check whether the app exists or not in the device
        Intent playStoreExists = getContext().getPackageManager().getLaunchIntentForPackage(playStorePackage);
        try {
            // Open Super3
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
            startActivity(intent);
        } catch (Exception e) {
            if (playStoreExists != null) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + super3package)));
            }
        }
    }

    public void openPrimeVideo() {
        String baseUrl = "http://www.primevideo.com/";
        String primeVideoPackage = "com.amazon.avod.thirdpartyclient";
        String playStorePackage = "com.android.vending";

        // Creating intents to check whether the app exists or not in the device
        Intent playStoreExists = getContext().getPackageManager().getLaunchIntentForPackage(playStorePackage);
        try {
            // Open Prime Video
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
            startActivity(intent);
        } catch (Exception e) {
            if (playStoreExists != null) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + primeVideoPackage)));
            }
        }
    }

    public void openFilmin() {
        String baseUrl = "http://www.filmin.cat/";
        String filminPackage = "com.filmin.filmin";
        String playStorePackage = "com.android.vending";

        // Creating intents to check whether the app exists or not in the device
        Intent playStoreExists = getContext().getPackageManager().getLaunchIntentForPackage(playStorePackage);
        try {
            // Open Prime Video
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl));
            startActivity(intent);
        } catch (Exception e) {
            if (playStoreExists != null) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + filminPackage)));
            }
        }
    }
}