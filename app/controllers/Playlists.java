package controllers;

import com.avaje.ebean.Ebean;
import models.Playlist;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;

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
        return ok(views.html.playlists.single.render(playlist));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        if (formFactory == null) {
            System.out.println("poop");
        }
        DynamicForm playlistForm = formFactory.form().bindFromRequest();
        if (playlistForm.hasErrors()) {
            flash("error", "Invalid form");
            return redirect(request().getHeader("referer"));
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
                return internalServerError("User could not be verified");
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


    private Result playlistNotFound() {
        /* holder for universal playlist not found return page */
        return notFound("playlist not found page goes here");
    }
}
