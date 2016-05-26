package statics;

        import javax.annotation.Nonnull;
        import java.util.regex.Pattern;

/**
 * Created by Mike on 5/12/2016.
 */
final class SoundCloud extends DomainWrapper {

    private static final Pattern ID_PATTERN = Pattern.compile("(^[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+$)|(^track/[0-9]+$)");
    private static final String prefix = "https://w.soundcloud.com/player/?url=";

    @Override
    public boolean validate(String identifier) {
        return ID_PATTERN.matcher(identifier).matches();
    }

    @Nonnull
    @Override
    public String getSrcLink(@Nonnull String identifier) {
        return prefix + identifier;
    }
}
