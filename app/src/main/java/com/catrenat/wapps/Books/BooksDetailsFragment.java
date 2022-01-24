package com.catrenat.wapps.Books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.R;

public class BooksDetailsFragment extends Fragment {

    Book book = new Book();

    public BooksDetailsFragment() {
        // Required empty public constructor
    }

    public BooksDetailsFragment(Book book) {
        this.book = book;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        return view;
    }
}
