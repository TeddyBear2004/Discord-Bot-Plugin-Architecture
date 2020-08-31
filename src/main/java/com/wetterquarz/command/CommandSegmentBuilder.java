package com.wetterquarz.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandSegmentBuilder {
    List<CommandSegmentBuilder> subCommandBuilders;
    CommandExecutable commandExecutable;
    String name;

    CommandSegmentBuilder(String name, CommandExecutable commandExecutable){
        if(name.contains(" "))
            throw new IllegalArgumentException("You may not use spaces within the name.");

        this.name = name;
        this.commandExecutable = commandExecutable;
    }

    @Nonnull
    protected CommandSegment build(){
        return new CommandSegment(name.toLowerCase(), buildSegments(), commandExecutable);
    }

    @Nullable
    protected Map<String, CommandSegment> buildSegments(){
        if(subCommandBuilders == null)
            return null;

        Map<String, CommandSegment> commandSegments = new TreeMap<>();

        for(CommandSegmentBuilder subCommandBuilder : subCommandBuilders)
            commandSegments.put(subCommandBuilder.name, subCommandBuilder.build());

        return commandSegments;
    }
}
