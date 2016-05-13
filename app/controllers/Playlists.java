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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.avaje.ebean.Ebean.createSqlQuery;

/**
 * Created by linmh on 3/18/2016.
 */
public class Playlists extends Controller {

    private static final Pattern UID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{12,20}$");

    @Inject
    private FormFactory formFactory;

    public Result play(String id) {

        return ok(views.html.playlists.playpage.render(null, null));
/*
        if (id == null || id.isEmpty()) {
            return movedPermanently(routes.Application.index());
        } else if (!UID_PATTERN.matcher(id).matches()) {
            return playlistNotFound();
        }
        Playlist playlist = Playlist.find.where().eq("id", id).findUnique();
        if (playlist == null) {
            return playlistNotFound();
        }
        return ok(views.html.playlists.playpage.render(playlist, playlist.getListItems()));*/
    }

    public Result getById(String id) {
        if (id == null || id.isEmpty()) {
            /* Empty id should be redirected to main page */
            return movedPermanently(routes.Application.index());
        } else if (!UID_PATTERN.matcher(id).matches()) {
            return notFound("Playlist not found page goes here");
        }

        Playlist playlist = Playlist.find.where().eq("id", id).findUnique();
        if (playlist == null) {
            return notFound("Playlist not found page goes here " + id);
        }

        return ok(views.html.playlists.single.render(playlist, playlist.getOwner()));
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
            return redirect(routes.Playlists.getById(nPlaylist.getId()));
        } finally {
            Ebean.endTransaction();
        }
    }

    @Security.Authenticated(UserAuth.class)
    public Result remove() {
        DynamicForm form = formFactory.form().bindFromRequest();
        if (form.hasErrors()) {
            return badRequest();
        }
        String plId = form.get("pl-uid");

        String userID = Application.getSessionUsrId();

        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("id", plId).findUnique();
            if (playlist == null) {
                return playlistNotFound();
            }
            if (playlist.getOwner().getId().equals(userID)) {
                //flash("error", "You're not the owner of the playlist");
                return forbidden("You're not the owner");
            }
            playlist.delete();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.UserProfile.showProfile(userID));
    }

    @Security.Authenticated(UserAuth.class)
    public Result addItem(String playlistID) {
        DynamicForm nItemForm = formFactory.form().bindFromRequest();
        if (nItemForm.hasErrors()) {
            return badRequest();
        }

        String urlStr = nItemForm.get("url");
        String srcTypeStr = nItemForm.get("source_type");
        Domain srcDomain = DomainData.getDomain(srcTypeStr);

        if (srcDomain == null) {
            flash("error", "Invalid source type");
            /* TODO redirect route should be the previous page or default to playlist page
            */
            return redirect(routes.Playlists.getById(playlistID));
        }

        // source type to be verified later
        // url to be verified later

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
                 * String logMsg = "User " + session("user_id") + " tried to access playlist " +
                 * playlistUID + " which belongs to user " + playlist.getOwner().rowID;
                 * Logger.info(logMsg);
                 */
                return forbidden();
            }
            PlaylistItem nItem = PlaylistItem.getNewItem(urlStr, playlist, srcDomain);
            nItem.save();
            playlist.update();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.Playlists.getById(playlistID));
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
            if (playlist.getOwner().getId().equals(userId)) {
                return forbidden();
            }
            PlaylistItem playlistItem = PlaylistItem.find.byId(itemID);
            if (playlistItem == null) {
                return notFound();
            }
            playlistItem.delete();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.Playlists.getById(playlistID));
    }

    private void getRedirectURL() {
        System.out.println(request().host());
    }

    private Result playlistNotFound() {
        /* holder for universal playlist not found return page */
        return notFound("playlist not found page goes here");
    }

}