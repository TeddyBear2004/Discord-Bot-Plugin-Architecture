package com.wetterquarz;

import com.wetterquarz.command.Command;
import com.wetterquarz.command.CommandExecutable;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

public class PongCommand implements CommandExecutable {
    @Override
    public Mono<Message> execute(@NotNull String[] usedAlias, String[] args, @NotNull User executor, @Nullable Command rootCommand, @NotNull MessageChannel channel, @NotNull GatewayDiscordClient discordClient){
        return channel.createMessage("Ping!");
    }
}
