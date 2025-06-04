package com.prueba.filminder.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.prueba.filminder.R;
import com.prueba.filminder.data.api.TmdbApi;
import com.prueba.filminder.data.api.TmdbClient;
import com.prueba.filminder.data.model.Movie;
import com.prueba.filminder.data.model.Movie.MovieResponse;
import com.prueba.filminder.ui.adapter.MovieAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final int FIRST_PAGE = 1;
    private static final int MAX_MOVIES_PER_SECTION = 4;

    private RecyclerView popularMoviesRecyclerView;
    private RecyclerView topRatedMoviesRecyclerView;
    private RecyclerView upcomingMoviesRecyclerView;
    private RecyclerView searchResultsRecyclerView;
    private MovieAdapter popularMoviesAdapter;
    private MovieAdapter topRatedMoviesAdapter;
    private MovieAdapter upcomingMoviesAdapter;
    private MovieAdapter searchResultsAdapter;
    private LinearLayout mainContentContainer;
    private LinearLayout searchResultsContainer;
    private TextInputEditText searchEditText;
    private TmdbApi tmdbApi;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "Iniciando HomeFragment");

        // Inicializar vistas
        initializeViews(root);

        // Inicializar adaptadores
        initializeAdapters();

        // Configurar RecyclerViews
        setupRecyclerViews();

        // Inicializar API
        tmdbApi = TmdbClient.getInstance().getApi();

        // Configurar listeners
        setupListeners();

        // Configurar manejo del botón de retroceso
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), 
            new androidx.activity.OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (searchResultsContainer.getVisibility() == View.VISIBLE) {
                        showSearchResults(false);
                        searchEditText.setText("");
                    } else {
                        setEnabled(false);
                        requireActivity().onBackPressed();
                    }
                }
            });

        // Cargar películas
        loadMovies();

        return root;
    }

    private void initializeViews(View root) {
        popularMoviesRecyclerView = root.findViewById(R.id.popular_movies_recycler_view);
        topRatedMoviesRecyclerView = root.findViewById(R.id.top_rated_movies_recycler_view);
        upcomingMoviesRecyclerView = root.findViewById(R.id.upcoming_movies_recycler_view);
        searchResultsRecyclerView = root.findViewById(R.id.search_results_recycler_view);
        mainContentContainer = root.findViewById(R.id.main_content_container);
        searchResultsContainer = root.findViewById(R.id.search_results_container);
        searchEditText = root.findViewById(R.id.search_edit_text);
    }

    private void initializeAdapters() {
        popularMoviesAdapter = new MovieAdapter();
        topRatedMoviesAdapter = new MovieAdapter();
        upcomingMoviesAdapter = new MovieAdapter();
        searchResultsAdapter = new MovieAdapter();
    }

    private void setupRecyclerViews() {
        // Configurar RecyclerView de películas populares
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        popularMoviesRecyclerView.setLayoutManager(popularLayoutManager);
        popularMoviesRecyclerView.setAdapter(popularMoviesAdapter);
        popularMoviesAdapter.setShowSeeMore(true);

        // Configurar RecyclerView de películas mejor valoradas
        LinearLayoutManager topRatedLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRatedMoviesRecyclerView.setLayoutManager(topRatedLayoutManager);
        topRatedMoviesRecyclerView.setAdapter(topRatedMoviesAdapter);
        topRatedMoviesAdapter.setShowSeeMore(true);

        // Configurar RecyclerView de próximos estrenos
        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        upcomingMoviesRecyclerView.setLayoutManager(upcomingLayoutManager);
        upcomingMoviesRecyclerView.setAdapter(upcomingMoviesAdapter);
        upcomingMoviesAdapter.setShowSeeMore(true);

        // Configurar RecyclerView de resultados de búsqueda
        GridLayoutManager searchLayoutManager = new GridLayoutManager(getContext(), 2);
        searchResultsRecyclerView.setLayoutManager(searchLayoutManager);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
    }

    private void setupListeners() {
        // Configurar listeners de "Ver más"
        setupSeeMoreListeners();

        // Configurar listeners de clic en películas
        setupMovieClickListeners();

        // Configurar listener de búsqueda
        setupSearchListener();
    }

    private void setupSeeMoreListeners() {
        popularMoviesAdapter.setSeeMoreClickListener(() -> 
            navigateToMovieGrid("popular", getString(R.string.popular_movies)));

        topRatedMoviesAdapter.setSeeMoreClickListener(() -> 
            navigateToMovieGrid("top_rated", getString(R.string.top_rated_movies)));

        upcomingMoviesAdapter.setSeeMoreClickListener(() -> 
            navigateToMovieGrid("upcoming", getString(R.string.upcoming_movies)));
    }

    private void setupMovieClickListeners() {
        MovieAdapter.OnMovieClickListener listener = movie -> 
            navigateToMovieDetail(movie);

        popularMoviesAdapter.setMovieClickListener(listener);
        topRatedMoviesAdapter.setMovieClickListener(listener);
        upcomingMoviesAdapter.setMovieClickListener(listener);
        searchResultsAdapter.setMovieClickListener(listener);
    }

    private void setupSearchListener() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true;
            }
            return false;
        });

        // Añadir listener para el cambio de texto
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    showSearchResults(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Añadir listener para el foco
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && searchEditText.getText().toString().trim().isEmpty()) {
                showSearchResults(false);
            }
        });
    }

    private void performSearch(String query) {
        Log.d(TAG, "Realizando búsqueda: " + query);

        tmdbApi.searchMovies(query, "es-ES", FIRST_PAGE)
            .enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> movies = response.body().getResults();
                        Log.d(TAG, "Resultados de búsqueda: " + movies.size());
                        searchResultsAdapter.setMovies(movies);
                        showSearchResults(true);
                    } else {
                        Log.e(TAG, "Error en la búsqueda: " + response.code() + " " + response.message());
                        showError("Error en la búsqueda: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.e(TAG, "Error en la búsqueda", t);
                    showError("Error en la búsqueda: " + t.getMessage());
                }
            });
    }

    private void showSearchResults(boolean show) {
        searchResultsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        mainContentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        
        if (!show) {
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) 
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            searchEditText.clearFocus();
        }
    }

    private void navigateToMovieGrid(String category, String title) {
        HomeFragmentDirections.ActionHomeToMovieGrid action =
            HomeFragmentDirections.actionHomeToMovieGrid(category, title);
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void navigateToMovieDetail(Movie movie) {
        Navigation.findNavController(requireView())
            .navigate(HomeFragmentDirections.actionHomeToMovieDetail(movie));
    }

    private void loadMovies() {
        Log.d(TAG, "Iniciando carga de películas");

        // Cargar películas populares
        tmdbApi.getPopularMovies("es-ES", FIRST_PAGE)
            .enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> movies = response.body().getResults()
                            .stream()
                            .limit(MAX_MOVIES_PER_SECTION)
                            .collect(Collectors.toList());
                        Log.d(TAG, "Películas populares cargadas: " + movies.size());
                        popularMoviesAdapter.setMovies(movies);
                    } else {
                        Log.e(TAG, "Error al cargar películas populares: " + response.code() + " " + response.message());
                        showError("Error al cargar películas populares: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.e(TAG, "Error al cargar películas populares", t);
                    showError("Error al cargar películas populares: " + t.getMessage());
                }
            });

        // Cargar películas mejor valoradas
        tmdbApi.getTopRatedMovies("es-ES", FIRST_PAGE)
            .enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> movies = response.body().getResults()
                            .stream()
                            .limit(MAX_MOVIES_PER_SECTION)
                            .collect(Collectors.toList());
                        Log.d(TAG, "Películas mejor valoradas cargadas: " + movies.size());
                        topRatedMoviesAdapter.setMovies(movies);
                    } else {
                        Log.e(TAG, "Error al cargar películas mejor valoradas: " + response.code() + " " + response.message());
                        showError("Error al cargar películas mejor valoradas: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.e(TAG, "Error al cargar películas mejor valoradas", t);
                    showError("Error al cargar películas mejor valoradas: " + t.getMessage());
                }
            });

        // Cargar próximos estrenos
        tmdbApi.getUpcomingMovies("es-ES", FIRST_PAGE)
            .enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> movies = response.body().getResults()
                            .stream()
                            .limit(MAX_MOVIES_PER_SECTION)
                            .collect(Collectors.toList());
                        Log.d(TAG, "Próximos estrenos cargados: " + movies.size());
                        upcomingMoviesAdapter.setMovies(movies);
                    } else {
                        Log.e(TAG, "Error al cargar próximos estrenos: " + response.code() + " " + response.message());
                        showError("Error al cargar próximos estrenos: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.e(TAG, "Error al cargar próximos estrenos", t);
                    showError("Error al cargar próximos estrenos: " + t.getMessage());
                }
            });
    }

    private void showError(String message) {
        Log.e(TAG, message);
        if (getContext() != null && isAdded()) {
            requireActivity().runOnUiThread(() -> 
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }
} 