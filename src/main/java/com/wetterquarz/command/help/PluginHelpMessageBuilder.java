package com.wetterquarz.command.help;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class PluginHelpMessageBuilder {
    private final EmbedCreateSpec.Builder specBuilder;

    public PluginHelpMessageBuilder(String pluginName){
        specBuilder = EmbedCreateSpec.builder();
        specBuilder.title(pluginName);
    }

    public PluginHelpMessageBuilder setDescription(String description){
        specBuilder.description(description);
        return this;
    }

    public PluginHelpMessageBuilder addCommand(String commandName, String description){
        specBuilder.addField(commandName, description, false);
        return this;
    }

    public PluginHelpMessageBuilder setColor(Color color) {
        specBuilder.color(color);
        return this;
    }

    public EmbedCreateSpec build(){
        return specBuilder.build();
    }

}
