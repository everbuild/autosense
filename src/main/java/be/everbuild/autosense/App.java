package be.everbuild.autosense;

import be.everbuild.autosense.gpio.GpioDriverFactory;
import be.everbuild.autosense.identity.Role;
import be.everbuild.autosense.identity.SimpleIdentityManager;
import be.everbuild.autosense.server.Server;

public class App {
    public static void main(String[] args) throws Exception {
        SimpleIdentityManager identityManager = new SimpleIdentityManager().addAccount("pi", "pi", Role.ADMIN); // TODO

        Integer port = Integer.valueOf(System.getProperty("port", "80"));
        String host = System.getProperty("host", "localhost");
        new Server(host, port, identityManager).start();

        String driverName = System.getProperty("driver", "real");
        GpioDriverFactory.create(driverName);
    }
}
