package com.example.malkoti.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 */
public class ReviewResult {
    @SerializedName("id")
    private int movieId;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("results")
    private List<Review> reviews;

    /* Getters */

    public int getMovieId() {
        return movieId;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Class to map to movie review in API response
     */
    public static class Review {
        @SerializedName("author")
        private String author;

        @SerializedName("content")
        private String content;

        /* Getters */

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
    }

}


