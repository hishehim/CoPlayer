package models;

/**
 * Created by yfle on 3/7/2016.
 */

import com.avaje.ebean.Model;
import javax.persistence.*;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

public class Users extends Model{

    @Id
    public Long id;

    @Constraints.Required
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Column(unique = true)
    public String username;

    @Constraints.Required
    @Column(unique = true)
    public String password_hash;

    public static Model.Finder<Long,Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate (String password) {return BCrypt.checkpw(password,this.password_hash);}

    @OneToMany(mappedBy = "uuid") //not sure mapped by what yet
    public List<mPlaylist> myPlaylists;

    public static Users createUser(String username, String password, String email){
        String passwordHash = BCrypt.hashpw(password,BCrypt.gensalt());

        Users nUser = new Users();
        nUser.username = username;
        nUser.password_hash = passwordHash;
        nUser.email = email;

        return nUser;
    }

}
