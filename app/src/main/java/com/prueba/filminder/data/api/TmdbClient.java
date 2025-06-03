package com.prueba.filminder.data.api;

import android.util.Log;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.prueba.filminder.BuildConfig;

public class TmdbClient {
    private static final String TAG = "TmdbClient";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static TmdbClient instance;
    private final TmdbApi api;

    private TmdbClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> 
            Log.d(TAG, message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            
            // Construir la URL con el api_key como parámetro de consulta
            String newUrl = originalRequest.url().newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
                .toString();

            // Agregar el token de autorización en el encabezado
            Request newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .header("Authorization", "Bearer " + BuildConfig.TMDB_ACCESS_TOKEN)
                .header("accept", "application/json")
                .build();

            Log.d(TAG, "URL de la solicitud: " + newUrl);
            Log.d(TAG, "Token de autorización: Bearer " + BuildConfig.TMDB_ACCESS_TOKEN);
            
            return chain.proceed(newRequest);
        };

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

        Log.d(TAG, "Inicializando TmdbClient con BASE_URL: " + BASE_URL);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        api = retrofit.create(TmdbApi.class);
    }

    public static synchronized TmdbClient getInstance() {
        if (instance == null) {
            instance = new TmdbClient();
        }
        return instance;
    }

    public TmdbApi getApi() {
        return api;
    }

    public String getAuthToken() {
        return "Bearer " + BuildConfig.TMDB_ACCESS_TOKEN;
    }
} 