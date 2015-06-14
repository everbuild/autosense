package be.everbuild.autosense.gpio.dummy;

import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.model.lightcontrol.BasicLightControlModule;
import be.everbuild.autosense.model.lightcontrol.LightControlModule;

/**
 * Used for running the app outside of RPi. Simulates the behaviour of {@link be.everbuild.autosense.gpio.real.RealGpioDriver}.
 */
public class DummyGpioDriver implements GpioDriver {
    @Override
    public LightControlModule createLightControlModule(String id, int busNumber, int address) {
        return new BasicLightControlModule(id);
    }
}
