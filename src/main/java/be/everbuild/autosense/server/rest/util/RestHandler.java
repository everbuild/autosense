package be.everbuild.autosense.server.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathTemplateHandler;
import io.undertow.util.Headers;

public class RestHandler<RequestType, ResponseType> implements HttpHandler {

    private final Class<RequestType> requestType;
    private final ObjectMapper objectMapper;
    private final Endpoint<RequestType, ResponseType> endpoint;

    public RestHandler(Class<RequestType> requestType, ObjectMapper objectMapper, Endpoint<RequestType, ResponseType> endpoint) {
        this.requestType = requestType;
        this.objectMapper = objectMapper;
        this.endpoint = endpoint;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        PathTemplateHandler.PathTemplateMatch match = exchange.getAttachment(PathTemplateHandler.PATH_TEMPLATE_MATCH);
        RequestType request = null;
        if(requestType != null) {
            request = objectMapper.readValue(exchange.getInputStream(), requestType);
        }
        ResponseType response = endpoint.handle(exchange, request, match.getParameters());
        if (response != null) {
            objectMapper.writeValue(exchange.getOutputStream(), response);
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        // TODO non-blocking way, e.g.: exchange.getRequestChannel().getReadSetter().set(new StringReadChannelListener() ...);
    }
}
