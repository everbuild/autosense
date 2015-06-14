package be.everbuild.autosense.gpio;

import be.everbuild.autosense.gpio.dummy.DummyGpioDriver;
import be.everbuild.autosense.gpio.real.RealGpioDriver;

import java.util.concurrent.ScheduledExecutorService;

public class GpioDriverFactory {
    public static GpioDriver create(String name, ScheduledExecutorService executorService) {
        switch (name) {
            case "real":
                return new RealGpioDriver(executorService);
            case "dummy":
                return new DummyGpioDriver();
            default:
                throw new IllegalArgumentException("Unknown driver name: " + name);
        }
    }
}
