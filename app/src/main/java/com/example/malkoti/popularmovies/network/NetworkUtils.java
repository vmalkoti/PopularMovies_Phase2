package com.example.malkoti.popularmovies.network;

import android.util.Log;

import com.example.malkoti.popularmovies.model.MovieResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     *
     * @param apiKey
     * @return
     */
    public static List<MovieResult.Movie> getPopularMovies(String apiKey) {
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getPopularMovies(apiKey);
        loadMoviesIntoList(call, movies);
        return movies;
    }

    /**
     *
     * @param apiKey
     * @return
     */
    public static List<MovieResult.Movie> getTopRatedMovies(String apiKey) {
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getTopRatedMovies(apiKey);
        loadMoviesIntoList(call, movies);
        return movies;
    }

    /**
     *
     * @param apiKey
     * @return
     */
    public static List<MovieResult.Movie> getUpcomingMovies(String apiKey) {
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getUpcomingMovies(apiKey);
        loadMoviesIntoList(call, movies);
        return movies;
    }

    /**
     *
     * @param apiKey
     * @return
     */
    public static List<MovieResult.Movie> getNowPlayingMovies(String apiKey) {
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getNowPlayingMovies(apiKey);
        loadMoviesIntoList(call, movies);
        return movies;
    }

    /**
     *
     * @param call
     * @param movies
     */
    private static void loadMoviesIntoList(Call<MovieResult> call, final List<MovieResult.Movie> movies) {
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult MovieResult = response.body();
                movies.clear();
                movies.addAll(MovieResult.moviesList);
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Log.e(LOG_TAG, "Error getting list of movies from internet "
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }
}
