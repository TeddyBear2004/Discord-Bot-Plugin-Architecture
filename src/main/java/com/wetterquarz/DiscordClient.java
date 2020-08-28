package com.wetterquarz;

import com.wetterquarz.config.ConfigHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;

public class DiscordClient {
	
	private final discord4j.core.DiscordClient client;
	private final Object commandManager = null;
	private final Object pluginManager = null;
	private final Object databaseManager = null;
	private GatewayDiscordClient gatewayDiscordClient = null;
	
	private DiscordClient(String token) {
		this.client = discord4j.core.DiscordClient.create(token);
    	this.client.withGateway(gateway -> {
    		gatewayDiscordClient = gateway;
    		
    		return gateway.onDisconnect();
    	});
	}
	
	public EventDispatcher getEventDispatcher() {
		return gatewayDiscordClient.getEventDispatcher();
	}
	
	private static DiscordClient discordClient;

    public static void main(String[] args){
    	ConfigHandler config = new ConfigHandler("config");
    	config.setObjectIfNotSet("token", "set here the token!");

    	discordClient = new DiscordClient(config.getString("token"));
    }
    
    public static DiscordClient getDiscordClient() {
    	return discordClient;
    }
    
}
