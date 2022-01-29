package com.catrenat.wapps.Models;

import java.util.ArrayList;

public class Book {
    private int resourceId;
    private String title;
    private String description;
    private String imagePath;
    private String category;
    private String author;
    private ArrayList<String> genres;
    private String url;

    public Book (){}

    public Book(int resourceId, String title, String description, String imagePath, String category, String author, ArrayList<String> genres, String url) {
        this.resourceId = resourceId;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.category = category;
        this.author = author;
        this.genres = genres;
        this.url = url;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
