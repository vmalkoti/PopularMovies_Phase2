package com.example.malkoti.popularmovies.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.malkoti.popularmovies.model.MovieResult;



@Database(entities = {MovieResult.Movie.class}, version = 1, exportSchema = false)
public abstract class FavoritesDatabase extends RoomDatabase {

    public abstract FavoritesDao favoritesDao();


    private static FavoritesDatabase DB_INSTANCE;


    public static FavoritesDatabase getFavoritesDatabase(final Context context) {
        if(DB_INSTANCE == null) {
            synchronized (FavoritesDatabase.class) {
                if(DB_INSTANCE == null) {
                    DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavoritesDatabase.class, "favorites_db")
                            .build();
                }
            }

        }
        return DB_INSTANCE;
    }

}


