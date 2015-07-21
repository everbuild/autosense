package be.everbuild.autosense.gpio;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Evert on 18/07/15.
 */
public class GpioAddress {
    public static final String SEPARATOR = ":";
    public static final int MAX_ADDRESS = 128;
    public static final int DEFAULT_BUS = 1;

    private final int bus;
    private final int address;

    public GpioAddress(int address) {
        this(DEFAULT_BUS, address);
    }

    public GpioAddress(int bus, int address) {
        Preconditions.checkArgument(bus > 0, "bus should be > 0");
        Preconditions.checkArgument(address >= 0 && address < MAX_ADDRESS, "address should be in [0, MAX_ADDRESS[");
        this.bus = bus;
        this.address = address;
    }

    public int getBus() {
        return bus;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return bus + GpioAddress.SEPARATOR + address;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GpioAddress && equals((GpioAddress)o);
    }

    private boolean equals(GpioAddress that) {
        return this.bus == that.bus && this.address == that.address;
    }

    @Override
    public int hashCode() {
        return bus*13 + address*37;
    }

    public static GpioAddress parse(String value) {
        String[] parts = StringUtils.split(value, SEPARATOR);
        if(parts.length == 1) {
            return new GpioAddress(Integer.parseInt(parts[0]));
        } else if(parts.length >= 2) {
            return new GpioAddress(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            throw new IllegalArgumentException("Can't understand " + value);
        }
    }
}
