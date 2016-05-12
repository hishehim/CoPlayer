package statics;

/**
* enum value used for constraining source types for cleaner source type parameter passing
* */
public enum Domain {
    YOUTUBE("YouTube"),
    //DAILYMOTION("DailyMotion"),
    //VIMEO("VIMEO"),
    SOUNDCLOUD("SoundCloud");

    private final String type;

    Domain(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
