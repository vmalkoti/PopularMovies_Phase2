package com.example.malkoti.popularmovies.persistence;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.malkoti.popularmovies.model.MovieResult;

import java.util.List;

@Dao
public interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieResult.Movie movie);

    @Delete
    void delete(MovieResult.Movie movie);

    @Query("SELECT * FROM favorites ORDER BY movie_title ASC")
    LiveData<List<MovieResult.Movie>> getFavorites();

    @Query("SELECT COUNT(movie_id) FROM favorites WHERE movie_id = :movieId")
    LiveData<Integer> isFavorite(int movieId);
}
