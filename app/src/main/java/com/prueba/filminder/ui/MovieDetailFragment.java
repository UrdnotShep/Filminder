package com.prueba.filminder.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.flexbox.FlexboxLayout;
import com.prueba.filminder.R;
import com.prueba.filminder.data.api.TmdbApi;
import com.prueba.filminder.data.api.TmdbClient;
import com.prueba.filminder.data.local.FavoriteMoviesManager;
import com.prueba.filminder.data.model.Movie;
import com.prueba.filminder.data.model.Genre;
import com.prueba.filminder.data.model.WatchProvider;
import com.prueba.filminder.data.model.WatchProvidersResponse;
import com.prueba.filminder.ui.adapter.WatchProviderAdapter;
import java.util.stream.Collectors;
import java.util.Arrays;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {
    private FavoriteMoviesManager favoriteMoviesManager;
    private Movie movie;
    private FloatingActionButton fabFavorite;
    private RecyclerView streamingProvidersRecycler;
    private RecyclerView rentProvidersRecycler;
    private RecyclerView buyProvidersRecycler;
    private TextView streamingTitle;
    private TextView rentTitle;
    private TextView buyTitle;
    private TextView noProvidersText;
    private WatchProviderAdapter streamingAdapter;
    private WatchProviderAdapter rentAdapter;
    private WatchProviderAdapter buyAdapter;
    private String watchProvidersLink;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteMoviesManager = new FavoriteMoviesManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MovieDetailFragmentArgs args = MovieDetailFragmentArgs.fromBundle(getArguments());
        movie = args.getMovie();

        setupViews(view);
        setupFavoriteButton(view);
        setupBackButton(view);
        loadWatchProviders();
    }

    private void setupBackButton(View view) {
        view.findViewById(R.id.toolbar).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
        });
    }

    private void setupViews(View view) {
        TextView titleView = view.findViewById(R.id.movie_title);
        TextView overviewView = view.findViewById(R.id.movie_overview);
        ImageView posterView = view.findViewById(R.id.movie_poster);
        com.google.android.material.chip.Chip ratingChip = view.findViewById(R.id.movie_rating);
        TextView releaseDateView = view.findViewById(R.id.movie_release_date);
        com.google.android.flexbox.FlexboxLayout genresGroup = view.findViewById(R.id.genres_group);
        ImageView backdropView = view.findViewById(R.id.movie_backdrop);
        CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.toolbar_layout);

        // Setup streaming providers views
        streamingTitle = view.findViewById(R.id.streaming_title);
        rentTitle = view.findViewById(R.id.rent_title);
        buyTitle = view.findViewById(R.id.buy_title);
        streamingProvidersRecycler = view.findViewById(R.id.streaming_providers_recycler);
        rentProvidersRecycler = view.findViewById(R.id.rent_providers_recycler);
        buyProvidersRecycler = view.findViewById(R.id.buy_providers_recycler);
        noProvidersText = view.findViewById(R.id.no_providers_text);

        setupProvidersRecyclerViews();

        titleView.setText(movie.getTitle());
        overviewView.setText(movie.getOverview());
        ratingChip.setText(String.format("%.1f", movie.getVoteAverage()));
        releaseDateView.setText(String.format("Estreno: %s", movie.getFormattedReleaseDate()));
        collapsingToolbar.setTitle(movie.getTitle());

        // Configurar los géneros usando TextView personalizados
        genresGroup.removeAllViews();
        Arrays.stream(movie.getGenreIds())
            .mapToObj(Genre::getGenreName)
            .forEach(genreName -> {
                View genreView = getLayoutInflater().inflate(R.layout.item_genre_tag, genresGroup, false);
                ((TextView) genreView).setText(genreName);
                genresGroup.addView(genreView);
            });

        // Cargar imágenes
        Glide.with(this)
            .load(movie.getPosterPath())
            .placeholder(R.drawable.placeholder_poster)
            .into(posterView);

        Glide.with(this)
            .load(movie.getBackdropPath())
            .placeholder(R.drawable.placeholder_backdrop)
            .into(backdropView);
    }

    private void setupProvidersRecyclerViews() {
        // Configurar adaptadores
        streamingAdapter = new WatchProviderAdapter();
        rentAdapter = new WatchProviderAdapter();
        buyAdapter = new WatchProviderAdapter();

        // Configurar click listeners
        WatchProviderAdapter.OnProviderClickListener listener = provider -> {
            if (watchProvidersLink != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(watchProvidersLink));
                startActivity(intent);
            }
        };

        streamingAdapter.setClickListener(listener);
        rentAdapter.setClickListener(listener);
        buyAdapter.setClickListener(listener);

        // Configurar recycler views con orientación horizontal
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        streamingProvidersRecycler.setLayoutManager(layoutManager1);
        rentProvidersRecycler.setLayoutManager(layoutManager2);
        buyProvidersRecycler.setLayoutManager(layoutManager3);

        streamingProvidersRecycler.setAdapter(streamingAdapter);
        rentProvidersRecycler.setAdapter(rentAdapter);
        buyProvidersRecycler.setAdapter(buyAdapter);
    }

    private void loadWatchProviders() {
        TmdbApi tmdbApi = TmdbClient.getInstance().getApi();
        tmdbApi.getWatchProviders(movie.getId()).enqueue(new Callback<WatchProvidersResponse>() {
            @Override
            public void onResponse(Call<WatchProvidersResponse> call, Response<WatchProvidersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WatchProvidersResponse.CountryProviders providers = response.body().getResults().get("ES");
                    if (providers != null) {
                        updateProvidersUI(providers);
                    } else {
                        showNoProviders();
                    }
                } else {
                    showError("Error al cargar los proveedores de streaming");
                }
            }

            @Override
            public void onFailure(Call<WatchProvidersResponse> call, Throwable t) {
                showError("Error al cargar los proveedores de streaming: " + t.getMessage());
            }
        });
    }

    private void updateProvidersUI(WatchProvidersResponse.CountryProviders providers) {
        watchProvidersLink = providers.getLink();

        boolean hasAnyProvider = false;

        if (providers.getFlatrate() != null && !providers.getFlatrate().isEmpty()) {
            streamingTitle.setVisibility(View.VISIBLE);
            streamingProvidersRecycler.setVisibility(View.VISIBLE);
            streamingAdapter.setProviders(providers.getFlatrate());
            hasAnyProvider = true;
        } else {
            streamingTitle.setVisibility(View.GONE);
            streamingProvidersRecycler.setVisibility(View.GONE);
        }

        if (providers.getRent() != null && !providers.getRent().isEmpty()) {
            rentTitle.setVisibility(View.VISIBLE);
            rentProvidersRecycler.setVisibility(View.VISIBLE);
            rentAdapter.setProviders(providers.getRent());
            hasAnyProvider = true;
        } else {
            rentTitle.setVisibility(View.GONE);
            rentProvidersRecycler.setVisibility(View.GONE);
        }

        if (providers.getBuy() != null && !providers.getBuy().isEmpty()) {
            buyTitle.setVisibility(View.VISIBLE);
            buyProvidersRecycler.setVisibility(View.VISIBLE);
            buyAdapter.setProviders(providers.getBuy());
            hasAnyProvider = true;
        } else {
            buyTitle.setVisibility(View.GONE);
            buyProvidersRecycler.setVisibility(View.GONE);
        }

        noProvidersText.setVisibility(hasAnyProvider ? View.GONE : View.VISIBLE);
    }

    private void showNoProviders() {
        streamingTitle.setVisibility(View.GONE);
        rentTitle.setVisibility(View.GONE);
        buyTitle.setVisibility(View.GONE);
        streamingProvidersRecycler.setVisibility(View.GONE);
        rentProvidersRecycler.setVisibility(View.GONE);
        buyProvidersRecycler.setVisibility(View.GONE);
        noProvidersText.setVisibility(View.VISIBLE);
    }

    private void setupFavoriteButton(View view) {
        fabFavorite = view.findViewById(R.id.fab_favorite);
        updateFavoriteButton();

        fabFavorite.setOnClickListener(v -> {
            if (movie.isSaved()) {
                favoriteMoviesManager.removeFavorite(movie);
                showMessage("Película eliminada de favoritos");
            } else {
                favoriteMoviesManager.addFavorite(movie);
                showMessage("Película añadida a favoritos");
            }
            movie.setSaved(!movie.isSaved());
            updateFavoriteButton();
        });
    }

    private void updateFavoriteButton() {
        movie.setSaved(favoriteMoviesManager.isFavorite(movie));
        fabFavorite.setImageResource(
            movie.isSaved() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
        );
    }

    private void showError(String message) {
        if (getContext() != null && isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        if (getContext() != null && isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
} 