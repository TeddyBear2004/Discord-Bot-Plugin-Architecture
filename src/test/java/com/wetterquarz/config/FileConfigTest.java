package com.wetterquarz.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileConfigTest {
    @Test
    void setObject() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setObject("test", "test");
        fileConfig.setObject("test2", 2);
        fileConfig.setObject("test3", 3.0);
        fileConfig.setObject("test4", true);
        fileConfig.setObject("test5", 2f);
        fileConfig.setObject("test6", (byte) 2);
        fileConfig.setObject("test7", (short) 2);
        fileConfig.setObject("test8", (char) 'a');
        fileConfig.setObject("test9", (long) 2);
        fileConfig.setObject("test10", new int[]{1, 2, 3});
        fileConfig.setObject("test11", new String[]{"a", "b", "c"});
        fileConfig.setObject("test12", new boolean[]{true, false, true});
        fileConfig.setObject("test13", new float[]{1.0f, 2.0f, 3.0f});
        fileConfig.setObject("test14", new double[]{1.0, 2.0, 3.0});
        fileConfig.setObject("test15", new byte[]{1, 2, 3});
        fileConfig.setObject("test16", new short[]{1, 2, 3});
        fileConfig.setObject("test17", new char[]{'a', 'b', 'c'});
        fileConfig.setObject("test18", new long[]{1, 2, 3});

        assertEquals(fileConfig.get("test"), "test");
        assertEquals(fileConfig.get("test2"), 2);
        assertEquals(fileConfig.get("test3"), 3.0);
        assertEquals(fileConfig.get("test4"), true);
        assertEquals(fileConfig.get("test5"), 2f);
        assertEquals(fileConfig.get("test6"), (byte) 2);
        assertEquals(fileConfig.get("test7"), (short) 2);
        assertEquals(fileConfig.get("test8"), (char) 'a');
        assertEquals(fileConfig.get("test9"), (long) 2);
        assertArrayEquals(new int[]{1, 2, 3}, (int[]) fileConfig.get("test10"));
        assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) fileConfig.get("test11"));
        assertArrayEquals(new boolean[]{true, false, true}, (boolean[]) fileConfig.get("test12"));
        assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f}, (float[]) fileConfig.get("test13"));
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, (double[]) fileConfig.get("test14"));
        assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) fileConfig.get("test15"));
        assertArrayEquals(new short[]{1, 2, 3}, (short[]) fileConfig.get("test16"));
        assertArrayEquals(new char[]{'a', 'b', 'c'}, (char[]) fileConfig.get("test17"));
        assertArrayEquals(new long[]{1, 2, 3}, (long[]) fileConfig.get("test18"));
    }

    @Test
    void setDefault() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setDefault("test", "test");
        fileConfig.setObject("test2", 2);
        fileConfig.setDefault("test2", 3);
        fileConfig.setObject("test3", 3.0);

        assertEquals(fileConfig.get("test"), "test");
        assertEquals(fileConfig.get("test2"), 2);
        assertEquals(fileConfig.get("test3"), 3.0);
    }

    @Test
    void remove() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setObject("test", "test");
        assertEquals(fileConfig.get("test"), "test");
        fileConfig.remove("test");
        assertNull(fileConfig.get("test"));
    }

    @Test
    void get() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setObject("test", "test");
        assertEquals("test", fileConfig.get("test"));
    }

    @Test
    void getString() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setObject("test", "test");
        assertEquals("test", fileConfig.getString("test"));
    }

    @Test
    void getList() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setObject("test", "test");
        assertThrows(ClassCastException.class, () -> fileConfig.getList("test"));

        List<String> list = new ArrayList<>();

        fileConfig.setObject("test2", new ArrayList<>());
        assertEquals(new ArrayList<>(), fileConfig.getList("test2"));

        list.add("a");
        list.add("b");
        list.add("c");

        fileConfig.setObject("test3", list);
        assertEquals(list, fileConfig.getList("test3"));
    }

    @Test
    void getSubConfig() {
        FileConfig fileConfig = new FileConfig("fileConfig");
        fileConfig.setObject("test", "test");
        assertThrows(ClassCastException.class, () -> fileConfig.getSubConfig("test"));

        FileConfig subConfig = new FileConfig("subConfig");
        fileConfig.setObject("test2", subConfig);
        assertEquals(subConfig, fileConfig.getSubConfig("test2"));
    }

    @Test
    void getInt() {
    }

    @Test
    void getBoolean() {
    }

    @Test
    void getLong() {
    }

    @Test
    void getDouble() {
    }

    @Test
    void getChar() {
    }

    @Test
    void getByte() {
    }

    @Test
    void getShort() {
    }

    @Test
    void getFloat() {
    }

    @Test
    void getMap() {
    }

    @Test
    void setMap() {
    }

    @Test
    void reload() {
    }

    @Test
    void save() {
    }

    @Test
    void testToString() {
    }
}