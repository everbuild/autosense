package be.everbuild.autosense.identity;

import java.security.Principal;

class SimplePrincipal implements Principal {
    private final String name;

    SimplePrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
