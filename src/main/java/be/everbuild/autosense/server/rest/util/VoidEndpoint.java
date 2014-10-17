package be.everbuild.autosense.server.rest.util;

import io.undertow.server.HttpServerExchange;

import java.util.Map;

public abstract class VoidEndpoint implements Endpoint<Void, Void> {

    @Override
    public Void handle(HttpServerExchange exchange, Void request, Map<String, String> parameters) throws Exception {
        handle(exchange, parameters);
        return null;
    }

    public abstract void handle(HttpServerExchange exchange, Map<String, String> parameters) throws Exception;
}
