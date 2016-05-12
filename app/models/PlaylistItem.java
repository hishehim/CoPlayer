package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import statics.SourceType;

import javax.annotation.Nonnull;
import javax.persistence.*;

/**
 * Created by Mike on 3/6/2016.
 * Entity for holding information on each playlists item
 */
@Table(
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"parent__id","_id"})
)
@Entity
public class PlaylistItem extends Model {

    @Id
    @GeneratedValue
    @Column(name = "_id")
    private long rowId;

    @Constraints.Required
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "parent__id")
    private Playlist parent;

    /**
     * Identifies the player source
     * */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @Constraints.Required
    @Constraints.MaxLength(40)
    @Column(name = "source_type")
    private String sourceType;

    /**
     * Embedded video link
     * */
    @Constraints.Required
    private String link;

    @Constraints.Required
    private String title;

    /**
     * used prevent initialization without the use of factory method
     * Also used to return a null-pattern object
     */
    private PlaylistItem() {}

    public static Finder<Long, PlaylistItem> find = new Finder<Long, PlaylistItem>(PlaylistItem.class);


    /**
     * Factory method for creating a playlists item. Data are validated here.
     * @param link the embedded video link
     * @param parent the playlist the new item shall belong to
     * @param srcType the original source of the link
     * */
    public static PlaylistItem getNewItem(@Nonnull String link,
                                          @Nonnull Playlist parent,
                                          @Nonnull SourceType.Type srcType) {
        /*
        Link to be validated on adding to playlist
        if (linkValidation(link, type)) {
            PlaylistItem item = new PlaylistItem(playlist, type, link);
            // link should be validated
            return item;
        }*/
        PlaylistItem nPlaylistItem = new PlaylistItem();
        nPlaylistItem.link = link;
        nPlaylistItem.parent = parent;
        nPlaylistItem.sourceType = srcType.toString();
        return nPlaylistItem;
    }


    public long getId() {
        return rowId;
    }

    @PrePersist
    private void prePersist() {
        parent.increaseSize();
    }

    @PostRemove
    private void postRemove() {
        parent.decreaseSize();
    }

    public Playlist getParent() {
        return parent;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}