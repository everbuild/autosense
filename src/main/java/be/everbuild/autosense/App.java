package be.everbuild.autosense;

import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.identity.Role;
import be.everbuild.autosense.identity.SimpleIdentityManager;
import be.everbuild.autosense.server.Server;

public class App {
    // TODO auto-reload config file -- https://gist.github.com/hindol-viz/394ebc553673e2cd0699
    // TODO extend config API to include lights etc
    // TODO consider using Netty (+ some web lib) in stead of undertow
    // TODO rest/ws API
    // TODO front-end app
    // TODO complete dummy GPIO driver (if even necessary, because frontend will be able to show state of lights, buttons...)
    // TODO mobile app (consider phonegap/cordova)

    public static void main(String[] args) throws Exception {
        GpioDriver gpioDriver = GpioDriver.create();
        AutomationContext context = new AutomationContext(gpioDriver);
        Configurator configurator = new Configurator(context);
        //startServer();
    }

    private static void startServer() {
        SimpleIdentityManager identityManager = new SimpleIdentityManager().addAccount("pi", "pi", Role.ADMIN); // TODO

        Integer port = Integer.valueOf(System.getProperty("port", "80"));
        String host = System.getProperty("host", "localhost");
        new Server(host, port, identityManager).start();
    }
}
