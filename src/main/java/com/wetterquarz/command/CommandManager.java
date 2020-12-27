package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandManager {
    private final Map<String, Command> commandMap;

    public CommandManager(@NotNull GatewayDiscordClient discordClient){
        this.commandMap = new TreeMap<>();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class).doOnNext(event -> {
            Message message = event.getMessage();
            List<String> args = new LinkedList<>(Arrays.asList(message.getContent().split(" ")));
            List<String> usedAlias = new ArrayList<>();

            Command command = commandMap
                    .get(args.get(0));

            if(command == null)
                return;


            if(command.canBotSend() || !message.getAuthor().map(User::isBot).orElse(false)){

                CommandSegment segment = command;

                for(int i = 1; segment.commandSegments != null; i++){
                    try{
                        CommandSegment commandSegment = segment.commandSegments.get(args.get(i));

                        if(commandSegment == null)
                            break;

                        usedAlias.add(args.get(i));

                        segment = commandSegment;
                    }catch(IndexOutOfBoundsException ignore){
                        break;
                    }
                }

                String[] usedAliasArray = new String[usedAlias.size()];
                usedAlias.toArray(usedAliasArray);

                Object[] argsArray = args.subList(usedAlias.size(), args.size()).toArray();

                CommandSegment finalSegment = segment;
                message.getChannel().subscribe(messageChannel -> {

                    User user = event.getMember().orElse(null);

                    if(user == null)
                        return;

                    finalSegment.commandExecutable.execute(usedAliasArray, argsArray, user, command, messageChannel, messageChannel.getClient()).subscribe();
                });
            }
        }).onErrorContinue((t, obj) -> t.printStackTrace()).subscribe();
    }

    public void registerCommands(Command... commands){
        for(Command command : commands){
            String prefix = command.getPrefix();
            this.commandMap.put(prefix + command.getName(), command);
            command.getAliases().forEach(s -> this.commandMap.put(prefix + s.split(" ")[0], command));
        }
    }
}
