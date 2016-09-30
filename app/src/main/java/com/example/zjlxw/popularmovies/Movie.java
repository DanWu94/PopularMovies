package com.example.zjlxw.popularmovies;

/**
 * Created by zjlxw on 2016/9/30.
 */

public class Movie {

    private String title;
    private String imageUrl;

    public Movie(String title, String imageUrl) {
        setTitle(title);
        setImageUrl(imageUrl);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
