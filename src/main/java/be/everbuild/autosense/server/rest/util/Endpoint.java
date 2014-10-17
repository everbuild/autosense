package be.everbuild.autosense.server.rest.util;

import io.undertow.server.HttpServerExchange;

import java.util.Map;

public interface Endpoint<RequestType, ResponseType> {
    ResponseType handle(HttpServerExchange exchange, RequestType request, Map<String, String> parameters) throws Exception;
}
