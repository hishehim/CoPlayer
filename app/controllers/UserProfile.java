package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yfle on 3/13/2016.
 */
public class UserProfile extends Controller {

    public Result showProfile(long id){
        /*if(Application.getSessionUsrId() != id){
            flash().put("error","Nice try, but that is not your profile!");
            return redirect(routes.Application.index());
        }*/

        Users user = Users.find.byId(id);
        if(user ==  null){
            return notFound("User not found");
        }

        @Nonnull List<Playlist> userPlaylist = user.playlists;
        //create a new list if user did not have a playlists
        /***Need a Profile View Page***/
        return ok(views.html.user.profile.render(user,
                (user.id == Application.getSessionUsrId())));
    }
}