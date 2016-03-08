package models;

/**
 * Created by yfle on 3/7/2016.
 */

import com.avaje.ebean.Model;
import javax.persistence.*;
import java.util.List;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

public class Users extends Model{
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]";
    public static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    @Id
    public Long id;

    public String email;

    @Constraints.Required
    @Column(unique = true)
    public String username;

    public String password_hash;

    public static Model.Finder<Long,Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate (String password) {return BCrypt.checkpw(password,this.password_hash);}

    //@OneToMany //not sure mapped by what yet
    //public List<mPlaylist> myPlaylists;

    public static Users createUser(String username, String password, String email){
        String passwordHash = BCrypt.hashpw(password,BCrypt.gensalt());

        Users nUser = new Users();
        nUser.username = username;
        nUser.password_hash = passwordHash;
        nUser.email = email;

        return nUser;
    }

}
