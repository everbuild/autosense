package be.everbuild.autosense.server.rest.util;

import com.google.common.base.Preconditions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import java.util.HashMap;
import java.util.Map;

public class MethodDispatcherHandler implements HttpHandler {
    private final Map<HttpString, HttpHandler> handlers = new HashMap<>();

    public MethodDispatcherHandler add(HttpString method, HttpHandler handler) {
        Preconditions.checkState(!handlers.containsKey(method), "Method %s already defined!", method);
        handlers.put(method, handler);
        return this;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpHandler handler = handlers.get(exchange.getRequestMethod());
        if(handler != null) {
            handler.handleRequest(exchange);
        } else {
            exchange.setResponseCode(StatusCodes.METHOD_NOT_ALLOWED);
            exchange.endExchange();
        }
    }
}
