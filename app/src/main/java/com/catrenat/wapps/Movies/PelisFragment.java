package com.catrenat.wapps.Movies;

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
import android.widget.SearchView;
import android.widget.TextView;

import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.PelisCategories;
import com.catrenat.wapps.Movies.RecyclerView.Pelis.AllPelisRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PelisFragment extends Fragment {

    // Properties
    private RecyclerView allPelisRecyclerView;
    private AllPelisRecyclerViewAdapter moviesAdapter;
    private ArrayList<Pelis> pelisList = new ArrayList();
    private List<PelisCategories> pelisCategories = new ArrayList<>();
    private List<Pelis> comedyPelis = new ArrayList<>();
    private List<Pelis> actionPelis = new ArrayList<>();
    private List<Pelis> romancePelis = new ArrayList<>();
    private List<Pelis> dramaPelis = new ArrayList<>();
    private List<Pelis> thrillerPelis = new ArrayList<>();
    private FirebaseFirestore db;
    private String selectedPlatform;
    private SearchView searchView;
    private MotionLayout moviesMotionLayout;

    public PelisFragment() {
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
        View view = inflater.inflate(R.layout.fragment_audiovisuals_movies_list, container, false);
        TextView noMovieTxt = view.findViewById(R.id.noMovieTxt);
        noMovieTxt.setText(getResources().getText(R.string.noMovie));
        noMovieTxt.setVisibility(view.GONE);

        // Properties
        searchView = view.findViewById(R.id.moviesSearchBar);
        moviesMotionLayout = view.findViewById(R.id.moviesMotionLayout);

        // Gets data from bundle
        Bundle bundle = getArguments();
        selectedPlatform = (String) bundle.getSerializable("moviePlatform");

        // Setting up categories recycler view
        allPelisRecyclerView = view.findViewById(R.id.moviesCategoryRecyclerView);
        allPelisRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allPelisRecyclerView.setHasFixedSize(false);
        clearData();

        // Data reading from firestore database
        db = FirebaseFirestore.getInstance();
        db.collection("Peli")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            clearData();
                            // RecyclerView array argument construction
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Pelis pelis = document.toObject(Pelis.class);
                                for (String platform : pelis.getPlatform()) {
                                    if (selectedPlatform.equals(platform)) {
                                        pelisList.add(pelis);
                                    }
                                }
                            }
                            if (pelisList.isEmpty() || pelisList == null) {
                                noMovieTxt.setVisibility(view.VISIBLE);
                                if (getContext()!=null) {
                                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                                    noMovieTxt.setAnimation(animation);
                                }
                            }
                            for (int i = 0; i < pelisList.size(); i++) {
                                if (pelisList.get(i).getCategory().equals("Acció")) {
                                    actionPelis.add(pelisList.get(i));
                                }
                                if (pelisList.get(i).getCategory().equals("Romanç")) {
                                    romancePelis.add(pelisList.get(i));
                                }
                                if (pelisList.get(i).getCategory().equals("Comèdia")) {
                                    comedyPelis.add(pelisList.get(i));
                                }
                                if (pelisList.get(i).getCategory().equals("Drama")) {
                                    dramaPelis.add(pelisList.get(i));
                                }
                                if (pelisList.get(i).getCategory().equals("Suspens")) {
                                    thrillerPelis.add(pelisList.get(i));
                                }
                            }
                            // Initializing the RecyclerView for the movie categories list
                            addCategories();
                            moviesAdapter = new AllPelisRecyclerViewAdapter(pelisCategories, getContext(), selectedPlatform);
                            allPelisRecyclerView.setAdapter(moviesAdapter);

                            // Filters on search click and resets when no string or cancelled
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    moviesAdapter.filter(query);
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String query) {
                                    if(query.isEmpty()) {
                                        moviesAdapter.filter(query);
                                    }
                                    return false;
                            }});
                        } else {
                            Log.d("SERIES", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // Calls animation on motionLayout on searchBar icon click
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moviesMotionLayout.transitionToEnd();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                moviesMotionLayout.transitionToStart();
                return false;
            }
        });

        return view;
    }

    private void addCategories() {
        if (pelisCategories != null){
            pelisCategories.clear();
        }
        if (getContext()!=null) {
            if (!actionPelis.isEmpty()) {
                pelisCategories.add(new PelisCategories(getActivity().getString(R.string.action), actionPelis));
            }
            if (!romancePelis.isEmpty()) {
                pelisCategories.add(new PelisCategories(getActivity().getString(R.string.romance), romancePelis));
            }
            if (!comedyPelis.isEmpty()) {
                pelisCategories.add(new PelisCategories(getActivity().getString(R.string.comedy), comedyPelis));
            }
            if (!dramaPelis.isEmpty()) {
                pelisCategories.add(new PelisCategories(getActivity().getString(R.string.drama), dramaPelis));
            }
            if (!thrillerPelis.isEmpty()) {
                pelisCategories.add(new PelisCategories(getActivity().getString(R.string.thriller), thrillerPelis));
            }
        }
    }

    // To add a serie to firebase programatically
    private void addPeliDataInFirebase() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("category", "Comèdia");
        user.put("genres", Arrays.asList("comèdia"));
        user.put("imagePath", "moviesImages/pelisImages/");
        user.put("name", "");
        user.put("platform", Arrays.asList("", ""));
        user.put("platformUrl", Arrays.asList(""));
        user.put("sagas", "1");
        user.put("sinopsis", "");
        user.put("youtubeUrl", "");


        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Peli")
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

    public void clearData() {
        allPelisRecyclerView.removeAllViewsInLayout();
        if (pelisList != null) {
            pelisList.clear();
        }
        if (comedyPelis != null) {
            comedyPelis.clear();
        }
        if (actionPelis != null) {
            actionPelis.clear();
        }
        if (romancePelis != null) {
            romancePelis.clear();
        }
        if (dramaPelis != null) {
            dramaPelis.clear();
        }
        if (thrillerPelis != null) {
            thrillerPelis.clear();
        }
    }
    // Resets search button to original position
    @Override
    public void onPause() {
        super.onPause();
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        }
    }
    // Resets search button to original position
    @Override
    public void onStop() {
        super.onStop();
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        }
    }
}