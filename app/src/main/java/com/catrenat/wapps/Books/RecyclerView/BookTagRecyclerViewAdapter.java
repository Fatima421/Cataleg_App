package com.catrenat.wapps.Books.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catrenat.wapps.R;

import java.util.ArrayList;

public class BookTagRecyclerViewAdapter extends RecyclerView.Adapter<BookTagRecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> genres;

    public BookTagRecyclerViewAdapter(ArrayList<String> genres) {
        this.genres = genres;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_tag, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tagTextView.setText(genres.get(position));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tagTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.itemBookTagTextView);
        }
    }
}
