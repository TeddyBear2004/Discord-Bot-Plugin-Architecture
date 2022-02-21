package com.wetterquarz.dialogapi;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class ConditionalSegment extends DialogSegment<Object> {
    private final DialogSegment<Boolean> condition;
    private final Dialog.Builder trueSegment;
    private final Dialog.Builder falseSegment;

    public ConditionalSegment(DialogSegment<Boolean> condition, Dialog.Builder trueSegment, Dialog.Builder falseSegment) {
        this.condition = condition;
        this.trueSegment = trueSegment;
        this.falseSegment = falseSegment;
    }

    @Override
    public Mono<Object> execute(User user, MessageChannel channel, Mono<Object> lastResult) {
        return condition.execute(user, channel, lastResult)
                .flatMap(result -> {
                    if (result) {
                        return trueSegment.build().send(user, channel);
                    }
                    return falseSegment.build().send(user, channel);
                });
    }
}
