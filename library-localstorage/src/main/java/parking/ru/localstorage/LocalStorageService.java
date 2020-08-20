package parking.ru.localstorage;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface LocalStorageService {

    String getData(String key, String defaultValue);

    boolean getData(String key, boolean defaultValue);

    long getData(String key, long defaultValue);

    int getData(String key, int defaultValue);

    void saveData(String key, String value);

    void saveData(String key, boolean value);

    void saveData(String key, long value);

    void saveData(String key, int value);

    void clearData(String key);

    void remove(String key);

    Set<String> getDataSet(String key);

    void saveSet(String key, Set<String> set);

    void addToSet(String key, String value);

    boolean contains(String key, String value);

    void print(String key, boolean isString);

    <K, V> HashMap<K, V> getMap(String key, Class<?> keyClass, Class<?> valueClass);

    void setMap(String key, HashMap map);

    <K> List<K> getList(String key, Class<K> type);

    <K> void saveList(String key, List<K> list);
}
