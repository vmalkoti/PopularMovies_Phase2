package com.example.malkoti.popularmovies.utils;

import com.example.malkoti.popularmovies.model.Movie;
import com.example.malkoti.popularmovies.model.SearchResult;

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
    Call<SearchResult> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<SearchResult> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<SearchResult> getUpcomingMovies(@Query("api_key") String api_key);

    @GET("movie/now_playing")
    Call<SearchResult> getNowPlayingMovies(@Query("api_key") String api_key);

    @GET("search/movie")
    Call<SearchResult> getSearchResults(@Query("api_key") String api_key, @Query("query") String keywords);

    @GET("movie/{movieId}")
    Call<Movie> getMovieDetails(@Path("movieId") int movieId, @Query("api_key") String api_key);
}
