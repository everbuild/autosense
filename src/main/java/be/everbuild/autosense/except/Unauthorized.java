package be.everbuild.autosense.except;

/**
 * Created by Evert on 30/07/15.
 */
public class Unauthorized extends RuntimeException {
    public Unauthorized() {
    }

    public Unauthorized(String message) {
        super(message);
    }

    public Unauthorized(String message, Throwable cause) {
        super(message, cause);
    }

    public Unauthorized(Throwable cause) {
        super(cause);
    }

    public Unauthorized(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
