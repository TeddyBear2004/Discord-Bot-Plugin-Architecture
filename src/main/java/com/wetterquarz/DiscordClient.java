package com.wetterquarz;

public class DiscordClient {
	
	private final discord4j.core.DiscordClient client;
	
	private DiscordClient(String token) {
		this.client = discord4j.core.DiscordClient.create(token);
	}
	
	private static DiscordClient discordClient;
    public static void main(String[] args){
    	discordClient = new DiscordClient("to be loaded from config");
    }
    
    public static DiscordClient getDiscordClient() {
    	return discordClient;
    }
    
}
