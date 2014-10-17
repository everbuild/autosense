package be.everbuild.autosense.gpio;

public class GpioDriverFactory {
    public static GpioDriver create(String name) {
        switch (name) {
            case "real":
                return new RealGpioDriver();
            case "dummy":
                return new DummyGpioDriver();
            default:
                throw new IllegalArgumentException("Unknown driver name: " + name);
        }
    }
}
