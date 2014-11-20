package be.everbuild.autosense.lightcontrol.button;

import java.util.EventListener;

public interface ButtonListener extends EventListener {
    void handleButtonPress(ButtonPressEvent event);
    void handleButtonRelease(ButtonReleaseEvent event);
}
