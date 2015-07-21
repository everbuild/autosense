package be.everbuild.autosense.gpio;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Evert on 18/07/15.
 */
public class GpioPinAddress {
    public static final int MAX_PIN = 8;

    private final GpioAddress address;
    private final int pin;

    public GpioPinAddress(GpioAddress address, int pin) {
        Preconditions.checkNotNull(address, "address");
        Preconditions.checkArgument(pin >= 0 && pin < MAX_PIN, "pin should be in [0, MAX_PIN[");
        this.address = address;
        this.pin = pin;
    }

    public GpioAddress getAddress() {
        return address;
    }

    public int getPin() {
        return pin;
    }

    @Override
    public String toString() {
        return address.toString() + GpioAddress.SEPARATOR + pin;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GpioPinAddress && equals((GpioPinAddress)o);
    }

    private boolean equals(GpioPinAddress that) {
        return this.pin == that.pin && this.address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode()*46 + pin;
    }

    public static GpioPinAddress parse(String value) {
        String[] parts = StringUtils.split(value, GpioAddress.SEPARATOR);
        if(parts.length == 2) {
            return new GpioPinAddress(
                    new GpioAddress(Integer.parseInt(parts[0])),
                    Integer.parseInt(parts[1])
            );
        } else if(parts.length >= 3) {
            return new GpioPinAddress(
                    new GpioAddress(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])),
                    Integer.parseInt(parts[2])
            );
        } else {
            throw new IllegalArgumentException("Can't understand " + value);
        }
    }
}
