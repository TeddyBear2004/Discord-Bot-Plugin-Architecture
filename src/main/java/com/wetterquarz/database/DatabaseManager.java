package com.wetterquarz.database;

import io.r2dbc.spi.*;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;


public class DatabaseManager {
    private final ConnectionFactory factory;

    public DatabaseManager(ConnectionFactoryOptions options){
        this.factory = ConnectionFactories.get(options);

    }

    public Mono<? extends Result> executeTransaction(Function<Connection, Mono<? extends Result>> function){
        final Mono<Connection> connectionMono = Mono.<Connection>from(this.factory.create()).doOnNext(Connection::beginTransaction);

        Mono<? extends Result> res = connectionMono.flatMap(function).cache();

        return res.delayUntil(ignore1 ->
                res.flatMap(ignore -> connectionMono
                        .doOnNext(Connection::commitTransaction)
                        .doOnNext(Connection::close)));
    }

    public Mono<? extends Result> executeSQL(String sql){
        return executeTransaction(connection -> Mono.from(connection.createStatement(sql).execute()));
    }

    public Mono<? extends Result> executeSQL(String sql, Consumer<Statement> statement){
        if(statement == null)
            return executeSQL(sql);

        return executeTransaction(connection -> {
            Statement statement1 = connection.createStatement(sql);

            statement.accept(statement1);
            return Mono.from(statement1.execute());
        });
    }
}
