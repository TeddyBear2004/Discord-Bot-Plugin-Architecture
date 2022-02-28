package com.wetterquarz.dialogapi;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Dialog {
    private final List<DialogSegment<Object>> dialogSegments;

    Dialog(Builder builder) {
        this.dialogSegments = builder.getDialogSegments();
    }

    public Flux<Object> send(User user, MessageChannel channel) {
        Flux<Object> empty = Flux.empty();

        for (DialogSegment<Object> dialogSegment : dialogSegments) {
            CompletableFuture<Object> future = new CompletableFuture<>();

            empty = empty.doOnNext(future::complete)
                    .concatWith(dialogSegment.execute(user, channel, Mono.fromFuture(future)));
        }
        return empty;
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

        public Builder text(String text) {
            return this.addSegment(new TextSegment(text));
        }

        public Builder text(EmbedCreateSpec embedCreateSpec) {
            return this.addSegment(new TextSegment(embedCreateSpec));
        }

        public Builder text(MessageCreateSpec messageCreateSpec) {
            return this.addSegment(new TextSegment(messageCreateSpec));
        }

        public Builder multipleChoiceSegment(@NotNull EmbedCreateSpec.Builder spec, @NotNull Function<String, String> description, @NotNull Map<ReactionEmoji, ?> options) {
            return this.addSegment(new MultipleChoiceSegment(spec, description, options));
        }

        public Builder addConditionalSegment(DialogSegment<Boolean> booleanDialogSegment, Builder ifTrue, Builder ifFalse) {
            this.dialogSegments.add(new ConditionalSegment(booleanDialogSegment, ifTrue, ifFalse));
            return this;
        }

        public Builder addQuestion(Function<String, String> description, EmbedCreateSpec.Builder builder) {
            return this.addSegment(new QuestionSegment(description, builder));
        }

        public Builder addQuestion(Function<String, String> description) {
            return this.addSegment(new QuestionSegment(description));
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
