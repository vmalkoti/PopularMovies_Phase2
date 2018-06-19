package com.example.malkoti.popularmovies;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.network.NetworkUtils;
import com.example.malkoti.popularmovies.persistence.FavoritesDao;
import com.example.malkoti.popularmovies.persistence.FavoritesDatabase;

import java.util.List;


public class MoviesRepository {
    private static final String LOG_TAG = MoviesRepository.class.getSimpleName();

    private FavoritesDao favoritesDao;
    private LiveData<List<MovieResult.Movie>> favoriteMovies;
    private MutableLiveData<List<MovieResult.Movie>> popularMovies = new MutableLiveData<>();
    private MutableLiveData<List<MovieResult.Movie>> topRatedMovies = new MutableLiveData<>();
    private MutableLiveData<List<MovieResult.Movie>> upcomingMovies = new MutableLiveData<>();
    private MutableLiveData<List<MovieResult.Movie>> nowPlayingMovies = new MutableLiveData<>();
    private MutableLiveData<List<MovieResult.Movie>> searchResults = new MutableLiveData<>();

    MoviesRepository(Application application) {
        FavoritesDatabase favoritesDatabase = FavoritesDatabase.getFavoritesDatabase(application);
        favoritesDao = favoritesDatabase.favoritesDao();
    }

    /**
     * Wrapper method to get favorite movies list
     * @return
     */
    LiveData<List<MovieResult.Movie>> getFavoriteMovies() {
        if(favoriteMovies == null) {
            favoriteMovies = favoritesDao.getFavorites();
        }
        return  favoriteMovies;
    }

    /**
     * Get list of popular movies
     * @return
     */
    LiveData<List<MovieResult.Movie>> getPopularMovies() {
        if(popularMovies == null) {
            popularMovies.setValue(NetworkUtils.getPopularMovies());
        }
        return popularMovies;
    }

    /**
     * Get list of top rated movies
     * @return
     */
    LiveData<List<MovieResult.Movie>> getTopRatedMovies() {
        if(topRatedMovies == null) {
            topRatedMovies.setValue(NetworkUtils.getPopularMovies());
        }
        return topRatedMovies;
    }

    /**
     * Get list of upcoming movies
     * @return
     */
    LiveData<List<MovieResult.Movie>> getUpcomingMovies() {
        if(upcomingMovies == null) {
            upcomingMovies.setValue(NetworkUtils.getPopularMovies());
        }
        return upcomingMovies;
    }

    /**
     * Get list of now playing movies
     * @return
     */
    LiveData<List<MovieResult.Movie>> getNowPlayingMovies() {
        if(nowPlayingMovies == null) {
            nowPlayingMovies.setValue(NetworkUtils.getPopularMovies());
        }
        return nowPlayingMovies;
    }

    /**
     * Get list of now playing movies
     * @param keywords
     * @return
     */
    LiveData<List<MovieResult.Movie>> getMoviesSearchResults(String keywords) {
        searchResults.setValue(NetworkUtils.getSearchResults(keywords));
        return searchResults;
    }

    /**
     *
     * @param movie
     * @return
     */
    LiveData<Integer> isFavorite(MovieResult.Movie movie) {
        Log.d(LOG_TAG, "Movie id to query " + movie.getMovieId());
        return favoritesDao.isFavorite(movie.getMovieId());
    }

    /**
     * Wrapper for insert operation
     * @param movie
     */
    void insert(MovieResult.Movie movie) {
        new InsertTask(favoritesDao).execute(movie);
    }

    /**
     *
     * @param movie
     */
    void delete(MovieResult.Movie movie) {
        new DeleteTask(favoritesDao).execute(movie);
    }

    /**
     * AsyncTask class for db insert operation
     */
    private static class InsertTask extends AsyncTask<MovieResult.Movie, Void, Void> {
        private FavoritesDao insertTaskDao;

        InsertTask(FavoritesDao dao) {
            this.insertTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MovieResult.Movie... movies) {
            insertTaskDao.insert(movies[0]);
            return null;
        }
    }

    /**
     * AsyncTask class for db insert operation
     */
    private static class DeleteTask extends AsyncTask<MovieResult.Movie, Void, Void> {
        private FavoritesDao insertTaskDao;

        DeleteTask(FavoritesDao dao) {
            this.insertTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MovieResult.Movie... movies) {
            insertTaskDao.delete(movies[0]);
            return null;
        }
    }
}
