package com.wetterquarz.database;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import io.r2dbc.spi.*;
import reactor.core.publisher.*;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;


public class DatabaseManager {
    private final ConnectionFactory factory;

    public DatabaseManager(ConnectionFactoryOptions options){
        this.factory = ConnectionFactories.get(options);
        //Scheduler s = Schedulers.newSingle("DBConWorker", true);
        Flux<Tuple2<Function<Connection, Mono<? extends Result>>, MonoSink<Result>>> transactionQueue = Flux.create(emitter -> this.transactionQueue = emitter);
        transactionQueue.window(Duration.ofSeconds(60)).flatMap(win -> {
        	//return win.next().flatMap(first -> {
    			return Mono.from(this.factory.create()).cache()/*.flatMap(con -> {
    				return first.getT1().apply(con).doOnSuccess((Result result) -> first.getT2().success(result)).thenReturn(con);
    			})*/.flatMap(con -> {
    				return win.flatMap(tran -> {
						return tran.getT1().apply(con).doOnSuccess((Result result) -> tran.getT2().success(result));
    				}).then(Mono.just(con));
    			}).doOnSuccess(c -> c.close())
    			.doOnError(t -> t.printStackTrace());
        	//});
        }).subscribe();
    }
    
    private FluxSink<Tuple2<Function<Connection, Mono<? extends Result>>, MonoSink<Result>>> transactionQueue;
    
    private Mono<? extends Result> runTransaction(Function<Connection, Mono<? extends Result>> transaction) {
    	if(transactionQueue == null) return Mono.error(new IllegalStateException("Transaction Queue not ready"));
    	return Mono.create(callback -> transactionQueue.next(Tuples.of(transaction, callback)));
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
