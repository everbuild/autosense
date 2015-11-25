package be.everbuild.autosense.user;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by Evert on 25/07/15.
 */
public class PlainPass {
    private final String value;

    public PlainPass(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public String getValue() {
        return value;
    }

    public HashedPass hash() {
        return new HashedPass(DigestUtils.sha1Hex(value));
    }
}
