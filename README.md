# Filminder

Aplicación Android para descubrir y gestionar películas usando la API de TMDB.

## Opciones de Ejecución

Hay dos formas de ejecutar la aplicación:

1. **[Opción Rápida] Instalar APK directamente**
   - Solo necesitas un dispositivo Android 7.0 o superior
   - No requiere configuración ni entorno de desarrollo
   
2. **[Opción Desarrollo] Compilar desde código fuente**
   - Requiere Android Studio y entorno de desarrollo configurado
   - Permite ver y modificar el código

## 1. Instalación Directa del APK

### Requisitos Mínimos
- Dispositivo Android 7.0 (API 24) o superior
- Espacio suficiente para la aplicación (~XMB)
- Conexión a internet para usar la app

### Pasos de Instalación
1. Localiza el archivo `filminder-release.apk` incluido en la entrega
2. Copia el APK a tu dispositivo Android
3. En tu dispositivo:
   - Abre el archivo APK
   - Permite la instalación de orígenes desconocidos si se solicita
   - Sigue las instrucciones de instalación
4. La aplicación estará lista para usar con todas las APIs configuradas

### Instalación vía ADB (opcional)
```bash
adb install filminder-release.apk
```

## 2. Compilación desde Código Fuente

### Requisitos de Desarrollo
- Android Studio (versión más reciente recomendada)
- **Android SDK**:
  - SDK Platform Android 14 (API 34) REQUERIDO
  - Android SDK Build-Tools 34
  - Para instalar/verificar el SDK:
    1. En Android Studio: Tools -> SDK Manager
    2. En "SDK Platforms", marca "Android 14.0 (API 34)"
    3. En "SDK Tools", marca:
       - Android SDK Build-Tools
       - Android SDK Command-line Tools
       - Android SDK Platform-Tools
    4. Click en "Apply" para instalar

- **Java Development Kit (JDK) 17** (REQUISITO OBLIGATORIO)
  - Para verificar tu versión de Java:
    ```bash
    java -version
    ```
  - Si no tienes Java 17:
    1. Descarga JDK 17 desde [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) o [OpenJDK](https://adoptium.net/)
    2. Instálalo en tu sistema
    3. En Android Studio: File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle
    4. Selecciona Java 17 en "Gradle JDK"

### Pasos para Compilar y Ejecutar
1. Descomprime el archivo del proyecto
2. Abre Android Studio:
   - File -> Open
   - Selecciona la carpeta del proyecto
   - Espera a que el proyecto se indexe y sincronice
3. Verifica la configuración:
   - El archivo `local.properties` ya está incluido con las API keys necesarias
   - Comprueba que tienes instalado el SDK 34
   - Verifica que estás usando Java 17
4. Ejecuta la aplicación:
   - Conecta un dispositivo Android (API 24+) o inicia un emulador
   - Presiona el botón "Run" (▶️) o usa `Shift + F10`

## Solución de Problemas Comunes

### Problemas con el APK
1. "No se puede instalar la aplicación":
   - Verifica que tu dispositivo es Android 7.0 o superior
   - Habilita la instalación de orígenes desconocidos
   - Asegúrate de tener espacio suficiente

2. "App no instalada":
   - Desinstala versiones anteriores de la app
   - Reinicia el dispositivo
   - Intenta la instalación vía ADB

### Problemas de Desarrollo
1. Errores de SDK:
   - "SDK location not found" o similar:
     1. Abre Tools -> SDK Manager
     2. Verifica que tienes instalado:
        - Android SDK Platform 34
        - Android SDK Build-Tools 34
        - Android SDK Command-line Tools
        - Android SDK Platform-Tools
     3. Si falta alguno, instálalo y aplica los cambios
   
   - "Failed to find target with hash string 'android-34'":
     1. Tools -> SDK Manager -> SDK Platforms
     2. Instala "Android 14.0 (API 34)"
     3. Sincroniza el proyecto

2. Errores de Gradle:
   - File -> Invalidate Caches / Restart
   - Clean Project y Rebuild Project

## Características de la Aplicación

- Exploración de películas populares
- Búsqueda de películas
- Gestión de favoritos
- Detalles de películas
- Proveedores de streaming disponibles

## Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/prueba/filminder/
│   │   ├── data/           # Capa de datos y modelos
│   │   ├── ui/             # Interfaces de usuario
│   │   └── utils/          # Utilidades
│   └── res/                # Recursos (layouts, strings, etc.)
```

## Tecnologías Utilizadas

- Retrofit + OkHttp para networking
- Gson para parsing JSON
- Glide para carga de imágenes
- Room para base de datos local
- ViewModel y LiveData para MVVM
- Material Design para UI
