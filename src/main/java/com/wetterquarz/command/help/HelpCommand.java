package com.wetterquarz.command.help;

import com.wetterquarz.DiscordClient;
import com.wetterquarz.command.Command;
import com.wetterquarz.command.CommandExecutable;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;


public class HelpCommand implements CommandExecutable {
    @Override
    public @NotNull Mono<Message> execute(@NotNull String[] usedAlias, @NotNull String[] args, @NotNull User executor, @Nullable Command rootCommand, @NotNull MessageChannel channel, @NotNull GatewayDiscordClient discordClient){
        var plugins = DiscordClient.getDiscordClient().getPluginManager().getPlugins();
        if(args.length != 0){
            for(String s : plugins.keySet())
                if(args[0].equals(s)){
                    var metadata = plugins.get(s);
                    if(metadata.getPlugin().getHelpCommand() != null)
                        return metadata.getPlugin().getHelpCommand().execute(usedAlias, args, executor, rootCommand, channel, discordClient);
                }
        }
        var builder = EmbedCreateSpec.builder()
                .color(Color.of(0, 0, 0.52f))
                .author("Plugin-Commands", null, "https://cdn.discordapp.com/avatars/" + discordClient.getSelfId().asLong() + "/90295cd0172896b8bb57c49973af18d4.webp?size=4096");

        plugins.forEach((s, pluginMetadata) ->
                builder.addField(pluginMetadata.getName(), "``\nVersion:" + pluginMetadata.getVersion() + "``", true));
        return channel.createMessage(builder.build());
    }
}
