package com.wetterquarz.dialogapi;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;

import java.time.temporal.TemporalUnit;
import java.util.function.Function;

public abstract class DialogSegment<T> {

    static TextSegment createTextSegment(String text) {
        return new TextSegment(text);
    }

    static TextSegment createTextSegment(EmbedCreateSpec embed) {
        return new TextSegment(embed);
    }

    static TextSegment createTextSegment(MessageCreateSpec message) {
        return new TextSegment(message);
    }

    static WaitSegment wait(long time, TemporalUnit unit) {
        return new WaitSegment(time, unit);
    }

    static QuestionSegment question(Function<String, String> description) {
        return new QuestionSegment(description);
    }

    static QuestionSegment question(Function<String, String> description, EmbedCreateSpec.Builder builder) {
        return new QuestionSegment(description, builder);
    }

    public abstract Mono<T> execute(User user, MessageChannel channel, Mono<Object> lastResult);
}