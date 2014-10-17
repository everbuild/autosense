package be.everbuild.autosense.identity;

import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.Set;

class SimpleAccount implements Account {
    private final Principal principal;
    private final Set<String> roles;

    public SimpleAccount(Principal principal, Set<String> roles) {
        this.principal = principal;
        this.roles = roles;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

}
