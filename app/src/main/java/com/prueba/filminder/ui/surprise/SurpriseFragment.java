package com.prueba.filminder.ui.surprise;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.prueba.filminder.R;
import com.prueba.filminder.data.api.TmdbApi;
import com.prueba.filminder.data.api.TmdbClient;
import com.prueba.filminder.data.model.Genre;
import com.prueba.filminder.data.model.Movie;
import com.prueba.filminder.data.model.Movie.MovieResponse;
import com.prueba.filminder.data.model.WatchProvider;
import com.prueba.filminder.data.model.WatchProvidersResponse;
import com.prueba.filminder.ui.MovieDetailFragmentDirections;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurpriseFragment extends Fragment {
    private Slider ratingSlider;
    private ChipGroup genreGroup;
    private ChipGroup providerGroup;
    private MaterialButton surpriseButton;
    private MaterialButton clearFiltersButton;
    private List<Movie> moviePool;
    private Random random;
    private int totalPages = 1;
    private static final int DEFAULT_PAGE = 1;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private int currentRetryAttempt = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_surprise, container, false);
        
        initializeViews(root);
        setupListeners();
        loadGenres();
        loadProviders();
        
        return root;
    }

    private void initializeViews(View root) {
        ratingSlider = root.findViewById(R.id.rating_slider);
        genreGroup = root.findViewById(R.id.genre_group);
        providerGroup = root.findViewById(R.id.provider_group);
        surpriseButton = root.findViewById(R.id.surprise_button);
        clearFiltersButton = root.findViewById(R.id.clear_filters_button);
        
        moviePool = new ArrayList<>();
        random = new Random();
        
        ratingSlider.setValue(7.0f); // Valor por defecto
    }

    private void setupListeners() {
        surpriseButton.setOnClickListener(v -> {
            // Animar el botón
            v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction(this::findRandomMovie)
                        .start();
                })
                .start();
        });
        clearFiltersButton.setOnClickListener(v -> clearFilters());
    }

    private void loadGenres() {
        TmdbApi api = TmdbClient.getInstance().getApi();
        api.getGenres().enqueue(new Callback<Genre.GenreResponse>() {
            @Override
            public void onResponse(Call<Genre.GenreResponse> call, Response<Genre.GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Genre genre : response.body().getGenres()) {
                        addGenreChip(genre);
                    }
                }
            }

            @Override
            public void onFailure(Call<Genre.GenreResponse> call, Throwable t) {
                showError("Error al cargar los géneros");
            }
        });
    }

    private void loadProviders() {
        TmdbApi api = TmdbClient.getInstance().getApi();
        api.getWatchProviders().enqueue(new Callback<WatchProvider.ProviderResponse>() {
            @Override
            public void onResponse(Call<WatchProvider.ProviderResponse> call, Response<WatchProvider.ProviderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (WatchProvider provider : response.body().getResults()) {
                        String providerName = WatchProvider.getProviderName(provider.getProviderId());
                        if (!"Desconocido".equals(providerName)) {
                            addProviderChip(provider);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WatchProvider.ProviderResponse> call, Throwable t) {
                showError("Error al cargar los servicios de streaming");
            }
        });
    }

    private void addGenreChip(Genre genre) {
        Chip chip = new Chip(new ContextThemeWrapper(requireContext(), R.style.CustomChipStyle));
        String genreName = Genre.getGenreName(genre.getId());
        chip.setText(genreName);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(true);
        chip.setTextSize(14);
        chip.setChipCornerRadius(16);
        chip.setEnsureMinTouchTargetSize(true);
        genreGroup.addView(chip);
    }

    private void addProviderChip(WatchProvider provider) {
        Chip chip = new Chip(new ContextThemeWrapper(requireContext(), R.style.CustomChipStyle));
        chip.setText(provider.getProviderName());
        chip.setCheckable(true);
        chip.setCheckedIconVisible(true);
        chip.setTextSize(14);
        chip.setChipCornerRadius(16);
        chip.setEnsureMinTouchTargetSize(true);
        providerGroup.addView(chip);
    }

    private void findRandomMovie() {
        // Primero obtenemos el total de páginas disponibles
        float minRating = ratingSlider.getValue();
        TmdbApi api = TmdbClient.getInstance().getApi();
        api.discoverMovies(
            "es-ES",
            minRating,
            null,
            null,
            "ES",
            DEFAULT_PAGE,
            "popularity.desc",
            null,
            null,
            100
        ).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    totalPages = Math.min(response.body().getTotalPages(), 500); // Limitamos a 500 páginas máximo
                    System.out.println("Debug - Total de páginas disponibles: " + totalPages);
                    // Ahora sí hacemos la búsqueda aleatoria con el número correcto de páginas
                    findRandomMovie(random.nextInt(totalPages) + 1);
                } else {
                    showError("Error al obtener el total de páginas");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showError("Error de conexión");
            }
        });
    }

    private void findRandomMovie(final int page) {
        if (currentRetryAttempt >= MAX_RETRY_ATTEMPTS) {
            showError("No se encontraron películas con los filtros actuales. Intenta con filtros menos restrictivos.");
            currentRetryAttempt = 0;
            return;
        }

        float minRating = ratingSlider.getValue();
        List<Integer> selectedGenres = getSelectedGenreIds();
        List<Integer> selectedProviders = getSelectedProviderIds();

        // Ajustar el vote_count mínimo basado en el rating
        int minVoteCount = minRating > 7.5 ? 50 : 20;

        // Generar un orden aleatorio
        String[] sortOptions = {
            "popularity.desc",
            "vote_average.desc",
            "primary_release_date.desc",
            "revenue.desc"
        };
        String randomSort = sortOptions[random.nextInt(sortOptions.length)];

        // Generar un rango de años aleatorio (entre 1950 y el año actual)
        int currentYear = java.time.Year.now().getValue();
        int minYear = 1950 + random.nextInt(currentYear - 1950);
        String minDate = minYear + "-01-01";
        String maxDate = currentYear + "-12-31";

        TmdbApi api = TmdbClient.getInstance().getApi();
        api.discoverMovies(
            "es-ES",
            minRating,
            null,
            null,
            "ES",
            page,
            randomSort,
            minDate,
            maxDate,
            minVoteCount  // Ajustado dinámicamente
        ).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    moviePool = response.body().getResults();
                    
                    if (moviePool.isEmpty()) {
                        currentRetryAttempt++;
                        findRandomMovie(random.nextInt(totalPages) + 1);
                        return;
                    }

                    // Filtrar por géneros si hay alguno seleccionado
                    List<Movie> filteredMovies = new ArrayList<>(moviePool);
                    if (!selectedGenres.isEmpty()) {
                        filteredMovies = moviePool.stream()
                            .filter(movie -> {
                                int[] movieGenres = movie.getGenreIds();
                                for (int movieGenreId : movieGenres) {
                                    if (selectedGenres.contains(movieGenreId)) {
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .collect(java.util.stream.Collectors.toList());

                        if (filteredMovies.isEmpty()) {
                            currentRetryAttempt++;
                            findRandomMovie(random.nextInt(totalPages) + 1);
                            return;
                        }
                    }

                    // Resetear el contador de intentos si encontramos películas
                    currentRetryAttempt = 0;

                    // Si no hay proveedores seleccionados o si hay películas filtradas por género
                    if (selectedProviders.isEmpty()) {
                        if (!filteredMovies.isEmpty()) {
                            Movie randomMovie = filteredMovies.get(random.nextInt(filteredMovies.size()));
                            navigateToMovieDetail(randomMovie);
                        }
                        return;
                    }

                    // Continuar con el filtrado por proveedores si hay proveedores seleccionados
                    if (!filteredMovies.isEmpty()) {
                        List<Movie> providerFilteredMovies = new ArrayList<>();
                        for (Movie movie : filteredMovies) {
                            api.getWatchProviders(movie.getId()).enqueue(new Callback<WatchProvidersResponse>() {
                                @Override
                                public void onResponse(Call<WatchProvidersResponse> call, Response<WatchProvidersResponse> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                                        WatchProvidersResponse.CountryProviders esProviders = response.body().getResults().get("ES");
                                        if (esProviders != null && esProviders.getFlatrate() != null) {
                                            for (WatchProvider provider : esProviders.getFlatrate()) {
                                                if (selectedProviders.contains(provider.getProviderId())) {
                                                    providerFilteredMovies.add(movie);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    
                                    if (providerFilteredMovies.size() > 0) {
                                        Movie randomMovie = providerFilteredMovies.get(random.nextInt(providerFilteredMovies.size()));
                                        navigateToMovieDetail(randomMovie);
                                    } else {
                                        if (page > 1) {
                                            findRandomMovie(random.nextInt(page - 1) + 1);
                                        } else {
                                            showError("No se encontraron películas disponibles en los servicios de streaming seleccionados");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<WatchProvidersResponse> call, Throwable t) {
                                    showError("Error al obtener proveedores de streaming");
                                }
                            });
                            break;
                        }
                    }
                } else {
                    showError("Error al buscar películas");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showError("Error de conexión");
            }
        });
    }

    private List<Integer> getSelectedGenreIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < genreGroup.getChildCount(); i++) {
            Chip chip = (Chip) genreGroup.getChildAt(i);
            if (chip.isChecked()) {
                int genreId = Genre.getGenreId(chip.getText().toString());
                System.out.println("Debug - Género seleccionado: " + chip.getText() + " (ID: " + genreId + ")");
                if (genreId > 0) {
                    selectedIds.add(genreId);
                }
            }
        }
        return selectedIds;
    }

    private List<Integer> getSelectedProviderIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < providerGroup.getChildCount(); i++) {
            Chip chip = (Chip) providerGroup.getChildAt(i);
            if (chip.isChecked()) {
                int providerId = WatchProvider.getProviderId(chip.getText().toString());
                if (providerId > 0) {
                    selectedIds.add(providerId);
                }
            }
        }
        return selectedIds;
    }

    private void clearFilters() {
        ratingSlider.setValue(7.0f);
        for (int i = 0; i < genreGroup.getChildCount(); i++) {
            ((Chip) genreGroup.getChildAt(i)).setChecked(false);
        }
        for (int i = 0; i < providerGroup.getChildCount(); i++) {
            ((Chip) providerGroup.getChildAt(i)).setChecked(false);
        }
    }

    private void navigateToMovieDetail(Movie movie) {
        if (getView() != null) {
            Bundle args = new Bundle();
            args.putParcelable("movie", movie);
            Navigation.findNavController(getView())
                .navigate(R.id.action_surprise_to_movieDetail, args);
        }
    }

    private void showError(String message) {
        if (getContext() != null && isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
} 