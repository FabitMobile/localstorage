package ru.fabit.localstorage;

import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LocalStorageServiceImpl implements LocalStorageService {
    private static ObjectMapper objectMapper;
    private final SharedPreferences sharedPreferences;

    public LocalStorageServiceImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    //region ===================== Implementation ======================

    @Override
    public String getData(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public boolean getData(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public long getData(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    @Override
    public int getData(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    @Override
    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void saveData(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public void saveData(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    @Override
    public void saveData(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    @Nullable
    public Set<String> getDataSet(String key) {
        Set<String> newSet = new HashSet<>();
        Set<String> dataSet = sharedPreferences.getStringSet(key, null);
        if (dataSet != null) {
            newSet.addAll(dataSet);
            return newSet;
        }
        return null;
    }

    @Override
    public void saveSet(String key, Set<String> set) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    @Override
    public void clearData(String key) {
        saveData(key, null);
    }

    @Override
    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    @Override
    public void addToSet(String key, String value) {
        if (!sharedPreferences.contains(key)) {
            Set<String> newSet = new HashSet<>();
            saveSet(key, newSet);
        }

        Set<String> setStrings = new HashSet<>(sharedPreferences.getStringSet(key, new HashSet<String>()));
        setStrings.add(value);
        saveSet(key, setStrings);
    }

    @Override
    public boolean contains(String key, String value) {
        if (!sharedPreferences.contains(key) || sharedPreferences.getStringSet(key, null) == null) {
            return false;
        }
        Set<String> set = sharedPreferences.getStringSet(key, null);
        return set.contains(value);
    }

    @Override
    public void print(String key, boolean isString) {
        if (isString) {
            Object dataString = sharedPreferences.getString(key, null);
        } else {
            Set<String> dataSet = sharedPreferences.getStringSet(key, null);
            int index = 0;
            if (dataSet != null) {
                for (String str : dataSet) {
                    index++;
                }
            }
        }
    }

    @Override
    public <K, V> HashMap<K, V> getMap(String key, Class<?> keyClass, Class<?> valueClass) {
        HashMap<K, V> parkOutTimestamp = new HashMap<>();

        String dataJson = getData(key, "");
        if (!dataJson.isEmpty()) {
            try {
                parkOutTimestamp = objectMapper.readValue(dataJson,
                        TypeFactory.defaultInstance().constructMapType(HashMap.class, keyClass, valueClass));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parkOutTimestamp;
    }

    @Override
    public void setMap(String key, HashMap map) {
        try {
            String dataJson = objectMapper.writeValueAsString(map);
            if (dataJson != null && !dataJson.isEmpty()) {
                saveData(key, dataJson);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <K> List<K> getList(String key, Class<K> type) {
        List<K> list = new ArrayList<>();
        String dataJson = getData(key, "");
        if (!dataJson.isEmpty()) {
            try {
                list = objectMapper.readValue(dataJson,
                        TypeFactory.defaultInstance().constructCollectionType(List.class, type));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public <K> void saveList(String key, List<K> list) {
        try {
            String dataJson = objectMapper.writeValueAsString(list);
            if (dataJson != null && !dataJson.isEmpty()) {
                saveData(key, dataJson);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //endregion
}
