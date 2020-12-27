package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

public class DefaultCommandExecutable implements CommandExecutable {
    private final CommandSegment commandSegment;

    public DefaultCommandExecutable(CommandSegment commandSegment){
        this.commandSegment = commandSegment;
    }

    @Override
    public Mono<Message> execute(@NotNull String[] usedAlias, Object[] args, @NotNull User executor, @Nullable Command rootCommand, @NotNull MessageChannel channel, @NotNull GatewayDiscordClient discordClient){
        StringBuilder builder;

        if(commandSegment.getCommandSegments() != null && commandSegment.getCommandSegments().size() != 0){
            builder = new StringBuilder("At least one argument is missing. Possible arguments are:");

            commandSegment.forEachPossibleArgument((s, commandSegment1) -> builder.append("\n").append(s));
        }else
            builder = new StringBuilder("This command contains no handler or any subcommands.");

        return channel.createMessage(builder.toString());

    }
}
