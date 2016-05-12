package statics;

import javax.annotation.Nonnull;

/**
 * Created by Mike on 5/12/2016.
 */
class SoundCloud extends DomainWrapper {

    private static final String prefix = "https://w.soundcloud.com/player/?url=";

    @Nonnull
    @Override
    public String getSrcLink(@Nonnull String identifier) {
        return prefix + identifier;
    }
}
