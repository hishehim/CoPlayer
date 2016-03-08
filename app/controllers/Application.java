package controllers;

import play.*;
import play.data.DynamicForm;
import play.mvc.*;

import views.html.*;
import models.Users;

import static play.data.Form.form;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }
    /***Need a view page for getting to the login page***/
    public Result getlogin() {return ok(index.render("login"));}

    public Result login() {
        DynamicForm userForm = form().bindFromRequest();
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        Users user = Users.find.where().eq("username",username).findUnique();
        /***need to set up flash message on main***/
        if (user != null && user.authenticate(password)){
            session("user_id",user.id.toString());
            flash("success","Welcome back " +user.username);
        }else{
            flash("error", "Invalid login. Please check your username and password");
            /*** login page ***/
            return ok(index.render("back to login page"));
        }
        /***need a view page for user profile***/
        return ok(index.render("go to profile page"));
    }
}
