package com.wetterquarz;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.InputMismatchException;
import java.util.Objects;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class DiscordClient {
    @NotNull private static final DiscordClient discordClient;

    static{
        FileConfig config = new FileConfig("config");
        FileConfig databaseOption = config.getSubConfig("database");

        databaseOption.setDefault("host", "set db host here");
        databaseOption.setDefault("user", "set db user here");
        databaseOption.setDefault("port", 3306);
        databaseOption.setDefault("password", "set db password here");
        databaseOption.setDefault("database", "set db database here");
        databaseOption.save();

        config.setDefault("token", "set here the token!");
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

        if(Objects.isNull(gatewayDiscordClient))
            throw new InputMismatchException("Cannot build the gateway discord client");

        this.gatewayDiscordClient = gatewayDiscordClient;

        this.commandManager = new CommandManager(this.gatewayDiscordClient);

        this.pluginManager = new PluginManager();

        FileConfig databaseOption = config.getSubConfig("database");

        if(Objects.isNull(databaseOption))
            throw new NullPointerException("Could not find any database connection strings.");

        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, databaseOption.getString("host"))
                .option(USER, databaseOption.getString("user"))
                .option(PORT, databaseOption.getInt("port"))
                .option(PASSWORD, databaseOption.getString("password"))
                .option(DATABASE, databaseOption.getString("database"))
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3))
                .build();

        this.databaseManager = new DatabaseManager(options);
    }

    public static void main(String[] args){
        DiscordClient discordClient = getDiscordClient();

        discordClient.getCommandManager().registerCommands(
                new CommandBuilder("pong", new PongCommand()).build());

        discordClient.gatewayDiscordClient.onDisconnect().block();
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
