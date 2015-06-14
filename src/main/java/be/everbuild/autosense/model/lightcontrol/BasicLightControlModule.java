package be.everbuild.autosense.model.lightcontrol;

import be.everbuild.autosense.model.lightcontrol.button.Button;
import be.everbuild.autosense.model.lightcontrol.light.Light;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicLightControlModule implements LightControlModule {
    private static final Logger LOG = LoggerFactory.getLogger(BasicLightControlModule.class);

    protected final String id;
    protected final Button[] buttons = new Button[8];
    protected final Light[] lights = new Light[8];

    public BasicLightControlModule(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void bindButton(Button button, int pin) {
        try {
            buttons[pin] = button;
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("invalid pin", e);
        }
    }

    public void unbindButton(Button button) {
        for (int pin = 0; pin < 8; pin++) {
            if(buttons[pin] == button) {
                buttons[pin] = null;
            }
        }
    }

    public void bindLight(Light light, int pin) {
        try {
            lights[pin] = light;
        } catch (Exception e) {
            LOG.error("invalid pin", e);
        }
    }

    public void unbindLight(Light light) {
        for (int pin = 0; pin < 8; pin++) {
            if(lights[pin] == light) {
                lights[pin] = null;
            }
        }
    }
}
