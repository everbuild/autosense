package be.everbuild.autosense.user;

import com.google.common.base.Preconditions;

/**
 * Created by Evert on 25/07/15.
 */
public class HashedPass {
    private final String value;

    public HashedPass(String value) {
        this.value = Preconditions.checkNotNull(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HashedPass && equals((HashedPass)obj);
    }

    private boolean equals(HashedPass that) {
        return this.value.equals(that);
    }
}
