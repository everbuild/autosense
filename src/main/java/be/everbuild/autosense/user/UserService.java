package be.everbuild.autosense.user;

import be.everbuild.autosense.except.Unauthorized;
import be.everbuild.autosense.vertx.UserData;
import com.google.common.base.Preconditions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

import java.util.List;

/**
 * Created by Evert on 25/07/15.
 */
public class UserService implements AuthProvider {
    private final UserStore store;

    public UserService(UserStore store) {
        this.store = store;
        if(store.getAllUsers().isEmpty()) {
            store.saveUser(new SimpleUser("admin", new PlainPass("admin"), Role.ADMIN));
        }
    }

    public List<SimpleUser> getAllUsers(SimpleUser requester) {
        if(requester.getRole() != Role.ADMIN) {
            throw new Unauthorized(String.format("User %s is not allowed to see all users", requester));
        }
        return store.getAllUsers();
    }

    public SimpleUser getUser(String name) {
        return store.getUser(name);
    }

    public void saveUser(SimpleUser requester, UserData data) {
        if(requester.getRole() == Role.ADMIN) {
            store.saveUser(new SimpleUser(
                    data.name,
                    new PlainPass(data.password),
                    data.role
            ));
        } else if(requester.getRole() == Role.NORMAL && requester.getName().equals(data.name)) {
            store.saveUser(new SimpleUser(
                    data.name,
                    new PlainPass(data.password),
                    requester.getRole()
            ));
        } else {
            throw new Unauthorized(String.format("User %s is not allowed to save data of user %s", requester, data.name));
        }
    }

    public void deleteUser(SimpleUser requester, String name) {
        if(requester.getRole() != Role.ADMIN) {
            throw new Unauthorized(String.format("User %s is not allowed delete other users", requester));
        } else if(requester.getName().equals(name)) {
            throw new Unauthorized(String.format("You can't delete yourself - user %s", requester));
        }
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String username = authInfo.getString("username");
        String password = authInfo.getString("password");
        SimpleUser user = getUser(username);
        if(user != null) {
            if(user.authenticate(new PlainPass(password))) {
                resultHandler.handle(Future.succeededFuture((User) user));
            } else {
                resultHandler.handle(Future.<User>failedFuture("wrong password"));
            }
        } else {
            resultHandler.handle(Future.<User>failedFuture("unknown user"));
        }
    }
}
