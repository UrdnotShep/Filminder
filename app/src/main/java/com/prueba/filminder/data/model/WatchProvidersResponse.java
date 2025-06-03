package com.prueba.filminder.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class WatchProvidersResponse {
    @SerializedName("results")
    private Map<String, CountryProviders> results;

    public Map<String, CountryProviders> getResults() {
        return results;
    }

    public void setResults(Map<String, CountryProviders> results) {
        this.results = results;
    }

    public static class CountryProviders {
        @SerializedName("link")
        private String link;

        @SerializedName("flatrate")
        private List<WatchProvider> flatrate; // Servicios de suscripción

        @SerializedName("rent")
        private List<WatchProvider> rent; // Servicios de alquiler

        @SerializedName("buy")
        private List<WatchProvider> buy; // Servicios de compra

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public List<WatchProvider> getFlatrate() {
            return flatrate;
        }

        public void setFlatrate(List<WatchProvider> flatrate) {
            this.flatrate = flatrate;
        }

        public List<WatchProvider> getRent() {
            return rent;
        }

        public void setRent(List<WatchProvider> rent) {
            this.rent = rent;
        }

        public List<WatchProvider> getBuy() {
            return buy;
        }

        public void setBuy(List<WatchProvider> buy) {
            this.buy = buy;
        }
    }
} 