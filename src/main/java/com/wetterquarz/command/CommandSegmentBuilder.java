package com.wetterquarz.command;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CommandSegmentBuilder {
    List<CommandSegmentBuilder> subCommandBuilders;
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

    @Nullable
    protected Map<String, CommandSegment> buildSegments(){
        if(subCommandBuilders == null)
            return null;

        Map<String, CommandSegment> commandSegments = new TreeMap<>();

        for(CommandSegmentBuilder subCommandBuilder : subCommandBuilders)
            commandSegments.put(subCommandBuilder.name, subCommandBuilder.build());

        return commandSegments;
    }


    /**
     * Register a new level of the command.
     *
     * @return the new {@link CommandBuilder} object
     * @throws IllegalArgumentException if the element has already been registered
     */
    public CommandSegmentBuilder addSubCommandLevel(CommandSegmentBuilder commandSegmentBuilder){


        /*CommandSegmentBuilder commandSegmentBuilder = this;

        for(int i = 0; i < args.length; i++){
            if(i + 1 == args.length){
                //Exactly one element remaining

                if(commandSegmentBuilder.subCommandBuilders == null){
                    commandSegmentBuilder.subCommandBuilders = new ArrayList<>();
                }else{
                    for(CommandSegmentBuilder subCommandBuilder : commandSegmentBuilder.subCommandBuilders){
                        if(subCommandBuilder.name.equalsIgnoreCase(args[i])){
                            throw new IllegalArgumentException("This element has already been registered: " + name);
                        }
                    }
                }

                commandSegmentBuilder.subCommandBuilders.add(new CommandSegmentBuilder(args[i], commandExecutable));
                return this;
            }else{
                //more than one element remaining

                if(commandSegmentBuilder.subCommandBuilders == null)
                    throw new IllegalArgumentException("You cannot create multiple levels at once");

                CommandSegmentBuilder commandSegmentBuilderTemp = null;

                for(CommandSegmentBuilder subCommandBuilder : commandSegmentBuilder.subCommandBuilders){
                    if(subCommandBuilder.name.equalsIgnoreCase(args[i])){
                        commandSegmentBuilderTemp = subCommandBuilder;
                        break;
                    }
                }

                if(commandSegmentBuilderTemp == null)
                    throw new IllegalArgumentException("One of the levels is not registered yet and needs to.");
                commandSegmentBuilder = commandSegmentBuilderTemp;
            }
        }*/
        return this;
    }
}
