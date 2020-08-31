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
import java.util.zip.ZipEntry;

public class PluginManager {
	public static final File PLUGIN_FOLDER = new File("./plugins");

	static {
		if (!PLUGIN_FOLDER.exists() || !PLUGIN_FOLDER.isDirectory())
			PLUGIN_FOLDER.mkdir();
	}

	@NotNull
	private static final Logger LOGGER = LogManager.getLogger(PluginManager.class.getName());
	@NotNull
	private final File pluginDir = PLUGIN_FOLDER;
	@NotNull
	private Map<String, PluginMetadata> plugins = new HashMap<>();

	public PluginManager() {
		reload();
	}

	public void reload() {
		plugins = new HashMap<>();
		if (pluginDir.mkdirs())
			return;

		for (File file : pluginDir.listFiles()) {
			if (file.getName().endsWith(".jar")) {
				try (JarFile jar = new JarFile(file)) {
					PluginMetadata pluginMeta = loadJar(jar, file.toURI().toURL());
					if(plugins.containsKey(pluginMeta.getName())) {
						LOGGER.error(pluginMeta.getName() + " already exists.");
					} else {
						plugins.put(pluginMeta.getName(), pluginMeta);
						pluginMeta.getPlugin().onLoad();
					}
				} catch (IOException e) {
					LOGGER.warn(e);
				}
			}
		}
	}

	private PluginMetadata loadJar(JarFile jar, URL jarLoc) throws IOException {
		ZipEntry pluginConfig = jar.getEntry("plugin.yml");
		if (pluginConfig == null) {
			LOGGER.error(jar.getName() + " does not contain a plugin.yml");
			return null;
		}
		ConfigHandler configHandler = new ConfigHandler(jar.getInputStream(pluginConfig));
		String main = configHandler.getString("main");
		String name = configHandler.getString("name");
		String label = configHandler.getString("label");
		String version = configHandler.getString("version");

		if (main == null || name == null || version == null) {
			LOGGER.error(jar.getName() + "'s plugin.yml does not contain main, name or version entries.");
			return null;
		}
		ClassLoader loader = URLClassLoader.newInstance(new URL[] { jarLoc }, getClass().getClassLoader());

		try {
			@SuppressWarnings("unchecked")
			Class<? extends Plugin> plugin = (Class<? extends Plugin>) loader.loadClass(main);
			try {
				Plugin p = plugin.newInstance();
				PluginMetadata meta = new PluginMetadata();
				meta.setName(name);
				meta.setVersion(version);
				meta.setPlugin(p);
				if(label != null) meta.setLabel(label);
				return meta;
			} catch (IllegalAccessException | InstantiationException e) {
				LOGGER.error(jar.getName() + "'s main class does not have an accessible default constructor.");
			}
		} catch (ClassNotFoundException | ClassCastException e) {
			LOGGER.error(jar.getName() + " points to an invalid main class.");
		}
		return null;
	}

}