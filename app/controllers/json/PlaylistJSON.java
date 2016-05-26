package controllers.json;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import controllers.Application;
import controllers.UserAuth;
import models.Playlist;
import models.Users;
import org.json.JSONObject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Ebean.createSqlQuery;


/**
 * Created by linmh on 4/20/2016.
 */
public class PlaylistJSON extends Controller {

    public Result getPlaylist(String playlistId) {
        if (playlistId == null) {
            return notFound();
        }
        if (!Playlist.UID_PATTERN.matcher(playlistId).matches()) {
            /* invalid id pattern */
            return notFound();
        }

        Playlist playlist = Playlist.find.where().eq("id", playlistId).findUnique();
        if (playlist == null || playlist.getSize() <= 0) {
            return notFound();
/*            JSONObject emptyResult = new JSONObject();
            try {
                emptyResult.put("error", "does not exist");
            } catch (JSONException ex) {
                Logger.error("Error with JSONObject.put(constants)", ex);
            }
            return notFound(Json.toJson(emptyResult));*/
        }
        if (playlist.isPrivate()) {
            String sessionUsrName = Application.getSessionUsrName();
            if (!playlist.getOwner().getUsername().equals(sessionUsrName)) {
                return unauthorized();
            }
        }
        return ok(Json.toJson(playlist)).as("text/json; charset='utf-8'");
    }

    /**
     * Query and find all playlist that are public and returns json
     * */
    public Result getPublicPlaylist() {
        /* Manual query required */
        String sql = "select u.username as owner, p.title, p.id, p.size from playlist p " +
                " inner join users u on u._id = p.owner__id " +
                " where p.is_private = :isPrivate and p.size > 0";
        List<SqlRow> sqlResult = new ArrayList<>();
        sqlResult.addAll(Ebean.createSqlQuery(sql)
                .setParameter("isPrivate", false)
                .findList());
        return ok(Json.toJson(sqlResult));
    }

    public Result getUsrPublicList(String username) {
        if (username == null) {
            return notFound();
        }
        Users user = Users.find.where().eq("username", username).findUnique();
        if (user == null) {
            return notFound(username + " not found");
        } else {
            return ok(Json.toJson(getList(user.getRowId(), false)));
        }
    }

    @Security.Authenticated(UserAuth.class)
    public Result getUsrPrivateList() {
        String userID = Application.getSessionUsrId();
        if (userID.isEmpty()) {
            return forbidden();
        }
        Users user = Users.find.where().eq("id",userID).findUnique();
        if (user == null) {
            session().clear();
            return internalServerError("Error with session data, please log in again");
        }
        return ok(Json.toJson(getList(user.getRowId(), true)));
    }

    private List<SqlRow> getList(long userRowID, boolean isPrivate) {
        String sql = "select p.title, p.id , p.size from playlist p " +
                " where p.is_private = :isPrivate and p.owner__id = :ownerId";
        List<SqlRow> result = new ArrayList<>();
        result.addAll(createSqlQuery(sql)
                .setParameter("isPrivate", isPrivate)
                .setParameter("ownerId", userRowID)
                .findList());
        return result;
    }
}