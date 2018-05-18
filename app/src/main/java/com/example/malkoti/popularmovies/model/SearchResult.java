package com.example.malkoti.popularmovies.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SearchResult {

    @SerializedName("page")
    public int pageNum;

    @SerializedName("results")
    public List<Movie> moviesList;
}
