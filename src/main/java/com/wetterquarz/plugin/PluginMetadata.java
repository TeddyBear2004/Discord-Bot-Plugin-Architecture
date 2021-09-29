package com.wetterquarz.plugin;

public class PluginMetadata {

	private Plugin plugin;
	private String name;
	private String label;
	private String version;
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
}
