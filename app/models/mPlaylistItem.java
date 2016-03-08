package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;

/**
 * Created by Mike on 3/6/2016.
 */
@Entity
public class mPlaylistItem extends Model {

    @ManyToOne
    @Constraints.Required
    private mPlaylist playListID;

    /*@Id
    private long id;*/

    @Enumerated(EnumType.STRING)
    @Constraints.Required
    private CONST.SOURCE sourceType;

    @Constraints.Required
    private String link;

    private mPlaylistItem() {}

    private static mPlaylistItem getNewItem(@Nonnull String link, CONST.SOURCE type) {
        mPlaylistItem item = new mPlaylistItem();
        item.link = link;
        item.sourceType = type;
        return item;
    }

}
