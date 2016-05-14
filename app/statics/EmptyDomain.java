package statics;

import javax.annotation.Nonnull;

/**
 * Created by Mike on 5/13/2016.
 */
public class EmptyDomain extends DomainWrapper {
    @Override
    public boolean validate(String identifier) {
        return false;
    }

    @Nonnull
    @Override
    public String getSrcLink(String identifier) {
        return "";
    }
}
