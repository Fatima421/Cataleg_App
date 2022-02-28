package com.catrenat.wapps.Models;

import android.graphics.Movie;

import java.util.ArrayList;

public class User {
    private String username;
    private String bio;
    private String email;
    private String password;
    private String imagePath;
    private ArrayList<String> musics;
    private ArrayList<String> movies;
    private ArrayList<String> games;
    private ArrayList<String> books;

    // Default constructor
    public User() {}

    public User(String username, String bio, String email, String password, String imagePath) {
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
    }

    public User(String username, String bio, String email, String password) {
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.password = password;
    }

    public User(String username, String bio, String email, String password, String imagePath, ArrayList<String> musics, ArrayList<String> movies, ArrayList<String> games, ArrayList<String> books) {
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.musics = musics;
        this.movies = movies;
        this.games = games;
        this.books = books;
    }

    // Getters and Setters
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<String> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<String> musics) {
        this.musics = musics;
    }

    public ArrayList<String> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<String> movies) {
        this.movies = movies;
    }

    public ArrayList<String> getGames() {
        return games;
    }

    public void setGames(ArrayList<String> games) {
        this.games = games;
    }

    public ArrayList<String> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<String> books) {
        this.books = books;
    }

}
