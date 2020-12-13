package com.wetterquarz.plugin;

import org.apache.log4j.Logger;

import com.wetterquarz.config.FileConfig;

public class Plugin {

	Logger logger;
	public Logger getLogger() {
		if(logger == null) throw new NullPointerException();
		return logger;
	}
	
	FileConfig config;
	public FileConfig getConfig() {
		if(config == null) throw new NullPointerException();
		return config;
	}
	
	public void onLoad() {}
	public void onGatewayReady() {}
	public void onUnload() {}

}
