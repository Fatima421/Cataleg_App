package com.catrenat.wapps.Favourites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.catrenat.wapps.Favourites.GameFav.GameFavFragment;
import com.catrenat.wapps.Favourites.MovieFav.MovieFavFragment;
import com.catrenat.wapps.Favourites.MusicFav.MusicFavFragment;
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Game;
import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GeneralFavFragment extends Fragment {
    // Properties
    private User user;
    private ArrayList<Music> musicArray = new ArrayList<>();
    private ArrayList<Serie> seriesList = new ArrayList();
    private ArrayList<Pelis> pelisList = new ArrayList();
    private ArrayList<Documental> documentalList = new ArrayList();
    private ArrayList<Game> gamesList = new ArrayList<>();
    FirebaseFirestore db;

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
                // Creating the database instance
                db = FirebaseFirestore.getInstance();

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
                                    // Get data from firebase
                                    db.collection("Music")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                                            Music music = document.toObject(Music.class);
                                                            musicArray.add(music);
                                                        }
                                                        // Fragment transaction
                                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MusicFavFragment(user, musicArray)).addToBackStack(null).commit();
                                                    } else {
                                                        Log.w("TAG", "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        // When movies fav square clicked
        movieFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating the database instance
                db = FirebaseFirestore.getInstance();

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
                                    // Get data from firebase
                                    db.collection("Serie")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                                            Serie serie = document.toObject(Serie.class);
                                                            seriesList.add(serie);
                                                        }

                                                        // Get data from firebase
                                                        db.collection("Peli")
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                Log.d("TAG", document.getId() + " => " + document.getData());
                                                                                Pelis peli = document.toObject(Pelis.class);
                                                                                pelisList.add(peli);
                                                                            }

                                                                            // Get data from firebase
                                                                            db.collection("Documental")
                                                                                    .get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                                                                                    Documental documental = document.toObject(Documental.class);
                                                                                                    documentalList.add(documental);
                                                                                                }
                                                                                                // Fragment transaction
                                                                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MovieFavFragment(user, seriesList, pelisList, documentalList)).addToBackStack(null).commit();
                                                                                            } else {
                                                                                                Log.w("TAG", "Error getting documents.", task.getException());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            Log.w("TAG", "Error getting documents.", task.getException());
                                                                        } else {
                                                                            Log.w("TAG", "Error getting documents.", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                        Log.w("TAG", "Error getting documents.", task.getException());
                                                    } else {
                                                        Log.w("TAG", "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        // When games fav square clicked
        gameFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating the database instance
                db = FirebaseFirestore.getInstance();

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
                                    // Get data from firebase
                                    db.collection("Games")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                                            Game game = document.toObject(Game.class);
                                                            gamesList.add(game);
                                                        }
                                                        // Fragment transaction
                                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GameFavFragment(user, gamesList)).addToBackStack(null).commit();
                                                    } else {
                                                        Log.w("TAG", "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
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