package be.everbuild.autosense.model.lightcontrol.light;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Light {
    private static final Logger LOG = LoggerFactory.getLogger(Light.class);

    private final String id;
    private final String name;
    private boolean on = false;
    private final Set<LightListener> listeners = new HashSet<>();

    public Light(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
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
            LOG.info("Light {} turned on", name);
        }
    }

    public void turnOff() {
        if(on) {
            on = false;
            LightOffEvent event = new LightOffEvent(this, System.currentTimeMillis());
            for (LightListener listener : listeners) {
                listener.handleLightOff(event);
            }
            LOG.info("Light {} turned off", name);
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

    public boolean isOff() {
        return !on;
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
