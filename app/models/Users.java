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
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

@Entity
public class Users extends Model{

    @Id
    @JsonIgnore
    public Long id;

    @Constraints.Required
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Column(unique = true)
    public String username;

    @Constraints.Required
    @Column(unique = true)
    @JsonIgnore
    public String password_hash;

    public static Finder<Long,Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate (String password) {return BCrypt.checkpw(password,this.password_hash);}

    @JsonIgnore
    @Nonnull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    public List<Playlist> playlists = new ArrayList<>();

    public static Users createUser(String username, String password, String email){
        String passwordHash = BCrypt.hashpw(password,BCrypt.gensalt());

        Users nUser = new Users();
        nUser.username = username;
        nUser.password_hash = passwordHash;
        nUser.email = email;

        return nUser;
    }

}
