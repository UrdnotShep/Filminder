package com.prueba.filminder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.prueba.filminder.GlideApp;
import com.prueba.filminder.R;
import com.prueba.filminder.data.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MOVIE = 0;
    private static final int TYPE_SEE_MORE = 1;
    
    private List<Movie> movies;
    private OnMovieClickListener movieClickListener;
    private OnSeeMoreClickListener seeMoreClickListener;
    private boolean showSeeMore = false;

    public MovieAdapter() {
        this.movies = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEE_MORE) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_see_more, parent, false);
            return new SeeMoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            Movie movie = movies.get(position);
            MovieViewHolder movieHolder = (MovieViewHolder) holder;
            
            movieHolder.titleTextView.setText(movie.getTitle());
            movieHolder.ratingTextView.setText(String.format("%.1f", movie.getVoteAverage()));
            
            if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                GlideApp.with(holder.itemView.getContext())
                    .load(movie.getPosterPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.error_poster)
                    .centerCrop()
                    .into(movieHolder.posterImageView);
            } else {
                movieHolder.posterImageView.setImageResource(R.drawable.placeholder_poster);
            }

            holder.itemView.setOnClickListener(v -> {
                if (movieClickListener != null) {
                    movieClickListener.onMovieClick(movie);
                }
            });
        } else if (holder instanceof SeeMoreViewHolder && seeMoreClickListener != null) {
            holder.itemView.setOnClickListener(v -> seeMoreClickListener.onSeeMoreClick());
        }
    }

    @Override
    public int getItemCount() {
        return showSeeMore ? movies.size() + 1 : movies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (showSeeMore && position == movies.size()) ? TYPE_SEE_MORE : TYPE_MOVIE;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void setMovieClickListener(OnMovieClickListener listener) {
        this.movieClickListener = listener;
    }

    public void setSeeMoreClickListener(OnSeeMoreClickListener listener) {
        this.seeMoreClickListener = listener;
    }

    public void setShowSeeMore(boolean showSeeMore) {
        this.showSeeMore = showSeeMore;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
        TextView ratingTextView;

        MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.poster_image_view);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            ratingTextView = itemView.findViewById(R.id.movie_rating);
        }
    }

    static class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        SeeMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }
} 