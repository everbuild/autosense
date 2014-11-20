package be.everbuild.autosense.lightcontrol.button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Button {
    private static final Logger LOG = LoggerFactory.getLogger(Button.class);

    private final String name;
    private final long maxPressDuration;
    private final ScheduledExecutorService executorService;
    private boolean pressed = false;
    private final Set<ButtonListener> listeners = new HashSet<>();
    private final Runnable autoReleaseTask = new AutoReleaseTask() ;
    private ScheduledFuture<?> autoReleaseFuture;

    public Button(String name, long maxPressDuration, ScheduledExecutorService executorService) {
        this.name = name;
        this.maxPressDuration = maxPressDuration;
        this.executorService = executorService;
    }

    public String getName() {
        return name;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void press() {
        if(!pressed) {
            LOG.info("Button {} pressed", name);
            pressed = true;
            ButtonPressEvent event = new ButtonPressEvent(this, System.currentTimeMillis());
            for (ButtonListener listener : listeners) {
                listener.handleButtonPress(event);
            }
            if (autoReleaseFuture != null && !autoReleaseFuture.isDone()) {
                autoReleaseFuture.cancel(false);
            }
            if(executorService != null && maxPressDuration > 0) {
                autoReleaseFuture = executorService.schedule(autoReleaseTask, maxPressDuration, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void release() {
        if(pressed) {
            LOG.info("Button {} released", name);
            pressed = false;
            ButtonReleaseEvent event = new ButtonReleaseEvent(this, System.currentTimeMillis());
            for (ButtonListener listener : listeners) {
                listener.handleButtonRelease(event);
            }
        }
    }

    public Button addListener(ButtonListener listener) {
        listeners.add(listener);
        return this;
    }

    public Button removeListener(ButtonListener listener) {
        listeners.remove(listener);
        return this;
    }

    private class AutoReleaseTask implements Runnable {
        @Override
        public void run() {
            release();
        }
    }
}
