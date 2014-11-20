package be.everbuild.autosense.lightcontrol.button;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ButtonFactory {
    private final long maxPressDuration;
    private final ScheduledExecutorService executorService;

    public ButtonFactory(long maxPressDuration, ScheduledExecutorService executorService) {
        this.maxPressDuration = maxPressDuration;
        this.executorService = executorService;
    }

    public ButtonFactory() {
        this(1000, Executors.newSingleThreadScheduledExecutor());
    }

    public Button createButton(String name) {
        return new Button(name, maxPressDuration, executorService);
    }
}
