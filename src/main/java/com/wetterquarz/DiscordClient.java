package com.wetterquarz;

import com.wetterquarz.config.Config;
import com.wetterquarz.config.FileConfig;
import com.wetterquarz.database.DatabaseManager;
import com.wetterquarz.plugin.PluginManager;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import io.r2dbc.spi.ConnectionFactoryOptions;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class DiscordClient {

    private static DiscordClient discordClient;
    private final Object commandManager = null;
    private final PluginManager pluginManager;
    private final DatabaseManager databaseManager;
    private final GatewayDiscordClient gatewayDiscordClient;

    private DiscordClient(Config config){
        String token = config.getString("token");
        System.out.println(token);

        this.gatewayDiscordClient = DiscordClientBuilder.create("gw9EkM6hdh5j6yr6MSsJrh9WOKRZwJF8").build().login().block();

        this.pluginManager = new PluginManager();

        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, config.getString("database.host"))
                .option(USER, config.getString("database.user"))
                .option(PORT, config.getInt("database.port"))
                .option(PASSWORD, config.getString("database.password"))
                .option(DATABASE, config.getString("database.database"))
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3))
                .build();

        this.databaseManager = new DatabaseManager(options);
    }

    public static void main(String[] args){
        FileConfig config = new FileConfig("config");

        config.setDefault("token", "set here the token!");
        config.setDefault("database.host", "set db host here");
        config.setDefault("database.user", "set db user here");
        config.setDefault("database.port", "set db port here");
        config.setDefault("database.password", "set db password here");
        config.setDefault("database.database", "set db database here");
        config.save();

        discordClient = new DiscordClient(config);

        discordClient.gatewayDiscordClient.onDisconnect().block();
    }

    public static DiscordClient getDiscordClient(){
        return discordClient;
    }

    public EventDispatcher getEventDispatcher(){
        return gatewayDiscordClient.getEventDispatcher();
    }

    public PluginManager getPluginManager(){
        return pluginManager;
    }

	public DatabaseManager getDatabaseManager(){
		return databaseManager;
	}
}
