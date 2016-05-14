package statics;

        import javax.annotation.Nonnull;
        import java.util.regex.Pattern;

/**
 * Created by Mike on 5/12/2016.
 */
class SoundCloud extends DomainWrapper {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-].+$");
    private static final String prefix = "https://w.soundcloud.com/player/?url=";

    @Override
    public boolean validate(String identifier) {
        String[] secs = identifier.split("/");
        for (String sd : secs) {
            System.out.println(sd);
        }
        return (secs.length == 2) &&
                (ID_PATTERN.matcher(secs[0]).matches() && ID_PATTERN.matcher(secs[1]).matches());
    }

    @Nonnull
    @Override
    public String getSrcLink(@Nonnull String identifier) {
        return prefix + identifier;
    }
}
