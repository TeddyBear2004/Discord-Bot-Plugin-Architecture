package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandManager {
    private final Map<String, Command> commandMap;

    public CommandManager(@NotNull GatewayDiscordClient discordClient){
        this.commandMap = new TreeMap<>();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class).flatMap(event -> {
            Message message = event.getMessage();
            List<String> args = Arrays.asList(message.getContent().toLowerCase().split(" "));

            Command command = commandMap.get(args.get(0).toLowerCase());
            if(command != null){
                if(message.getAuthor().isPresent()
                        && (command.canBotSend()
                        || !message.getAuthor().map(User::isBot)
                        .orElse(false))){
                    Tuple2<CommandSegment, Integer> commandSegmentIntegerPair =
                            getLastCommandSegment(command, args, 1);


                    return event.getMember().isEmpty()
                            ? message.getChannel().flatMap(messageChannel ->
                            commandSegmentIntegerPair.getT1().getExecutableCommand().execute(
                                    args.subList(0, commandSegmentIntegerPair.getT2()).toArray(new String[0]),
                                    args.subList(commandSegmentIntegerPair.getT2(), args.size()).toArray(new String[0]),
                                    message.getAuthor().get(),
                                    command,
                                    messageChannel,
                                    messageChannel.getClient())
                                    .then())
                            : message.getChannel().flatMap(messageChannel ->
                            commandSegmentIntegerPair.getT1().getExecutableCommand().execute(
                                    args.subList(0, commandSegmentIntegerPair.getT2()).toArray(new String[0]),
                                    args.subList(commandSegmentIntegerPair.getT2(), args.size()).toArray(new String[0]),
                                    event.getMember().get(),
                                    command,
                                    messageChannel,
                                    messageChannel.getClient()))
                            .then();
                }
            }
            return Mono.empty();
        }).onErrorContinue((t, obj) -> t.printStackTrace()).subscribe();
    }

    private Tuple2<CommandSegment, Integer> getLastCommandSegment(@NotNull CommandSegment segment, @NotNull List<String> args, int i){
        if(segment.getCommandSegmentsLowerCase() == null)
            return Tuples.of(segment, i);

        try{
            CommandSegment commandSegment = segment.getCommandSegmentsLowerCase().get(args.get(i));

            if(commandSegment == null)
                return Tuples.of(segment, i);

            return getLastCommandSegment(commandSegment, args, ++i);
        }catch(IndexOutOfBoundsException ignore){
            return Tuples.of(segment, i);
        }
    }

    public void registerCommands(Command... commands){
        for(Command command : commands)
            registerCommand(command);
    }

    public void registerCommand(Command command){
        String prefix = command.getPrefix();
        this.commandMap.put(prefix + command.getName().toLowerCase(), command);
        command.getAliases().forEach(s -> this.commandMap.put(prefix + s.split(" ")[0].toLowerCase(), command));
    }
}
