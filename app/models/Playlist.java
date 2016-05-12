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

import static controllers.Application.random;


@Table(
    name = "playlist",
    // sets a composite unique constraint for user_id and playlists-title
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"owner__id", "title"})
)
@Entity
public class Playlist extends Model {

    @Id
    @GeneratedValue
    @Column(name = "_id")
    private long rowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Constraints.Required
    @Column(name = "owner__id")
    private Users owner;

    @Constraints.Required
    @Column(name = "title")
    private String title;

    @Constraints.MaxLength(20)
    @Column(name = "id", unique = true)
    private String id;

    @Constraints.Required
    @CreatedTimestamp
    private Timestamp createTime;

    @Column(name = "is_private")
    private boolean isPrivate = false;

    @Column(name = "size")
    private int size = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    private List<PlaylistItem> listItems = new ArrayList<>();

    /* Used to find entire object */
    public static final Finder<String, Playlist> find = new Finder<String, Playlist>(Playlist.class);

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

        /*
        * Calculated collision rate with half full database is 0.5^4 ~= 6%
        * */
        for (int i = 0; i < 4; i++){
            playlist.id = genID();
            if (find.where().eq("id", playlist.id).findRowCount() == 0) {
                return playlist;
            }
        }
        return null;
    }

    public long getRowId() { return rowId; }

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

    void increaseSize() { size++; }

    void decreaseSize() { size--; }

    public List<PlaylistItem> getListItems() {
        return listItems;
    }

    private static String genID() {
        /*
        * 6 bits = one base 64 char (8 bits)
        * Conversion from byte to base64 is 3:4
        *   AKA every 3 bytes = 4 chars in base 64
        * Current 15-byte generates list _id of length 20
        * TODO: make ID length random within certain range? Increases ID pool = less collision
        * */
        byte[] byteArr = new byte[15];
        random.nextBytes(byteArr);
        return BaseEncoding.base64Url().encode(byteArr);
    }

}