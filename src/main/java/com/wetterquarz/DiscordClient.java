package com.wetterquarz;

import com.wetterquarz.config.ConfigHandler;
import com.wetterquarz.plugin.PluginManager;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import reactor.core.publisher.Mono;

public class DiscordClient {
	
	private final discord4j.core.DiscordClient client;
	private final Object commandManager = null;
	private final PluginManager pluginManager;
	private final Object databaseManager = null;
	private GatewayDiscordClient gatewayDiscordClient = null;
	private final Mono<Void> onDisconnect;
	
	private DiscordClient(String token) {
		this.client = discord4j.core.DiscordClient.create(token);
		this.pluginManager = new PluginManager();
    	this.onDisconnect = this.client.withGateway(gateway -> {
    		gatewayDiscordClient = gateway;
    		
    		return gateway.onDisconnect();
    	});
	}
	
	public EventDispatcher getEventDispatcher() {
		return gatewayDiscordClient.getEventDispatcher();
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	private static DiscordClient discordClient;

    public static void main(String[] args){
    	ConfigHandler config = new ConfigHandler("config");

    	config.setDefault("token", "set here the token!");
    	config.save();

    	discordClient = new DiscordClient(config.getString("token"));
    	
    	discordClient.onDisconnect.block();
    }
    
    public static DiscordClient getDiscordClient() {
    	return discordClient;
    }
    
}
