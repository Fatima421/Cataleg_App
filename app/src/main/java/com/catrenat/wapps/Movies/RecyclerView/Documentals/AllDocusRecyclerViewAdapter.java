package com.catrenat.wapps.Movies.RecyclerView.Documentals;

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

import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.DocusCategories;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.R;

import java.util.ArrayList;
import java.util.List;

public class AllDocusRecyclerViewAdapter extends RecyclerView.Adapter<AllDocusRecyclerViewAdapter.AllDocusViewHolder> {
    private List<DocusCategories> documentalsCategories;
    private Context context;
    private String selectedPlatform;
    private DocusRecyclerViewAdapter docusRecyclerViewAdapter;
    private ArrayList<ArrayList<Documental>> all_docus;

    public AllDocusRecyclerViewAdapter(List<DocusCategories> documentalsCategories, Context context, String selectedPlatform) {
        this.context = context;
        this.documentalsCategories = documentalsCategories;
        this.selectedPlatform = selectedPlatform;
        all_docus = new ArrayList<>();
        for(int i = 0; i < documentalsCategories.size(); i++) {
            all_docus.add(new ArrayList<>());
            all_docus.get(i).addAll(documentalsCategories.get(i).getDocumentals());
        }
    }

    @NonNull
    @Override
    public AllDocusRecyclerViewAdapter.AllDocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_all_categories, parent, false);
        AllDocusRecyclerViewAdapter.AllDocusViewHolder holder = new AllDocusRecyclerViewAdapter.AllDocusViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllDocusRecyclerViewAdapter.AllDocusViewHolder holder, int position) {

        // Hides and shows category recycler view and title if is empty or contains items
        if(documentalsCategories.get(position).getDocumentals().size()==0) {
            holder.categoryTitle.setVisibility(View.GONE);
            holder.docusRecyclerView.setVisibility(View.GONE);
            holder.contentRecyclerViewConstraintLayout.setVisibility(View.GONE);
        } else {
            holder.categoryTitle.setVisibility(View.VISIBLE);
            holder.docusRecyclerView.setVisibility(View.VISIBLE);
            holder.contentRecyclerViewConstraintLayout.setVisibility(View.VISIBLE);
        }
        // Set the movie category title
        holder.categoryTitle.setText(documentalsCategories.get(position).getTitle());
        setDocusRecycler(holder.docusRecyclerView, documentalsCategories.get(position).getDocumentals());
    }

    // SearchBar filter
    public void filter(String string){
        for(int i = 0; i < documentalsCategories.size(); i++) {
            String search = string.toLowerCase();
            if(search.length() == 0){
                documentalsCategories.get(i).getDocumentals().clear();
                documentalsCategories.get(i).getDocumentals().addAll(all_docus.get(i));
            } else {
                documentalsCategories.get(i).getDocumentals().clear();
                for(Documental documental: all_docus.get(i)) {
                    if(documental.getName().toLowerCase().contains(search)) {
                        documentalsCategories.get(i).getDocumentals().add(documental);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return documentalsCategories.size();
    }

    public class AllDocusViewHolder extends RecyclerView.ViewHolder{
        // View items
        TextView categoryTitle;
        RecyclerView docusRecyclerView;
        ConstraintLayout contentRecyclerViewConstraintLayout;

        public AllDocusViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.movieCategoryTitle);
            docusRecyclerView = itemView.findViewById(R.id.contentRecyclerView);
            contentRecyclerViewConstraintLayout = itemView.findViewById(R.id.contentRecyclerViewConstraintLayout);
        }
    }

    // Set the Documental recycler view
    private void setDocusRecycler(RecyclerView docusRecyclerView, List<Documental> documentals) {
        docusRecyclerViewAdapter = new DocusRecyclerViewAdapter(documentals, context, selectedPlatform);
        docusRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        docusRecyclerView.setAdapter(docusRecyclerViewAdapter);
    }
}
