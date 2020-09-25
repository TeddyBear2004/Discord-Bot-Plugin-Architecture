package com.wetterquarz.plugin;

import org.apache.log4j.Logger;

import com.wetterquarz.config.Config;

public class Plugin {

	Logger logger;
	public Logger getLogger() {
		if(logger == null) throw new NullPointerException();
		return logger;
	}
	
	Config config;
	public Config getConfig() {
		if(config == null) throw new NullPointerException();
		return config;
	}
	
	public void onLoad() {}
	public void onGatewayReady() {}
	public void onUnload() {}

}
