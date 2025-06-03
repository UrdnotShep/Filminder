package com.prueba.filminder.ui.movies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prueba.filminder.R;
import com.prueba.filminder.data.api.TmdbApi;
import com.prueba.filminder.data.api.TmdbClient;
import com.prueba.filminder.data.model.Movie;
import com.prueba.filminder.data.model.Movie.MovieResponse;
import com.prueba.filminder.ui.adapter.MovieAdapter;
import com.prueba.filminder.ui.util.GridSpacingItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MovieGridFragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private TextView titleTextView;
    private String category;
    private String title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // Obtener argumentos
        MovieGridFragmentArgs args = MovieGridFragmentArgs.fromBundle(getArguments());
        category = args.getCategory();
        title = args.getTitle();

        // Inicializar vistas
        initializeViews(root);
        
        // Configurar RecyclerView
        setupRecyclerView();
        
        // Cargar películas
        loadMovies();

        return root;
    }

    private void initializeViews(View root) {
        recyclerView = root.findViewById(R.id.movies_recycler_view);
        titleTextView = root.findViewById(R.id.title_text_view);
        titleTextView.setText(title);

        // Configurar toolbar para navegación
        root.findViewById(R.id.toolbar).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
        });
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        adapter.setMovieClickListener(this::navigateToMovieDetail);
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Añadir decoración para espaciado uniforme
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
    }

    private void navigateToMovieDetail(Movie movie) {
        MovieGridFragmentDirections.ActionMovieGridToMovieDetail action =
            MovieGridFragmentDirections.actionMovieGridToMovieDetail(movie);
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void loadMovies() {
        TmdbApi tmdbApi = TmdbClient.getInstance().getApi();
        Call<MovieResponse> call;

        switch (category) {
            case "popular":
                call = tmdbApi.getPopularMovies("es-ES", 1);
                break;
            case "top_rated":
                call = tmdbApi.getTopRatedMovies("es-ES", 1);
                break;
            case "upcoming":
                call = tmdbApi.getUpcomingMovies("es-ES", 1);
                break;
            default:
                showError("Categoría no válida");
                return;
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    adapter.setMovies(movies);
                } else {
                    showError("Error al cargar las películas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showError("Error al cargar las películas: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null && isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
} 