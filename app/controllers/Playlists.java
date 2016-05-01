package controllers;

import com.avaje.ebean.Ebean;
import models.Playlist;
import models.PlaylistItem;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;
import statics.SourceType;

import javax.inject.Inject;
import java.util.regex.Pattern;

/**
 * Created by linmh on 3/18/2016.
 */
public class Playlists extends Controller {

    private static final Pattern UID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{12,20}$");

    @Inject
    private FormFactory formFactory;

    public Result getByUID(String uid) {
        if (uid == null || uid.isEmpty()) {
            /* Empty uid should be redirected to main page */
            return movedPermanently(routes.Application.index());
        } else if (!UID_PATTERN.matcher(uid).matches()) {
            return notFound("BAD ID Playlist not found page goes here");
        }

        Playlist playlist = Playlist.find.where().eq("uid", uid).findUnique();
        if (playlist == null) {
            return notFound("NOT FOUND Playlist not found page goes here " + uid);
        }

        return ok(views.html.playlists.single.render(playlist,
                                        (getSessionUsrId() == playlist.getOwner().id)));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        DynamicForm playlistForm = formFactory.form().bindFromRequest();
        if (playlistForm.hasErrors()) {
            flash("error", "Invalid form");
            return badRequest();
        }
        String title = playlistForm.get("title");
        if (title == null || title.isEmpty()) {
            flash("error", "Invalid title");
            return redirect(request().getHeader("referer"));
        }
        if (!Playlist.find.where().eq("title", title).findList().isEmpty()) {
            flash("error", "title already in use");
            return redirect(request().getHeader("referer"));
        }
        long userID;

        try {
            userID = Long.parseLong(session("user_id"));
        } catch (NumberFormatException e) {
            // error parse user ID as long
            flash("error", "Something went wrong with your session. Please relog!");
            // logout()
            return redirect(request().getHeader("referer"));
        }

        Ebean.beginTransaction();
        try {
            Users user = Users.find.byId(Long.parseLong(session("user_id")));
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
                return internalServerError("Could not create new playlist");
            }
            nPlaylist.save();
            Ebean.commitTransaction();
            return redirect(routes.Playlists.getByUID(nPlaylist.getUID()));
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
        String uid = form.get("pl-uid");

        long userID = getSessionUsrId();

        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("uid", uid).findUnique();
            if (playlist == null) {
                return playlistNotFound();
            }
            if (playlist.getOwner().id != userID) {
            /*
            * TODO separate normal error from AUTHENTICATION error
            * */
                flash("error", "You're not the owner of the playlist");
                return unauthorized();
            }
            playlist.delete();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.UserProfile.showProfile(userID));
    }

    @Security.Authenticated(UserAuth.class)
    public Result addItem(String playlistUID) {
        DynamicForm nItemForm = formFactory.form().bindFromRequest();
        if (nItemForm.hasErrors()) {
            return badRequest();
        }

        String urlStr = nItemForm.get("url");
        String srcTypeStr = nItemForm.get("source_type");
        SourceType.Type srcType = SourceType.sourceMap.get(srcTypeStr);

        if (srcType == null) {
            flash("error", "Invalid source type");
            /* TODO redirect route should be the previous page or default to playlist page
            */
            return redirect(routes.Playlists.getByUID(playlistUID));
        }

        // source type to be verified later
        // url to be verified later

        long userID = getSessionUsrId();

        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("uid", playlistUID).findUnique();
            if (playlist == null) {
                return notFound("Playlist does not exist");
            }
            if (!(playlist.getOwner().id == userID)) {
                /*
                 * flash("error", "You are not the owner of this playlist! Your attempt has been logged");
                 * String logMsg = "User " + session("user_id") + " tried to access playlist " +
                 * playlistUID + " which belongs to user " + playlist.getOwner().id;
                 * Logger.info(logMsg);
                 */
                return unauthorized();
            }
            PlaylistItem nItem = PlaylistItem.getNewItem(urlStr, playlist, srcType);
            playlist.increaseSize();
            nItem.save();
            playlist.update();
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.Playlists.getByUID(playlistUID));
    }

    @Security.Authenticated(UserAuth.class)
    public Result removeItem(String uid) {
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
        long userId = getSessionUsrId();

        Ebean.beginTransaction();
        try {
            Playlist playlist = Playlist.find.where().eq("uid", uid).findUnique();
            if (playlist == null) {
                return notFound();
            }
            if (playlist.getOwner().id != userId) {
                return unauthorized();
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
        return redirect(routes.Playlists.getByUID(uid));
    }

    private void getRedirectURL() {
        System.out.println(request().host());
    }

    /**
     * Utility function to get the user_id from session as long
     * @return returns the id of the logged in user, -1 if user is not logged in
     * */
    private static long getSessionUsrId() {
        try {
            return Long.parseLong(session("user_id"));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private Result playlistNotFound() {
        /* holder for universal playlist not found return page */
        return notFound("playlist not found page goes here");
    }

}