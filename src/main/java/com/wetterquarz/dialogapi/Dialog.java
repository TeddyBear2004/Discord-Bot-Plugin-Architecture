package com.wetterquarz.dialogapi;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Dialog {
    private final List<DialogSegment<Object>> dialogSegments;

    Dialog(Builder builder) {
        this.dialogSegments = builder.getDialogSegments();
    }

    public Mono<List<Object>> send(User user, MessageChannel channel) {
        Mono<?> send = Mono.empty();
        List<Object> results = new ArrayList<>();

        for (DialogSegment<Object> dialogSegment : dialogSegments) {
            CompletableFuture<Object> future = new CompletableFuture<>();

            send = send
                    .doOnSuccess(future::complete)
                    .then(dialogSegment.execute(user, channel, Mono.fromFuture(future)))
                    .doOnNext(results::add);
        }

        return send.then(Mono.just(results));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<DialogSegment<Object>> dialogSegments = new ArrayList<>();

        public Builder addSegment(DialogSegment<?> segment) {
            this.dialogSegments.add((DialogSegment<Object>) segment);
            return this;
        }

        public Builder addConditionalSegment(DialogSegment<Boolean> booleanDialogSegment, Builder ifTrue, Builder ifFalse) {
            this.dialogSegments.add(new ConditionalSegment(booleanDialogSegment, ifTrue, ifFalse));
            return this;
        }

        public Builder wait(int time, TemporalUnit unit) {
            return this.addSegment(new WaitSegment(time, unit));
        }

        public Dialog build() {
            return new Dialog(this);
        }

        List<DialogSegment<Object>> getDialogSegments() {
            return this.dialogSegments;
        }
    }
}
