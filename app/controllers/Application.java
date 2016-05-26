package controllers;

import com.avaje.ebean.Ebean;
import play.data.DynamicForm;
import play.mvc.*;
import play.routing.JavaScriptReverseRouter;

import models.Users;

import java.util.Random;
import java.util.regex.Pattern;

import play.data.FormFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class Application extends Controller {

    private final Pattern username_pattern = Pattern.compile("^[a-zA-Z0-9_-]{4,40}$");
    private final Pattern password_pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,256}$");
    public static final Random random = new Random();

    @Inject
    private FormFactory formfactory;

    /**
     * Utility function to get the user_id from session as long
     * @return returns the rowID of the logged in user, -1 if user is not logged in
     * */
    public static String getSessionUsrId() {
        if (session().containsKey("user_id")) {
            return session("user_id");
        } else {
            return "";
        }
    }

    public static String getSessionUsrName() {
        if (session().containsKey("username")) {
            return session("username");
        } else {
            return "";
        }
    }

    public Result index() {
        return ok(views.html.index.render("CoPlay"));
    }

    public Result login() {

        DynamicForm userForm = formfactory.form().bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.index.render(""));
        }

        String username = userForm.data().get("username").toLowerCase();
        String password = userForm.data().get("password");

        Users user = null;
        if (username.contains("@")) {
            user = Users.find.where().eq("email", username).findUnique();
        } else {
            user = Users.find.where().eq("username",username).findUnique();
        }
        if (user != null && user.authenticate(password)){
            login(user);
            flash("success","Welcome back " +user.getUsername());
        }else {
            flash("error", "Invalid login. Please check your username and password");
            return redirect(routes.Application.index());
        }

        return redirect(routes.UserProfile.showProfile(user.getUsername()));
    }

    private static void login(Users user) {
        logoutRoutine();
        session("user_id",user.getId());
        session("user_row_id", String.valueOf(user.getRowId()));
        session("username",user.getUsername());
    }

    public static void logoutRoutine() {
        session().clear();
    }

    public Result signup(){return ok(views.html.index.render("CoPlay"));}

    public Result newUser(){
        DynamicForm userForm = formfactory.form().bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.index.render("CoPlay"));
        }
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");
        String email = userForm.data().get("email");

        /*
        if(password.isEmpty()||username.isEmpty()||email.isEmpty()){
            flash("error" , "Empty Fields");
            return redirect(routes.Application.signup());
        }*/
        //Check for valid characters for username and password
        if(!username_pattern.matcher(username).matches() || !password_pattern.matcher(password).matches()){
            flash("error","Invalid Character");
            return redirect(routes.Application.signup());
        }
        //Check if username is already in use
        if(Users.find.where().eq("username",username).findUnique() != null){
            flash("error","Duplicate username");
            return redirect(routes.Application.signup());
        }
        //Check if email is already in use
        if(Users.find.where().eq("email",email).findUnique() != null){
            flash("error","Email already registered");
            return redirect(routes.Application.signup());
        }

        Users nUser = Users.createUser(username,password,email);
        nUser.save();
        flash("success","Welcome new user "+ nUser.getUsername());

        login(nUser);

        return redirect(routes.UserProfile.showProfile(username));
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
                routes.javascript.Playlists.play(),
                routes.javascript.Playlists.edit(),
                routes.javascript.Playlists.addItem(),
                routes.javascript.UserProfile.showProfile(),
                controllers.json.routes.javascript.PlaylistJSON.getPlaylist(),
                controllers.json.routes.javascript.PlaylistJSON.getPublicPlaylist(),
                controllers.json.routes.javascript.PlaylistJSON.getUsrPublicList(),
                controllers.json.routes.javascript.PlaylistJSON.getUsrPrivateList()
            )
        ).as("text/javascript");
    }
}