package be.everbuild.autosense.gpio.dummy;

import be.everbuild.autosense.gpio.GpioAddress;
import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.model.lightcontrol.BasicLightControlModule;
import be.everbuild.autosense.model.lightcontrol.LightControlModule;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Used for running the app outside of RPi. Simulates the behaviour of {@link be.everbuild.autosense.gpio.real.RealGpioDriver}.
 */
public class DummyGpioDriver extends GpioDriver {
    public static final String NAME = "dummy";

    @Override
    public LightControlModule createLightControlModule(GpioAddress address, ScheduledExecutorService executorService) {
        return new BasicLightControlModule(address);
    }
}
