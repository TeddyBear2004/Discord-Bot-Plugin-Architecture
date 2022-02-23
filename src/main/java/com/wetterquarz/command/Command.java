package com.wetterquarz.command;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Command extends CommandSegment {
    @NotNull final String name;
    @NotNull private final List<String> aliases;
    @NotNull private final String prefix;
    private final boolean botCanSend;

    Command(@NotNull String name,
            @Nullable CommandExecutable commandExecutable,
            @NotNull List<String> aliases,
            @NotNull String prefix,
            boolean botsCanSend,
            @Nullable Map<String, CommandSegment> commandSegments){
        super(commandSegments, commandExecutable);

        this.name = name;
        this.aliases = aliases;
        this.prefix = prefix;
        this.botCanSend = botsCanSend;
    }

    public @NotNull List<String> getAliases(){
        return aliases;
    }

    public @NotNull String getPrefix(){
        return prefix;
    }

    public boolean canBotSend(){
        return botCanSend;
    }

    public @NotNull String getName(){
        return name;
    }
}
