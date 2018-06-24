package com.example.malkoti.popularmovies.network;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.malkoti.popularmovies.BuildConfig;
import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.model.ReviewResult;
import com.example.malkoti.popularmovies.model.TrailerResult;

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
        Call<MovieResult> call = ApiClient.getApiInterface().getPopularMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getTopRatedMovies() {
        Call<MovieResult> call = ApiClient.getApiInterface().getTopRatedMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getUpcomingMovies() {
        Call<MovieResult> call = ApiClient.getApiInterface().getUpcomingMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getNowPlayingMovies() {
        Call<MovieResult> call = ApiClient.getApiInterface().getNowPlayingMovies(API_KEY);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @param keywords
     * @return
     */
    public static LiveData<List<MovieResult.Movie>> getSearchResults(String keywords) {
        Call<MovieResult> call = ApiClient.getApiInterface().getMovieSearchResults(API_KEY, keywords);
        return new RetrofitLiveData(call);
    }

    /**
     *
     * @param movieId
     * @return
     */
    public static LiveData<TrailerResult> getTrailers(int movieId) {
        Call<TrailerResult> call = ApiClient.getApiInterface().getMovieTrailers(movieId, API_KEY);
        return new RetrofitLiveDataNew(call);
    }

    /**
     *
     * @param movieId
     * @return
     */
    public static LiveData<ReviewResult> getReviews(int movieId) {
        Call<ReviewResult> call = ApiClient.getApiInterface().getMovieReviews(movieId, API_KEY);
        return new RetrofitLiveDataNew(call);
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

    /**
     * Generic class for all types of network call
     * @param <T>
     */
    static class RetrofitLiveDataNew<T> extends LiveData<T>  implements  Callback<T> {
        private final String LOG_TAG = RetrofitLiveData.class.getSimpleName();
        private Call<T> call;

        public RetrofitLiveDataNew(Call<T> call) {
            this.call = call;
        }

        @Override
        protected void onActive() {
            if(!this.call.isCanceled() && !this.call.isExecuted()) {
                this.call.enqueue(this);
            }
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            T results = response.body();
            setValue(results);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e(LOG_TAG, "Error getting network data. "
                    + "URL: " + call.request().url().toString() + " "
                    + "ERROR: " + t.getMessage());
        }
    }
}
