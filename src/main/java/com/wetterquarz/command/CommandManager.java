package com.wetterquarz.command;

import com.google.common.collect.ImmutableMap;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandManager {
    private final Map<String, Command> commandMap;
    private Command helpCommand;

    public CommandManager(@NotNull GatewayDiscordClient discordClient){
        this.commandMap = new TreeMap<>();

        discordClient.getEventDispatcher().on(MessageCreateEvent.class).flatMap(event -> {
            Message message = event.getMessage();
            List<String> args = Arrays.asList(message.getContent().toLowerCase().split(" "));

            Command command = args.get(0).equals("help") ? this.helpCommand : commandMap.get(args.get(0));
            if(command != null){
                if(message.getAuthor().isPresent()
                        && (command.canBotSend()
                        || !message.getAuthor().map(User::isBot)
                        .orElse(false))){
                    Tuple2<CommandSegment, Integer> commandSegmentIntegerPair =
                            getLastCommandSegment(command, args, 1);


                    return event.getMember().isEmpty()
                            ? message.getChannel().flatMap(messageChannel ->
                            commandSegmentIntegerPair.getT1().getExecutableCommand().execute(
                                            args.subList(0, commandSegmentIntegerPair.getT2()).toArray(new String[0]),
                                            args.subList(commandSegmentIntegerPair.getT2(), args.size()).toArray(new String[0]),
                                            message.getAuthor().get(),
                                            command,
                                            messageChannel,
                                            messageChannel.getClient())
                                    .then())
                            : message.getChannel().flatMap(messageChannel ->
                                    commandSegmentIntegerPair.getT1().getExecutableCommand().execute(
                                            args.subList(0, commandSegmentIntegerPair.getT2()).toArray(new String[0]),
                                            args.subList(commandSegmentIntegerPair.getT2(), args.size()).toArray(new String[0]),
                                            event.getMember().get(),
                                            command,
                                            messageChannel,
                                            messageChannel.getClient()))
                            .then();
                }
            }
            return Mono.empty();
        }).onErrorContinue((t, obj) -> t.printStackTrace()).subscribe();
    }

    /**
     * @return Returns the immutable version of the command map, where all commands are stored.
     */
    public @NotNull Map<String, Command> getCommandMap(){
        return ImmutableMap.copyOf(commandMap);
    }

    private Tuple2<CommandSegment, Integer> getLastCommandSegment(@NotNull CommandSegment segment, @NotNull List<String> args, int i){
        if(segment.getCommandSegmentsLowerCase() == null)
            return Tuples.of(segment, i);

        try{
            CommandSegment commandSegment = segment.getCommandSegmentsLowerCase().get(args.get(i).toLowerCase());

            if(commandSegment == null)
                return Tuples.of(segment, i);

            return getLastCommandSegment(commandSegment, args, ++i);
        }catch(IndexOutOfBoundsException ignore){
            return Tuples.of(segment, i);
        }
    }

    /**
     * Register a list of commands to the command listener. If a command has already been added,
     * this is output as an error and the return of the method is false. If a command has already
     * been added, all other commands will continue to try to be added.
     * See also {@link CommandManager#registerCommand(Command)}
     *
     * @param commands The commands to be added. Should be build with the {@link CommandBuilder}
     * @return true if all commands and subcommands are added.
     * @throws IllegalArgumentException If either a command name or an alias is named "help".
     */
    public boolean registerCommands(@NotNull Command... commands) throws IllegalArgumentException{
        boolean allAdded = true;
        for(Command command : commands)
            allAdded = registerCommand(command) && allAdded;

        return allAdded;
    }

    /**
     * Registers a command to the command listener. If a command has already been added,
     * this is output as an error and the return of the method is false. If a command has already
     * been added, all other commands will continue to try to be added.
     *
     * @param command The command to be added. Should be build with the {@link CommandBuilder}
     * @return True if the command and all subcommands are added.
     * @throws IllegalArgumentException If either the command name or an alias is named "help".
     */
    public boolean registerCommand(@NotNull Command command) throws IllegalArgumentException{
        String prefix = command.getPrefix();
        boolean allAdded = true;

        if(command.getName().equalsIgnoreCase("help"))
            throw new IllegalArgumentException("Help commands should be set with the CommandManager#setHelpCommand method.");

        if(this.commandMap.get(prefix + command.getName().toLowerCase()) != null){
            System.err.println("The command \"" + prefix + command.getName().toLowerCase() + "\" has already been already initialised and has not been not added.");
            allAdded = false;
        }else{
            this.commandMap.put(prefix + command.getName().toLowerCase(), command);
        }

        for(String alias : command.getAliases()){
            alias = alias.toLowerCase();

            if(alias.equalsIgnoreCase("help"))
                throw new IllegalArgumentException("Help commands should be set with the CommandManager#setHelpCommand method.");

            if(this.commandMap.get(prefix + alias) != null){
                System.err.println("The command alias \"" + prefix + command.getName().toLowerCase() + "\" has already been already initialised and has not been not added.");
                allAdded = false;
            }else{
                this.commandMap.put(prefix + alias.split(" ")[0], command);
            }
        }

        return allAdded;
    }

    public void setHelpCommand(@NotNull Command command){
        this.helpCommand = command;
    }
}
