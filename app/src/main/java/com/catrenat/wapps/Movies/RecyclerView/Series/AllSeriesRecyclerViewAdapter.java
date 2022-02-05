package com.catrenat.wapps.Movies.RecyclerView.Series;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catrenat.wapps.Models.SerieCategories;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.R;

import java.util.ArrayList;
import java.util.List;

public class AllSeriesRecyclerViewAdapter extends RecyclerView.Adapter<AllSeriesRecyclerViewAdapter.AllSeriesViewHolder> {
    private List<SerieCategories> allCategories;
    private Context context;
    private String selectedPlatform;
    private SeriesRecyclerViewAdapter seriesRecyclerViewAdapter;
    private ArrayList<ArrayList<Serie>> all_series;

    public AllSeriesRecyclerViewAdapter(List<SerieCategories> allCategories, Context context, String selectedPlatform) {
        this.context = context;
        this.allCategories = allCategories;
        this.selectedPlatform = selectedPlatform;
        all_series = new ArrayList<>();
        for(int i = 0; i < allCategories.size(); i++) {
            all_series.add(new ArrayList<>());
            all_series.get(i).addAll(allCategories.get(i).getSeries());
            Log.d("ALLSERIES", "THIS: " + all_series.get(i));
        }
    }

    @NonNull
    @Override
    public AllSeriesRecyclerViewAdapter.AllSeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_all_categories, parent, false);
        AllSeriesRecyclerViewAdapter.AllSeriesViewHolder holder = new AllSeriesRecyclerViewAdapter.AllSeriesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllSeriesRecyclerViewAdapter.AllSeriesViewHolder holder, int position) {
        // Set the movie category title
        if(allCategories.get(position).getSeries().size()==0) {
            holder.categoryTitle.setVisibility(View.GONE);
            holder.seriesRecyclerView.setVisibility(View.GONE);
            holder.seriesRecyclerViewConstraintLayout.setVisibility(View.GONE);
        } else {
            holder.categoryTitle.setVisibility(View.VISIBLE);
            holder.seriesRecyclerView.setVisibility(View.VISIBLE);
            holder.seriesRecyclerViewConstraintLayout.setVisibility(View.VISIBLE);
        }
        holder.categoryTitle.setText(allCategories.get(position).getTitle());
        setSeriesRecycler(holder.seriesRecyclerView, allCategories.get(position).getSeries());
    }

    // SearchBar filter
    public void filter(String string){
        for(int i = 0; i < allCategories.size(); i++) {
            String search = string.toLowerCase();
            if(search.length() == 0){
                Log.d("CLICK", "series antes: " + allCategories.get(i).getSeries());
                Log.d("CLICK", "All series: " + all_series.get(i));
                Log.d("CLICK", "All series: " + all_series);

                allCategories.get(i).getSeries().clear();
                allCategories.get(i).getSeries().addAll(all_series.get(i));
                Log.d("CLICK", "series despues: " + allCategories.get(i).getSeries());
            } else {
                allCategories.get(i).getSeries().clear();
                for(Serie serie: all_series.get(i)) {
                    if(serie.getName().toLowerCase().contains(search)) {
                        allCategories.get(i).getSeries().add(serie);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return allCategories.size();
    }

    public class AllSeriesViewHolder extends RecyclerView.ViewHolder{
        // View items
        TextView categoryTitle;
        RecyclerView seriesRecyclerView;
        ConstraintLayout seriesRecyclerViewConstraintLayout;

        public AllSeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.movieCategoryTitle);
            seriesRecyclerView = itemView.findViewById(R.id.seriesRecyclerView);
            seriesRecyclerViewConstraintLayout = itemView.findViewById(R.id.seriesRecyclerViewConstraintLayout);
        }
    }

    // Set the series recycler view
    private void setSeriesRecycler(RecyclerView seriesRecyclerView, List<Serie> series) {
        seriesRecyclerViewAdapter = new SeriesRecyclerViewAdapter(series, context, selectedPlatform);
        seriesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        seriesRecyclerView.setAdapter(seriesRecyclerViewAdapter);

    }
}
