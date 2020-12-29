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

        if((commandSegments == null || commandSegments.size() == 0) && commandExecutable == null)
            throw new UnsupportedOperationException("Empty command segment.");

        try{
            if(commandSegments != null && commandSegments.containsValue(null))
                throw new IllegalArgumentException();
        }catch(NullPointerException ignore){
        }finally{
            this.commandSegments = commandSegments == null ? null : ImmutableMap.copyOf(commandSegments);
            this.commandExecutable = commandExecutable == null ? new DefaultCommandExecutable(this) : commandExecutable;
        }
    }

    public void forEachPossibleArgument(BiConsumer<String, CommandSegment> biConsumer){
        if(commandSegments == null)return;

        commandSegments.forEach(biConsumer);
    }

    public @Nullable Map<String, CommandSegment> getCommandSegments(){
        return commandSegments;
    }

    public @NotNull CommandExecutable getExecutableCommand(){
        return commandExecutable;
    }

    public @NotNull String getName(){
        return name;
    }
}
