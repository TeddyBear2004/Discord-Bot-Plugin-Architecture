package com.wetterquarz.command;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class CommandSegmentBuilder {
    CommandExecutable commandExecutable;
    String name;
    @NotNull protected final List<String> aliases;


    CommandSegmentBuilder(String name, CommandExecutable commandExecutable){
        if(name.contains(" "))
            throw new IllegalArgumentException("You may not use spaces within the name.");

        this.aliases = new ArrayList<>();
        this.name = name;
        this.commandExecutable = commandExecutable;
    }


    /**
     * A list of {@link String} which shows which commands can be used to call the command.
     *
     * @param aliases The list of String
     * @return the new {@link CommandBuilder} object
     */
    public CommandSegmentBuilder addAliases(String... aliases){
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    @Nonnull
    protected CommandSegment build(){
        return new CommandSegment(name.toLowerCase(), buildSegments(), commandExecutable);
    }

    List<CommandSegmentBuilder> subCommandBuilders;
    @Nullable
    protected Map<String, CommandSegment> buildSegments(){
        if(subCommandBuilders == null)
            return null;

        Map<String, CommandSegment> commandSegments = new TreeMap<>();

        for(CommandSegmentBuilder subCommandBuilder : subCommandBuilders) {
        	CommandSegment seg = subCommandBuilder.build();
            commandSegments.put(subCommandBuilder.name, seg);
            for (String alias : subCommandBuilder.aliases) {
				commandSegments.put(alias, seg);
			}
        }

        return commandSegments;
    }


    /**
     * Register a new level of the command.
     *
     * @return the new {@link CommandBuilder} object
     * @throws IllegalArgumentException if the element has already been registered
     */
    private CommandSegmentBuilder addSubCommandLevel(CommandSegmentBuilder commandSegmentBuilder){
    	if(subCommandBuilders == null) {
    		subCommandBuilders = new ArrayList<CommandSegmentBuilder>(Collections.singleton(commandSegmentBuilder));
    	} else {
    		List<String> reserved = new ArrayList<>();
    		for(CommandSegmentBuilder b : subCommandBuilders) {
    			reserved.add(b.name);
    			reserved.addAll(b.aliases);
    		}
    		List<String> newReserved = new ArrayList<>(commandSegmentBuilder.aliases);
    		newReserved.add(commandSegmentBuilder.name);
    		if(Collections.disjoint(reserved, newReserved)) {
    			subCommandBuilders.add(commandSegmentBuilder);
    		} else {
    			throw new IllegalArgumentException("Duplicate meanings for one command");
    		}
    	}
        return this;
    }


	public CommandSegmentBuilder addSubCommandLevel(@NotNull String name, CommandExecutable e, Consumer<CommandSegmentBuilder> commandSegmentBuilder) {
		CommandSegmentBuilder b = new CommandSegmentBuilder(name, e);
		commandSegmentBuilder.accept(b);
    	this.addSubCommandLevel(b);
		return this;
	}
}
