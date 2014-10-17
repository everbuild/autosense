package be.everbuild.autosense.server.rest.util;

import io.undertow.server.HttpServerExchange;

import java.util.Map;

public abstract class VoidResponseRestHandler<RequestType> implements Endpoint<RequestType, Void> {

    @Override
    public Void handle(HttpServerExchange exchange, RequestType request, Map<String, String> parameters) throws Exception {
        handleVoid(exchange, request, parameters);
        return null;
    }

    public abstract void handleVoid(HttpServerExchange exchange, RequestType request, Map<String, String> parameters) throws Exception;
}
