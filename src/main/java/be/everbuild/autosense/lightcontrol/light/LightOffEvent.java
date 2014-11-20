package be.everbuild.autosense.lightcontrol.light;

public class LightOffEvent extends LightEvent {
    public LightOffEvent(Light source, long time) {
        super(source, time);
    }
}
