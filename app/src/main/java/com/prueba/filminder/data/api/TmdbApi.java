package com.prueba.filminder.data.api;

import com.prueba.filminder.data.model.Genre.GenreResponse;
import com.prueba.filminder.data.model.Movie;
import com.prueba.filminder.data.model.Movie.MovieResponse;
import com.prueba.filminder.data.model.WatchProvider.ProviderResponse;
import com.prueba.filminder.data.model.WatchProvidersResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {
    @Headers({
        "Accept: application/json"
    })
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
        @Query("language") String language,
        @Query("page") int page
    );

    @Headers({
        "Accept: application/json"
    })
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
        @Query("language") String language,
        @Query("page") int page
    );

    @Headers({
        "Accept: application/json"
    })
    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
        @Query("language") String language,
        @Query("page") int page
    );

    @Headers({
        "Accept: application/json"
    })
    @GET("search/movie")
    Call<MovieResponse> searchMovies(
        @Query("query") String query,
        @Query("language") String language,
        @Query("page") int page
    );

    @Headers({
        "Accept: application/json"
    })
    @GET("movie/{movie_id}/watch/providers")
    Call<WatchProvidersResponse> getWatchProviders(
        @Path("movie_id") int movieId
    );

    @GET("genre/movie/list")
    Call<GenreResponse> getGenres();

    @GET("watch/providers/movie")
    Call<ProviderResponse> getWatchProviders();

    @Headers({
        "Accept: application/json"
    })
    @GET("discover/movie")
    Call<MovieResponse> discoverMovies(
        @Query("language") String language,
        @Query("vote_average.gte") float minRating,
        @Query("with_genres") String genres,
        @Query("with_watch_providers") String providers,
        @Query("watch_region") String region,
        @Query("page") Integer page,
        @Query("sort_by") String sortBy,
        @Query("primary_release_date.gte") String minReleaseDate,
        @Query("primary_release_date.lte") String maxReleaseDate,
        @Query("vote_count.gte") Integer minVoteCount
    );
} 