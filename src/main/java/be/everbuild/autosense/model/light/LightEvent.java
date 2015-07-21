package be.everbuild.autosense.model.light;

import java.util.EventObject;

public abstract class LightEvent extends EventObject {

    private final long time;

    public LightEvent(Light source, long time) {
        super(source);
        this.time = time;
    }

    public Light getSource() {
        return (Light)source;
    }

    public long getTime() {
        return time;
    }
}
