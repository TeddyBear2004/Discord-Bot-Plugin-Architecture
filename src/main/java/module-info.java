module com.wetterquarz.discordbot {
	exports com.wetterquarz.plugin;
	exports com.wetterquarz;
	exports com.wetterquarz.command;
	exports com.wetterquarz.database;
	exports com.wetterquarz.config;

	requires com.google.common;
	requires transitive discord4j.common;
	requires transitive discord4j.core;
	requires transitive discord4j.rest;
	requires org.jetbrains.annotations;
	requires org.reactivestreams;
	requires org.yaml.snakeyaml;
	requires r2dbc.spi;
	requires reactor.core;
}