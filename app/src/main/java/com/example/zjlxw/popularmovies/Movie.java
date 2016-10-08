package com.example.zjlxw.popularmovies;

import java.io.Serializable;

/**
 * Created by zjlxw on 2016/9/30.
 */

public class Movie implements Serializable{

    private String title;
    private String imageUrl;
    private String vote;

    public Movie(String title, String imageUrl, String vote) {
        setTitle(title);
        setImageUrl(imageUrl);
        setVote(vote);
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

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
