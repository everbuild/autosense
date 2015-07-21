package be.everbuild.autosense.model.button;

import be.everbuild.autosense.model.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Button {
    private static final Logger LOG = LoggerFactory.getLogger(Button.class);

    private final String name;
    private final long maxPressDuration;
    private final ScheduledExecutorService executorService;
    private boolean pressed = false;
    private final EventSource<ButtonPressEvent> onPress = new EventSource<>();
    private final EventSource<ButtonReleaseEvent> onRelease = new EventSource<>();
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
            onPress.fire(event);
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
            onRelease.fire(event);
        }
    }

    public Button onPress(Consumer<ButtonPressEvent> listener) {
        this.onPress.add(listener);
        return this;
    }

    public Button onRelease(Consumer<ButtonReleaseEvent> listener) {
        this.onRelease.add(listener);
        return this;
    }

    private class AutoReleaseTask implements Runnable {
        @Override
        public void run() {
            release();
        }
    }
}
