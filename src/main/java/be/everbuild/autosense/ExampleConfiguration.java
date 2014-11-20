package be.everbuild.autosense;

import be.everbuild.autosense.lightcontrol.LightControlModule;
import be.everbuild.autosense.lightcontrol.button.*;
import be.everbuild.autosense.lightcontrol.light.Light;

import java.util.concurrent.ScheduledExecutorService;

public class ExampleConfiguration {

    private final ButtonFactory buttonFactory;
    private final Button pushLightButton;
    private final Light pushLight;
    private final Button toggleLightButton;
    private final Light toggleLight;

    public ExampleConfiguration(ScheduledExecutorService executorService) {
        buttonFactory = new ButtonFactory(1000, executorService);

        pushLightButton = buttonFactory.createButton("pushLightButton");
        pushLight = new Light("pushLight");
        pushLightButton.addListener(new ButtonListener() {
            @Override
            public void handleButtonPress(ButtonPressEvent event) {
                pushLight.turnOn();
            }

            @Override
            public void handleButtonRelease(ButtonReleaseEvent event) {
                pushLight.turnOff();
            }
        });

        toggleLightButton = buttonFactory.createButton("toggleLightButton");
        toggleLight = new Light("toggleLight");
        toggleLightButton.addListener(new ButtonListener() {
            @Override
            public void handleButtonPress(ButtonPressEvent event) {
                toggleLight.toggle();
            }

            @Override
            public void handleButtonRelease(ButtonReleaseEvent event) {
            }
        });
    }

    public void bindModule(LightControlModule controlModule) {
        controlModule.bindButton(pushLightButton, 6);
        controlModule.bindLight(pushLight, 6);
        controlModule.bindButton(toggleLightButton, 7);
        controlModule.bindLight(toggleLight, 7);
    }
}
