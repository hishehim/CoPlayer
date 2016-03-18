import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import controllers.*;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {
    /*
    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2, a);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertEquals("text/html", contentType(html));
        assertTrue(contentAsString(html).contains("Your new application is ready."));
    }
    */

    public FakeApplication app;

    @Before
    public void startApp() {
        System.out.println("\n\nTest begin\n");
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);
    }

    @After
    public void stopApp() {
        Helpers.stop(app);
        System.out.println("\nTest Ended\n\n");
    }

    @Test
    public void playlistListTest() {
        Users user = Users.find.where().eq("username", "test").findUnique();
        if (user != null) {
            System.out.println("User name: " + user.username);
            user.delete();
        }

        user = Users.createUser("test", "testpassword1", "test@testemail.com");
        user.save();

        for (int i = 0; i < 100; i++) {
            Playlist playlist = Playlist.getNewPlaylist("title-" + i, user);
            playlist.save();
            System.out.println("New Playlist Added: " + playlist.getTitle() + " - UUID: " + playlist.getUuid());
        }

        Playlist dup = Playlist.getNewPlaylist("title-4", user);
        try {
            dup.save();
        } catch (PersistenceException exception) {
            System.out.println("** Duplicate detected ** Failed to Save **");
        }

        user = Users.find.where().eq("username", "test").findUnique();
        user.delete();
        if (!Playlist.find.all().isEmpty()) {
            System.out.print("Cascade has failed");
        };
    }
}
