package controllers;

import models.Playlist;
import models.Users;
import play.libs.Json;
import play.mvc.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linmh on 3/18/2016.
 */
public class Playlists extends Controller {

    /* Temporary method to retrieve a single playlist. Currently using id, will
     *  be replaced with uuid to make each playlist more obscure to general user */
    public Result showList(String idString) {
        long playlistId;
        if (idString == null) {
            List<Playlist> publicLists = Playlist.find.where()
                    .eq("isPrivate", false)
                    .orderBy("createTime")
                    .findList();
            if (publicLists == null) {
                publicLists = new ArrayList<>();
            }
            return ok(views.html.playlists.listall.render(publicLists));
        }
        try {
            playlistId = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            return badRequest("failed");
        }
        Playlist playlist = Playlist.find.byId(playlistId);
        return ok(views.html.playlists.singlePlaylist.render());
    }

    public Result getPublicList() {
        return ok(Json.toJson(Playlist.find.where().eq("is_private", false).findList()));
    }

    public Result create(@Nonnull String title, @Nonnull Users owner) {
        Playlist nPlaylist = Playlist.getNewPlaylist(title, owner);
        if (nPlaylist == null) {
            // too many collision on ID gen, need to expand ID length
            // return bad request with unable to generate new playlist error
        }
        return showList(null);
    }
}
