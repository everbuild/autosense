package be.everbuild.autosense.model;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Evert on 27/06/15.
 */
public class EventSource<T> {
    private final List<Consumer<T>> listeners = new LinkedList<>();

    public void fire(T t) {
        listeners.forEach(l -> l.accept(t));
    }

    public void add(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void remove(Consumer<T> listener) {
        listeners.remove(listener);
    }
}
