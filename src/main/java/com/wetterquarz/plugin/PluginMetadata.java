package com.wetterquarz.plugin;

import discord4j.gateway.intent.IntentSet;
import org.jetbrains.annotations.NotNull;

public class PluginMetadata {

	private Plugin plugin;
	private String name;
	private String label;
	private String version;
	private @NotNull IntentSet intents = IntentSet.none();
	Plugin getPlugin() {
		return plugin;
	}
	void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	String getLabel() {
		return label;
	}
	void setLabel(String label) {
		this.label = label;
	}
	String getVersion() {
		return version;
	}
	void setVersion(String version) {
		this.version = version;
	}
    @NotNull IntentSet getIntents(){
		return intents;
	}
	public void setIntents(@NotNull IntentSet intents){
		this.intents = intents;
	}
}
