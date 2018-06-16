package com.example.malkoti.popularmovies.network;

import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.model.ReviewResult;
import com.example.malkoti.popularmovies.model.TrailerResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiRetrofitInterface {
    String BASE_URL = "https://api.themoviedb.org/3/";
    String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
    String IMG_POSTER_SIZE = "w185";
    String IMG_BKDROP_SIZE = "w780";

    @GET("movie/popular")
    Call<MovieResult> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieResult> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MovieResult> getUpcomingMovies(@Query("api_key") String api_key);

    @GET("movie/now_playing")
    Call<MovieResult> getNowPlayingMovies(@Query("api_key") String api_key);

    @GET("search/movie")
    Call<MovieResult> getMovieResults(@Query("api_key") String api_key, @Query("query") String keywords);

    @GET("movie/{movieId}")
    Call<MovieResult.Movie> getMovieDetails(@Path("movieId") int movieId, @Query("api_key") String api_key);

    @GET("movie/{movieId}/videos")
    Call<TrailerResult> getMovieTrailers(@Path("movieId") int movieId, @Query("api_key") String api_key);

    @GET("movie/{movieId}/reviews")
    Call<ReviewResult> getMovieReviews(@Path("movieId") int movieId, @Query("api_key") String api_key);
}
