package be.everbuild.autosense.gpio.impl.real;

import be.everbuild.autosense.gpio.GpioAddress;
import be.everbuild.autosense.gpio.impl.shared.BasicLightControlModule;
import be.everbuild.autosense.model.button.Button;
import be.everbuild.autosense.model.light.Light;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RealLightControlModule extends BasicLightControlModule {
    private static final Logger LOG = LoggerFactory.getLogger(RealLightControlModule.class);

    private I2CBus bus;
    private I2CDevice device;

    private static final int IODIRA     = 0x00;
    private static final int IODIRB     = 0x01;
    private static final int IPOLA      = 0x02;
    private static final int GPINTENA   = 0x04;
    private static final int INTCONA    = 0x08;
    private static final int IOCON      = 0x0A;
    private static final int GPPUA      = 0x0C;
    private static final int INTFA      = 0x0E;
    private static final int INTCAPA    = 0x10;
    private static final int GPIOB      = 0x13;

    private final ScheduledExecutorService executorService;
    private final SetupTask setupTask = new SetupTask();
    private ScheduledFuture<?> setupFuture;
    private final UpdateTask updateTask = new UpdateTask();
    private ScheduledFuture<?> updateTaskFuture;
    private boolean ready = false;
    private int currentOutputValue;

    public RealLightControlModule(GpioAddress address, ScheduledExecutorService executorService) {
        super(address);
        this.executorService = executorService;
        runSetupTask();
    }

    public void bindLight(Light light, int pin) {
        super.bindLight(light, pin);
        syncLights();
    }

    public boolean isReady() {
        return ready;
    }

    private void setup() throws IOException {

        // create I2C communications bus instance
        bus = I2CFactory.getInstance(address.getBus());

        // create I2C device instance
        device = bus.getDevice(address.getAddress());

        // port A = input
        device.write(IODIRA, (byte) 0xFF);

        // port B = output
        device.write(IODIRB, (byte) 0x00);

        // disable polarity inversion
        device.write(IPOLA, (byte) 0x00);

        // enable interrupts on all port A pins
        device.write(GPINTENA, (byte) 0xFF);

        // set interrupts to fire when pins change compared to their previous state (as opposed to compared to a default state)
        device.write(INTCONA, (byte) 0x00);

        // set configuration register (bank/mirror/byte-mode/disslw/not-used/odr/intpol/not-used)
        device.write(IOCON, (byte) 0b00100000);

        // enable all pin pull up resistors on port A
        device.write(GPPUA, (byte) 0xFF);

        // read interrupt capture register of port A to clear it
        device.read(INTCAPA);

        // get current state of port B pins
        currentOutputValue = device.read(GPIOB);

        syncLights();

        // output all register state
        if (LOG.isDebugEnabled()) {
            LOG.debug("MCP23017 state after setup:");
            for(int i = 0; i < 0x16; i ++) {
                int val = device.read(i);
                LOG.debug("register {} = {}", Integer.toHexString(i), Integer.toBinaryString(val));
            }
        }
    }

    private void updateButtons() throws IOException {
        int flag = device.read(INTFA);
        if(flag > 0) {
            LOG.debug("interrupt flag = {}", Integer.toBinaryString(flag));
            try {
                // sleep a bit to debounce button press (a single physical press might be seen as a short burst of pulses due too spring loading and such)
                Thread.sleep(100); // TODO this value may need some tweaking depending on real-world conditions
            } catch (InterruptedException e) {
            }
            int capturedValue = device.read(INTCAPA);

            for(int pin = 0; pin < 8; pin ++) {
                int mask = 1 << pin;
                if((flag & mask) > 0) {
                    boolean high = (capturedValue & mask) > 0;
                    LOG.debug("pin {} interrupt high = {}", pin, high ? "1" : "0");
                    Button button = buttons[pin];
                    if(button != null) {
                        if (high) {
                            button.release();
                        } else {
                            button.press();
                        }
                    }
                }
            }
        }
    }

    private void updateLights() throws IOException {
        int value = 0;
        for(int pin = 0; pin < 8; pin ++) {
            Light light = lights[pin];
            if(light != null && light.isOn()) {
                value |= 1 << pin;
            }
        }

        if (currentOutputValue != value) {
            LOG.debug("setting port B to {}", Integer.toBinaryString(value));
            device.write(GPIOB, (byte) value);
            currentOutputValue = value;
        }
    }

    private void syncLights() {
        for(int pin = 0; pin < 8; pin ++) {
            Light light = lights[pin];
            if(light != null) {
                boolean actuallyOn = (currentOutputValue & (1 << pin)) > 0;
                if(light.isOff() && actuallyOn) {
                    light.turnOn();
                } else if(light.isOn() && !actuallyOn) {
                    light.turnOff();
                }
            }
        }
    }

    private void runSetupTask() {
        setupFuture = executorService.schedule(setupTask, 0, TimeUnit.MINUTES);
    }

    private void scheduleSetupTask() {
        setupFuture = executorService.schedule(setupTask, 1, TimeUnit.MINUTES);
    }

    private void startUpdateTask() {
        // TODO the delay value may need some tweaking depending on real-world conditions
        updateTaskFuture = executorService.scheduleWithFixedDelay(updateTask, 0, 50, TimeUnit.MILLISECONDS);
    }

    private boolean stopUpdateTask() {
        return updateTaskFuture.cancel(false);
    }

    private class SetupTask implements Runnable {

        private boolean quiet = false;

        @Override
        public void run() {
            try {
                if (!quiet) {
                    LOG.info("setting up light control module on I2C bus {}, address {}", address.getBus(), Integer.toBinaryString(address.getAddress()));
                }
                setup();
                ready = true;
                LOG.info("light control module setup OK");
                quiet = false;
                startUpdateTask();
            } catch (IOException e) {
                if (!quiet) {
                    LOG.error("That didn't work, will retry again later", e);
                    quiet = true;
                }
                scheduleSetupTask();
            }
        }
    }

    private class UpdateTask implements Runnable {

        @Override
        public void run() {
            try {
                updateButtons();
                updateLights();
            } catch (IOException e) {
                LOG.error("Something went wrong during update, will retry setup in a bit", e);
                ready = false;
                stopUpdateTask();
                scheduleSetupTask();
            }
        }
    }
}
