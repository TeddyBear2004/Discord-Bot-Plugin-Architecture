package com.wetterquarz.config;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface Config {
    /**
     * Set the given value to the key. If the key already exist it will override the old value.
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     * @throws IllegalArgumentException      If the Object cannot be parsed into plain text
     */
    void setObject(@NotNull String key, Object value);

    /**
     * Set multiple entries at once into the config. If a key already exist it will be overridden.
     *
     * @param objectMap The map of all entries.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setObjects(@NotNull Map<String, Object> objectMap);

    /**
     * Insert all entries of the given map. If a key is already set it will be ignored
     *
     * @param objectMap The map of the entries.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefaults(@NotNull Map<String, Object> objectMap);

    /**
     * Set a Object with the given key if the key does not exist
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, Object value);

    /**
     * Set a byte to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, byte value);

    /**
     * Set a short to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, short value);

    /**
     * Set a int to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, int value);

    /**
     * Set a long to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, long value);

    /**
     * Set a float to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, float value);

    /**
     * Set a double to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, double value);

    /**
     * Set a char to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, char value);

    /**
     * Set a boolean to the given key
     *
     * @param key   The key where the value should take place.
     * @param value The value which will be set.
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void setDefault(@NotNull String key, boolean value);

    /**
     * Removes an element of the map.
     *
     * @param key The key where the element should be removed.
     */
    void remove(@NotNull String key);

    /**
     * Return the object where the key is set as key or null if the key is not set
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     */
    Object get(@NotNull String key);

    /**
     * Return the string where the key is set or null if the key is not set or the value is no string.
     *
     * @param key The key of the value
     * @return null if the key is not set or is not a string or the set value
     * @throws NoSuchElementException If the value is null or not set.
     */
    String getString(@NotNull String key);

    /**
     * Return the list where the key is set or null if the key is not set or the value is no list.
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     * @throws ClassCastException if the value is not List.
     */
    List<Object> getList(@NotNull String key);

    /**
     * Return the map where the key is set or null if the key is not set or the value is no list.
     *
     * @param key The key of the value
     * @return null if the key is not set or the set value
     * @throws ClassCastException if the value is not List.
     */
    Map<String, Object> getSubMap(@NotNull String key);

    /**
     * Return the integer where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a integer.
     */
    int getInt(@NotNull String key);

    /**
     * Return the boolean where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a boolean.
     */
    boolean getBoolean(@NotNull String key);

    /**
     * Return the long where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a long.
     */
    long getLong(@NotNull String key);

    /**
     * Return the double where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a double.
     */
    double getDouble(@NotNull String key);

    /**
     * Return the character where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a character.
     */
    char getChar(@NotNull String key);

    /**
     * Return the byte where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a byte.
     */
    byte getByte(@NotNull String key);

    /**
     * Return the short where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a short.
     */
    short getShort(@NotNull String key);

    /**
     * Return the float where the key is set.
     *
     * @param key The key of the value
     * @return The set value
     * @throws NoSuchElementException If the value is neither set nor is a float.
     */
    float getFloat(@NotNull String key);

    /**
     * Reading the file and load it into the cache. If the file do not exist it will be created.
     *
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void reload();

    /**
     * Create the file if it not exist and save it into the file.
     *
     * @throws UnsupportedOperationException If the config is read-only.
     */
    void save();
}
