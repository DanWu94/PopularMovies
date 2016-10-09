package com.example.zjlxw.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zjlxw on 2016/9/30.
 */

public class Movie implements Parcelable{

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

    //Parcelling part
    public Movie(Parcel in) {
        String[] data = new String[5];//remember to modify this when adding new fields
        in.readStringArray(data);
        setTitle(data[0]);
        setImageUrl(data[1]);
        setVote(data[2]);
        setReleaseDate(data[3]);
        setOverview(data[4]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                getTitle(),
                getImageUrl(),
                getVote(),
                getReleaseDate(),
                getOverview()
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
