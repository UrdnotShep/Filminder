package com.prueba.filminder.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.prueba.filminder.R;

public class SavedFragment extends Fragment {

    private RecyclerView savedMoviesRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saved, container, false);

        savedMoviesRecyclerView = root.findViewById(R.id.saved_movies_recycler_view);
        savedMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadSavedMovies();

        return root;
    }

    private void loadSavedMovies() {
        // TODO: Implementar la carga de películas guardadas desde la base de datos local
    }
} 