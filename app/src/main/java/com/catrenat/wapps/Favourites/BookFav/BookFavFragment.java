package com.catrenat.wapps.Favourites.BookFav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.catrenat.wapps.Favourites.BookFav.BookFavRecyclerView.BookFavRecyclerView;
import com.catrenat.wapps.Favourites.MusicFav.MusicFavRecyclerView.MusicFavRecyclerView;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.Music;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;

import java.util.ArrayList;

public class BookFavFragment extends Fragment {
    // Properties
    private User user;
    private ArrayList<Book> booksArray = new ArrayList<>();
    private ArrayList<Book> favBooksArray = new ArrayList<>();

    public BookFavFragment() {
        // Required empty public constructor
    }

    public BookFavFragment(User user, ArrayList<Book> booksArray) {
        this.user = user;
        this.booksArray = booksArray;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_fav, container, false);
        // Elements from the view
        TextView emptyBooksFav = view.findViewById(R.id.emptyBooksFav);
        emptyBooksFav.setVisibility(View.INVISIBLE);
        TextView bookFavIntro = view.findViewById(R.id.booksFavIntro);
        bookFavIntro.setVisibility(View.INVISIBLE);

        // Check if user has music in favourites
        if (user != null) {
            if (user.getBooks() != null) {
                if (user.getBooks().isEmpty()) {
                    emptyBooksFav.setVisibility(View.VISIBLE);
                    bookFavIntro.setVisibility(View.INVISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    emptyBooksFav.setAnimation(animation);
                } else {
                    emptyBooksFav.setVisibility(View.INVISIBLE);
                    bookFavIntro.setVisibility(View.VISIBLE);
                    // Making the welcome text fade in
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    bookFavIntro.setAnimation(animation);
                    for (int i = 0; i < user.getBooks().size(); i++) {
                        for (int j = 0; j < booksArray.size(); j++) {
                            if (user.getBooks().get(i).equals(booksArray.get(j).getTitle())) {
                                favBooksArray.add(booksArray.get(j));
                            }
                        }
                    }
                }
            } else {
                emptyBooksFav.setVisibility(View.VISIBLE);
                bookFavIntro.setVisibility(View.INVISIBLE);
                // Making the welcome text fade in
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                emptyBooksFav.setAnimation(animation);
            }
        }

        // Creating Recycler view
        // RecyclerView declared and init with array
        RecyclerView recyclerView = view.findViewById(R.id.booksFavRecyclerView);
        BookFavRecyclerView adapter = new BookFavRecyclerView(favBooksArray, getContext());
        recyclerView.setAdapter(adapter);

        // Disables recyclerView nested scroll
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3) {
            @Override
            public boolean canScrollVertically() { return false; }
        });

        return view;
    }
}