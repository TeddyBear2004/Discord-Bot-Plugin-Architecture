package com.wetterquarz;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

import java.time.Duration;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.wetterquarz.command.CommandBuilder;
import com.wetterquarz.command.CommandManager;
import com.wetterquarz.config.Config;
import com.wetterquarz.config.FileConfig;
import com.wetterquarz.database.DatabaseManager;
import com.wetterquarz.plugin.PluginManager;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import io.r2dbc.spi.ConnectionFactoryOptions;

public class DiscordClient {
    @NotNull private static final DiscordClient discordClient;

    static{
        FileConfig config = new FileConfig("config");

        config.setDefault("token", "set here the token!");
        Map<String, Object> databaseOption = new HashMap<>();
        databaseOption.put("host", "set db host here");
        databaseOption.put("user", "set db user here");
        databaseOption.put("port", "set db port here");
        databaseOption.put("password", "set db password here");
        databaseOption.put("database", "set db database here");
        config.setDefault("database", databaseOption);

        config.save();

        discordClient = new DiscordClient(config);
    }

    private final @NotNull CommandManager commandManager;
    private final @NotNull PluginManager pluginManager;
    private final @Nullable DatabaseManager databaseManager;
    private final @NotNull GatewayDiscordClient gatewayDiscordClient;
    private final @NotNull Config config;

    private DiscordClient(Config config){
        this.config = config;

        GatewayDiscordClient gatewayDiscordClient = DiscordClientBuilder.create(config.getString("token")).build().login().block();

        System.out.println(config.get("database.host"));

        if(Objects.isNull(gatewayDiscordClient))
            throw new InputMismatchException("Cannot build the gateway discord client");

        this.gatewayDiscordClient = gatewayDiscordClient;

        this.commandManager = new CommandManager(this.gatewayDiscordClient);

        this.pluginManager = new PluginManager();

        Map<String, Object> databaseOption = config.getSubMap("database");

        if(Objects.isNull(databaseOption))
            throw new NullPointerException("Could not find any database connection strings.");


        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, databaseOption.get("host").toString())
                .option(USER, databaseOption.get("user").toString())
                .option(PORT, (int)databaseOption.get("port"))
                .option(PASSWORD, databaseOption.get("password").toString())
                .option(DATABASE, databaseOption.get("database").toString())
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3))
                .build();

        this.databaseManager = new DatabaseManager(options);
    }

    public static void main(String[] args){
        DiscordClient discordClient = getDiscordClient();

        discordClient.getCommandManager().registerCommands(
                new CommandBuilder("pong", new PongCommand()).build());

        discordClient.gatewayDiscordClient.onDisconnect().block();
        System.exit(0);
    }

    public static @NotNull DiscordClient getDiscordClient(){
        return discordClient;
    }

    public @NotNull EventDispatcher getEventDispatcher(){
        return gatewayDiscordClient.getEventDispatcher();
    }

    public @NotNull PluginManager getPluginManager(){
        return pluginManager;
    }

    public @Nullable DatabaseManager getDatabaseManager(){
        return databaseManager;
    }

    public @NotNull CommandManager getCommandManager(){
        return commandManager;
    }

    public @NotNull Config getConfig(){
        return config;
    }

    public @NotNull GatewayDiscordClient getGatewayDiscordClient(){
        return gatewayDiscordClient;
    }
}
