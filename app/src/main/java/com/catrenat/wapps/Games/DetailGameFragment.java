package com.catrenat.wapps.Games;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Games.RecyclerView.GameGalleryRecyclerViewAdapter;
import com.catrenat.wapps.Games.RecyclerView.PlatformLogoRecyclerViewAdapter;
import com.catrenat.wapps.Games.RecyclerView.SelectListener;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.Music.MusicFragment;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DetailGameFragment extends Fragment implements SelectListener {
    private Game game;
    private TextView gameTitle, gameReleaseDate, gameDeveloper, gameDescription, gameEditor, translateString;
    private RecyclerView gameGalleryRecyclerView, gamePlatformRecyclerView;
    private ImageView gameMainImage, translateImage, gameFavImage;
    private View shareView, moreView, translateView;
    private YouTubePlayerView youTubePlayerView;
    private FirebaseFirestore db;
    boolean heartPressed = false;
    private User user;
    BottomNavigationView bottomNav;

    public DetailGameFragment(Game game, User user) {
        this.game = game;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_games_detail, container, false);

        // Properties
        gameTitle = root.findViewById(R.id.gameTitle);
        gameReleaseDate = root.findViewById(R.id.gameReleaseDate);
        gameDescription = root.findViewById(R.id.gameDescription);
        gameDeveloper = root.findViewById(R.id.gameDeveloper);
        gameEditor = root.findViewById(R.id.gameEditor);
        gameMainImage = root.findViewById(R.id.gameMainImage);
        gameGalleryRecyclerView = root.findViewById(R.id.galleryRecyclerView);
        gamePlatformRecyclerView = root.findViewById(R.id.platformLogoRecyclerView);
        shareView = root.findViewById(R.id.shareView);
        moreView = root.findViewById(R.id.moreView);
        translateView = root.findViewById(R.id.translateView);
        translateImage = root.findViewById(R.id.translateImage);
        translateString = root.findViewById(R.id.translateString);
        gameFavImage = root.findViewById(R.id.gameFavImage);
        youTubePlayerView = root.findViewById(R.id.gameYoutubePlayer);
        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);
        CardView onBack = root.findViewById(R.id.onBackGames);


        // OnBack button goes back to music list
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets data from bundle
                Bundle bundle = getArguments();
                GamesListFragment gamesListFragment = new GamesListFragment();
                gamesListFragment.setArguments(bundle);

                AppCompatActivity app = (AppCompatActivity) view.getContext();
                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,  gamesListFragment).addToBackStack(null).commit();
            }
        });

        // Set game details
        gameTitle.setText(game.getName());
        gameReleaseDate.setText(game.getReleaseDate());
        gameDescription.setText(game.getGameDescription());
        gameDeveloper.setText(game.getDeveloper());
        gameEditor.setText(game.getEditor());

        // Thumbnail gallery RecyclerView
        GameGalleryRecyclerViewAdapter gameGalleryAdapter = new GameGalleryRecyclerViewAdapter(game.getGalleryPaths(), getContext(), DetailGameFragment.this);
        gameGalleryRecyclerView.setAdapter(gameGalleryAdapter);
        gameGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // MainImage from thumbnail gallery onClick setter with Glide
        if (game != null) {
            if (!game.getGalleryPaths().get(0).isEmpty()){
                FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com")
                        .getReference()
                        .child(game.getGalleryPaths().get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(getContext())
                                .load(uri.toString())
                                .into(gameMainImage);
                    }
                });
            }
        }

        // Platform logo available RecyclerView
        PlatformLogoRecyclerViewAdapter platformLogoAdapter = new PlatformLogoRecyclerViewAdapter(game.getPlatforms());
        gamePlatformRecyclerView.setAdapter(platformLogoAdapter);
        gamePlatformRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Favorites button
        gameFavImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = (!heartPressed) ? R.drawable.ic_music_filled_heart : R.drawable.ic_music_heart;
                heartPressed = current != R.drawable.ic_music_heart;
                gameFavImage.setImageResource(current);
                if (game != null) {
                    if (heartPressed) {
                        addFavToFirebase(game.getName());
                    } else {
                        deleteFavFromFirebase(game.getName());
                    }
                }
            }
        });

        // Load favourite image
        if (user != null) {
            if (user.getGames() != null) {
                for (int i = 0; i < user.getGames().size(); i++) {
                    if (user.getGames().get(i).equals(game.getName())) {
                        gameFavImage.setImageResource(R.drawable.ic_music_filled_heart);
                    }
                }
            }
        }

        // Share button
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, game.getMoreURL());
                sendIntent.setType("text/plain");
                getContext().startActivity(sendIntent);
            }
        });

        // More button
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(game.getMoreURL()));
                startActivity(browserIntent);
            }
        });

        // Translate button
        if(!game.getTranslateURL().isEmpty()) {
            translateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(game.getTranslateURL()));
                    startActivity(browserIntent);
                }
            });
        } else {
            translateImage.setVisibility(View.GONE);
            translateString.setVisibility(View.GONE);
        }

        // Youtube video player
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = game.getYoutubeURL();
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        // Youtube UI listener
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // Using pre-made custom ui
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youTubePlayerView, youTubePlayer);
                defaultPlayerUiController.showFullscreenButton(false);
                youTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
            }
        };
        // Disable IFrame UI
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        youTubePlayerView.initialize(listener, options);

        return root;
    }

    // onItemClicked SelectListener interface
    @Override
    public void onItemClicked(Uri uri, int position) {
        // When thumbnail clicked shows in main image or player and hides current.
        if(position == 0) {
            youTubePlayerView.setVisibility(View.VISIBLE);
            gameMainImage.setVisibility(View.INVISIBLE);
        } else {
            youTubePlayerView.setVisibility(View.INVISIBLE);
            gameMainImage.setVisibility(View.VISIBLE);
            // Load image with glide
            Glide.with(getContext())
                    .load(uri.toString())
                    .into(gameMainImage);
            youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                @Override
                public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.pause();
                }
            });
        }
    }

    private void addGameOnFirebase() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("developer", "");
        user.put("editor", "");
        user.put("galleryPaths", Arrays.asList(""));
        user.put("gameDescription", "");
        user.put("imagePath", "/gamesImages/gameCover/");
        user.put("moreURL", "");
        user.put("name", "");
        user.put("platforms", Arrays.asList(""));
        user.put("releaseDate", "");
        user.put("translateURL", "");
        user.put("youtubeURL", "");

        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Games")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    public void addFavToFirebase(String gameName) {
        // Create a new user with a first and last name
        String document = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(document)
                .update("games", FieldValue.arrayUnion(gameName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = document.toObject(User.class);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void deleteFavFromFirebase(String gameName) {
        // Create a new user with a first and last name
        String document = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(document)
                .update("games", FieldValue.arrayRemove(gameName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = document.toObject(User.class);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}