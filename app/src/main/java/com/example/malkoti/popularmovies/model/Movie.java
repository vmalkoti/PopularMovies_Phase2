package com.example.malkoti.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {

    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("vote_average")
    private float mRating;

    @SerializedName("genere_ids")
    private List<Integer> mGenres;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("backdrop_path")
    private String mBackdropPath;

    @SerializedName("tagline")
    private String mTagline;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("original_language")
    private String mLanguage;

    @SerializedName("runtime")
    private String mRunTime;

    @SerializedName("overview")
    private String mOverview;


    /* Getters */
    public String getTitle() {
        return mTitle;
    }

    public float getRating() {
        return mRating;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public List<Integer> getGenres() {
        return mGenres;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getId() {
        return mId;
    }

    public String getTagline() {
        return mTagline;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getRunTime() {
        return mRunTime;
    }
}
