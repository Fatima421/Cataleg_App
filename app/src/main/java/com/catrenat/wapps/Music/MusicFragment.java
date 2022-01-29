package com.catrenat.wapps.Music;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Music.RecyclerView.MusicRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;


public class MusicFragment extends Fragment {

    private static final String TAG = "music";
    // Properties
    private ArrayList<Music> musicArray = new ArrayList<>();
    private RecyclerView musicRecyclerView;
    private MusicRecyclerViewAdapter adapter;
    YouTubePlayerView youTubePlayerView;

    public MusicFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        // Properties
        MotionLayout motionLayout = view.findViewById(R.id.musicMotionLayout);
        TextView musicIntroText = view.findViewById(R.id.musicIntroText);

        // Making the welcome text fade in
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        musicIntroText.setAnimation(animation);

        // Initializing the RecyclerView for the list
        musicRecyclerView = view.findViewById(R.id.musicRecyclerView);
        // To disable nested scroll
        musicRecyclerView.setLayoutManager(new LinearLayoutManager((getContext())) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            }
        );

        // Creating the database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get data from firebase
        db.collection("Music")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Music music = document.toObject(Music.class);
                                musicArray.add(music);
                            }
                            adapter = new MusicRecyclerViewAdapter(musicRecyclerView, musicArray, getContext(), youTubePlayerView);
                            musicRecyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        // Searcher that lets search items by name
        SearchView searchItem = view.findViewById(R.id.musicSearchBar);
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
                if(newText.isEmpty()) {
                    adapter.filter(newText);
                }
                return false;
            }});

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}