package com.catrenat.wapps.Books.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Books.BooksDetailsFragment;
import com.catrenat.wapps.Games.DetailGameFragment;
import com.catrenat.wapps.Models.Book;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Models.User;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    private final Context context;
    private User user;

    public BookAdapter(List<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Book> list) {
        this.books = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        // Serie in the current position
        Book book = books.get(position);

        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!books.get(position).getImagePath().isEmpty()) {
            storageReference.child(books.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.imgBook);
                }
            });
        }
        holder.imgBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Preparation for fragment transaction
                AppCompatActivity app = (AppCompatActivity) view.getContext();

                // Creating the database instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get user from firebase
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        user = document.toObject(User.class);
                                    }
                                    // Fragment transaction
                                    app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BooksDetailsFragment(book, user), "booksDetailsFragment").addToBackStack(null).commit();
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (books != null){
            return books.size();
        }
        return 0;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.img_book);
        }
    }
}


