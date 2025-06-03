package com.prueba.filminder.data.local;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class Converters {
    @TypeConverter
    public static String fromIntArray(int[] value) {
        if (value == null) {
            return null;
        }
        return new Gson().toJson(value);
    }

    @TypeConverter
    public static int[] toIntArray(String value) {
        if (value == null) {
            return null;
        }
        Type type = new TypeToken<int[]>() {}.getType();
        return new Gson().fromJson(value, type);
    }
} 