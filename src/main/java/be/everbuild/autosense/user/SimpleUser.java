package be.everbuild.autosense.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

/**
 * Created by Evert on 25/07/15.
 */
public class SimpleUser extends AbstractUser {
    private final String name;
    private HashedPass password;
    private Role role;
    private AuthProvider authProvider;

    public SimpleUser(String name, PlainPass password, Role role) {
        this(name, password.hash(), role);
    }

    public SimpleUser(String name, HashedPass password, Role role) {
        this.name = Preconditions.checkNotNull(name);
        this.password = Preconditions.checkNotNull(password);
        this.role = Preconditions.checkNotNull(role);
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public HashedPass getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        Role requiredRole = Role.valueOf(permission);
        resultHandler.handle(Future.succeededFuture(requiredRole == role));
    }

    @Override
    public JsonObject principal() {
        return new JsonObject().put("username", name);
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public boolean authenticate(PlainPass password) {
        return this.password.equals(password.hash());
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, role.name());
    }
}
