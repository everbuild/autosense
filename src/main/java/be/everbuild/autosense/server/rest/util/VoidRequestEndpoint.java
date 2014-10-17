package be.everbuild.autosense.server.rest.util;

import io.undertow.server.HttpServerExchange;

import java.util.Map;

public abstract class VoidRequestEndpoint<ResponseType> implements Endpoint<Void, ResponseType> {

    @Override
    public ResponseType handle(HttpServerExchange exchange, Void request, Map<String, String> parameters) throws Exception {
        return handle(exchange, parameters);
    }

    public abstract ResponseType handle(HttpServerExchange exchange, Map<String, String> parameters) throws Exception;
}
