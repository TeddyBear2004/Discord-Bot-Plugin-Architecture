package com.wetterquarz.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author teddy
 */
public class ConfigHandler {
    private static final Logger LOGGER = LogManager.getLogger(ConfigHandler.class.getName());

    @NotNull private final File file;
    @NotNull private Map<String, Object> map;

    /**
     * Create or initialise a configFile at the given location.
     *
     * @param configFileName The config file name
     */
    public ConfigHandler(@NotNull String configFileName){
        this.file = new File("config/" + configFileName + ".yml");

        if(this.file.getParentFile() != null)
            this.file.getParentFile().mkdirs();

        try{
            this.file.createNewFile();

        }catch(IOException e){
            LOGGER.warn("Cannot create config file", e);
        }
        map = new HashMap<>();
        reload();
    }

    /**
     * Set the given value to the key. If the key already exist it will override the old value.
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     */
    public void setObject(@NotNull String key, @NotNull Object value){
        Map<String, Object> data = new HashMap<>(1);
        data.put(key, value);

        setObjects(data);
    }

    /**
     * Set multiple entries at once into the config. If a key already exist it will be overridden.
     *
     * @param objectMap The map of all entries.
     */
    public void setObjects(@NotNull Map<String, @NotNull Object> objectMap){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))){
            Map<String, Object> data = new HashMap<>();

            data.putAll(map);
            data.putAll(objectMap);

            new Yaml().dump(data, writer);
        }catch(IOException e){
            LOGGER.warn(e);
        }
    }

    /**
     * Insert all entries of the given map. If a key is already set it will be ignored
     *
     * @param objectMap The map of the entries.
     */
    public void setObjectsIfNotSet(@NotNull Map<String, Object> objectMap){
        map.forEach((s, o) -> objectMap.remove(s));

        setObjects(objectMap);
    }

    /**
     * Set a Object with the given key if the key does not exist
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     */
    public void setObjectIfNotSet(@NotNull String key, @NotNull Object value){
        if(get(key) == null)
            setObject(key, value);
    }

    /**
     * Return the object where the key is set as key or null if the key is not set
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     */
    @Nullable
    public Object get(@NotNull String key){
        return map.get(key);
    }

    /**
     * Return the list where the key is set as key or null if the key is not set or the value is no string.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a string or the set value
     */
    @Nullable
    public String getString(@NotNull String key){
        Object o = get(key);

        return o instanceof String ? (String)o : null;
    }

    /**
     * Return the list where the key is set as key or null if the key is not set or the value is no integer.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a integer or the set value
     */
    @Nullable
    public Integer getInt(@NotNull String key){
        Object o = get(key);

        return o instanceof Integer ? (Integer)o : null;
    }

    /**
     * Return the list where the key is set as key or null if the key is not set or the value is no boolean.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a boolean or the set value
     */
    @Nullable
    public Boolean getBoolean(@NotNull String key){
        Object o = get(key);

        return o instanceof Boolean ? (Boolean)o : null;
    }

    /**
     * Return the list where the key is set as key or null if the key is not set or the value is no long.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a long or the set value
     */
    @Nullable
    public Long getLong(@NotNull String key){
        Object o = get(key);

        return o instanceof Long ? (Long)o : null;
    }

    /**
     * Return the list where the key is set as key or null if the key is not set or the value is no double.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a double or the set value
     */
    @Nullable
    public Double getDouble(@NotNull String key){
        Object o = get(key);

        return o instanceof Double ? (Double)o : null;
    }

    /**
     * Return the list where the key is set as key or null if the key is not set or the value is no list.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a list or the set value
     */
    @Nullable
    public List<?> getList(@NotNull String key){
        Object o = get(key);

        return o instanceof List<?> ? (List<?>)o : null;
    }

    /**
     * Reading the file and load it into the cache.
     */
    public void reload(){
        try(BufferedReader reader = new BufferedReader(new FileReader(this.file))){
            Map<String, Object> map = new Yaml().load(reader);

            this.map = map == null ? new LinkedHashMap<>() : map;
        }catch(IOException e){
            LOGGER.warn(e);
        }
    }
}
