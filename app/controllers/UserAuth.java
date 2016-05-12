package controllers;

import models.Users;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by yfle on 3/13/2016.
 */
public class UserAuth extends Security.Authenticator{
    @Override
    public String getUsername(final Http.Context ctx){
        if (ctx.session().containsKey("user_id")) {
            Users user = Users.find.where().eq("id", ctx.session().get("user_id")).findUnique();
            if (user == null) {
                ctx.session().clear();
                return null;
            }
            return user.getUsername();
        }
        return null;
    }

    @Override
    public Result onUnauthorized(final Http.Context ctx){
        ctx.flash().put("error", "Please log in first");
        return redirect(routes.Application.index());
    }
}