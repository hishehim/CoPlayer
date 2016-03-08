package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;

/**
 * Created by Mike on 3/6/2016.
 * Entity for holding information on each playlist item
 */
@Entity
public class PlaylistItem extends Model {

    @ManyToOne(cascade = CascadeType.ALL)
    private Playlist playlistID;

    /*@Id
    private long id;*/

    @Enumerated(EnumType.STRING)
    @Constraints.Required
    private CONST.SOURCE sourceType;

    @Constraints.Required
    private String link;

    private PlaylistItem() {}

    public static Finder<Long, PlaylistItem> find = new Finder<Long, PlaylistItem>(PlaylistItem.class);

    private static PlaylistItem getNewItem(@Nonnull String link,@Nonnull CONST.SOURCE type) {
        PlaylistItem item = new PlaylistItem();
        // link should be validated
        item.link = link;
        // type is currently not secure
        item.sourceType = type;
        return item;
    }

    public Playlist getPlaylistID() {
        return playlistID;
    }

    public CONST.SOURCE getSourceType() {
        return sourceType;
    }

    public String getLink() {
        return link;
    }
}