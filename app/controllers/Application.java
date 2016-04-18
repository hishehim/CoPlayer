package controllers;

import play.*;
import play.data.DynamicForm;
import play.mvc.*;

import views.html.*;
import models.Users;

import java.util.regex.Pattern;

import static play.data.Form.form;

public class Application extends Controller {


    final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]$";
    final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }
    public Result getlogin() {return ok(views.html.login.render(""));}

    public Result login() {
        DynamicForm userForm = form().bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.login.render(""));
        }
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        Users user = Users.find.where().eq("username",username).findUnique();
        /***need to set up flash message on main***/
        if (user != null && user.authenticate(password)){
            session("user_id",String.valueOf(user.id));
            session("username",user.username);
            flash("success","Welcome back " +user.username);
        }else{
            flash("error", "Invalid login. Please check your username and password");
            return redirect(routes.Application.getlogin());
        }
        /***need a view page for user profile***/
        return ok(index.render("go to profile page"));
    }

    public Result logout() {
        session().remove("user_id");
        return redirect(routes.Application.index());
    }

    public Result signup(){return ok(views.html.signupform.render(""));}

    public Result newUser() {

        DynamicForm userForm = form().bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.signupform.render(""));
        }
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");
        String email = userForm.data().get("email");

        //Check for empty fields in the form
        if(password.isEmpty()||username.isEmpty()||email.isEmpty()){
            flash("error" , "Empty Fields");
            return redirect(routes.Application.signup());
        }
        //Check for valid characters for username and password
        if(!pattern.matcher(username).matches() || !pattern.matcher(password).matches()){
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
        flash("success","Welcome new user "+ nUser.username);
        /***need a view page for user profile***/
        return ok(index.render("go to profile page"));

    }
}
