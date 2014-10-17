package be.everbuild.autosense.identity;

import com.google.common.base.Enums;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleIdentityManager implements IdentityManager {

    private final Map<String, Login> logins = new HashMap<>();

    @Override
    public Account verify(Account account) {
        throw null;
    }

    @Override
    public Account verify(final String id, Credential credential) {
        if(id != null && credential instanceof PasswordCredential) {
            Login login = logins.get(id.toLowerCase());
            if (login != null) {
                String password = new String(((PasswordCredential) credential).getPassword());
                if (login.password.equals(password)) {
                    return login.account;
                }
            }
        }
        return null;
    }

    @Override
    public Account verify(Credential credential) {
        throw null;
    }

    public SimpleIdentityManager addAccount(String name, String password, Role... roles) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(password);
        Set<String> roleSet = ImmutableSet.copyOf(Iterables.transform(Arrays.asList(roles), Enums.stringConverter(Role.class).reverse()));
        SimpleAccount account = new SimpleAccount(new SimplePrincipal(name), roleSet);
        Login login = new Login(account, password);
        logins.put(name.toLowerCase(), login);
        return this;
    }

    private static class Login {
        final Account account;
        final String password;

        Login(Account account, String password) {
            this.account = account;
            this.password = password;
        }
    }
}
