package statics;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * Created by Mike on 5/12/2016.
 */
final class YouTube extends DomainWrapper {
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{5,20}$");
    private static final String embeddedPrefix = "https://www.youtube.com/embed/";

    @Override
    public boolean validate(String identifier) {
        return ID_PATTERN.matcher(identifier).matches();
    }

    @Nonnull
    @Override
    public String getSrcLink(@Nonnull String identifier) {
        return embeddedPrefix + identifier;
    }
}