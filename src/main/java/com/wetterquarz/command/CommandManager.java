package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class CommandManager {
    private final Map<String, Command> commandMap;

    public CommandManager(@NotNull GatewayDiscordClient discordClient){
        this.commandMap = new TreeMap<>();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class).doOnNext(event -> {
            Message message = event.getMessage();
            List<String> args = new LinkedList<>(Arrays.asList(message.getContent().split(" ")));

            Stream.of(commandMap.get(args.get(0)))
                    .filter(Objects::nonNull)
                    .filter(command -> command.canBotSend()
                            || !message.getAuthor().map(User::isBot).orElse(false))
                    .forEach(command -> {

                        Pair<CommandSegment, Integer> commandSegmentIntegerPair =
                                getLastCommandSegment(command, args, 1);

                        Stream.of(event.getMember().orElse(null))
                                .filter(Objects::nonNull)
                                .forEach(member ->
                                        message.getChannel().subscribe(
                                                messageChannel -> commandSegmentIntegerPair.getA().commandExecutable.execute(
                                                        args.subList(0, commandSegmentIntegerPair.getB()).toArray(new String[0]),
                                                        args.subList(commandSegmentIntegerPair.getB(), args.size()).toArray(new String[0]),
                                                        member,
                                                        command,
                                                        messageChannel,
                                                        messageChannel.getClient()).subscribe()));
                    });
        }).onErrorContinue((t, obj) -> t.printStackTrace()).subscribe();
    }

    @NotNull
    private Pair<CommandSegment, Integer> getLastCommandSegment(@NotNull CommandSegment segment, @NotNull List<String> args, int i){
        if(segment.commandSegments == null)
            return new Pair<>(segment, i);

        try{
            CommandSegment commandSegment = segment.commandSegments.get(args.get(i));

            if(commandSegment == null)
                return new Pair<>(segment, i);

            return getLastCommandSegment(commandSegment, args, ++i);
        }catch(IndexOutOfBoundsException ignore){
            return new Pair<>(segment, i);
        }
    }

    public void registerCommands(Command... commands){
        for(Command command : commands)
            registerCommand(command);
    }

    public void registerCommand(Command command){
        String prefix = command.getPrefix();
        this.commandMap.put(prefix + command.getName(), command);
        command.getAliases().forEach(s -> this.commandMap.put(prefix + s.split(" ")[0], command));
    }

    private static class Pair<A, B>{
        private final A a;
        private final B b;

        public Pair(A a, B b){

            this.a = a;
            this.b = b;
        }

        public A getA(){
            return a;
        }

        public B getB(){
            return b;
        }

        @Override
        public String toString(){
            return "Pair{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }
}
