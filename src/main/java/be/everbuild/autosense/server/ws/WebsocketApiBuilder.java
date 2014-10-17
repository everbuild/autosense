package be.everbuild.autosense.server.ws;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;

public class WebsocketApiBuilder {

    public HttpHandler build() {
        return Handlers.websocket(new InternalWebSocketHandler());
    }
}
