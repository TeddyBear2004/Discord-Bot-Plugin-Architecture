package com.wetterquarz.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * With this class you can easily create a new instance of {@link Command} which will be used to call commands.
 *
 * @author Teddy
 */
public class CommandBuilder extends CommandSegmentBuilder {
    @NotNull
    private final List<String> aliases;
    @NotNull
    private String prefix;
    private boolean canBotSend;

    /**
     * Create a new instance of a commandBuilder
     *
     * @param name              The name of the command
     * @param commandExecutable The object which will be started if the command passed through the {@link CommandManager}
     * @exception IllegalArgumentException if the name contains a space
     */
    public CommandBuilder(@NotNull String name, @Nullable CommandExecutable commandExecutable){
        super(name, commandExecutable);

        if(name.contains(" "))
            throw new IllegalArgumentException("You may not use spaces within the name.");

        this.aliases = new ArrayList<>();
        this.prefix = "!";//todo get prefix from config

        this.canBotSend = false;
    }

    /**
     * Configures whether bot messages are included in the check for the command.
     *
     * @param canBotSend Include bot messages when checking for the command. (default = false)
     * @return the new {@link CommandBuilder} object
     */
    public CommandBuilder includingBotMessages(boolean canBotSend){
        this.canBotSend = canBotSend;
        return this;
    }

    /**
     * Sets the prefix of the command.
     *
     * @param prefix the new prefix
     * @return the new {@link CommandBuilder} object
     */
    public CommandBuilder withPrefix(String prefix){
        this.prefix = prefix == null ? "!" : prefix;//todo get prefix from config
        return this;
    }

    /**
     * A list of {@link String} which shows which commands can be used to call the command.
     *
     * @param aliases The list of String
     * @return the new {@link CommandBuilder} object
     */
    public CommandBuilder addAliases(String... aliases){
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    /**
     *
     * @throws IllegalArgumentException if the element has already been registered
     */
    public CommandBuilder addSubCommandLevel(String name, CommandExecutable commandExecutable){
        String[] args = name.split(" ");

        CommandSegmentBuilder commandSegmentBuilder = this;

        for(int i = 0; i < args.length; i++){
            if(i+ 1 == args.length){
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
            }else {
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
        }
        return this;
    }

    /**
     * Through this method you can build the command. You need to register the command in the {@link CommandManager}.
     *
     * @return The {@link Command} which was built.
     */
    @NotNull public Command build(){
        return new Command(name, commandExecutable, aliases, prefix, canBotSend, buildSegments());
    }
}
