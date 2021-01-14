package com.wetterquarz.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.*;

/**
 * You can handle your YAML based config file with this class.
 * Create a object with an file/filename or with an input stream.
 * If this class is created with an input stream it is read-only else you can do everything.
 * This file will be created the first time it does not exist and you call save.
 *
 * @author teddy
 */
public class FileConfig implements Config {
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
    @NotNull private static final Logger LOGGER = LogManager.getLogger(FileConfig.class.getName());
    /**
     * The file of the config. Can either be set with the name or directly be set.
     * If this field is null the object is created with an input stream and the object is read-only.
     */
    @Nullable private final File file;
    @NotNull private final Yaml yaml;
    private final boolean isSubConfig;
    /**
     * This map contains all yaml elements of the file or those which are newly set.
     * Can be synchronised by method .save();
     */
    @NotNull private Map<String, Object> map;

    @Nullable private String key;
    @Nullable private FileConfig fileConfig;

    /**
     * Create or initialise a configFile at the given location.
     *
     * @param file Where the config file is or where it should be created
     */
    public FileConfig(@NotNull File file){
        this.file = file;

        this.yaml = getYaml();

        this.map = load(this.file);
        isSubConfig = false;
    }

    /**
     * Create or initialise a configFile with the given name.
     *
     * @param configFileName The config file name
     */
    public FileConfig(@NotNull String configFileName){
        this(new File(CONFIG_FOLDER + configFileName + CONFIG_FILE_SUFFIX));
    }

    private FileConfig(@NotNull FileConfig fileConfig, @NotNull String key){
        this.yaml = getYaml();
        this.file = null;
        this.key = key;
        this.fileConfig = fileConfig;
        this.map = new LinkedHashMap<>();
        this.isSubConfig = true;
        this.reload();
    }

    /**
     * Creates a read-only version of the ConfigHandler.
     *
     * @param in The input stream of the ConfigHandler.
     */
    public FileConfig(@NotNull InputStream in){
        this.file = null;

        this.yaml = getYaml();

        this.map = load(in);
        isSubConfig = false;
    }

    private static Yaml getYaml(){
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return new Yaml(options);
    }

    /**
     * Set the given value to the key. If the key already exist it will override the old value.
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setObject(@NotNull String key, Object value){
        if(file == null && !isSubConfig)
            throw new UnsupportedOperationException("This config is read-only.");

        int j = key.indexOf('@');
        int indexFromList = 0;
        if(j != -1){
            String s = key.substring(j);
            try{
                indexFromList = Integer.parseInt(s);
            }catch(NumberFormatException ignore){
            }
        }

        String[] path = key.split("\\.");

        if(path.length <= 1){
            Object o = map.get(key);
            if(o instanceof List && indexFromList != 0)
                ((List<Object>)o).set(indexFromList, value);
            else
                map.put(key, value);
        }else{
            Map<String, Object> cache = map;

            for(int i = 0; i < path.length - 1; i++){
                Map<String, Object> map1 = new LinkedHashMap<>();

                cache.put(path[i], map1);

                cache = map1;
            }
            Object o = cache.get(path[path.length - 1]);
            if(o instanceof List && indexFromList != 0)
                ((List<Object>)o).set(indexFromList, value);
            else
                cache.put(path[path.length - 1], value);
        }
    }

    /**
     * Set multiple entries at once into the config. If a key already exist it will be overridden.
     *
     * @param objectMap The map of all entries.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setObjects(@NotNull Map<String, Object> objectMap){
        if(file == null || !isSubConfig)
            throw new UnsupportedOperationException("This config is read-only.");

        map.forEach(this::setObject);
    }

    /**
     * Insert all entries of the given map. If a key is already set it will be ignored
     *
     * @param objectMap The map of the entries.
     * @throws UnsupportedOperationException If the config is read-only.
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
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, Object value){
        if(get(key) == null)
            setObject(key, value);
    }

    /**
     * Set a byte to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, byte value){
        setDefault(key, Byte.valueOf(value));
    }

    /**
     * Set a short to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, short value){
        setDefault(key, Short.valueOf(value));
    }

    /**
     * Set a int to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, int value){
        setDefault(key, Integer.valueOf(value));
    }

    /**
     * Set a long to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, long value){
        setDefault(key, Long.valueOf(value));
    }

    /**
     * Set a float to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, float value){
        setDefault(key, Float.valueOf(value));
    }

    /**
     * Set a double to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, double value){
        setDefault(key, Double.valueOf(value));
    }

    /**
     * Set a char to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, char value){
        setDefault(key, Character.valueOf(value));
    }

    /**
     * Set a boolean to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void setDefault(@NotNull String key, boolean value){
        setDefault(key, Boolean.valueOf(value));
    }

    /**
     * Removes an element of the map.
     *
     * @param key The key where the element should be removed.
     */
    public void remove(@NotNull String key){
        String[] path = key.split("\\.");

        if(path.length == 1)
            map.remove(key);
        else{
            Map<String, Object> cache = map;

            for(String s : path){
                Object o = cache.get(s);

                if(!(o instanceof Map)){
                    cache.remove(s);
                    break;
                }

                cache = (Map<String, Object>)o;
            }
        }
        map.remove(key);
    }

