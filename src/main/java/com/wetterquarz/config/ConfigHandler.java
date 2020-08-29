package com.wetterquarz.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import reactor.util.annotation.NonNull;

import java.io.*;
import java.util.*;

/**
 * @author teddy
 */
public class ConfigHandler {
    private static final Logger LOGGER = LogManager.getLogger(ConfigHandler.class.getName());

    @Nullable private final File file;
    @Nullable private final InputStream in;
    @NotNull private Map<String, Object> map;

    /**
     * Create or initialise a configFile at the given location.
     *
     * @param configFileName The config file name
     */
    public ConfigHandler(@NotNull String configFileName){
        this.file = new File("config/" + configFileName + ".yml");
        this.in = null;

        this.map = new HashMap<>();
        reload();
    }

    public ConfigHandler(@NotNull InputStream in){
        this.file = null;
        this.in = null;

        this.map = new HashMap<>();
        reload();
    }

    /**
     * Set the given value to the key. If the key already exist it will override the old value.
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     */
    public void setObject(@NotNull String key, Object value){
        Map<String, Object> data = new HashMap<>(1);
        data.put(key, value);

        setObjects(data);
    }

    /**
     * Set multiple entries at once into the config. If a key already exist it will be overridden.
     *
     * @param objectMap The map of all entries.
     */
    public void setObjects(@NotNull Map<String, Object> objectMap){
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
    public void setDefaults(@NotNull Map<String, Object> objectMap){
        map.forEach((s, o) -> objectMap.remove(s));

        setObjects(objectMap);
    }

    /**
     * Set a Object with the given key if the key does not exist
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NotNull String key, Object value){
        if(file == null)
            throw new UnsupportedOperationException("You cannot set values if this object is created with an input stream.");
        if(get(key) == null)
            setObject(key, value);
    }

    /**
     * Set a byte to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, byte value){
        setDefault(key, Byte.valueOf(value));
    }

    /**
     * Set a short to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, short value){
        setDefault(key, Short.valueOf(value));
    }

    /**
     * Set a int to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, int value){
        setDefault(key, Integer.valueOf(value));
    }

    /**
     * Set a long to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, long value){
        setDefault(key, Long.valueOf(value));
    }

    /**
     * Set a float to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, float value){
        setDefault(key, Float.valueOf(value));
    }

    /**
     * Set a double to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, double value){
        setDefault(key, Double.valueOf(value));
    }

    /**
     * Set a char to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, char value){
        setDefault(key, Character.valueOf(value));
    }

    /**
     * Set a boolean to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setDefault(@NonNull String key, boolean value){
        setDefault(key, Boolean.valueOf(value));
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
     * Return the string where the key is set or null if the key is not set or the value is no string.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a string or the set value
     * @throws NoSuchElementException If the value is null or not set.
     */
    @Nullable
    public String getString(@NotNull String key){
        Object value = get(key);
        if(value == null)
            throw new NoSuchElementException();

        return (String)get(key);
    }

    /**
     * Return the list where the key is set or null if the key is not set or the value is no list.
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     * @throws ClassCastException if the value is not List.
     */
    @Nullable
    public List<?> getList(@NotNull String key){
        return (List<?>)get(key);
    }

    /**
     * Return the integer where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a integer.
     */
    @SuppressWarnings("ConstantConditions")
    public int getInt(@NotNull String key){
        try{
            return (int)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the boolean where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a boolean.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean getBoolean(@NotNull String key){
        try{
            return (boolean)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the long where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a long.
     */
    @SuppressWarnings("ConstantConditions")
    public long getLong(@NotNull String key){
        try{
            return (long)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the double where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a double.
     */
    @SuppressWarnings("ConstantConditions")
    public double getDouble(@NotNull String key){
        try{
            return (double)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the character where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a character.
     */
    @SuppressWarnings("ConstantConditions")
    public char getChar(@NotNull String key){
        try{
            return (char)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the byte where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a byte.
     */
    @SuppressWarnings("ConstantConditions")
    public byte getByte(@NotNull String key){
        try{
            return (byte)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the short where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a short.
     */
    @SuppressWarnings("ConstantConditions")
    public short getShort(@NotNull String key){
        try{
            return (short)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the float where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a float.
     */
    @SuppressWarnings("ConstantConditions")
    public float getFloat(@NotNull String key){
        try{
            return (float)get(key);
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    /**
     * Reading the file and load it into the cache. If the file do not exist it will be created.
     */
    public void reload(){
        if(file == null){
            Map<String, Object> map = new Yaml().load(this.in);
            this.map = map == null ? new LinkedHashMap<>() : map;

        }else{
            if(this.file.getParentFile() != null)
                this.file.getParentFile().mkdirs();

            try(BufferedReader reader = new BufferedReader(new FileReader(this.file))){
                this.file.createNewFile();

                Map<String, Object> map = new Yaml().load(reader);

                this.map = map == null ? new LinkedHashMap<>() : map;
            }catch(IOException e){
                LOGGER.warn(e);
            }
        }
    }
}
