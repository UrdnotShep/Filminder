package com.prueba.filminder.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Genre {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    private static final Map<String, Integer> genreMap = new HashMap<>();
    private static final Map<Integer, String> genreNameMap = new HashMap<>();

    static {
        genreMap.put("Acción", 28);
        genreMap.put("Aventura", 12);
        genreMap.put("Animación", 16);
        genreMap.put("Comedia", 35);
        genreMap.put("Crimen", 80);
        genreMap.put("Documental", 99);
        genreMap.put("Drama", 18);
        genreMap.put("Familia", 10751);
        genreMap.put("Fantasía", 14);
        genreMap.put("Historia", 36);
        genreMap.put("Terror", 27);
        genreMap.put("Música", 10402);
        genreMap.put("Misterio", 9648);
        genreMap.put("Romance", 10749);
        genreMap.put("Ciencia ficción", 878);
        genreMap.put("Película de TV", 10770);
        genreMap.put("Suspense", 53);
        genreMap.put("Bélica", 10752);
        genreMap.put("Western", 37);

        for (Map.Entry<String, Integer> entry : genreMap.entrySet()) {
            genreNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static int getGenreId(String name) {
        return genreMap.getOrDefault(name, 0);
    }

    public static String getGenreName(int id) {
        return genreNameMap.getOrDefault(id, "Desconocido");
    }

    public static class GenreResponse {
        @SerializedName("genres")
        private List<Genre> genres;

        public List<Genre> getGenres() {
            return genres;
        }
    }
} 