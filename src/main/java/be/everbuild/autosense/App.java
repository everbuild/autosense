package be.everbuild.autosense;

import be.everbuild.autosense.gpio.GpioDriverFactory;
import be.everbuild.autosense.identity.Role;
import be.everbuild.autosense.identity.SimpleIdentityManager;
import be.everbuild.autosense.lightcontrol.LightControlModule;
import be.everbuild.autosense.server.Server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class App {
    public static void main(String[] args) throws Exception {
        startGpio();
        startServer();
    }

    private static void startGpio() {
        String driverName = System.getProperty("driver", "real");
        GpioDriverFactory.create(driverName);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ExampleConfiguration configuration = new ExampleConfiguration(executorService);

        if("real".equals(driverName)) {
            LightControlModule lightControlModule = new LightControlModule(1, 0x20, executorService);
            configuration.bindModule(lightControlModule);
        }
    }

    private static void startServer() {
        SimpleIdentityManager identityManager = new SimpleIdentityManager().addAccount("pi", "pi", Role.ADMIN); // TODO

        Integer port = Integer.valueOf(System.getProperty("port", "80"));
        String host = System.getProperty("host", "localhost");
        new Server(host, port, identityManager).start();
    }
}
