package com.prueba.filminder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.prueba.filminder.R;
import com.prueba.filminder.data.model.WatchProvider;
import java.util.ArrayList;
import java.util.List;

public class WatchProviderAdapter extends RecyclerView.Adapter<WatchProviderAdapter.ProviderViewHolder> {
    private List<WatchProvider> providers = new ArrayList<>();
    private OnProviderClickListener clickListener;

    public interface OnProviderClickListener {
        void onProviderClick(WatchProvider provider);
    }

    public void setProviders(List<WatchProvider> providers) {
        this.providers = providers != null ? providers : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setClickListener(OnProviderClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_watch_provider, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        holder.bind(providers.get(position));
    }

    @Override
    public int getItemCount() {
        return providers.size();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView logoImageView;

        ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.provider_logo);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onProviderClick(providers.get(position));
                }
            });
        }

        void bind(WatchProvider provider) {
            Glide.with(itemView.getContext())
                .load(provider.getLogoPath())
                .placeholder(R.drawable.placeholder_poster)
                .into(logoImageView);
        }
    }
} 