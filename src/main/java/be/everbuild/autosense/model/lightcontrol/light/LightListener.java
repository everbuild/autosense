package be.everbuild.autosense.model.lightcontrol.light;

import java.util.EventListener;

public interface LightListener extends EventListener {
    void handleLightOn(LightOnEvent event);
    void handleLightOff(LightOffEvent event);
}
