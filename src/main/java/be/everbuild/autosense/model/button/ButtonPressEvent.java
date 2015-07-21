package be.everbuild.autosense.model.button;

public class ButtonPressEvent extends ButtonEvent {
    public ButtonPressEvent(Button source, long time) {
        super(source, time);
    }
}
