package com.catrenat.wapps.Books;

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

import com.catrenat.wapps.R;
import com.catrenat.wapps.Books.RecyclerView.BooksCategoryAdapter;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.BooksCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SearchView searchView;
    private MotionLayout booksMotionLayout;
    BottomNavigationView bottomNav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View bookView = inflater.inflate(R.layout.fragment_books_list, container, false);

        // Properties
        TextView bookIntroText = bookView.findViewById(R.id.bookIntroText);
        TextView bookSubtitleText = bookView.findViewById(R.id.booksSubtitle);
        searchView = bookView.findViewById(R.id.booksSearchBar);
        booksMotionLayout = bookView.findViewById(R.id.booksMotionLayout);
        bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.VISIBLE);

        // Making the welcome text fade in
        Animation animationTitle = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        bookIntroText.setAnimation(animationTitle);

        Animation animationSubtitle = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        bookSubtitleText.setAnimation(animationSubtitle);


        rcvCategory = bookView.findViewById(R.id.booksCategoriesRecyclerView);
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
                                book.setUrl(document.getString("url"));
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

                            // Filters on search click and resets when no string or cancelled
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    categoryAdapter.filter(query);
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String query) {
                                    if(query.isEmpty()) {
                                        categoryAdapter.filter(query);
                                    }
                                    return false;
                                }});

                        } else {
                            Log.d("BOOKS", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // Calls animation on motionLayout on searchBar icon click
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                booksMotionLayout.transitionToEnd();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                booksMotionLayout.transitionToStart();
                return false;
            }
        });

        return bookView;
    }

    private void addCategories() {
        if (booksCategoriesList != null){
            booksCategoriesList.clear();
        }
        if (getContext()!=null) {
            if (!romanceBooks.isEmpty()) {
                booksCategoriesList.add(new BooksCategory(getActivity().getString(R.string.romance), romanceBooks));
            }
            if (!thrillerBooks.isEmpty()) {
                booksCategoriesList.add(new BooksCategory(getActivity().getString(R.string.thriller), thrillerBooks));
            }
            if (!childsBooks.isEmpty()) {
                booksCategoriesList.add(new BooksCategory(getActivity().getString(R.string.childBooks), childsBooks));
            }
            if (!comediaBooks.isEmpty()) {
                booksCategoriesList.add(new BooksCategory(getActivity().getString(R.string.comedy), comediaBooks));
            }
            if (!literaturaBooks.isEmpty()) {
                booksCategoriesList.add(new BooksCategory(getActivity().getString(R.string.literature), literaturaBooks));
            }
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
        if (comediaBooks != null) {
            comediaBooks.clear();
        }
        if (literaturaBooks != null) {
            literaturaBooks.clear();
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

    private void addDataBookFirebase() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("author", "");
        user.put("category", "");
        user.put("description", "");
        user.put("genres", Arrays.asList(""));
        user.put("imagePath", "/booksImages/comedia/");
        user.put("title", "");
        user.put("url", "");


        // Add a new document with a generated ID
        db = FirebaseFirestore.getInstance();
        db.collection("Books")
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
}