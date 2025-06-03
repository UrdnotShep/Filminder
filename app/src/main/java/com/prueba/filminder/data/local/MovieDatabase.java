package com.prueba.filminder.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.prueba.filminder.data.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class MovieDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "movie_db";
    private static MovieDatabase instance;

    public abstract MovieDao movieDao();

    public static synchronized MovieDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                MovieDatabase.class,
                DATABASE_NAME
            ).build();
        }
        return instance;
    }
} 