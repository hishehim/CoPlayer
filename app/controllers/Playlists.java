package controllers;

import models.Playlist;
import models.PlaylistItem;
import models.Users;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by linmh on 3/18/2016.
 */
public class Playlists extends Controller {

    /* Temporary method to retrieve a single playlist. Currently using id, will
     *  be replaced with uuid to make each playlist more obscure to general user */
    public Result showList(String idString) {
        String playlistId;
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
            playlistId = idString;
        } catch (NumberFormatException e) {
            return badRequest("failed");
        }
        Playlist playlist = Playlist.find.byId(playlistId);
        return ok(views.html.playlists.singlePlaylist.render());
    }

    public Result getPublicList() {
        return ok(Json.toJson(Playlist.find.where().eq("is_private", false).findList()));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        /* CHANGE TYPE WHEN USER ID SWITCH TO UID */
        DynamicForm playlistForm = form().bindFromRequest();
        if (playlistForm.hasErrors()) {
            flash("error", "Invalid form");
            return badRequest(request().getHeader("referer"));
        }
        String title = playlistForm.get("title");
        if (title == null || title.isEmpty()) {
            flash("error", "Invalid title");
            return badRequest(request().getHeader("referer"));
        }
        if (!Playlist.find.where().eq("title", title).findList().isEmpty()) {
            flash("error", "title already in use");
            return badRequest(request().getHeader("referer"));
        }
        long userID;

        try {
            userID = Long.parseLong(session("user_id"));
        } catch (NumberFormatException e) {
            // error parse user ID as long
            flash("error", "Something is wrong with your session. Plase relog!");
            return badRequest(request().getHeader("referer"));
        }

        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        Playlist nPlaylist = Playlist.getNewPlaylist(title, user);

        if (nPlaylist == null) {
            flash("error", "too many collisions"); // REMOVE ON DEPLOY
            // too many collision on ID gen, need to expand ID length
            // return bad request with unable to generate new playlist error
            return internalServerError(request().getHeader("referer"));
        }
        nPlaylist.save();
        flash("success", "New playlist created!");
        return ok(views.html.playlists.editor.render(nPlaylist.getTitle(), nPlaylist.getId()));
    }

    public Result addItem(@Nonnull String listID) {
        DynamicForm playlistItemForm = form().bindFromRequest();
        if (playlistItemForm.hasErrors()) {
            // redirect to else where
            return badRequest();
        }
        PlaylistItem nPlaylistItem = PlaylistItem.getNewItem(playlistItemForm.get("url"));
        return ok(Json.toJson(nPlaylistItem));
    }
}
