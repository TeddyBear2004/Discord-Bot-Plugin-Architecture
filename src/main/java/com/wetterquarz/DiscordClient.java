package com.wetterquarz;

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

import java.time.Duration;
import java.util.InputMismatchException;
import java.util.Objects;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class DiscordClient {
    @NotNull private static final DiscordClient discordClient;

    static{
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

    @NotNull private final CommandManager commandManager;
    @NotNull private final PluginManager pluginManager;
    @NotNull private final DatabaseManager databaseManager;
    @NotNull private final GatewayDiscordClient gatewayDiscordClient;

    private DiscordClient(Config config){
        GatewayDiscordClient gatewayDiscordClient = DiscordClientBuilder.create(config.getString("token")).build().login().block();

        if(Objects.isNull(gatewayDiscordClient))
            throw new InputMismatchException("Cannot build the gateway discord client");

        this.gatewayDiscordClient = gatewayDiscordClient;

        this.commandManager = new CommandManager(this.gatewayDiscordClient);

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

    public static @NotNull DiscordClient getDiscordClient(){
        return discordClient;
    }

    public EventDispatcher getEventDispatcher(){
        return gatewayDiscordClient.getEventDispatcher();
    }

    public @NotNull PluginManager getPluginManager(){
        return pluginManager;
    }

    public @NotNull DatabaseManager getDatabaseManager(){
        return databaseManager;
    }

    public @NotNull CommandManager getCommandManager(){
        return commandManager;
    }
}
