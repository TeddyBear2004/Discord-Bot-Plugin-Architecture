package com.wetterquarz.dialogapi;

import com.wetterquarz.DiscordClient;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MultipleChoiceSegment <T> extends DialogSegment<T> {
    private static boolean eventsStarted = false;

    private final static ReactionEmoji WHITE_CHECK_MARK = ReactionEmoji.unicode("✅");
    private final static ReactionEmoji X = ReactionEmoji.unicode("❌");

    private static final Map<Tuple2<User, MessageChannel>, MultiChoiceSegmentInformation<?>> CURRENT_ACTIONS = new HashMap<>();

    private final Map<ReactionEmoji, T> options;
    private final EmbedCreateSpec.Builder spec;
    private final Function<String, String> description;

    public MultipleChoiceSegment(@NotNull EmbedCreateSpec.Builder spec, @NotNull Function<String, String> description, @NotNull Map<ReactionEmoji, T> options) {
        if (options.size() > 18)
            throw new IllegalArgumentException("Size can't be more than 18");

        if (options.containsKey(WHITE_CHECK_MARK) || options.containsKey(X))
            throw new IllegalArgumentException("White check mark and X are reserved");

        this.spec = spec;
        this.description = description;
        this.options = options;

        if (!eventsStarted) {
            eventsStarted = true;
            Disposable reactionDisposable = DiscordClient.getDiscordClient().getEventDispatcher().on(ReactionAddEvent.class).flatMap(MultipleChoiceSegment::onReaction)
                    .subscribe();

            Disposable reactionRemoveDisposable = DiscordClient.getDiscordClient().getEventDispatcher().on(ReactionRemoveEvent.class).flatMap(MultipleChoiceSegment::onReaction)
                    .subscribe();

            DiscordClient.getDiscordClient().addDisposable(reactionDisposable);
            DiscordClient.getDiscordClient().addDisposable(reactionRemoveDisposable);
        }
    }

    @Override
    public Mono<T> execute(User user, MessageChannel channel, Mono<Object> lastResult) {
        Tuple2<User, MessageChannel> tuple2 = Tuples.of(user, channel);
        if (CURRENT_ACTIONS.containsKey(tuple2)) return Mono.error(new IllegalStateException("Already in use"));

        CompletableFuture<T> future = new CompletableFuture<>();
        CURRENT_ACTIONS.put(tuple2, new MultiChoiceSegmentInformation<T>().setCompletableFuture(future).setEmbedCreateSpecBuilder(spec).setOptions(options).setDescription(description));

        return channel.createMessage(generateEmbed(tuple2))
                .doOnNext(message -> CURRENT_ACTIONS.get(tuple2).setMessage(message))
                .flatMap(message -> message.addReaction(WHITE_CHECK_MARK)
                        .then(message.addReaction(X))
                        .then(Flux.fromIterable(options.keySet())
                                .flatMap(message::addReaction)
                                .then(Mono.fromFuture(future))));
    }

    private static @NotNull EmbedCreateSpec generateEmbed(Tuple2<User, MessageChannel> tuple2) {
        return CURRENT_ACTIONS.get(tuple2).generateEmbed();
    }

    private static Mono<Void> onReaction(ReactionAddEvent event) {
        return Mono.zip(event.getUser(), event.getChannel())
                .flatMap(tuple2 ->
                        onReaction(tuple2,
                                !event.getUserId().equals(tuple2.getT1().getClient().getSelfId()),
                                event.getEmoji(),
                                event.getMessageId(),
                                true));
    }

    private static Mono<Void> onReaction(ReactionRemoveEvent event) {
        return Mono.zip(event.getUser(), event.getChannel())
                .flatMap(tuple2 ->
                        onReaction(tuple2,
                                !event.getUserId().equals(tuple2.getT1().getClient().getSelfId()),
                                event.getEmoji(),
                                event.getMessageId(),
                                false));
    }

    private static <T> Mono<Void> onReaction(Tuple2<User, MessageChannel> tuple2, boolean filter, ReactionEmoji emoji, Snowflake messageId, boolean isAdd) {
        MultiChoiceSegmentInformation<T> multiChoiceSegmentInformation1 = (MultiChoiceSegmentInformation<T>) CURRENT_ACTIONS.get(tuple2);
        if (multiChoiceSegmentInformation1 != null && multiChoiceSegmentInformation1.getMessage().getId().equals(messageId)) {
            if (filter) {
                if (emoji.equals(WHITE_CHECK_MARK)) {
                    ((CompletableFuture<T>) CURRENT_ACTIONS.get(tuple2).getCompletableFuture()).complete((T) CURRENT_ACTIONS.get(tuple2).getCurrentlySelected());
                    CURRENT_ACTIONS.remove(tuple2);
                }else if (emoji.equals(X)) {
                    MultiChoiceSegmentInformation<T> multiChoiceSegmentInformation = (MultiChoiceSegmentInformation<T>) CURRENT_ACTIONS.get(tuple2);
                    multiChoiceSegmentInformation.getCompletableFuture().completeExceptionally(new IllegalStateException("Cancelled"));
                    CURRENT_ACTIONS.remove(tuple2);

                    return multiChoiceSegmentInformation
                            .getMessage()
                            .edit()
                            .withEmbeds(multiChoiceSegmentInformation.generateEmbed())
                            .then();
                }else{
                    T o = multiChoiceSegmentInformation1.getOptions().get(emoji);
                    if (o != null) {
                        if (isAdd)
                            multiChoiceSegmentInformation1.setCurrentlySelected(o);
                        else
                            multiChoiceSegmentInformation1.setCurrentlySelected(null);

                        return multiChoiceSegmentInformation1
                                .getMessage()
                                .edit()
                                .withEmbeds(multiChoiceSegmentInformation1.generateEmbed())
                                .then();
                    }
                }
            }
        }
        return Mono.empty();
    }

    private static class MultiChoiceSegmentInformation<T> {
        private Message message;
        private Function<String, String> description;
        private T currentlySelected;
        private CompletableFuture<T> completableFuture;
        private EmbedCreateSpec.Builder embedCreateSpecBuilder;
        private Map<ReactionEmoji, T> options;

        public @NotNull EmbedCreateSpec generateEmbed() {
            return embedCreateSpecBuilder
                    .description(getDescription())
                    .build();
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public @NotNull MultiChoiceSegmentInformation<T> setEmbedCreateSpecBuilder(EmbedCreateSpec.Builder embedCreateSpecBuilder) {
            this.embedCreateSpecBuilder = embedCreateSpecBuilder;
            return this;
        }

        public MultiChoiceSegmentInformation<T> setOptions(Map<ReactionEmoji, T> options) {
            this.options = options;
            return this;
        }

        public @NotNull MultiChoiceSegmentInformation<T> setDescription(Function<String, String> description) {
            this.description = description;
            return this;
        }

        public void setCurrentlySelected(T currentlySelected) {
            this.currentlySelected = currentlySelected;
        }

        public T getCurrentlySelected() {
            return currentlySelected;
        }

        public @NotNull MultiChoiceSegmentInformation<T> setCompletableFuture(CompletableFuture<T> completableFuture) {
            this.completableFuture = completableFuture;
            return this;
        }

        public Message getMessage() {
            return message;
        }

        public Map<ReactionEmoji, T> getOptions() {
            return options;
        }

        public String getDescription() {
            return description.apply(currentlySelected != null ? currentlySelected.toString() : "");
        }

        public CompletableFuture<T> getCompletableFuture() {
            return completableFuture;
        }
    }
}
