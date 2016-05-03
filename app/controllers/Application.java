package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import play.routing.JavaScriptReverseRouter;

import models.Users;

import java.util.regex.Pattern;

import play.data.FormFactory;
import views.html.user.signupform;

import javax.inject.Inject;

public class Application extends Controller {

    private final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{4,40}$";
    private final Pattern pattern = Pattern.compile(USERNAME_PATTERN);
    @Inject
    private FormFactory formfactory;

    public Result index() {
        System.out.println(request().host());
        return ok(views.html.index.render("CoPlay"));
    }
    public Result getlogin() {return ok(views.html.user.login.render(""));}

    private long loginRoutine() {
        DynamicForm userForm = formfactory.form().bindFromRequest();
        if (userForm.hasErrors()) {
            return -1;
        }
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        Users user = Users.find.where().eq("username",username).findUnique();
        if (user != null && user.authenticate(password)){
            session("user_id",String.valueOf(user.id));
            session("username",user.username);
            flash("success","Welcome back " +user.username);
        }else{
            flash("error", "Invalid login. Please check your username and password");
            return 1;
        }

        return 0;
        /***need a view page for user profile***/
        /** Mike edit: use redirect so "Confirm Form Resubmission" does not pop up on refresh */
        //return redirect(routes.Application.index());
        //return ok(views.html.index.render("go to profile page"));
    }

    public Result login() {
        long rUser = loginRoutine();
        if (rUser == -1)
            return badRequest(views.html.user.login.render(""));
        if (rUser == 1)
            return redirect(routes.Application.getlogin());

        return redirect(routes.Application.index());
    }
    private void logoutRoutine() {
        //logout stuff here
        session().clear();
    }

    public Result signup(){return ok(views.html.user.signupform.render(""));}

    private long createNewUser(){
        DynamicForm userForm = formfactory.form().bindFromRequest();
        if (userForm.hasErrors()) {
            return -1;
        }
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");
        String email = userForm.data().get("email");

        if(password.isEmpty()||username.isEmpty()||email.isEmpty()){
            flash("error" , "Empty Fields");
            return 1;
        }
        //Check for valid characters for username and password
        if(!pattern.matcher(username).matches() || !pattern.matcher(password).matches()){
            flash("error","Invalid Character");
            return 1;
        }
        //Check if username is already in use
        if(Users.find.where().eq("username",username).findUnique() != null){
            flash("error","Duplicate username");
            return 1;
        }
        //Check if email is already in use
        if(Users.find.where().eq("email",email).findUnique() != null){
            flash("error","Email already registered");
            return 1;
        }

        Users nUser = Users.createUser(username,password,email);
        nUser.save();
        flash("success","Welcome new user "+ nUser.username);
        session("user_id",nUser.id.toString());
        session("username",nUser.username);
        return 0;
        /***need a view page for user profile***/
    }

    public Result newUser() {
        long nUser = createNewUser();
        if (nUser == -1)
            return badRequest(signupform.render(""));
        if (nUser == 1)
            return redirect(routes.Application.signup());

        return redirect(routes.Application.index());
    }


    public Result logout() {
        logoutRoutine();
        return redirect(routes.Application.index());
    }


    /**
     * Class used for routes in javascript
     * All routes to be used in javascript should go in here
     * See:
     *      https://www.playframework.com/documentation/2.5.x/JavaJavascriptRouter#Javascript-Routing
     * for more detail explanation
     * Ensure Javascript is added to conf.routes
     *
     * See:
     *      http://stackoverflow.com/questions/26747536/play-framework-template-that-is-actually-a-js-file
     * For when dynamic JS needs to be built
     * */
    public Result javascriptRoutes() {
        return ok(JavaScriptReverseRouter.create("jsRouter",
                controllers.json.routes.javascript.PlaylistJSON.getPublicPlaylist(),
                routes.javascript.Playlists.getByUID()
            )
        ).as("text/javascript");
    }
}
