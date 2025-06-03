package com.prueba.filminder.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@Entity(tableName = "movies")
public class Movie implements Parcelable {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genre_ids")
    private int[] genreIds;

    private boolean isSaved;

    @Ignore
    private List<WatchProvider> streamingProviders;

    @Ignore
    private List<WatchProvider> rentProviders;

    @Ignore
    private List<WatchProvider> buyProviders;

    @Ignore
    private String watchProvidersLink;

    // Constructor por defecto necesario para Room y Gson
    public Movie() {
    }

    // Constructor completo ignorado por Room
    @Ignore
    public Movie(int id, String title, String overview, String posterPath, 
                String backdropPath, double voteAverage, String releaseDate, 
                int[] genreIds) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.isSaved = false;
    }

    // Constructor desde Parcel
    @Ignore
    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        genreIds = in.createIntArray();
        isSaved = in.readByte() != 0;
    }

    // Implementación de Parcelable
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
        dest.writeIntArray(genreIds);
        dest.writeByte((byte) (isSaved ? 1 : 0));
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPosterPath() { 
        return posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : null;
    }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getBackdropPath() { 
        return backdropPath != null ? "https://image.tmdb.org/t/p/original" + backdropPath : null;
    }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }

    public String getReleaseDate() { return releaseDate; }
    
    public String getFormattedReleaseDate() {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return "Fecha no disponible";
        }
        try {
            String[] parts = releaseDate.split("-");
            if (parts.length == 3) {
                return String.format("%s/%s/%s", parts[2], parts[1], parts[0].substring(2));
            }
        } catch (Exception e) {
            return "Fecha no disponible";
        }
        return "Fecha no disponible";
    }
    
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public int[] getGenreIds() { return genreIds; }
    public void setGenreIds(int[] genreIds) { this.genreIds = genreIds; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }

    public List<WatchProvider> getStreamingProviders() { return streamingProviders; }
    public void setStreamingProviders(List<WatchProvider> streamingProviders) { 
        this.streamingProviders = streamingProviders; 
    }

    public List<WatchProvider> getRentProviders() { return rentProviders; }
    public void setRentProviders(List<WatchProvider> rentProviders) { 
        this.rentProviders = rentProviders; 
    }

    public List<WatchProvider> getBuyProviders() { return buyProviders; }
    public void setBuyProviders(List<WatchProvider> buyProviders) { 
        this.buyProviders = buyProviders; 
    }

    public String getWatchProvidersLink() { return watchProvidersLink; }
    public void setWatchProvidersLink(String watchProvidersLink) { 
        this.watchProvidersLink = watchProvidersLink; 
    }

    public static class MovieResponse {
        @SerializedName("results")
        private List<Movie> results;

        @SerializedName("page")
        private int page;

        @SerializedName("total_pages")
        private int totalPages;

        @SerializedName("total_results")
        private int totalResults;

        public List<Movie> getResults() {
            return results;
        }

        public void setResults(List<Movie> results) {
            this.results = results;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }
    }
} 