package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCommandExecutable implements CommandExecutable {
    private final @NotNull CommandSegment commandSegment;

    public DefaultCommandExecutable(@NotNull CommandSegment commandSegment){
        this.commandSegment = commandSegment;
    }

    @Override
    public @NotNull Mono<Message> execute(@NotNull String[] usedAlias, String[] args, @NotNull User executor, @Nullable Command rootCommand, @NotNull MessageChannel channel, @NotNull GatewayDiscordClient discordClient){
        StringBuilder builder;

        if(commandSegment.getCommandSegments() != null && commandSegment.getCommandSegments().size() != 0){
            builder = new StringBuilder("At least one argument is missing. Possible arguments are:");

            Map<CommandSegment, List<String>> map = new HashMap<>();

            if(commandSegment.commandSegments != null){
                commandSegment.commandSegments.forEach((s, segment) -> {
                    if(!map.containsKey(segment))
                        map.put(segment, new ArrayList<>());
                    map.get(segment).add(s);
                });
            }

            map.forEach((commandSegment, strings) -> {
                builder.append("\n- ");
                strings.forEach(s -> builder.append(s).append(", "));
                builder.replace(builder.length()-2, builder.length(), "");
                builder.append("   ").append("or");
            });
            builder.replace(builder.length()-5,builder.length(),"");
        }else
            builder = new StringBuilder("This command contains no handler or any subcommands.");

        return channel.createMessage(builder.toString());
    }
}
