package be.everbuild.autosense.gpio;

import be.everbuild.autosense.model.lightcontrol.LightControlModule;

public interface GpioDriver {
    LightControlModule createLightControlModule(String id, int busNumber, int address);
}
