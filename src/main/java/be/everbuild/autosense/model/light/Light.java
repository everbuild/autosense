package be.everbuild.autosense.model.light;

import be.everbuild.autosense.model.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class Light {
    private static final Logger LOG = LoggerFactory.getLogger(Light.class);

    private final String name;
    private boolean on = false;
    private final EventSource<LightOnEvent> onTurnOn = new EventSource<>();
    private final EventSource<LightOffEvent> onTurnOff = new EventSource<>();

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
            onTurnOn.fire(event);
            LOG.info("Light {} turned on", name);
        }
    }

    public void turnOff() {
        if(on) {
            on = false;
            LightOffEvent event = new LightOffEvent(this, System.currentTimeMillis());
            onTurnOff.fire(event);
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

    public Light onTurnOn(Consumer<LightOnEvent> listener) {
        this.onTurnOn.add(listener);
        return this;
    }

    public Light onTurnOff(Consumer<LightOffEvent> listener) {
        this.onTurnOff.add(listener);
        return this;
    }
}
