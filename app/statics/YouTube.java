package statics;

import javax.annotation.Nonnull;

/**
 * Created by Mike on 5/12/2016.
 */
class YouTube extends DomainWrapper {

    private static final String embeddedPrefix = "https://www.youtube.com/embed/";

    @Nonnull
    @Override
    public String getSrcLink(@Nonnull String identifier) {
        return embeddedPrefix + identifier;
    }
}
