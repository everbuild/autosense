package be.everbuild.autosense.gpio;

import be.everbuild.autosense.model.button.Button;
import be.everbuild.autosense.model.light.Light;

public interface LightControlModule extends GpioModule {
    void bindButton(Button button, int pin);
    void unbindButton(Button button);
    void bindLight(Light light, int pin);
    void unbindLight(Light light);
}
