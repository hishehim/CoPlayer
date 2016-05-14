package statics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.regex.Pattern;

import static java.util.Arrays.sort;

/**
 * Created by Mike on 4/29/2016.
 * TODO: DomainData may need to be reimplemented as abstract class
 */
public final class DomainData {

    /**
     * Used for a list of accepted source values in String form. List is sorted.
     * */
    public static final ImmutableList<String> domainList =
            ImmutableList.copyOf(DomainData.getSrcStrings());

    /*
    * Used for mapping domain names to the enum Domain
    * */
    private static final ImmutableMap<String, Domain> domainMap =
            ImmutableMap.copyOf(getMapping());

    /*
    * DomainWrapper classes used to validate links
    * */
    private static final YouTube youtube = new YouTube();
    private static final SoundCloud soundcloud = new SoundCloud();

    /*
    *
    * */
    private static final Pattern youtubePattern = Pattern.compile("");

    /* Prevent initialization of this class */
    private DomainData() {}

    /**
     * Main method used to convert domain name in string to Domain enum
     * */
    @Nullable
    public static Domain getDomain(String domain) {
        String dName = "";

        return domainMap.get(domain.toLowerCase());
    }

    /**
     * @param domain Indicates the domain of the original source (ie. YouTube, SoundCloud, etc...)
     * @return If domain is not null, return the corresponding DomainWrapper object for the given domain
     * */
    @Nonnull
    public static DomainWrapper getDomainWrapper(@Nonnull Domain domain) {
        switch (domain) {
            case YOUTUBE:
                return youtube;
            case SOUNDCLOUD:
                return soundcloud;
            default:
                return new EmptyDomain();
        }
    }

    /**
     * @return A sorted array of Domain as their string value
     * */
    private static String[] getSrcStrings() {
        Domain[] allDomains = Domain.values();
        String[] srcStr = new String[allDomains.length];
        for (int i = 0; i < allDomains.length; i++) {
            srcStr[i] = allDomains[i].toString();
        }
        sort(srcStr);
        return srcStr;
    }
    // all source related checking to be in here

    /**
     * @return A map of all Domain's string value to themselves
     * */
    private static HashMap<String, Domain> getMapping() {
        HashMap<String, Domain> hashMap = new HashMap<>();
        for (Domain domain : Domain.values()) {
            hashMap.put(domain.toString().toLowerCase(), domain);
        }
        return hashMap;
    }
}