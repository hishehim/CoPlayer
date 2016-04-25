package models;

/**
 * Created by Mike on 3/6/2016.
 */

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.google.common.io.BaseEncoding;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Table(
    name = "playlist",
    // sets a composite unique constraint for user_id and playlists-title
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"owner_id", "title"})
)
@Entity
public class Playlist extends Model {

    @Transient
    private static final Random random = new Random();

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "owner_id")
    @Constraints.Required
    private Users owner;

    @Constraints.Required
    @Column(name = "title")
    private String title;

    @Id
    @Constraints.MaxLength(12)
    @Column(name = "id")
    private String id;

    @Constraints.Required
    @CreatedTimestamp
    private Timestamp createTime;

    @Column(name = "is_private")
    private boolean isPrivate = false;

    @Column(name = "size")
    private int size = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentList", cascade = CascadeType.ALL)
    List<PlaylistItem> listItems = new ArrayList<>();

    /* Used to find entire object */
    public static Finder<String, Playlist> find = new Finder<String, Playlist>(Playlist.class);

    private Playlist() {}

    @Nullable
    public static Playlist getNewPlaylist(@Nonnull String title, @Nonnull Users owner) {
        if (title.isEmpty()) {
            return new Playlist();
        }
        Playlist playlist = new Playlist();
        playlist.owner = owner;
        playlist.title = title; // Title should be checked for uniqueness among the same user
        playlist.isPrivate = false;

        int failCount = 0;
        do {
            playlist.id = genUID();
            if (find.where().eq("id", playlist.id).findRowCount() == 0) {
                return playlist;
            }
            failCount++;
        } while (failCount <= 2);

        return null;
    }

    @Nullable
    public PlaylistItem addItem(@Nonnull String url) {
        PlaylistItem nItem = PlaylistItem.getNewItem(url);
        listItems.add(nItem);
        return nItem;
    }

    public Users getOwner() { return owner; }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public List<PlaylistItem> getListItems() {
        return listItems;
    }

    private static String genUID() {
        byte[] byteArr = new byte[9];
        random.nextBytes(byteArr);
        return BaseEncoding.base64Url().encode(byteArr);
    }

}