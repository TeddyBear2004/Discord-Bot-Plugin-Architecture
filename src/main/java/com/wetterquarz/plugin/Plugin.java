package com.wetterquarz.plugin;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.wetterquarz.config.ConfigHandler;

public class Plugin {
	
	public Plugin(String name) {
		logger = LogManager.getLogger(name);
		config = new ConfigHandler(new File(PluginHandler.PLUGIN_FOLDER, name + File.pathSeparator + "config.yml"));
	}

	private final Logger logger;
	public Logger getLogger() {
		return logger;
	}
	
	private final ConfigHandler config;
	public ConfigHandler getConfig() {
		return config;
	}
	
	public void onLoad() {}
	public void onUnload() {}

}
