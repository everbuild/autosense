package be.everbuild.autosense;

import be.everbuild.autosense.identity.Role;
import be.everbuild.autosense.identity.SimpleIdentityManager;
import be.everbuild.autosense.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        Integer port = Integer.valueOf(System.getProperty("port", "80"));
        String host = System.getProperty("host", "localhost");
        SimpleIdentityManager identityManager = new SimpleIdentityManager().addAccount("pi", "pi", Role.ADMIN); // TODO
        new Server(host, port, identityManager).start();
    }
}
