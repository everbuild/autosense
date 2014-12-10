package be.everbuild.autosense;

import be.everbuild.autosense.lightcontrol.LightControlModule;
import be.everbuild.autosense.lightcontrol.button.*;
import be.everbuild.autosense.lightcontrol.light.Light;

import java.util.concurrent.ScheduledExecutorService;

public class TestConfiguration {

    private final ButtonFactory buttonFactory;
    private final Button[] buttons = new Button[8];
    private final Light[] lights = new Light[8];

    public TestConfiguration(ScheduledExecutorService executorService) {
        buttonFactory = new ButtonFactory(1000, executorService);

        for(int i = 0; i < 8; i ++) {
            buttons[i] = buttonFactory.createButton("button " + i);
            lights[i] = new Light("light " + i);
            buttons[i].addListener(new SingleLightController(lights[i]));
        }
    }

    public void bindModule(LightControlModule controlModule) {
        for(int i = 0; i < 8; i ++) {
            controlModule.bindButton(buttons[i], i);
            controlModule.bindLight(lights[i], i);
        }
    }

    private static class SingleLightController implements ButtonListener {
        private final Light light;

        private SingleLightController(Light light) {
            this.light = light;
        }

        @Override
        public void handleButtonPress(ButtonPressEvent event) {
            light.turnOn();
        }

        @Override
        public void handleButtonRelease(ButtonReleaseEvent event) {
            light.turnOff();
        }
    }
}
