package com.catrenat.wapps.Favourites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;

import java.util.ArrayList;

public class MusicFavFragment extends Fragment {
    // Properties
    private User user;
    private ArrayList<Music> musicArray = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_music_fav, container, false);

        // Check if user has music in favourites
        if (user != null) {
            
        }


        // Elements from the view
        TextView emptyMusicTxt = view.findViewById(R.id.emptyMusicFavs);
        emptyMusicTxt.setVisibility(View.INVISIBLE);
        TextView musicFavIntro = view.findViewById(R.id.musicFavIntro);

        // Making the welcome text fade in
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        musicFavIntro.setAnimation(animation);

        return view;
    }
}