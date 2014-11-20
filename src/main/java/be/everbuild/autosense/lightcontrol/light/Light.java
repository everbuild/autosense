package be.everbuild.autosense.lightcontrol.light;

import java.util.HashSet;
import java.util.Set;

public class Light {
    private final String name;
    private boolean on = false;
    private final Set<LightListener> listeners = new HashSet<>();

    public Light(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void turnOn() {
        if(!on) {
            on = true;
            LightOnEvent event = new LightOnEvent(this, System.currentTimeMillis());
            for (LightListener listener : listeners) {
                listener.handleLightOn(event);
            }
        }
    }

    public void turnOff() {
        if(on) {
            on = false;
            LightOffEvent event = new LightOffEvent(this, System.currentTimeMillis());
            for (LightListener listener : listeners) {
                listener.handleLightOff(event);
            }
        }
    }

    public void toggle() {
        if(on) {
            turnOff();
        } else {
            turnOn();
        }
    }

    public boolean isOn() {
        return on;
    }

    public Light addListener(LightListener listener) {
        listeners.add(listener);
        return this;
    }

    public Light removeListener(LightListener listener) {
        listeners.remove(listener);
        return this;
    }
}
