package com.catrenat.wapps.Information;

import android.animation.LayoutTransition;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.catrenat.wapps.R;

public class InformationFragment extends Fragment {
    // Properties
    TextView imagesRightsText;
    ConstraintLayout imagesRightsConstraint;
    CardView imagesRightCardView;

    public InformationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        imagesRightsText = view.findViewById(R.id.imageRightsText);
        imagesRightsConstraint = view.findViewById(R.id.aboutImageRightsConstraint);
        imagesRightCardView = view.findViewById(R.id.imagesRightsCardView);
        imagesRightsConstraint.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        imagesRightCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (imagesRightsText.getVisibility() == View.GONE) ? View.VISIBLE: View.GONE;
                TransitionManager.beginDelayedTransition(imagesRightsConstraint, new AutoTransition());
                imagesRightsText.setVisibility(v);
            }
        });
        return view;
    }
}