package com.example.malkoti.popularmovies.network;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.malkoti.popularmovies.BuildConfig;
import com.example.malkoti.popularmovies.model.MovieResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String API_KEY = BuildConfig.apiKey;

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getPopularMovies() {
        /*
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getPopularMovies(API_KEY);
        loadMoviesIntoList(call, movies);
        return movies;
        */
        Call<MovieResult> call = ApiClient.getApiInterface().getPopularMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getTopRatedMovies() {
        /*
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getTopRatedMovies(API_KEY);
        loadMoviesIntoList(call, movies);
        return movies;
        */
        Call<MovieResult> call = ApiClient.getApiInterface().getTopRatedMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getUpcomingMovies() {
        /*
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getUpcomingMovies(API_KEY);
        loadMoviesIntoList(call, movies);
        return movies;
        */
        Call<MovieResult> call = ApiClient.getApiInterface().getUpcomingMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getNowPlayingMovies() {
        /*
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getNowPlayingMovies(API_KEY);
        loadMoviesIntoList(call, movies);
        return movies;
        */
        Call<MovieResult> call = ApiClient.getApiInterface().getNowPlayingMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @param keywords
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getSearchResults(String keywords) {
        /*
        List<MovieResult.Movie> movies = new ArrayList<>();
        Call<MovieResult> call = ApiClient.getApiInterface().getMovieSearchResults(API_KEY, keywords);
        loadMoviesIntoList(call, movies);
        return movies;
        */
        Call<MovieResult> call = ApiClient.getApiInterface().getMovieSearchResults(API_KEY, keywords);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @param call
     * @param movies
     */
    private static void loadMoviesIntoList(Call<MovieResult> call, final List<MovieResult.Movie> movies) {
        Log.d(LOG_TAG, "Performing network call");

        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult MovieResult = response.body();
                //movies.clear();
                movies.addAll(MovieResult.moviesList);
                Log.d(LOG_TAG, "Added list to movies");
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Log.e(LOG_TAG, "Error getting list of movies from internet "
                        + ". \nERROR : " + t.getMessage());
            }
        });
    }

    /**
     * Custom LiveData and Callback class to handle network data loading
     */
    static class RetrofitLiveData  extends LiveData<List<MovieResult.Movie>> implements Callback<MovieResult> {
        private final String LOG_TAG = RetrofitLiveData.class.getSimpleName();
        private Call<MovieResult> call;

        RetrofitLiveData(Call<MovieResult> call) {
            this.call = call;
        }

        /* Override LiveData method */
        @Override
        protected void onActive() {
            if(!this.call.isCanceled() && !this.call.isExecuted()) {
                this.call.enqueue(this);
            }
        }


        /* Implement Callback methods */
        @Override
        public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
            MovieResult result = response.body();
            setValue(result.moviesList);
        }

        @Override
        public void onFailure(Call<MovieResult> call, Throwable t) {
            Log.e(LOG_TAG, "Error getting network data. "
                    + "URL: " + call.request().url().toString() + " "
                    + "ERROR: " + t.getMessage());
        }
    }
}
