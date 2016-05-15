package models;

/**
 * Created by yfle on 3/7/2016.
 */

import com.avaje.ebean.Model;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.io.BaseEncoding;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import static controllers.Application.random;

@Entity
public class Users extends Model{

    @Id
    @Column(name = "_id")
    @JsonIgnore
    private long rowId;

    @Constraints.Required
    @Column(unique = true)
    @JsonIgnore
    private String email;

    @Constraints.Required
    @Column(name = "username", unique = true)
    private String username;

    @Constraints.Required
    @Column(unique = true)
    @JsonIgnore
    private String password_hash;

    @Constraints.Required
    @Constraints.MaxLength(20)
    @JsonIgnore
    @Column(name = "id", unique = true)
    private String id;

    public static Finder<Long,Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate (String password) {return BCrypt.checkpw(password,this.password_hash);}

    @Nonnull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    private  List<Playlist> playlists = new ArrayList<>();

    public static Users createUser(@Nonnull String username, @Nonnull String password,
                                   @Nonnull String email){
        Users nUser = new Users();
        nUser.username = username.toLowerCase();
        nUser.password_hash = BCrypt.hashpw(password,BCrypt.gensalt());
        nUser.email = email;
        nUser.id = genUID();
        return nUser;
    }

    private static String genUID() {
        byte[] byteArr = new byte[15];
        random.nextBytes(byteArr);
        return BaseEncoding.base64Url().encode(byteArr);
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public long getRowId() {
        return rowId;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @Nonnull
    @JsonIgnore
    public List<Playlist> getPlaylists() {
        return playlists;
    }
}
