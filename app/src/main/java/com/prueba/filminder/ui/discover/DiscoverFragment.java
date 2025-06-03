package com.prueba.filminder.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.prueba.filminder.R;

public class DiscoverFragment extends Fragment {

    private ImageView moviePosterImageView;
    private TextView movieTitleTextView;
    private TextView movieOverviewTextView;
    private ChipGroup genreChipGroup;
    private RangeSlider ratingRangeSlider;
    private Button findMovieButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discover, container, false);

        initializeViews(root);
        setupListeners();

        return root;
    }

    private void initializeViews(View root) {
        moviePosterImageView = root.findViewById(R.id.movie_poster_image_view);
        movieTitleTextView = root.findViewById(R.id.movie_title_text_view);
        movieOverviewTextView = root.findViewById(R.id.movie_overview_text_view);
        genreChipGroup = root.findViewById(R.id.genre_chip_group);
        ratingRangeSlider = root.findViewById(R.id.rating_range_slider);
        findMovieButton = root.findViewById(R.id.find_movie_button);
    }

    private void setupListeners() {
        findMovieButton.setOnClickListener(v -> findRandomMovie());
    }

    private void findRandomMovie() {
        // TODO: Implementar la búsqueda aleatoria de películas basada en los filtros seleccionados
    }
} 