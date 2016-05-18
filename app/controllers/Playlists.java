package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import models.Playlist;
import models.PlaylistItem;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;
import statics.DomainData;
import statics.Domain;
import statics.DomainWrapper;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by linmh on 3/18/2016.
 */
public class Playlists extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result play(String playlistId) {

        //return ok(views.html.playlists.playpage.render(null, null));

        if (playlistId == null || playlistId.isEmpty()) {
            return movedPermanently(routes.Application.index());
        } else if (!Playlist.UID_PATTERN.matcher(playlistId).matches()) {
            return playlistNotFound();
        }
        Playlist playlist = Playlist.find.where().eq("id", playlistId).findUnique();
        if (playlist == null || playlist.getSize() <= 0) {
            return playlistNotFound();
        }
        return ok(views.html.playlists.playpage.render(playlist, playlist.getTracks()));
    }

    @Security.Authenticated(UserAuth.class)
    public Result edit(String id) {
        if (id == null || id.isEmpty()) {
            /* Empty id should be redirected to main page */
            return movedPermanently(routes.Application.index());
        } else if (!Playlist.UID_PATTERN.matcher(id).matches()) {
            return notFound("Playlist not found page goes here");
        }

        Playlist playlist = Playlist.find.where().eq("id", id).findUnique();
        if (playlist == null) {
            return notFound("Playlist not found page goes here " + id);
        }

        return ok(views.html.playlists.edit.render(playlist, playlist.getOwner()));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        DynamicForm playlistForm = formFactory.form().bindFromRequest();
        if (playlistForm.hasErrors()) {
            flash("error", "Invalid form");
            return badRequest();
        }

        String userID = Application.getSessionUsrId();
        String title = playlistForm.get("title");

        if (title == null || title.isEmpty()) {
            flash("error", "Invalid title");
            return redirect(request().getHeader("referer"));
        }
        String sql = "select u._id from playlist p " +
                " inner join users u on u._id = p.owner__id " +
                " where p.title = :title and u.id = :userID";
        List<SqlRow> sqlResult = Ebean.createSqlQuery(sql)
                    .setParameter("title", title)
                    .setParameter("userID", userID)
                    .findList();
        if (!sqlResult.isEmpty()) {
            flash("error", "title already in use");
            return redirect(request().getHeader("referer"));
        }

        Ebean.beginTransaction();
        try {
            Users user = Users.find.where().eq("id",userID).findUnique();
            /*
            * Validate user still exist for the entire duration of this transaction
            * */
            if (user == null) {
                // session user info went out of sync with db somehow
                Logger.error("Playlist.create(): Authenticated user no longer in DB", userID);
                // force log out ? logout()
                return internalServerError("Error verifying user data");
            }

            Playlist nPlaylist = Playlist.getNewPlaylist(title, user);
            if (nPlaylist == null) {
                flash("error", "could not create new playlist"); // REMOVE ON DEPLOY
                // too many collision on ID gen, need to expand ID length
                // return bad request with unable to generate new playlist error
                // return redirect(request().getHeader("referer"));
                return internalServerError("Failed to create new playlist");
            }
            nPlaylist.save();
            Ebean.commitTransaction();
            return redirect(routes.Playlists.edit(nPlaylist.getId()));
        } finally {
            Ebean.endTransaction();
        }
    }

    @Security.Authenticated(UserAuth.class)
    public Result remove(String playlistId) {
        if (playlistId == null || playlistId.isEmpty()) {
            return movedPermanently(routes.Application.index());
        }
        String userID = Application.getSessionUsrId();
        String username = Application.getSessionUsrName();
        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("id", playlistId).findUnique();
            if (playlist == null) {
                return playlistNotFound();
            }
            if (!playlist.getOwner().getId().equals(userID)) {
                //flash("error", "You're not the owner of the playlist");
                return forbidden("You're not the owner " + playlist.getOwner().getId() + " " + userID);
            }
            playlist.delete();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.UserProfile.showProfile(username));
    }

    @Security.Authenticated(UserAuth.class)
    public Result addItem(String playlistID) {
        DynamicForm nItemForm = formFactory.form().bindFromRequest();
        if (nItemForm.hasErrors()) {
            return badRequest();
        }

        String urlStr = nItemForm.get("src-url");
        String srcTypeStr = nItemForm.get("src-type");
        Domain srcDomain = DomainData.getDomain(srcTypeStr);

        if (srcDomain == null) {
            flash("error", "Invalid source type");
            /* TODO redirect route should be the previous page or default to playlist page
            */
            return redirect(routes.Playlists.edit(playlistID));
        }

        DomainWrapper wrapper = DomainData.getDomainWrapper(srcDomain);
        if (!wrapper.validate(urlStr)) {
            flash("error", "invalid identifier for " + srcTypeStr + " id");
            return redirect(routes.Playlists.edit(playlistID));
        }

        String userID = Application.getSessionUsrId();

        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("id", playlistID).findUnique();
            if (playlist == null) {
                return notFound("Playlist does not exist");
            }
            if (!(playlist.getOwner().getId().equals(userID))) {
                /*
                 * flash("error", "You are not the owner of this playlist! Your attempt has been logged");
                 * String logMsg = "User " + session("user_id") + " ftried to access playlist " +
                 * playlistUID + " which belongs to user " + playlist.getOwner().rowID;
                 * Logger.info(logMsg);
                 */
                return forbidden();
            }
            PlaylistItem nItem = PlaylistItem.getNewItem(urlStr, playlist, srcDomain);
            nItem.save();
            playlist.increaseSize();
            playlist.update();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.Playlists.edit(playlistID));
    }

    @Security.Authenticated(UserAuth.class)
    public Result removeItem(String playlistID) {

        DynamicForm itemForm = formFactory.form().bindFromRequest();

        if (itemForm.hasErrors()) {
            return badRequest();
        }

        long itemID;

        try {
            itemID = Long.parseLong(itemForm.get("it-id"));
        } catch (NumberFormatException ex) {
            return badRequest();
        }
        String userId = Application.getSessionUsrId();

        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("id", playlistID).findUnique();
            if (playlist == null) {
                return playlistNotFound();
            }
            if (!playlist.getOwner().getId().equals(userId)) {
                return forbidden();
            }
            PlaylistItem playlistItem = PlaylistItem.find.byId(itemID);
            if (playlistItem == null) {
                return notFound();
            }
            playlistItem.delete();
            playlist.decreaseSize();
            playlist.update();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.Playlists.edit(playlistID));
    }

    private void getRedirectURL() {
        System.out.println(request().host());
    }

    private Result playlistNotFound() {
        /* holder for universal playlist not found return page */
        return notFound("playlist not found page goes here");
    }

}