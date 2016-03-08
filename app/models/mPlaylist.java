package models;

/**
 * Created by Mike on 3/6/2016.
 */

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
public class mPlaylist extends Model {

    //private <user> owner //for mapping

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String uuid = UUID.randomUUID().toString();

    @Constraints.Required
    private String title;

    @Constraints.Required
    private long createTime;

    boolean isPrivate = false;

    private int size = 0;

    @OneToMany(mappedBy = "playListID", cascade = CascadeType.ALL)
    List<mPlaylistItem> listItems = new ArrayList<>();

    private mPlaylist() {}

    public static mPlaylist getNewPlaylist(@Nonnull String title) {
        mPlaylist playlist = new mPlaylist();
        playlist.title = title;
        return playlist;
    }



}
