package be.everbuild.autosense.gpio;

import be.everbuild.autosense.gpio.dummy.DummyGpioDriver;
import be.everbuild.autosense.gpio.real.RealGpioDriver;
import be.everbuild.autosense.model.lightcontrol.LightControlModule;

import java.util.concurrent.ScheduledExecutorService;

public abstract class GpioDriver {

    public abstract LightControlModule createLightControlModule(GpioAddress address, ScheduledExecutorService executorService);

    public static GpioDriver create() {
        return create(System.getProperty("driver", RealGpioDriver.NAME));
    }

    public static GpioDriver create(String name) {
        switch (name) {
            case RealGpioDriver.NAME:
                return new RealGpioDriver();
            case DummyGpioDriver.NAME:
                return new DummyGpioDriver();
            default:
                throw new IllegalArgumentException("Unknown driver name: " + name);
        }
    }
}
