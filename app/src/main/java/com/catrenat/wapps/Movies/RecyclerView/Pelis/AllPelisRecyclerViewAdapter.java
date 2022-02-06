package com.catrenat.wapps.Movies.RecyclerView.Pelis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.PelisCategories;
import com.catrenat.wapps.R;

import java.util.ArrayList;
import java.util.List;

public class AllPelisRecyclerViewAdapter extends RecyclerView.Adapter<AllPelisRecyclerViewAdapter.AllPelisViewHolder> {
    private List<PelisCategories> pelisCategories;
    private Context context;
    private String selectedPlatform;
    private ArrayList<ArrayList<Pelis>> all_movies;


    public AllPelisRecyclerViewAdapter(List<PelisCategories> pelisCategories, Context context, String selectedPlatform) {
        this.context = context;
        this.pelisCategories = pelisCategories;
        this.selectedPlatform = selectedPlatform;
        all_movies = new ArrayList<>();
        for(int i = 0; i < pelisCategories.size(); i++) {
            all_movies.add(new ArrayList<>());
            all_movies.get(i).addAll(pelisCategories.get(i).getPelis());
            Log.d("ALLPELIS", "THIS: " + all_movies.get(i));
        }
    }

    @NonNull
    @Override
    public AllPelisRecyclerViewAdapter.AllPelisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_all_categories, parent, false);
        AllPelisRecyclerViewAdapter.AllPelisViewHolder holder = new AllPelisRecyclerViewAdapter.AllPelisViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllPelisRecyclerViewAdapter.AllPelisViewHolder holder, int position) {
        // Hides and shows category recycler view and title if is empty or contains items
        if(pelisCategories.get(position).getPelis().size()==0) {
            holder.categoryTitle.setVisibility(View.GONE);
            holder.pelisRecyclerView.setVisibility(View.GONE);
            holder.contentRecyclerViewConstraintLayout.setVisibility(View.GONE);
        } else {
            holder.categoryTitle.setVisibility(View.VISIBLE);
            holder.pelisRecyclerView.setVisibility(View.VISIBLE);
            holder.contentRecyclerViewConstraintLayout.setVisibility(View.VISIBLE);
        }
        // Set the movie category title
        holder.categoryTitle.setText(pelisCategories.get(position).getTitle());
        setPelisRecycler(holder.pelisRecyclerView, pelisCategories.get(position).getPelis());
    }

    // SearchBar filter
    public void filter(String string){
        for(int i = 0; i < pelisCategories.size(); i++) {
            String search = string.toLowerCase();
            if(search.length() == 0){
                Log.d("CLICK", "Pelis antes: " + pelisCategories.get(i).getPelis());
                Log.d("CLICK", "All Pelis: " + all_movies.get(i));
                Log.d("CLICK", "All Pelis: " + all_movies);

                pelisCategories.get(i).getPelis().clear();
                pelisCategories.get(i).getPelis().addAll(all_movies.get(i));
                Log.d("CLICK", "Pelis despues: " + pelisCategories.get(i).getPelis());
            } else {
                pelisCategories.get(i).getPelis().clear();
                for(Pelis pelis: all_movies.get(i)) {
                    if(pelis.getName().toLowerCase().contains(search)) {
                        pelisCategories.get(i).getPelis().add(pelis);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return pelisCategories.size();
    }

    public class AllPelisViewHolder extends RecyclerView.ViewHolder{
        // View items
        TextView categoryTitle;
        RecyclerView pelisRecyclerView;
        ConstraintLayout contentRecyclerViewConstraintLayout;

        public AllPelisViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.movieCategoryTitle);
            pelisRecyclerView = itemView.findViewById(R.id.contentRecyclerView);
            contentRecyclerViewConstraintLayout = itemView.findViewById(R.id.contentRecyclerViewConstraintLayout);
        }
    }

    // Set the Pelis recycler view
    private void setPelisRecycler(RecyclerView pelisRecyclerView, List<Pelis> pelis) {
        PelisRecyclerViewAdapter pelisRecyclerViewAdapter = new PelisRecyclerViewAdapter(pelis, context, selectedPlatform);
        pelisRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        pelisRecyclerView.setAdapter(pelisRecyclerViewAdapter);
    }
}
