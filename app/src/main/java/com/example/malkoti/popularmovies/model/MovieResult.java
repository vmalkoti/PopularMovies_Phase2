package com.example.malkoti.popularmovies.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MovieResult {

    @SerializedName("page")
    public int pageNum;

    @SerializedName("results")
    public List<Movie> moviesList;


    @Entity(tableName="favorites")
    public static class Movie implements Parcelable {

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
        private int runTime;

        @ColumnInfo(name="overview")
        @SerializedName("overview")
        private String overview;

        /* Public constructor for Room */
        public Movie(int movieId, String movieTitle, float movieRating,
                     String posterPath, String backdropPath, String tagline,
                     String releaseDate, String language, int runTime, String overview) {
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.movieRating = movieRating;
            this.posterPath = posterPath;
            this.backdropPath = backdropPath;
            this.tagline = tagline;
            this.releaseDate = releaseDate;
            this.language = language;
            this.runTime = runTime;
            this.overview = overview;
        }

        /*
         * Parcelable members
         */
        public static final Parcelable.Creator<Movie> CREATOR
                = new Parcelable.Creator<Movie>() {

            @Override
            public Movie createFromParcel(Parcel source) {
                return new Movie(source);
            }

            @Override
            public Movie[] newArray(int size) {
                return new Movie[size];
            }
        };

        private Movie(Parcel in) {
            movieId = in.readInt();
            movieTitle = in.readString();
            movieRating = in.readFloat();
            posterPath = in.readString();
            backdropPath = in.readString();
            tagline = in.readString();
            releaseDate = in.readString();
            language = in.readString();
            runTime = in.readInt();
            overview = in.readString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.movieId);
            out.writeString(this.movieTitle);
            out.writeFloat(this.movieRating);
            out.writeString(this.posterPath);
            out.writeString(this.backdropPath);
            out.writeString(this.tagline);
            out.writeString(this.releaseDate);
            out.writeString(this.language);
            out.writeInt(this.runTime);
            out.writeString(this.overview);
        }

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

        public int getRunTime() {
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

        public void setRunTime(int runTime) {
            this.runTime = runTime;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }
    }




}


