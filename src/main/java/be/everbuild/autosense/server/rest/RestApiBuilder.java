package be.everbuild.autosense.server.rest;

import be.everbuild.autosense.server.rest.model.Test;
import be.everbuild.autosense.server.rest.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathTemplateHandler;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;

import java.util.HashMap;
import java.util.Map;

public class RestApiBuilder {
    private final ObjectMapper objectMapper;
    private final PathTemplateHandler pathHandler = Handlers.pathTemplate();
    private final Map<String, MethodDispatcherHandler> dispatcherHandlers = new HashMap<>();

    public RestApiBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HttpHandler build() {

        endpoint(Methods.GET, "/test/{param1}", new VoidRequestEndpoint<Test>() {
            @Override
            public Test handle(HttpServerExchange exchange, Map<String, String> parameters) throws Exception {
                return new Test("reply");
            }
        });
        endpoint(Methods.POST, "/test/{param1}", Test.class, new Endpoint<Test, Test>() {
            @Override
            public Test handle(HttpServerExchange exchange, Test request, Map<String, String> parameters) throws Exception {
                return new Test("reply to: " + request.getMessage() + " with param: " + parameters.get("param1"));
            }
        });

        return new BlockingHandler(pathHandler);
    }

    private <RequestType, ResponseType> void endpoint(HttpString method, String path, Class<RequestType> requestType, Endpoint<RequestType, ResponseType> endpoint) {
        MethodDispatcherHandler handler = dispatcherHandlers.get(path);
        if(handler == null) {
            handler = new MethodDispatcherHandler();
            pathHandler.add(path, handler);
            dispatcherHandlers.put(path, handler);
        }
        handler.add(method, new RestHandler<>(requestType, objectMapper, endpoint));
    }

    private <ResponseType> void endpoint(HttpString method, String path, VoidRequestEndpoint<ResponseType> endpoint) {
        endpoint(method, path, null, endpoint);
    }

    private void endpoint(HttpString method, String path, VoidEndpoint endpoint) {
        endpoint(method, path, null, endpoint);
    }
}
