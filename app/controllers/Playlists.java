package controllers;

import models.Playlist;
import play.mvc.*;

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
}
