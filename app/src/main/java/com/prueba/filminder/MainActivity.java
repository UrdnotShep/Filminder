package com.prueba.filminder;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el NavHostFragment y el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Configurar la navegación inferior
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_surprise, R.id.navigation_favorites)
                .build();

        NavigationUI.setupWithNavController(navView, navController);

        // Configurar el comportamiento del botón de inicio
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Limpiar el back stack y navegar al inicio
                navController.navigate(R.id.navigation_home);
                return true;
            }
            // Para otros items, usar la navegación normal
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        // Configurar FAB
        FloatingActionButton fabSurprise = findViewById(R.id.fab_surprise);
        fabSurprise.setOnClickListener(view -> {
            navController.navigate(R.id.navigation_surprise);
        });
    }
}