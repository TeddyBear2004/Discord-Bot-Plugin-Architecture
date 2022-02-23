package com.wetterquarz.plugin;

import com.google.common.collect.ImmutableMap;
import com.wetterquarz.config.FileConfig;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginManager {
    public static final @NotNull File PLUGIN_FOLDER = new File("./plugins");

    static{
        if(!PLUGIN_FOLDER.exists() || !PLUGIN_FOLDER.isDirectory())
            PLUGIN_FOLDER.mkdir();
    }

    private static final @NotNull Logger LOGGER = LogManager.getLogger(PluginManager.class.getName());
    private @NotNull Map<String, PluginMetadata> plugins = new HashMap<>();

    public PluginManager(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            plugins.forEach((str, pm) -> {
                pm.getPlugin().onUnload();
            });
        }));
    }

    public @NotNull Map<String, PluginMetadata> getPlugins(){
        return ImmutableMap.copyOf(plugins);
    }

    public @NotNull IntentSet loadIntents(){
        IntentSet intents = IntentSet.none();
        for(File file : PluginManager.PLUGIN_FOLDER.listFiles()){
            if(file.getName().endsWith(".jar")){
                try(JarFile jar = new JarFile(file)){
                    ZipEntry pluginConfig = jar.getEntry("plugin.yml");
                    if(pluginConfig == null){
                        LOGGER.error(jar.getName() + " does not contain a plugin.yml");
                        return null;
                    }
                    FileConfig config = new FileConfig(jar.getInputStream(pluginConfig));
                    List<?> rawIntents;
                    try{
                        rawIntents = config.getList("intents");
                    }catch(NoSuchElementException e){
                        rawIntents = Collections.emptyList();
                    }
                    IntentSet intentSet = IntentSet.none();

                    if(rawIntents != null){
                        for(Object rawIntent : rawIntents){
                            String s = rawIntent.toString();
                            IntentSet cache = IntentSet.of(Intent.valueOf(s));

                            intents = intents.or(cache);
                        }
                    }

                }catch(IOException e){
                    LOGGER.warn(e);
                }
            }
        }
        return intents;
    }

    public void reload(){
        plugins = new HashMap<>();

        for(File file : PluginManager.PLUGIN_FOLDER.listFiles()){
            if(file.getName().endsWith(".jar")){
                try(JarFile jar = new JarFile(file)){
                    PluginMetadata pluginMeta = loadJar(jar, file.toURI().toURL());
                    if(pluginMeta != null){
                        if(plugins.containsKey(pluginMeta.getName().toLowerCase())){
                            LOGGER.error(pluginMeta.getName() + " already exists.");
                        }else{
                            plugins.put(pluginMeta.getName().toLowerCase(), pluginMeta);
                            pluginMeta.getPlugin().logger = LogManager.getLogger(pluginMeta.getName());
                            pluginMeta.getPlugin().config = new FileConfig(new File(PluginManager.PLUGIN_FOLDER, pluginMeta.getName() + File.pathSeparator + "config.yml"));
                            pluginMeta.getPlugin().onLoad();
                            System.out.println(pluginMeta.getName() + " was loaded.");
                        }
                    }
                }catch(IOException e){
                    LOGGER.warn(e);
                }
            }
        }
    }

    private @Nullable PluginMetadata loadJar(@NotNull JarFile jar, @NotNull URL jarLoc) throws IOException{
        ZipEntry pluginConfig = jar.getEntry("plugin.yml");
        if(pluginConfig == null){
            LOGGER.error(jar.getName() + " does not contain a plugin.yml");
            return null;
        }
        FileConfig config = new FileConfig(jar.getInputStream(pluginConfig));
        String main = config.getString("main");
        String name = config.getString("name");
        String label = config.getString("label");
        String version = config.getString("version");
        String description;
        try{
            description = config.getString("description");
        }catch(NoSuchElementException e){
            description = "No description provided.";
        }

        if(main == null || name == null || version == null){
            LOGGER.error(jar.getName() + "'s plugin.yml does not contain main, name or version entries.");
            return null;
        }

        if(name.contains(" ")){
            LOGGER.error(jar.getName() + " Plugin names may not contain spaces.");
            return null;
        }

        ClassLoader loader = URLClassLoader.newInstance(new URL[]{jarLoc}, getClass().getClassLoader());

        try{
            @SuppressWarnings("unchecked")
            Class<? extends Plugin> plugin = (Class<? extends Plugin>)loader.loadClass(main);
            try{
                Plugin p = plugin.newInstance();
                PluginMetadata meta = new PluginMetadata();
                meta.setName(name);
                meta.setVersion(version);
                meta.setPlugin(p);
                if(label != null)
                    meta.setLabel(label);
                meta.setDescription(description);

                return meta;
            }catch(IllegalAccessException | InstantiationException e){
                LOGGER.error(jar.getName() + "'s main class does not have an accessible default constructor.");
            }
        }catch(ClassNotFoundException | ClassCastException e){
            LOGGER.error(jar.getName() + " points to an invalid main class.");
        }
        return null;
    }
}
