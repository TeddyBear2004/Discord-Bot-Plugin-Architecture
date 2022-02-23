package com.wetterquarz.dialogapi;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class WaitSegment extends DialogSegment<Void> {
    private final long time;
    private final TemporalUnit unit;

    public WaitSegment(long time, TemporalUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    @Override
    public Mono<Void> execute(User user, MessageChannel channel, Mono<Object> lastResult) {
        return Mono
                .delay(Duration.of(time, unit))
                .then(Mono.empty());
    }
}
