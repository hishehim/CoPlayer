package statics;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mike on 5/12/2016.
 */
public abstract class DomainWrapper {

    /**
     * Test if the given identifier for the given domain links to a existing file
     * @param identifier The unique identifier for the given video or audio file for the given domain
     * @return true if the given identifier generates a valid link for embedding. False otherwise
     * */
    public abstract boolean validate(String identifier);

    /**
     * Generates the http link used for embedding
     * @param identifier The unique identifier for the given video or audio file for the given domain
     * @return returns the link used for the 'src' attribute
     * */
    @Nonnull
    public abstract String getSrcLink(String identifier);
}