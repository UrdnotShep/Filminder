package com.prueba.filminder.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prueba.filminder.data.model.Movie;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteMoviesManager {
    private static final String PREFS_NAME = "FavoriteMovies";
    private static final String KEY_FAVORITES = "favorites";
    private final SharedPreferences preferences;
    private final Gson gson;

    public FavoriteMoviesManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addFavorite(Movie movie) {
        List<Movie> favorites = getFavorites();
        if (!isFavorite(movie)) {
            favorites.add(movie);
            saveFavorites(favorites);
        }
    }

    public void removeFavorite(Movie movie) {
        List<Movie> favorites = getFavorites();
        favorites.removeIf(m -> m.getId() == movie.getId());
        saveFavorites(favorites);
    }

    public boolean isFavorite(Movie movie) {
        List<Movie> favorites = getFavorites();
        return favorites.stream().anyMatch(m -> m.getId() == movie.getId());
    }

    public List<Movie> getFavorites() {
        String json = preferences.getString(KEY_FAVORITES, "[]");
        Type type = new TypeToken<ArrayList<Movie>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveFavorites(List<Movie> favorites) {
        String json = gson.toJson(favorites);
        preferences.edit().putString(KEY_FAVORITES, json).apply();
    }
} 