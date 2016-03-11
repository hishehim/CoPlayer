package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;

/**
 * Created by Mike on 3/6/2016.
 * Entity for holding information on each playlist item
 */
@Table(
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"playlist_id","id"})
)
@Entity
public class PlaylistItem extends Model {

    @Constraints.Required
    @ManyToOne(cascade = CascadeType.ALL)
    private Playlist playlist;

    @GeneratedValue
    private long id;

    /**
     * Identifies the player source
     * */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @Constraints.Required
    private SourceType sourceType;

    /**
     * Embedded video link
     * */
    @Constraints.Required
    private String link;


    /**
     * used prevent initialization without the use of factory method
     * Also used to return a null-pattern object
     */
    private PlaylistItem() {}

    /**
     * Default private constructor used to create a playlist item
     * @param playlist the playlist this item belongs to.
     * @param type defines the type of the source.
     * @param link the embedded video link
     * */
    private PlaylistItem(Playlist playlist, SourceType type, String link) {
        this.playlist = playlist;
        this.sourceType = type;
        this.link = link;
    }

    public static Finder<Long, PlaylistItem> find = new Finder<Long, PlaylistItem>(PlaylistItem.class);


    /**
     * Factory method for creating a playlist item. Data are validated here.
     * @param playlist the playlist this item belongs to.
     * @param type defines the type of the source.
     * @param link the embedded video link
     * */
    private static PlaylistItem getNewItem(@Nonnull Playlist playlist,
                                           @Nonnull String link,
                                           @Nonnull SourceType type) {
        if (linkValidation(link, type)) {
            PlaylistItem item = new PlaylistItem(playlist, type, link);
            // link should be validated
            return item;
        }
        return new PlaylistItem();
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getLink() {
        return link;
    }

    /**
     * Ensures that the link being added is a valid format for the source type
     * */
    private static boolean linkValidation(String link, SourceType type) {
        // TO-DO LATER
        return true;
    }
}