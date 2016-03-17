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
        String userIDStr = ctx.session().get("user_id");
        if(userIDStr == null)
            return null;

        Users user = Users.find.byId(Long.parseLong(userIDStr));
        if (user == null) {
            ctx.session().clear();
            return null;
        }
        return  String.valueOf(user.id);
    }

    @Override
    public Result onUnauthorized(final Http.Context ctx){
        ctx.flash().put("error", "Please log in first");
        return redirect(routes.Application.index());
    }
}
