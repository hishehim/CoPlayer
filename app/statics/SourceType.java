package statics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

import static java.util.Arrays.sort;

/**
 * Created by Mike on 4/29/2016.
 * TODO: SourceType may need to be reimplemented as abstract class
 */
public final class SourceType {

    public static final ImmutableList<String> sourceList =
            ImmutableList.copyOf(SourceType.getSrcStrings());

    public static final ImmutableMap<String, Type> sourceMap =
            ImmutableMap.copyOf(getMapping());

    /* TODO
    * the strings for each type does not properly sync with options list as of yet
    * */
    public enum Type {
        YOUTUBE("YouTube"),
        DAILYMOTION("DailyMotion"),
        VIMEO("VIMEO"),
        SOUNDCLOUD("SoundCloud");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    /* Prevent initialization of this class */
    private SourceType() {}

    /**
     * @return A sorted array of Type as their string value
     * */
    private static String[] getSrcStrings() {
        Type[] allTypes = Type.values();
        String[] srcStr = new String[allTypes.length];
        for (int i = 0; i < allTypes.length; i++) {
            srcStr[i] = allTypes[i].toString();
        }
        sort(srcStr);
        return srcStr;
    }
    // all source related checking to be in here

    /**
     * @return A map of all Type's string value to themselves
     * */
    private static HashMap<String, Type> getMapping() {
        HashMap<String, Type> hashMap = new HashMap<>();
        for (Type type: Type.values()) {
            hashMap.put(type.toString(), type);
        }
        return hashMap;
    }
}