package models;

/**
 * Created by Mike on 3/6/2016.
 */

/**
 * Used to define the list of compatible host services
 * */
public final class CONST {

    public enum SOURCE {
        NONE(""),
        YOUTUBE("YouTube"),
        DAILYMOTION("DailyMotion"),
        VIMEO("VIEMO"),
        SOUNDCLOUD("SoundCloud");

        private final String type;

        private SOURCE(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
