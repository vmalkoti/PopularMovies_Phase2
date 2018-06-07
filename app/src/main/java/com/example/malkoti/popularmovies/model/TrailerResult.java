package com.example.malkoti.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResult {

    @SerializedName("id")
    private int movieId;

    @SerializedName("results")
    private List<Trailer> trailersList;

    /* Getters */
    public int getMovieId() {
        return movieId;
    }

    public List<Trailer> getTrailersList() {
        return trailersList;
    }

    /**
     * Class to map movie trailers from API results
     */
    public static class Trailer {

        @SerializedName("key")
        private String key;

        @SerializedName("name")
        private String name;

        @SerializedName("site")
        private String site;

        @SerializedName("size")
        private int size;

        @SerializedName("type")
        private String type;

        /* Getters */
        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getSite() {
            return site;
        }

        public int getSize() {
            return size;
        }

        public String getType() {
            return type;
        }
    }
}


