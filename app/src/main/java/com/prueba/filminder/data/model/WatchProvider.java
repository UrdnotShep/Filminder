package com.prueba.filminder.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchProvider {
    @SerializedName("provider_id")
    private int providerId;

    @SerializedName("provider_name")
    private String providerName;

    @SerializedName("logo_path")
    private String logoPath;

    private static final Map<String, Integer> providerMap = new HashMap<>();
    private static final Map<Integer, String> providerNameMap = new HashMap<>();

    static {
        // Solo los 7 servicios más populares en España
        providerMap.put("Netflix", 8);
        providerMap.put("Amazon Prime Video", 119);
        providerMap.put("Disney Plus", 337);
        providerMap.put("HBO Max", 384);
        providerMap.put("Movistar Plus", 149);
        providerMap.put("Apple TV Plus", 350);
        providerMap.put("SkyShowtime", 1773);

        for (Map.Entry<String, Integer> entry : providerMap.entrySet()) {
            providerNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    public WatchProvider() {
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getLogoPath() {
        return logoPath != null ? "https://image.tmdb.org/t/p/original" + logoPath : null;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public static int getProviderId(String name) {
        return providerMap.getOrDefault(name, 0);
    }

    public static String getProviderName(int id) {
        return providerNameMap.getOrDefault(id, "Desconocido");
    }

    public static class ProviderResponse {
        @SerializedName("results")
        private List<WatchProvider> results;

        public List<WatchProvider> getResults() {
            return results;
        }
    }
} 