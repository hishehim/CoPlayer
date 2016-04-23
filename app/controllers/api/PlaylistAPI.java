package controllers.api;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import models.Playlist;
import org.json.JSONException;
import org.json.JSONObject;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


import javax.annotation.Nonnull;
import java.util.List;

import static com.avaje.ebean.Ebean.createSqlQuery;
import static javafx.beans.binding.Bindings.select;
import static sun.misc.MessageUtils.where;

/**
 * Created by linmh on 4/20/2016.
 */
public class PlaylistAPI extends Controller {
    public Result getPlaylist(@Nonnull String uid) {
        Playlist playlist = Playlist.find.where().eq("uid", uid).findUnique();
        if (playlist != null) {
            return ok(Json.toJson(playlist));
        }
        JSONObject emptyResult = new JSONObject();
        try {
            emptyResult.put("error", "does not exit");
        } catch (JSONException exp) {

        }
        return ok(Json.toJson(emptyResult));
        // TO DO
    }

    /**
     * Query and find all playlist that are public and returns json
     * */
    public Result getPublicPlaylist() {

        String sql = "select u.username as owner, p.title, p.id, p.size from playlist p " +
                " inner join users u on u.id = p.owner_id " +
                " where p.is_private = :is_private";
        List<SqlRow> sqlResult = Ebean.createSqlQuery(sql)
                .setParameter("is_private", false)
                .findList();
        return ok(Json.toJson(sqlResult));
    }
}