package models;

/**
 * Created by Mike on 3/6/2016.
 */

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Table(
    // sets a composite unique constraint for user_id and playlists-title
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"owner_id", "title"})
)

@Entity
public class Playlist extends Model {

    @ManyToOne
    @Column(name = "owner_id")
    @Constraints.Required
    private Users owner;

    @Id
    private long id;

    @Constraints.Required
    private String title;

    @Column(unique = true)
    @Constraints.Required
    private String uuid;

    @Constraints.Required
    @CreatedTimestamp
    private Timestamp createTime;

    private boolean isPrivate = false;

    private int size = 0;

    @OneToMany(mappedBy = "parentList", cascade = CascadeType.ALL)
    List<PlaylistItem> listItems = new ArrayList<>();

    public static Finder<Long, Playlist> find = new Finder<Long, Playlist>(Playlist.class);

    private Playlist() {}

    public static Playlist getNewPlaylist(@Nonnull String title, @Nonnull Users owner) {
        if (title.isEmpty()) {
            return new Playlist();
        }
        Playlist playlist = new Playlist();
        playlist.owner = owner;
        playlist.title = title; // Title should be checked for uniqueness among the same user
        playlist.uuid = UUID.randomUUID().toString();
        playlist.isPrivate = false;
        return playlist;
    }

    public int addItem(@Nonnull PlaylistItem item) {
        listItems.add(item);
        return 0;
    }

    @PrePersist


    public Users getOwner() { return owner; }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getUuid() {
        return uuid;
    }

    public long getId() {
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


}