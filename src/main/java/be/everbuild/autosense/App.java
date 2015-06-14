package be.everbuild.autosense;

import be.everbuild.autosense.config.Configuration;
import be.everbuild.autosense.config.Configurator;
import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.gpio.GpioDriverFactory;
import be.everbuild.autosense.identity.Role;
import be.everbuild.autosense.identity.SimpleIdentityManager;
import be.everbuild.autosense.model.Model;
import be.everbuild.autosense.server.Server;
import com.google.gson.Gson;

import java.io.FileReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class App {
    public static void main(String[] args) throws Exception {

        Gson gson = new Gson();

        Configuration config = gson.fromJson(new FileReader(System.getProperty("config")), Configuration.class);

        startGpio(config);
        //startServer();
    }

    private static void startGpio(Configuration config) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        String driverName = System.getProperty("driver", "real");
        GpioDriver gpioDriver = GpioDriverFactory.create(driverName, executorService);

        Configurator configurator = new Configurator(gpioDriver, executorService);

        Model model = configurator.apply(config);
    }

    private static void startServer() {
        SimpleIdentityManager identityManager = new SimpleIdentityManager().addAccount("pi", "pi", Role.ADMIN); // TODO

        Integer port = Integer.valueOf(System.getProperty("port", "80"));
        String host = System.getProperty("host", "localhost");
        new Server(host, port, identityManager).start();
    }
}
