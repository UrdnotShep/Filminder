# Filminder

Filminder es una aplicación Android para descubrir y gestionar películas utilizando la API de TMDB.

## Características

- Descubre nuevas películas
- Guarda tus películas favoritas
- Interfaz moderna y fácil de usar
- Integración con la API de TMDB

## Requisitos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17
- Android SDK 34
- Dispositivo Android con API 24 (Android 7.0) o superior

## Configuración

1. Clona el repositorio:
```bash
git clone https://github.com/TU_USUARIO/Filminder.git
```

2. Crea un archivo `local.properties` en la raíz del proyecto y añade tus claves de API de TMDB:
```properties
TMDB_API_KEY=tu_api_key_aquí
TMDB_ACCESS_TOKEN=tu_access_token_aquí
```

3. Sincroniza el proyecto con Gradle
4. Ejecuta la aplicación

## Tecnologías utilizadas

- Retrofit para llamadas a la API
- Room para la base de datos local
- Glide para la carga de imágenes
- Navigation Component
- ViewModel y LiveData
- Material Design Components
- FlexboxLayout

## Licencia

Este proyecto está bajo la Licencia MIT. 