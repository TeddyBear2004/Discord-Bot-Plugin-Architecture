package com.wetterquarz.database;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;

import io.r2dbc.spi.*;
import reactor.core.publisher.*;


public class DatabaseManager {
    private final ConnectionFactory factory;
    private final Semaphore simulConnections;

    public DatabaseManager(ConnectionFactoryOptions options){
        this.factory = ConnectionFactories.get(options);
        this.simulConnections = new Semaphore(10, true);
    }
    
    private Mono<? extends Result> runTransaction(Function<Connection, Mono<? extends Result>> transaction) {
    	return Mono.create(callback -> {
    		new Thread(() -> {
				simulConnections.acquireUninterruptibly();
	    		Mono.from(this.factory.create())
	    			.doOnError(ignore -> simulConnections.release())
	    			.flatMap(c -> transaction.apply(c)
	    					.doOnTerminate(() -> Mono.from(c.close()).doOnTerminate(() -> simulConnections.release()).subscribe()))
	    			.doOnError(t -> callback.error(t))
	    			.doOnSuccess(r -> callback.success(r))
	    			.subscribe();
    		});
    	});
    }
    
    public Mono<? extends Result> executeTransaction(Function<Connection, Mono<? extends Result>> function){
        return runTransaction(c -> {
	    	c.beginTransaction();
	        return function.apply(c).delayUntil(ignore -> c.commitTransaction()).cache();
        });
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
