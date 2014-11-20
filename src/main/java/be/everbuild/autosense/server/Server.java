package be.everbuild.autosense.server;

import be.everbuild.autosense.Slf4jLogReceiver;
import be.everbuild.autosense.server.rest.RestApiBuilder;
import be.everbuild.autosense.server.ws.WebsocketApiBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Undertow undertow;
    private final IdentityManager identityManager;

    public Server(String host, int port, IdentityManager identityManager) {
        this.identityManager = identityManager;
        LOG.info("Creating server on {}:{}", host, port);
        // TODO ssl?
        this.undertow = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(initHandlers())
                .build();
    }

    public void start() {
        undertow.start();
        LOG.info("Server started");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Stopping Undertow");
                undertow.stop();
            }
        });
    }

    private HttpHandler initHandlers() {
        HttpHandler websocketHandler = new WebsocketApiBuilder().build();
        HttpHandler restHandler = new RestApiBuilder(objectMapper).build();
        ResourceHandler resourceHandler = Handlers.resource(new ClassPathResourceManager(ClassLoader.getSystemClassLoader(), "site"));

        PathHandler pathHandler = Handlers.path()
                .addPrefixPath("/api/ws", secure(websocketHandler))
                .addPrefixPath("/api/rest", secure(restHandler))
                .addPrefixPath("/", resourceHandler);

        AccessLogHandler accessLogHandler = new AccessLogHandler(pathHandler, new Slf4jLogReceiver(), "common", ClassLoader.getSystemClassLoader());

        return Handlers.gracefulShutdown(accessLogHandler);
    }

    private HttpHandler secure(HttpHandler handler) {
        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        handler = new AuthenticationMechanismsHandler(handler, ImmutableList.of((AuthenticationMechanism) new BasicAuthenticationMechanism("Control AutoSense")));
        handler = new SecurityInitialHandler(AuthenticationMode.CONSTRAINT_DRIVEN, identityManager, handler);
        return handler;
    }
}
