package com.catrenat.wapps.Movies.RecyclerView.Series;

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

import com.catrenat.wapps.Models.SerieCategories;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.R;

import java.util.ArrayList;
import java.util.List;

public class AllSeriesRecyclerViewAdapter extends RecyclerView.Adapter<AllSeriesRecyclerViewAdapter.AllSeriesViewHolder> {
    private List<SerieCategories> seriesCategories;
    private Context context;
    private String selectedPlatform;
    private SeriesRecyclerViewAdapter seriesRecyclerViewAdapter;
    private ArrayList<ArrayList<Serie>> all_series;

    public AllSeriesRecyclerViewAdapter(List<SerieCategories> seriesCategories, Context context, String selectedPlatform) {
        this.context = context;
        this.seriesCategories = seriesCategories;
        this.selectedPlatform = selectedPlatform;
        all_series = new ArrayList<>();
        for(int i = 0; i < seriesCategories.size(); i++) {
            all_series.add(new ArrayList<>());
            all_series.get(i).addAll(seriesCategories.get(i).getSeries());
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

        // Hides and shows category recycler view and title if is empty or contains items
        if(seriesCategories.get(position).getSeries().size()==0) {
            holder.categoryTitle.setVisibility(View.GONE);
            holder.seriesRecyclerView.setVisibility(View.GONE);
            holder.contentRecyclerViewConstraintLayout.setVisibility(View.GONE);
        } else {
            holder.categoryTitle.setVisibility(View.VISIBLE);
            holder.seriesRecyclerView.setVisibility(View.VISIBLE);
            holder.contentRecyclerViewConstraintLayout.setVisibility(View.VISIBLE);
        }
        // Set the movie category title
        holder.categoryTitle.setText(seriesCategories.get(position).getTitle());
        setSeriesRecycler(holder.seriesRecyclerView, seriesCategories.get(position).getSeries());
    }

    // SearchBar filter
    public void filter(String string){
        for(int i = 0; i < seriesCategories.size(); i++) {
            String search = string.toLowerCase();
            if(search.length() == 0){
                seriesCategories.get(i).getSeries().clear();
                seriesCategories.get(i).getSeries().addAll(all_series.get(i));
            } else {
                seriesCategories.get(i).getSeries().clear();
                for(Serie serie: all_series.get(i)) {
                    if(serie.getName().toLowerCase().contains(search)) {
                        seriesCategories.get(i).getSeries().add(serie);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return seriesCategories.size();
    }

    public class AllSeriesViewHolder extends RecyclerView.ViewHolder{
        // View items
        TextView categoryTitle;
        RecyclerView seriesRecyclerView;
        ConstraintLayout contentRecyclerViewConstraintLayout;

        public AllSeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.movieCategoryTitle);
            seriesRecyclerView = itemView.findViewById(R.id.contentRecyclerView);
            contentRecyclerViewConstraintLayout = itemView.findViewById(R.id.contentRecyclerViewConstraintLayout);
        }
    }

    // Set the series recycler view
    private void setSeriesRecycler(RecyclerView seriesRecyclerView, List<Serie> series) {
        seriesRecyclerViewAdapter = new SeriesRecyclerViewAdapter(series, context, selectedPlatform);
        seriesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        seriesRecyclerView.setAdapter(seriesRecyclerViewAdapter);

    }
}
