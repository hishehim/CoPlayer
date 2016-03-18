package controllers;

import models.Playlist;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linmh on 3/18/2016.
 */
public class Playlists extends Controller {

    public Result fetchPublicLists() {
        List<Playlist> publicLists = Playlist.find.where()
                .eq("isPrivate", false)
                .orderBy("createTime")
                .findList();
        if (publicLists == null) {
            publicLists = new ArrayList<>();
        }
        return ok(views.html.playlists.listall.render(publicLists));
    }
}
