package be.everbuild.autosense.gpio.real;

import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.model.lightcontrol.LightControlModule;
import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

public class RealGpioDriver implements GpioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(RealGpioDriver.class);

    private final ScheduledExecutorService executorService;

    public RealGpioDriver(ScheduledExecutorService executorService) {
        // Mostly experiments for now

        this.executorService = executorService;

        // create gpio controller instance
        final GpioController gpio = GpioFactory.getInstance();

        //testOnboardGpio(gpio);
        //testExternalGpio(gpio);

        final GpioPinDigitalInput shutdownButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04,             // PIN NUMBER
                "shutdownButton",                   // PIN FRIENDLY NAME (optional)
                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)
        shutdownButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.LOW) {
                    poweroff();
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Stopping GPIO");
                gpio.shutdown();
            }
        });
    }

    private void testOnboardGpio(GpioController gpio) {
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
    }

    private void testExternalGpio(GpioController gpio) {
        try {
            // address composition: 0 1 0 0 A2 A1 A0
            // bus is always 1 it seems
            MCP23017GpioProvider mcp23017GpioProvider = new MCP23017GpioProvider(1, 0x20);

            // output
            GpioPinDigitalOutput extOut1 = gpio.provisionDigitalOutputPin(mcp23017GpioProvider, MCP23017Pin.GPIO_A1, "extOut1", PinState.LOW);
            extOut1.blink(500);

            // input
            final GpioPinDigitalInput extInp1 = gpio.provisionDigitalInputPin(mcp23017GpioProvider, MCP23017Pin.GPIO_A2, "extInp1", PinPullResistance.PULL_UP);
            extInp1.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    // display pin state on console
                    LOG.info(" --> GPIO PIN STATE CHANGE: {} = {}", event.getPin().getName(), event.getState().getName());
                }
            });

            final GpioPinDigitalInput mcpInterrupt = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "MCP Interrupt", PinPullResistance.PULL_UP);
            // interrupt line is active low: a transition from LOW (and immediately back to HIGH) signals an interrupt
            mcpInterrupt.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if(event.getState() == PinState.LOW) {
                        LOG.info(" --> GPIO INTERRUPT");
                    }
                }
            });

        } catch (IOException e) {
            LOG.error("Can't do it", e);
        }
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

    @Override
    public LightControlModule createLightControlModule(String id, int busNumber, int address) {
        return new GpioLightControlModule(id, busNumber, address, executorService);
    }
}
