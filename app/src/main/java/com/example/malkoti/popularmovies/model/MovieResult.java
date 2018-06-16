package com.example.malkoti.popularmovies.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MovieResult {

    @SerializedName("page")
    public int pageNum;

    @SerializedName("results")
    public List<Movie> moviesList;


    @Entity(tableName="favorites")
    public static class Movie {

        @PrimaryKey
        @ColumnInfo(name="movie_id")
        @SerializedName("id")
        private int movieId;

        @ColumnInfo(name="movie_title")
        @SerializedName("title")
        private String movieTitle;

        @ColumnInfo(name="movie_rating")
        @SerializedName("vote_average")
        private float movieRating;

        @ColumnInfo(name="poster_path")
        @SerializedName("poster_path")
        private String posterPath;

        @ColumnInfo(name="backdrop_path")
        @SerializedName("backdrop_path")
        private String backdropPath;

        @ColumnInfo(name="movie_tagline")
        @SerializedName("tagline")
        private String tagline;

        @ColumnInfo(name="release_date")
        @SerializedName("release_date")
        private String releaseDate;

        @ColumnInfo(name="language")
        @SerializedName("original_language")
        private String language;

        @ColumnInfo(name="runtime")
        @SerializedName("runtime")
        private String runTime;

        @ColumnInfo(name="overview")
        @SerializedName("overview")
        private String overview;


        /* Getters */
        public String getMovieTitle() {
            return movieTitle;
        }

        public float getMovieRating() {
            return movieRating;
        }

        public String getPosterPath() {
            return posterPath;
        }

        /*
        public List<Integer> getGenres() {
            return movieGeneres;
        }
        */

        public String getBackdropPath() {
            return backdropPath;
        }

        public String getOverview() {
            return overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public int getMovieId() {
            return movieId;
        }

        public String getTagline() {
            return tagline;
        }

        public String getLanguage() {
            return language;
        }

        public String getRunTime() {
            return runTime;
        }


        /* Setters for room db */

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public void setMovieTitle(String movieTitle) {
            this.movieTitle = movieTitle;
        }

        public void setMovieRating(float movieRating) {
            this.movieRating = movieRating;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        public void setTagline(String tagline) {
            this.tagline = tagline;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setRunTime(String runTime) {
            this.runTime = runTime;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }
    }




}


