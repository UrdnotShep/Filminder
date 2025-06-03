package com.prueba.filminder.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prueba.filminder.R;
import com.prueba.filminder.data.local.FavoriteMoviesManager;
import com.prueba.filminder.data.model.Movie;
import com.prueba.filminder.ui.adapter.MovieAdapter;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FavoriteMoviesManager favoriteMoviesManager;
    private MovieAdapter movieAdapter;
    private TextView emptyView;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteMoviesManager = new FavoriteMoviesManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyView = view.findViewById(R.id.empty_view);
        recyclerView = view.findViewById(R.id.favorites_recycler_view);

        setupRecyclerView();
        loadFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupRecyclerView() {
        movieAdapter = new MovieAdapter();
        movieAdapter.setMovieClickListener(movie -> {
            Navigation.findNavController(requireView())
                    .navigate(FavoritesFragmentDirections.actionFavoritesToMovieDetail(movie));
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(movieAdapter);
    }

    private void loadFavorites() {
        List<Movie> favorites = favoriteMoviesManager.getFavorites();
        movieAdapter.setMovies(favorites);
        
        // Mostrar mensaje si no hay favoritos
        if (favorites.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
} 