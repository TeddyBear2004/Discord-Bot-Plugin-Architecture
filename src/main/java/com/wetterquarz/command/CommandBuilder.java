package com.wetterquarz.command;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.wetterquarz.DiscordClient;

/**
 * With this class you can easily create a new instance of {@link Command} which will be used to call commands.
 *
 * @author Teddy
 */
public class CommandBuilder extends CommandSegmentBuilder {
    @NotNull
    private String prefix;
    private boolean canBotSend;

    /**
     * Create a new instance of a commandBuilder
     *
     * @param name              The name of the command
     * @param commandExecutable The object which will be started if the command passed through the {@link CommandManager}
     * @throws IllegalArgumentException if the name contains a space
     */
    public CommandBuilder(@NotNull String name, @Nullable CommandExecutable commandExecutable){
        super(name, commandExecutable);

        if(name.contains(" "))
            throw new IllegalArgumentException("You may not use spaces within the name.");

        this.prefix = DiscordClient.getDiscordClient().getConfig().getString("prefix");

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
     * Sets the prefix of the command if prefix is null it will be set to the standard.
     *
     * @param prefix the new prefix or null if it should be the normal.
     * @return the new {@link CommandBuilder} object
     */
    public CommandBuilder withPrefix(@Nullable String prefix){
        this.prefix = prefix == null ? DiscordClient.getDiscordClient().getConfig().getString("prefix") : prefix;
        return this;
    }

    /**
     * A list of {@link String} which shows which commands can be used to call the command.
     *
     * @param aliases The list of String
     * @return the new {@link CommandBuilder} object
     */
    public CommandBuilder addAliases(String... aliases){
        super.addAliases(aliases);
        return this;
    }

    /**
     * Register a new level of the command.
     *
     * @return the new {@link CommandBuilder} object
     * @throws IllegalArgumentException if the element has already been registered
     */
    @Override
    public CommandBuilder addSubCommandLevel(@NotNull String name, CommandExecutable e, Consumer<CommandSegmentBuilder> commandSegmentBuilder){
    	super.addSubCommandLevel(name, e, commandSegmentBuilder);
        return this;
    }

    /**
     * Through this method you can build the command. You need to register the command in the {@link CommandManager}.
     *
     * @return The {@link Command} which was built.
     */
    @NotNull
    public Command build(){
        return new Command(name, commandExecutable, aliases, prefix, canBotSend, buildSegments());
    }
}
