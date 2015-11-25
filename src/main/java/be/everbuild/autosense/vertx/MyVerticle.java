package be.everbuild.autosense.vertx;

import be.everbuild.autosense.user.SimpleUser;
import be.everbuild.autosense.user.UserService;
import be.everbuild.autosense.user.UserStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.util.List;

/**
 * Created by Evert on 24/07/15.
 */
public class MyVerticle extends AbstractVerticle {

    public static final String USERS_PATH = "/app/users";

    @Override
    public void start() throws Exception {
        UserStore userStore = new UserStore();
        UserService userService = new UserService(userStore);

        AuthHandler basicAuthHandler = BasicAuthHandler.create(userService);

        Router router = Router.router(vertx);

        router.route()
                .handler(LoggerHandler.create())
                .handler(CookieHandler.create())
                .handler(SessionHandler.create(LocalSessionStore.create(vertx)))
                .handler(UserSessionHandler.create(userService))
                .handler(BodyHandler.create());

        // TODO
        router.route("/private/*").handler(basicAuthHandler); // All requests to paths starting with '/private/' will be protected

        Router userRouter = getUsersRouter(userService);
        router.mountSubRouter(USERS_PATH, userRouter);

        router.get("logout").handler(routingContext -> {
            routingContext.clearUser();
            routingContext.session().destroy();
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private Router getUsersRouter(UserService userService) {
        Router router = Router.router(vertx);
        router.get().handler(ctx -> {
            SimpleUser requester = getRequester(ctx);
            List<SimpleUser> users = userService.getAllUsers(requester);
            end(ctx, users);
        });
        router.get("/current").handler(ctx -> {
            SimpleUser requester = getRequester(ctx);
            end(ctx, requester);
        });
        router.put().handler(ctx -> {
            UserData data = Json.decodeValue(ctx.getBodyAsString(), UserData.class);
            SimpleUser requester = getRequester(ctx);
            userService.saveUser(requester, data);
            ctx.response().setStatusCode(201).putHeader("location", USERS_PATH + "/" + data.name).end();
        });
        router.delete("/:userName").handler(ctx -> {
            SimpleUser requester = getRequester(ctx);
            String userName = ctx.request().getParam("userName");
            userService.deleteUser(requester, userName);
            ctx.response().setStatusCode(200).end();
        });
        return router;
    }

    private SimpleUser getRequester(RoutingContext ctx) {
        return (SimpleUser)ctx.user();
    }

    private void end(RoutingContext ctx, Object response) {
        String json = Json.encodePrettily(response);
        ctx.response().putHeader("content-type", "application/json").end(json);
    }

    @Override
    public void stop() throws Exception {
    }
}
