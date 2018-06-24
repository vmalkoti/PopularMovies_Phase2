package com.example.malkoti.popularmovies.data;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.malkoti.popularmovies.model.MovieResult;
import com.example.malkoti.popularmovies.model.ReviewResult;
import com.example.malkoti.popularmovies.model.TrailerResult;

import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {
    public enum MovieListTypes {POPULAR, TOP_RATED, NOW_PLAYING, UPCOMING, FAVORITES, SEARCH, EMPTY}

    private MoviesRepository moviesRepository;

    private MutableLiveData<MovieListTypes> movieType = new MutableLiveData<>();
    private LiveData<List<MovieResult.Movie>> favoriteMovies;
    private String searchKeywords = "";

    private LiveData<List<MovieResult.Movie>> movies = Transformations.switchMap(movieType,
            new Function<MovieListTypes, LiveData<List<MovieResult.Movie>>>() {
        @Override
        public LiveData<List<MovieResult.Movie>> apply(MovieListTypes type) {
            switch (type) {
                case POPULAR:
                    return moviesRepository.getPopularMovies();
                case TOP_RATED:
                    return moviesRepository.getTopRatedMovies();
                case NOW_PLAYING:
                    return moviesRepository.getNowPlayingMovies();
                case UPCOMING:
                    return moviesRepository.getUpcomingMovies();
                case FAVORITES:
                    return moviesRepository.getFavoriteMovies();
                case SEARCH:
                    return moviesRepository.getMoviesSearchResults(searchKeywords);
                case EMPTY:
                    MutableLiveData<List<MovieResult.Movie>> list = new MutableLiveData<>();
                    list.setValue(new ArrayList<MovieResult.Movie>());
                    return list;
                default:
                    return null;

            }
        }
    });


    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        moviesRepository = new MoviesRepository(application);
        favoriteMovies = moviesRepository.getFavoriteMovies();
    }

    public LiveData<List<MovieResult.Movie>> getMovies() {
        return movies;
    }

    public LiveData<TrailerResult> getTrailers(int movieId) {
        return moviesRepository.getMovieTrailerResults(movieId);
    }

    public LiveData<ReviewResult> getReviews(int movieId) {
        return moviesRepository.getMovieReviewResults(movieId);
    }

    public void insertFavorite(MovieResult.Movie movie) {
        moviesRepository.insert(movie);
    }

    public void deleteFavorite(MovieResult.Movie movie) {
        moviesRepository.delete(movie);
    }

    public LiveData<Integer> isFavorite(MovieResult.Movie movie) {
        return moviesRepository.isFavorite(movie);
    }

    public void setMovieType(MovieListTypes type) {
        movieType.setValue(type);
    }

    public void setSearchKeywords(String keywords) {
        searchKeywords = keywords;
    }
}
