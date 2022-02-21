package com.wetterquarz.dialogapi;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;

public class TextSegment extends DialogSegment<Void> {
    private final EmbedCreateSpec embedSpec;
    private final String text;
    private final MessageCreateSpec messageSpec;

    public TextSegment(EmbedCreateSpec embedSpec) {
        this.embedSpec = embedSpec;
        text = null;
        messageSpec = null;
    }

    public TextSegment(String text) {
        this.text = text;
        embedSpec = null;
        messageSpec = null;
    }

    public TextSegment(MessageCreateSpec messageSpec) {
        this.messageSpec = messageSpec;
        text = null;
        embedSpec = null;
    }

    @Override
    public Mono<Void> execute(User user, MessageChannel channel, Mono<Object> lastResult) {
        Mono<Message> messageMono;
        if (embedSpec != null) messageMono = channel.createMessage(embedSpec);
        else if (text != null) messageMono = channel.createMessage(text);
        else if (messageSpec != null) messageMono = channel.createMessage(messageSpec);
        else messageMono = channel.createMessage("");

        return messageMono.then(Mono.empty());
    }
}
