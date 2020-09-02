package com.wetterquarz.plugin;

import org.apache.log4j.Logger;

import com.wetterquarz.config.ConfigHandler;

public class Plugin {

	Logger logger;
	public Logger getLogger() {
		if(logger == null) throw new NullPointerException();
		return logger;
	}
	
	ConfigHandler config;
	public ConfigHandler getConfig() {
		if(config == null) throw new NullPointerException();
		return config;
	}
	
	public void onLoad() {}
	public void onGatewayReady() {}
	public void onUnload() {}

}
