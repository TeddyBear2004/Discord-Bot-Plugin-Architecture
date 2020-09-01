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
 * You can handle your yaml based config file with this class.
 * Create a object with an file/filename or with an input stream.
 * If this class is created with an input stream it is read-only else you can do everything.
 * This file will be created the first time it does not exist and you call save.
 *
 * @author teddy
 */
public class ConfigHandler {
    /**
     * The folder where all configs should be saved in.
     */
    @NotNull public static final String CONFIG_FOLDER = "config\\";
    /**
     * The suffix of the file. Should be either *.yml or *.yaml.
     */
    @NotNull public static final String CONFIG_FILE_SUFFIX = ".yml";
    /**
     * A Logger of this class.
     */
    @NotNull private static final Logger LOGGER = LogManager.getLogger(ConfigHandler.class.getName());
    /**
     * The file of the config. Can either be set with the name or directly be set.
     * If this field is null the object is created with an input stream and the object is read-only.
     */
    @Nullable private final File file;
    /**
     * This map contains all yaml elements of the file or those which are newly set.
     * Can be synchronised by method .save();
     */
    @NotNull private Map<String, Object> map;

    /**
     * Create or initialise a configFile at the given location.
     *
     * @param file Where the config file is or where it should be created
     */
    public ConfigHandler(@NotNull File file){
        this.file = file;

        this.map = load(this.file);
    }

    /**
     * Create or initialise a configFile with the given name.
     *
     * @param configFileName The config file name
     */
    public ConfigHandler(@NotNull String configFileName){
        this(new File(CONFIG_FOLDER + configFileName + CONFIG_FILE_SUFFIX));
    }

    /**
     * Creates a read-only version of the ConfigHandler.
     *
     * @param in The input stream of the ConfigHandler.
     */
    public ConfigHandler(@NotNull InputStream in){
        this.file = null;

        this.map = load(in);
    }

    /**
     * Set the given value to the key. If the key already exist it will override the old value.
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If this object is created with an input stream.
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
     * @throws UnsupportedOperationException If this object is created with an input stream.
     */
    public void setObjects(@NotNull Map<String, Object> objectMap){
        if(file == null)
            throw new UnsupportedOperationException("This config is read-only.");

        map.putAll(objectMap);
    }

    /**
     * Insert all entries of the given map. If a key is already set it will be ignored
     *
     * @param objectMap The map of the entries.
     * @throws UnsupportedOperationException If this object is created with an input stream.
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
            throw new UnsupportedOperationException("This config is read-only.");
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
     * Removes an element of the map.
     * @param key The key where the element should be removed.
     */
    public void remove(String key){
        setDefault(key, null);
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
     * Fetches the content of the file and parse it into a map.
     *
     * @param f The config file
     * @return a map from the config. If the file does not exist or other errors appear this will return an empty map.
     */
    @NonNull
    private Map<String, Object> load(@NonNull File f){
        try{
            return load(new FileInputStream(f));
        }catch(FileNotFoundException e){
            return new LinkedHashMap<>();
        }
    }

    /**
     * Fetches the content of the input stream and parse it into a map.
     *
     * @param in The input stream
     * @return a map from the config. If the input stream cannot be parsed or other errors appear this will return an empty map.
     */
    @NonNull
    private Map<String, Object> load(@NonNull InputStream in){
        Map<String, Object> map = new Yaml().load(in);

        return map == null ? new LinkedHashMap<>() : map;
    }

    /**
     * Reading the file and load it into the cache. If the file do not exist it will be created.
     */
    public void reload(){
        if(this.file == null)
            throw new UnsupportedOperationException("This config is read-only.");

        this.map = load(this.file);
    }

    /**
     * Create the file if it not exist and save it into the file.
     *
     * @throws UnsupportedOperationException if the config is read-only
     */
    public void save(){
        if(file == null)
            throw new UnsupportedOperationException("This config is read-only.");

        if(file.getParentFile() != null)
            file.getParentFile().mkdirs();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            new Yaml().dump(map, writer);
        }catch(IOException e){
            LOGGER.warn("cannot create config file", e);
        }
    }
}
