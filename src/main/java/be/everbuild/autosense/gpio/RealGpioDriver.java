package be.everbuild.autosense.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RealGpioDriver implements GpioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(RealGpioDriver.class);

    public RealGpioDriver() {
        // Mostly experiments for now

        // create gpio controller instance
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        // (configure pin edge to both rising and falling to get notified for HIGH and LOW state
        // changes)
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06,             // PIN NUMBER
                "MyButton",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)

        // provision gpio pins #04 as an output pin and make sure is is set to LOW at startup
        final GpioPinDigitalOutput myLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05,   // PIN NUMBER
                "My LED",           // PIN FRIENDLY NAME (optional)
                PinState.LOW);      // PIN STARTUP STATE (optional)

        updateLed(myButton, myLed);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                LOG.info(" --> GPIO PIN STATE CHANGE: {} = {}", event.getPin().getName(), event.getState().getName());
                updateLed(myButton, myLed);
            }
        });

        final GpioPinDigitalInput shutdownButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04,             // PIN NUMBER
                "shutdownButton",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)
        shutdownButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if(event.getState() == PinState.LOW) {
                    poweroff();
                }
            }
        });

        // TODO why don't the shutdown hooks work???
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Stopping GPIO");
                gpio.shutdown();
            }
        });
    }

    private void updateLed(GpioPinDigitalInput myButton, GpioPinDigitalOutput myLed) {
        if(myButton.getState() == PinState.HIGH) {
            myLed.blink(250);
        } else {
            myLed.blink(1000);
        }
    }

    private void poweroff() {
        try {
            LOG.info("Shutting down the pi...");
            Runtime.getRuntime().exec("sudo poweroff");
        } catch (IOException e) {
            LOG.error("Can't shutdown", e);
        }
    }
}
