package be.everbuild.autosense.model.lightcontrol.button;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ButtonFactory {
    private final long maxPressDuration;
    private final ScheduledExecutorService executorService;
    private int count = 0;

    public ButtonFactory(Long maxPressDuration, ScheduledExecutorService executorService) {
        this.maxPressDuration = maxPressDuration != null ? maxPressDuration : 1000;
        this.executorService = executorService != null ? executorService : Executors.newSingleThreadScheduledExecutor();
    }

    public ButtonFactory() {
        this(null, null);
    }

    public Button createButton(String id, String name) {
        count ++;
        if(id == null) {
            id = "" + count;
        }
        if(name == null) {
            name = "button " + id;
        }
        return new Button(id, name, maxPressDuration, executorService);
    }
}
