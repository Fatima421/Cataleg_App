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

        // Elements from the view
        TextView imagesRightsText = view.findViewById(R.id.imageRightsText);
        imagesRightsText.setText(getString(R.string.aboutImagesRightsTextPart1)+System.lineSeparator()+getString(R.string.aboutImageRightsTextPart2));
        ConstraintLayout imagesRightsConstraint = view.findViewById(R.id.aboutImageRightsConstraint);
        CardView imagesRightCardView = view.findViewById(R.id.imagesRightsCardView);
        TextView aboutUsText = view.findViewById(R.id.aboutUsText);
        ConstraintLayout aboutUsConstraint = view.findViewById(R.id.aboutUsConstraintLayout);
        CardView aboutUsCardView = view.findViewById(R.id.aboutUsCardView);
        TextView aboutAppText = view.findViewById(R.id.aboutAppText);
        ConstraintLayout aboutAppConstraint = view.findViewById(R.id.aboutAppConstraint);
        CardView aboutAppCardView = view.findViewById(R.id.aboutAppCardView);

        // Transition of the constraint to show text
        imagesRightsConstraint.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        imagesRightCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (imagesRightsText.getVisibility() == View.GONE) ? View.VISIBLE: View.GONE;
                TransitionManager.beginDelayedTransition(imagesRightsConstraint, new AutoTransition());
                imagesRightsText.setVisibility(v);
            }
        });

        // Transition of the constraint to show text
        aboutUsConstraint.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        aboutUsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (aboutUsText.getVisibility() == View.GONE) ? View.VISIBLE: View.GONE;
                TransitionManager.beginDelayedTransition(aboutUsConstraint, new AutoTransition());
                aboutUsText.setVisibility(v);
            }
        });

        // Transition of the constraint to show text
        aboutAppConstraint.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        aboutAppCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = (aboutAppText.getVisibility() == View.GONE) ? View.VISIBLE: View.GONE;
                TransitionManager.beginDelayedTransition(aboutAppConstraint, new AutoTransition());
                aboutAppText.setVisibility(v);
            }
        });

        return view;
    }
}