    /**
     * Return the object where the key is set as key or null if the key is not set
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     */
    @Nullable
    public Object get(@NotNull String key){
        String[] path = key.split("\\.");

        if(path.length == 1)
            return map.get(key);
        else{
            Map<String, Object> cache = map;

            for(String s : path){
                Object o = cache.get(s);

                if(!(o instanceof Map))
                    return o;

                cache = (Map<String, Object>)o;
            }
        }
        return null;
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

        return value.toString();
    }

    /**
     * Return the list where the key is set or null if the key is not set or the value is no list.
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     * @throws ClassCastException if the value is not List.
     */
    public @Nullable List<?> getList(@NotNull String key){

        Object o = get(key);

        if(o instanceof List){
            List<?> list = (List<?>)o;

            if((list).size() != 0){
                List<Object> subObjects = new ArrayList<>();

                for(int i = 0; i < list.size(); i++){
                    Object o1 = list.get(i);
                    if(o1 instanceof Map)
                        subObjects.add(new FileConfig(this, key + "@" + i));
                    else
                        subObjects.add(o1);
                }

                return subObjects;
            }
        }
        throw new ClassCastException();
    }

    /**
     * Return the map where the key is set or null if the key is not set or the value is no map.
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     * @throws ClassCastException if the value is not List.
     */
    @NotNull
    public FileConfig getSubConfig(@NotNull String key){
        return new FileConfig(this, key);
    }

    /**
     * Return the integer where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a integer.
     */
    public int getInt(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (int)o;

            throw new NoSuchElementException();
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
    public boolean getBoolean(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (boolean)o;
            throw new NoSuchElementException();
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
    public long getLong(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (long)o;
            throw new NoSuchElementException();
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
    public double getDouble(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (double)o;
            throw new NoSuchElementException();
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
    public char getChar(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (char)o;
            throw new NoSuchElementException();
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
    public byte getByte(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (byte)o;
            throw new NoSuchElementException();
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
    public short getShort(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (short)o;
            throw new NoSuchElementException();
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
    public float getFloat(@NotNull String key){
        try{
            Object o = get(key);
            if(o != null)
                return (float)o;
            throw new NoSuchElementException();
        }catch(ClassCastException | NullPointerException e){
            throw new NoSuchElementException();
        }
    }

    @NotNull Map<String, Object> getMap(){
        return this.map;
    }

    public void setMap(@NotNull Map<String, Object> map){
        this.map = map;
    }

    /**
     * Fetches the content of the file and parse it into a map.
     *
     * @param f The config file
     * @return a map from the config. If the file does not exist or other errors appear this will return an empty map.
     */
    @NotNull
    private Map<String, Object> load(@NotNull File f){
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
    @NotNull
    private Map<String, Object> load(@NotNull InputStream in){
        Map<String, Object> map = this.yaml.load(in);

        return map == null ? new LinkedHashMap<>() : map;
    }

    /**
     * Reading the file and load it into the cache. If the file do not exist it will be created.
     *
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void reload(){
        if(isSubConfig){
            if(this.fileConfig != null && key != null){
                this.fileConfig.reload();
                Object o = this.fileConfig.get(key);
                if(o == null){
                    setMap(new LinkedHashMap<>());
                    return;
                }else if(!(o instanceof Map))
                    throw new UnsupportedOperationException("Could not find a map at the given location");
                setMap((Map<String, Object>)o);
            }
        }else{
            if(this.file == null)
                throw new UnsupportedOperationException("This config is read-only.");
            this.map = load(this.file);
        }


    }

    /**
     * Create the file if it not exist and save it into the file.
     *
     * @throws UnsupportedOperationException If the config is read-only.
     */
    public void save(){
        if(isSubConfig){
            if(this.fileConfig != null && key != null){
                Map<String, Object> map = this.fileConfig.getMap();
                map.put(this.key, this.getMap());

                this.fileConfig.setMap(map);
                this.fileConfig.save();
            }
        }else{
            if(file == null)
                throw new UnsupportedOperationException("This config is read-only.");

            if(file.getParentFile() != null)
                file.getParentFile().mkdirs();
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                this.yaml.dump(map, writer);
            }catch(IOException e){
                LOGGER.warn("cannot create config file", e);
            }catch(YAMLException e){
                LOGGER.warn("cannot dump into config file", e);
            }
        }
    }

    @Override
    public String toString(){
        return "FileConfig{" +
                "file=" + file +
                ", yaml=" + yaml +
                ", isSubConfig=" + isSubConfig +
                ", map=" + map +
                ", key='" + key + '\'' +
                ", fileConfig=" + fileConfig +
                '}';
    }
}
