package com.wetterquarz.command;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class Command extends CommandSegment {
    @NotNull final String name;
    private final List<String> aliases;
    private final String prefix;
    private final boolean botCanSend;

    Command(String name,
            CommandExecutable commandExecutable,
            List<String> aliases,
            String prefix,
            boolean botsCanSend,
            Map<String, CommandSegment> commandSegments){
        super(commandSegments, commandExecutable);

        this.name = name;
        this.aliases = aliases;
        this.prefix = prefix;
        this.botCanSend = botsCanSend;
    }

    public List<String> getAliases(){
        return aliases;
    }

    public String getPrefix(){
        return prefix;
    }

    public boolean canBotSend(){
        return botCanSend;
    }

    public @NotNull String getName(){
        return name;
    }
}
