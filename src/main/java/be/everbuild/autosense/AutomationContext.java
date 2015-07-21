package be.everbuild.autosense;

import be.everbuild.autosense.gpio.GpioAddress;
import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.gpio.GpioPinAddress;
import be.everbuild.autosense.model.Module;
import be.everbuild.autosense.model.lightcontrol.LightControlModule;
import be.everbuild.autosense.model.lightcontrol.button.Button;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Evert on 18/07/15.
 */
public class AutomationContext {
    private static final Logger log = LoggerFactory.getLogger(AutomationContext.class);

    private ScheduledExecutorService executorService;
    public long maxPressDuration = 1000;

    private final GpioDriver gpioDriver;
    private final Map<GpioAddress, Module> modules = new HashMap<>();

    public AutomationContext(GpioDriver gpioDriver) {
        Preconditions.checkNotNull(gpioDriver, "gpioDriver");
        this.gpioDriver = gpioDriver;
    }

    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public ScheduledExecutorService getExecutorService() {
        if(executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        return executorService;
    }

    public long getMaxPressDuration() {
        return maxPressDuration;
    }

    public void setMaxPressDuration(long maxPressDuration) {
        this.maxPressDuration = maxPressDuration;
    }

    public LightControlModule createLightControlModule(GpioAddress address) {
        Preconditions.checkState(!modules.containsKey(address), "Module already registered at " + address);
        LightControlModule module = gpioDriver.createLightControlModule(address, getExecutorService());
        modules.put(address, module);
        return module;
    }

    public LightControlModule createLightControlModule(String address) {
        return createLightControlModule(GpioAddress.parse(address));
    }

    public Button createButton(String name) {
        Preconditions.checkNotNull(name, "name");
        return new Button(name, getMaxPressDuration(), getExecutorService());
    }

    public void bindButton(Button button, GpioPinAddress address) {
        Module module = modules.get(address.getAddress());
        if(module instanceof LightControlModule) {
            ((LightControlModule)module).bindButton(button, address.getPin());
        } else if(module == null) {
            log.error("Can't bind button '{}' to {} because there's no module registered at that address", button.getName(), address.getAddress());
        } else {
            log.error("Can't bind button '{}' to {} because the module at that address is not a LightControlModule", button.getName(), address.getAddress());
        }
    }

    public void bindButton(Button button, String address) {
        bindButton(button, GpioPinAddress.parse(address));
    }

}
