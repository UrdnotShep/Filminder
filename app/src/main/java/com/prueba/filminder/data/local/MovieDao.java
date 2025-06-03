package com.prueba.filminder.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.prueba.filminder.data.model.Movie;
import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :movieId")
    LiveData<Movie> getMovie(int movieId);

    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :movieId)")
    boolean isMovieSaved(int movieId);
} 