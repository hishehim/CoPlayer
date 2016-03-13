package controllers;

import models.Users;
import models.mPlaylist;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yfle on 3/13/2016.
 */
public class UserProfile extends Controller {
    @Security.Authenticated(UserAuth.class)
    public Result showProfile(Long id){
        if(Long.parseLong(session().get("user_id")) != id){
            flash().put("error","Nice try, but that is not your profile!");
            return redirect(routes.Application.index());
        }

        Users user = Users.find.byId(id);
        if(user ==  null){
            return notFound("not found");
        }else{
            List<mPlaylist> userPlaylist = user.myPlaylists;
            //create a new list if user did not have a plylist
            if(userPlaylist == null){
                userPlaylist = new ArrayList<>();
                /***Need a Profile View Page***/
                return ok(index.render("Profile"));
            }
        }
        /***Need a Profile View Page***/
        return ok(index.render("Profile"));
    }
}
