package com.wetterquarz.dialogapi;

import com.wetterquarz.DiscordClient;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class QuestionSegment extends DialogSegment<String> {
    private final Function<String, String> description;
    private final EmbedCreateSpec.Builder builder;

    private static boolean eventsStarted = false;
    private static final Map<Tuple2<User, MessageChannel>, QuestionSegmentInformation> CURRENT_ACTIONS = new HashMap<>();

    private final static ReactionEmoji WHITE_CHECK_MARK = ReactionEmoji.unicode("✅");
    private final static ReactionEmoji RELOAD = ReactionEmoji.unicode("♻");
    private final static ReactionEmoji X = ReactionEmoji.unicode("❌");

    public QuestionSegment(Function<String, String> description) {
        this(description, EmbedCreateSpec.builder());
    }

    public QuestionSegment(Function<String, String> description, EmbedCreateSpec.Builder builder) {
        this.description = description;
        this.builder = builder;

        if (!eventsStarted) {
            eventsStarted = true;
            Disposable messageDisposable = DiscordClient.getDiscordClient().getEventDispatcher().on(MessageCreateEvent.class).flatMap(QuestionSegment::onMessage)
                    .subscribe();
            Disposable reactionDisposable = DiscordClient.getDiscordClient().getEventDispatcher().on(ReactionAddEvent.class).flatMap(QuestionSegment::onReaction)
                    .subscribe();

            Disposable reactionRemoveDisposable = DiscordClient.getDiscordClient().getEventDispatcher().on(ReactionRemoveEvent.class).flatMap(QuestionSegment::onReaction)
                    .subscribe();

            DiscordClient.getDiscordClient().addDisposable(messageDisposable);
            DiscordClient.getDiscordClient().addDisposable(reactionDisposable);
            DiscordClient.getDiscordClient().addDisposable(reactionRemoveDisposable);
        }
    }

    @Override
    public Mono<String> execute(User user, MessageChannel channel, Mono<Object> lastResult) {
        if (CURRENT_ACTIONS.containsKey(Tuples.of(user, channel)))
            return Mono.error(new IllegalStateException("Already in use"));

        CompletableFuture<String> future = new CompletableFuture<>();
        QuestionSegmentInformation questionSegmentInformation = new QuestionSegmentInformation().setStringBuilder(new StringBuilder()).setCompletableFuture(future).setDescription(description).setEmbedCreateSpecBuilder(builder);
        CURRENT_ACTIONS.put(Tuples.of(user, channel), questionSegmentInformation);

        return channel.createMessage(questionSegmentInformation.generateEmbed())
                .doOnNext(questionSegmentInformation::setMessage)
                .flatMap(message -> message.addReaction(WHITE_CHECK_MARK)
                        .then(message.addReaction(RELOAD))
                        .then(message.addReaction(X))
                        .then(Mono.fromFuture(future)));
    }

    private static Mono<Void> onMessage(MessageCreateEvent messageCreateEvent) {
        Message message = messageCreateEvent.getMessage();
        Optional<User> author = message.getAuthor();

        if (author.isPresent()) {
            if (author.get().getId().asLong() != DiscordClient.getDiscordClient().getGatewayDiscordClient().getSelfId().asLong())
                return message.getChannel()
                        .flatMap(channel -> {
                            QuestionSegmentInformation questionSegmentInformation = CURRENT_ACTIONS.get(Tuples.of(author.get(), messageCreateEvent.getMessage().getChannel()));
                            if (questionSegmentInformation != null) {
                                StringBuilder stringBuilder = questionSegmentInformation.getStringBuilder();
                                if (stringBuilder.length() > 0)
                                    stringBuilder.append("\n");

                                stringBuilder.append(messageCreateEvent.getMessage().getContent());

                                return questionSegmentInformation
                                        .getMessage()
                                        .flatMap(message1 -> message1.edit().withEmbeds(questionSegmentInformation.generateEmbed()));
                            }
                            return Mono.empty();
                        })
                        .then();
        }

        return Mono.empty();
    }

    private static Mono<Void> onReaction(ReactionAddEvent event) {
        return Mono.zip(event.getUser(), event.getChannel())
                .flatMap(tuple2 ->
                        onReaction(tuple2,
                                !event.getUserId().equals(tuple2.getT1().getClient().getSelfId()),
                                event.getEmoji(),
                                event.getMessageId()));
    }

    private static Mono<Void> onReaction(ReactionRemoveEvent event) {
        return Mono.zip(event.getUser(), event.getChannel())
                .flatMap(tuple2 ->
                        onReaction(tuple2,
                                !event.getUserId().equals(tuple2.getT1().getClient().getSelfId()),
                                event.getEmoji(),
                                event.getMessageId()));
    }

    private static Mono<Void> onReaction(Tuple2<User, MessageChannel> tuple2, boolean filter, ReactionEmoji emoji, Snowflake messageId) {
        QuestionSegmentInformation questionSegmentInformation = CURRENT_ACTIONS.get(tuple2);
        if (questionSegmentInformation != null) {
            return questionSegmentInformation.getMessage()
                    .filter(message -> message.getId().equals(messageId))
                    .flatMap(message -> {
                        if (filter) {
                            if (emoji.equals(WHITE_CHECK_MARK)) {
                                questionSegmentInformation.getCompletableFuture().complete(questionSegmentInformation.getStringBuilder().toString());
                                CURRENT_ACTIONS.remove(tuple2);
                            }else if (emoji.equals(RELOAD)) {
                                questionSegmentInformation.getStringBuilder().setLength(0);

                                return message.edit().withEmbeds(questionSegmentInformation.generateEmbed())
                                        .then();
                            }else if (emoji.equals(X)) {
                                questionSegmentInformation.getCompletableFuture().completeExceptionally(new IllegalStateException("Cancelled"));
                                CURRENT_ACTIONS.remove(tuple2);

                                return message.edit().withEmbeds(questionSegmentInformation.generateEmbed())
                                        .then();
                            }
                        }
                        return Mono.empty();
                    });

        }
        return Mono.empty();
    }

    private static class QuestionSegmentInformation {
        private final CompletableFuture<Message> message = new CompletableFuture<>();
        private Function<String, String> description;
        private StringBuilder stringBuilder;
        CompletableFuture<String> completableFuture;
        private EmbedCreateSpec.Builder embedCreateSpecBuilder;

        public @NotNull EmbedCreateSpec generateEmbed() {
            return embedCreateSpecBuilder
                    .description(getDescription())
                    .build();
        }

        public void setMessage(Message message) {
            if (this.message.isDone()) throw new IllegalStateException("Message already set");
            this.message.complete(message);
        }

        public @NotNull QuestionSegmentInformation setEmbedCreateSpecBuilder(EmbedCreateSpec.Builder embedCreateSpecBuilder) {
            this.embedCreateSpecBuilder = embedCreateSpecBuilder;
            return this;
        }

        public @NotNull QuestionSegmentInformation setDescription(Function<String, String> description) {
            this.description = description;
            return this;
        }

        public @NotNull QuestionSegmentInformation setStringBuilder(StringBuilder stringBuilder) {
            this.stringBuilder = stringBuilder;
            return this;
        }

        public @NotNull QuestionSegmentInformation setCompletableFuture(CompletableFuture<String> completableFuture) {
            this.completableFuture = completableFuture;
            return this;
        }

        public Mono<Message> getMessage() {
            return Mono.fromFuture(message);
        }

        public String getDescription() {
            return description.apply(getStringBuilder().toString().isEmpty() ? " " : getStringBuilder().toString());
        }

        public StringBuilder getStringBuilder() {
            return stringBuilder;
        }

        public CompletableFuture<String> getCompletableFuture() {
            return completableFuture;
        }
    }
}
