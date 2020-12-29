package com.wetterquarz.command;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

public class CommandSegment {
    @NotNull final String name;
    @NotNull final CommandExecutable commandExecutable;
    @Nullable final Map<String, CommandSegment> commandSegments;

    public CommandSegment(@NotNull String name, @Nullable Map<String, CommandSegment> commandSegments, @Nullable CommandExecutable commandExecutable){
        this.name = name;
        this.commandSegments = commandSegments == null ? null : ImmutableMap.copyOf(commandSegments);
        this.commandExecutable = commandExecutable == null ? new DefaultCommandExecutable(this) : commandExecutable;
    }

    public void forEachPossibleArgument(BiConsumer<String, CommandSegment> biConsumer){
        if(commandSegments == null)return;

        commandSegments.forEach(biConsumer);
    }

    @Nullable
    public Map<String, CommandSegment> getCommandSegments(){
        return commandSegments;
    }


}
