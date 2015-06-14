package be.everbuild.autosense.model.lightcontrol;

import be.everbuild.autosense.model.Module;
import be.everbuild.autosense.model.lightcontrol.button.Button;
import be.everbuild.autosense.model.lightcontrol.light.Light;

public interface LightControlModule extends Module {
    void bindButton(Button button, int pin);
    void unbindButton(Button button);
    void bindLight(Light light, int pin);
    void unbindLight(Light light);
}
