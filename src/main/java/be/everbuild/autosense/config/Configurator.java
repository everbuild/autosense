package be.everbuild.autosense.config;

import be.everbuild.autosense.gpio.GpioDriver;
import be.everbuild.autosense.model.Model;
import be.everbuild.autosense.model.lightcontrol.LightControlModule;
import be.everbuild.autosense.model.lightcontrol.button.Button;
import be.everbuild.autosense.model.lightcontrol.button.ButtonFactory;
import be.everbuild.autosense.model.lightcontrol.light.Light;
import be.everbuild.autosense.model.lightcontrol.light.LightFactory;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class Configurator {

    private final GpioDriver gpioDriver;
    private final ScheduledExecutorService executorService;

    public Configurator(GpioDriver gpioDriver, ScheduledExecutorService executorService) {
        this.gpioDriver = gpioDriver;
        this.executorService = executorService;
    }

    public Model apply(Configuration config) {
        // TODO support partial ok
        ButtonFactory buttonFactory = new ButtonFactory(config.getMaxPressDuration(), executorService);

        Map<String, Button> buttons = config.getButtons().stream()
                .map(cfg -> buttonFactory.createButton(cfg.getId(), cfg.getName()))
                .collect(Collectors.<Button, String, Button>toMap(btn -> btn.getId(), btn -> btn));

        LightFactory lightFactory = new LightFactory();

        Map<String, Light> lights = config.getLights().stream()
                .map(cfg -> lightFactory.createLight(cfg.getId(), cfg.getName()))
                .collect(Collectors.<Light, String, Light>toMap(l -> l.getId(), l -> l));

        // TODO validate configuration?

        List<LightControlModule> modules = config.getModules().stream()
                .map(cfg -> {
                    LightControlModule module = gpioDriver.createLightControlModule(
                            cfg.getId(),
                            Optional.ofNullable(cfg.getBusNumber()).orElse(1),
                            Integer.parseInt(cfg.getAddress(), 2) + 0x20
                    );
                    cfg.getButtons().entrySet().stream()
                            .forEach(e -> module.bindButton(
                                    Preconditions.checkNotNull(buttons.get(e.getValue()), "No button found with ID %s", e.getValue()),
                                    e.getKey()
                            ));
                    cfg.getLights().entrySet().stream()
                            .forEach(e -> module.bindLight(
                                    Preconditions.checkNotNull(lights.get(e.getValue()), "No light found with ID %s", e.getValue()),
                                    e.getKey()
                            ));
                    return module;
                })
                .collect(Collectors.<LightControlModule>toList());

        // TODO warn about unbound lights/buttons

        Model model = new Model();
        model.getModules().addAll(modules);

        return model;
    }

    // TODO update config - isolate button/light/module setup in separate methods
}
