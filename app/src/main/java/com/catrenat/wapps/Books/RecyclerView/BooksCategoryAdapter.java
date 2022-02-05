package com.catrenat.wapps.Books.RecyclerView;

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

import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.BooksCategory;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.R;

import java.util.ArrayList;
import java.util.List;

public class BooksCategoryAdapter extends RecyclerView.Adapter<BooksCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<BooksCategory> booksCategories;
    private ArrayList<ArrayList<Book>> all_books;

    public BooksCategoryAdapter(List<BooksCategory> booksCategoriesList, Context context){
        this.context = context;
        this.booksCategories = booksCategoriesList;
        all_books = new ArrayList<>();
        for(int i = 0; i < booksCategories.size(); i++) {
            all_books.add(new ArrayList<>());
            all_books.get(i).addAll(booksCategories.get(i).getBooks());
            Log.d("ALLSERIES", "THIS: " + all_books.get(i));
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Hides and shows category recycler view and title if is empty or contains items
        if(booksCategories.get(position).getBooks().size()==0) {
            holder.bookNameCategory.setVisibility(View.GONE);
            holder.rcvBookCategory.setVisibility(View.GONE);
            holder.booksCategoryConstraintLayout.setVisibility(View.GONE);
        } else {
            holder.bookNameCategory.setVisibility(View.VISIBLE);
            holder.rcvBookCategory.setVisibility(View.VISIBLE);
            holder.booksCategoryConstraintLayout.setVisibility(View.VISIBLE);
        }
        // Set the movie category title
        holder.bookNameCategory.setText(booksCategories.get(position).getNameCategory());
        setBooksRecycler(holder.rcvBookCategory, booksCategories.get(position).getBooks());
    }

    // SearchBar filter
    public void filter(String string){
        for(int i = 0; i < booksCategories.size(); i++) {
            String search = string.toLowerCase();
            if(search.length() == 0){
                Log.d("CLICK", "series antes: " + booksCategories.get(i).getBooks());
                Log.d("CLICK", "All series: " + all_books.get(i));
                Log.d("CLICK", "All series: " + all_books);

                booksCategories.get(i).getBooks().clear();
                booksCategories.get(i).getBooks().addAll(all_books.get(i));
                Log.d("CLICK", "series despues: " + booksCategories.get(i).getBooks());
            } else {
                booksCategories.get(i).getBooks().clear();
                for(Book book: all_books.get(i)) {
                    if(book.getTitle().toLowerCase().contains(search)) {
                        booksCategories.get(i).getBooks().add(book);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return booksCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView bookNameCategory;
        RecyclerView rcvBookCategory;
        ConstraintLayout booksCategoryConstraintLayout;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            bookNameCategory = itemView.findViewById(R.id.books_name_category);
            rcvBookCategory = itemView.findViewById(R.id.booksRecyclerView);
            booksCategoryConstraintLayout = itemView.findViewById(R.id.booksCategoryConstraintLayout);
        }
    }

    private void setBooksRecycler(RecyclerView booksRecyclerView, List<Book> books) {
        BookAdapter bookAdapter = new BookAdapter(books, context);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        booksRecyclerView.setAdapter(bookAdapter);
    }
}
