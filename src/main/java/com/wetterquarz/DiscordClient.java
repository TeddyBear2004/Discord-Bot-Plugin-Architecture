package com.wetterquarz;

import com.wetterquarz.command.CommandManager;
import com.wetterquarz.config.Config;
import com.wetterquarz.config.FileConfig;
import com.wetterquarz.database.DatabaseManager;
import com.wetterquarz.plugin.PluginManager;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class DiscordClient {
    @NotNull private static final DiscordClient discordClient;

    static{
        FileConfig config = new FileConfig("config");

        config.setDefault("token", "set here the token!");
        config.setDefault("prefix", "!");
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

    private DiscordClient(@NotNull Config config){
        this.config = config;

        this.pluginManager = new PluginManager();
        IntentSet intents = this.pluginManager.loadIntents();
        intents = intents.or(IntentSet.of(Intent.GUILD_MESSAGES));

        System.out.println("Starting discord bot with following intents: " + intents);

        GatewayDiscordClient gatewayDiscordClient = DiscordClientBuilder
                .create(config.getString("token"))
                .build()
                .gateway()
                .setEnabledIntents(intents)
                .login()
                .block();

        if(Objects.isNull(gatewayDiscordClient))
            throw new InputMismatchException("Cannot build the gateway discord client");

        this.gatewayDiscordClient = gatewayDiscordClient;

        this.commandManager = new CommandManager(this.gatewayDiscordClient);

        FileConfig databaseOption = config.getSubConfig("database");

        if(Objects.isNull(databaseOption))
            throw new NullPointerException("Could not find any database connection strings.");

        DatabaseManager databaseManagerTEMP;
        try{
            ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                    .option(DRIVER, "mysql")
                    .option(HOST, databaseOption.getString("host"))
                    .option(USER, databaseOption.getString("user"))
                    .option(PORT, databaseOption.getInt("port"))
                    .option(PASSWORD, databaseOption.getString("password"))
                    .option(DATABASE, databaseOption.getString("database"))
                    .option(CONNECT_TIMEOUT, Duration.ofSeconds(3))
                    .build();
            databaseManagerTEMP = new DatabaseManager(options);
        }catch(NoSuchElementException | NullPointerException ignore){
            databaseManagerTEMP = null;
        }
        this.databaseManager = databaseManagerTEMP;
    }

    public static void main(String[] args){
        DiscordClient discordClient = getDiscordClient();

        discordClient.getPluginManager().reload();

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
