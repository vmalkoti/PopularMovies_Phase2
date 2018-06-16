package com.example.malkoti.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.malkoti.popularmovies.model.MovieResult;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {
    private MoviesRepository moviesRepository;
    private LiveData<List<MovieResult.Movie>> favoriteMovies;


    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        moviesRepository = new MoviesRepository(application);
        favoriteMovies = moviesRepository.getFavoriteMovies();
    }

    public LiveData<List<MovieResult.Movie>> getFavoriteMovies() {
        return favoriteMovies;
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
}
