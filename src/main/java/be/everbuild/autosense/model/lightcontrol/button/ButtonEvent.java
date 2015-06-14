package be.everbuild.autosense.model.lightcontrol.button;

import java.util.EventObject;

public abstract class ButtonEvent extends EventObject {

    private final long time;

    public ButtonEvent(Button source, long time) {
        super(source);
        this.time = time;
    }

    public Button getSource() {
        return (Button)source;
    }

    public long getTime() {
        return time;
    }
}
