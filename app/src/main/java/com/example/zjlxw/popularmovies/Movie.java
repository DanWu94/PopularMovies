package com.example.zjlxw.popularmovies;

import java.io.Serializable;

/**
 * Created by zjlxw on 2016/9/30.
 */

public class Movie implements Serializable{

    private String title;
    private String imageUrl;
    private String vote;
    private String releaseDate;
    private String overview;

    public Movie(String title, String imageUrl, String vote, String releaseDate, String overview) {
        setTitle(title);
        setImageUrl(imageUrl);
        setVote(vote);
        setReleaseDate(releaseDate);
        setOverview(overview);
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
