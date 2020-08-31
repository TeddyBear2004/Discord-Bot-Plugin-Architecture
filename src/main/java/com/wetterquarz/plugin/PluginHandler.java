package com.wetterquarz.plugin;

import com.wetterquarz.config.ConfigHandler;
import com.wetterquarz.plugin.events.PluginStartEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;

public class PluginHandler {
	public static final File PLUGIN_FOLDER = new File("./plugins");
	
	static {
		if(!PLUGIN_FOLDER.exists() || !PLUGIN_FOLDER.isDirectory())
			PLUGIN_FOLDER.mkdir();
	}
	
    @NotNull private static final Logger LOGGER = LogManager.getLogger(PluginHandler.class.getName());
    @NotNull private final File pluginDir;
    @NotNull private final List<File> jarFiles;
    @NotNull private Map<String, File> plugins;

    public PluginHandler(@NotNull File pluginDir){
        this.pluginDir = pluginDir;
        this.jarFiles = new ArrayList<>();
        this.plugins = new HashMap<>();

        reload();
    }

    public static void main(String[] args){
        new PluginHandler(PLUGIN_FOLDER);
    }

    public void reload(){
        plugins = new HashMap<>();
        if(pluginDir.mkdirs())
            return;

        for(File file : pluginDir.listFiles()){
            if(file.getName().endsWith(".jar")){
                try(JarFile jar = new JarFile(file)) {
                    ConfigHandler configHandler = new ConfigHandler(jar.getInputStream(jar.getEntry("plugin.yml")));

                    String name = configHandler.getString("name");
                    if(this.plugins.containsKey(name)){
                        //todo throw exception
                    }else {
                        String mainClass = configHandler.getString("mainClass");

                        ClassLoader loader = URLClassLoader.newInstance(
                                new URL[]{file.toURI().toURL()},
                                PluginHandler.class.getClassLoader());

                        Class<?> clazz = Class.forName(mainClass, true, loader);

                        for(Method method : clazz.getMethods()){
                            Class<?>[] paraTypes = method.getParameterTypes();
                            if(paraTypes.length == 1)
                                if(paraTypes[0] == PluginStartEvent.class){
                                    method.invoke(null, new PluginStartEvent());
                                }
                        }
                    }

                }catch(IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e){
                    LOGGER.warn(e);
                }
            }
        }
        jarFiles.addAll(Arrays.asList(pluginDir.listFiles()));
    }
}
