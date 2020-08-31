package com.wetterquarz.command;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class DefaultCommandExecutable implements CommandExecutable {
    private final CommandSegment commandSegment;

    public DefaultCommandExecutable(CommandSegment commandSegment){
        this.commandSegment = commandSegment;
    }

    @Override
    public void execute(MessageCreateEvent event){
        StringBuilder builder;

        if(commandSegment .getCommandSegments() != null && commandSegment.getCommandSegments().size() != 0){
            builder = new StringBuilder("Es fehlt mindestens ein Argument. Mögliche Argumente sind:");

            commandSegment.forEachPossibleArgument((s, commandSegment1) -> builder.append("\n").append(s));
        }else
            builder = new StringBuilder("Scheinbar hast du gerade einen Fehler in der Matrix gefunden :o");

        event.getMessage().getChannel().map(messageChannel -> messageChannel.createMessage(builder.toString())).subscribe();
    }
}
