package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

public interface CommandExecutable {
    Mono<Message> execute(@NotNull String[] usedAlias, Object[] args, @NotNull User executor, @Nullable Command rootCommand, @NotNull MessageChannel channel, @NotNull GatewayDiscordClient discordClient);
}
