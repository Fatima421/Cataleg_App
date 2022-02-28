package com.catrenat.wapps.Favourites.MusicFav;

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

import com.catrenat.wapps.Favourites.MusicFav.MusicFavRecyclerView.MusicFavRecyclerView;
import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;

import java.util.ArrayList;

public class MusicFavFragment extends Fragment {
    // Properties
    private User user;
    private ArrayList<Music> musicArray = new ArrayList<>();
    private ArrayList<Music> favMusicArray = new ArrayList<>();

    public MusicFavFragment() {
        // Required empty public constructor
    }

    public MusicFavFragment(User user, ArrayList<Music> musicArray) {
        this.user = user;
        this.musicArray = musicArray;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_list_fav, container, false);

        // Elements from the view
        TextView emptyMusicTxt = view.findViewById(R.id.emptyMusicFavs);
        emptyMusicTxt.setVisibility(View.INVISIBLE);
        TextView musicFavIntro = view.findViewById(R.id.musicFavIntro);
        musicFavIntro.setVisibility(View.INVISIBLE);

        // Check if user has music in favourites
        if (user != null) {
            if (user.getMusics() != null) {
                if (user.getMusics().isEmpty()) {
                    emptyMusicTxt.setVisibility(View.VISIBLE);
                    musicFavIntro.setVisibility(View.INVISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    emptyMusicTxt.setAnimation(animation);
                } else {
                    emptyMusicTxt.setVisibility(View.INVISIBLE);
                    musicFavIntro.setVisibility(View.VISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    musicFavIntro.setAnimation(animation);
                    for (int i = 0; i < user.getMusics().size(); i++) {
                        for (int j = 0; j < musicArray.size(); j++) {
                            if (user.getMusics().get(i).equals(musicArray.get(j).getSongName())) {
                                favMusicArray.add(musicArray.get(j));
                            }
                        }
                    }
                }
            } else {
                emptyMusicTxt.setVisibility(View.VISIBLE);
                musicFavIntro.setVisibility(View.INVISIBLE);
                // Making the welcome text fade in
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                emptyMusicTxt.setAnimation(animation);
            }
        }

        // Creating Recycler view
        // RecyclerView declared and init with array
        RecyclerView recyclerView = view.findViewById(R.id.musicFavRecyclerView);
        MusicFavRecyclerView adapter = new MusicFavRecyclerView(favMusicArray, getContext());
        recyclerView.setAdapter(adapter);

        // Disables recyclerView nested scroll
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3) {
            @Override
            public boolean canScrollVertically() { return false; }
        });

        return view;
    }
}