package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Created by yfle on 3/13/2016.
 */
public class UserProfile extends Controller {

    public Result showProfile(String username){
        if (username == null) {
            return movedPermanently(routes.Application.index());
        }
        /*if(Application.getSessionUsrId() != rowID){
            flash().put("error","Nice try, but that is not your profile!");
            return redirect(routes.Application.index());
        }*/
        Users user = Users.find.where().eq("username",username).findUnique();
        if(user ==  null){
            return notFound(username + " user not found");
        }

        //@Nonnull List<Playlist> userPlaylist = user.getPlaylists();
        //create a new list if user did not have a playlists
        /***Need a Profile View Page***/
        return ok(views.html.user.profile.render(user,
                user.getId().equals(Application.getSessionUsrId())));
    }
}