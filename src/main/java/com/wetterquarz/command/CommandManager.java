package com.wetterquarz.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import java.util.*;

public class CommandManager {
    private final Map<String, Command> commandMap;

    public CommandManager(GatewayDiscordClient discordClient) {
        this.commandMap = new TreeMap<>();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class).doOnNext(event -> {
            Message message = event.getMessage();
            List<String> args = getArgs(message.getContent());

            Command command = commandMap
                    .get(args.get(0));

            if(command == null)return;
            if (command.canBotSend() || !message.getAuthor().map(User::isBot).orElse(false)) {

                CommandSegment segment = command;

                for (int i = 1; segment.commandSegments != null; i++) {
                    try {
                        CommandSegment commandSegment = segment.commandSegments.get(args.get(i));

                        if (commandSegment == null)
                            break;

                        segment = commandSegment;
                    } catch (IndexOutOfBoundsException ignore) {
                        break;
                    }
                }

                segment.commandExecutable.execute(event);
            }
        }).onErrorContinue((t, obj) -> t.printStackTrace()).subscribe();
    }

    public static List<String> getArgs(String string) {

        return new LinkedList<>(Arrays.asList(string.split(" ")));
    }

    public void registerCommands(Command... commands) {
        for (Command command : commands) {
            String prefix = command.getPrefix();
            this.commandMap.put(prefix + command.getName(), command);
            command.getAliases().forEach(s -> this.commandMap.put(prefix + s.split(" ")[0], command));
        }
    }
}
