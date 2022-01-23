package com.catrenat.wapps.Books;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Models.SerieCategories;
import com.catrenat.wapps.Movies.RecyclerView.Series.AllSeriesRecyclerViewAdapter;
import com.catrenat.wapps.R;
import com.catrenat.wapps.Books.RecyclerView.BooksCategoryAdapter;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.BooksCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment {

    private FirebaseFirestore db;

    RecyclerView rcvCategory;
    BooksCategoryAdapter categoryAdapter;


    private ArrayList<Book> booksList = new ArrayList();

    private List<BooksCategory> booksCategoriesList = new ArrayList<>();

    private List<Book> romanceBooks = new ArrayList<>();
    private List<Book> thrillerBooks = new ArrayList<>();
    private List<Book> childsBooks = new ArrayList<>();
    private List<Book> comediaBooks = new ArrayList<>();
    private List<Book> literaturaBooks = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View bookView = inflater.inflate(R.layout.fragment_books, container, false);

        TextView bookIntroText = bookView.findViewById(R.id.bookIntroText);

        // Making the welcome text fade in
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        bookIntroText.setAnimation(animation);

        rcvCategory = bookView.findViewById(R.id.rcv_category);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);

        // Data reading from firestore database
        db = FirebaseFirestore.getInstance();

        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            clearData();
                            // RecyclerView array argument construction
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Book book = document.toObject(Book.class);
                                booksList.add(book);
                            }
                            for (int i = 0; i < booksList.size(); i++) {
                                if (booksList.get(i).getCategory().equals("romance")) {
                                    romanceBooks.add(booksList.get(i));
                                }
                                if (booksList.get(i).getCategory().equals("thriller")) {
                                    thrillerBooks.add(booksList.get(i));
                                }
                                if (booksList.get(i).getCategory().equals("childs")) {
                                    childsBooks.add(booksList.get(i));
                                }
                                if (booksList.get(i).getCategory().equals("comedia")) {
                                    comediaBooks.add(booksList.get(i));
                                }
                                if (booksList.get(i).getCategory().equals("literatura")) {
                                    literaturaBooks.add(booksList.get(i));
                                }
                            }
                            // Initializing the RecyclerView for the movie categories list
                            addCategories();
                            categoryAdapter = new BooksCategoryAdapter(booksCategoriesList, getContext());
                            rcvCategory.setAdapter(categoryAdapter);
                        } else {
                            Log.d("BOOKS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return bookView;
    }

    private void addCategories() {
        if (booksCategoriesList != null){
            booksCategoriesList.clear();
        }
        if (!romanceBooks.isEmpty()) {
            booksCategoriesList.add(new BooksCategory("Romance", romanceBooks));
        }
        if (!thrillerBooks.isEmpty()) {
            booksCategoriesList.add(new BooksCategory("Suspens i misteri", thrillerBooks));
        }
        if (!childsBooks.isEmpty()) {
            booksCategoriesList.add(new BooksCategory("Llibres Infantils", childsBooks));
        }
        if (!comediaBooks.isEmpty()) {
            booksCategoriesList.add(new BooksCategory("Comèdia", comediaBooks));
        }
        if (!literaturaBooks.isEmpty()) {
            booksCategoriesList.add(new BooksCategory("Literatura", literaturaBooks));
        }
    }

    public void clearData() {
        rcvCategory.removeAllViewsInLayout();
        if (booksList != null) {
            booksList.clear();
        }
        if (romanceBooks != null) {
            romanceBooks.clear();
        }
        if (thrillerBooks != null) {
            thrillerBooks.clear();
        }
        if (childsBooks != null) {
            childsBooks.clear();
        }
        if (romanceBooks != null) {
            romanceBooks.clear();
        }
        if (literaturaBooks != null) {
            literaturaBooks.clear();
        }
    }
}