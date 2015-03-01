package controllers;

import forms.Login;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import services.PlexSyncService;
import views.html.index;
import views.html.sync;

public class Application extends Controller {

    private static final Configuration CONFIG = play.Play.application().configuration();

    private static final String USERNAME = CONFIG.getString("username");
    private static final String PASSWORD = CONFIG.getString("password");
    public static final String AUTHORIZED = "authorized";

    private static final PlexSyncService syncService = new PlexSyncService();

    public static Result index() {
        if (!isAuthorized()) {
            return ok(index.render());
        }

        return redirect("/syncStatus");
    }

    public static Result login() {
        Form<Login> form = new Form<>(Login.class).bindFromRequest(request());
        if (form.hasErrors()) {
            Logger.warn("login error " + form.errors());
            session().clear();
            return redirect("/");
        }

        Login login = form.get();

        String username = login.username;
        String password = login.password;

        if (!username.equals(USERNAME) || !password.equals(PASSWORD)) {
            Logger.warn("Login failed username: " + username + " password: " + password);
            session().clear();
            return redirect("/");
        }

        session(AUTHORIZED, USERNAME);
        return redirect("/syncStatus");
    }

    public static F.Promise<Result> syncStatus() {
        if (!isAuthorized()) {
            return F.Promise.pure(redirect("/"));
        }

        F.Promise<String> connected = syncService.isServerConnected();
        return connected.map(conn -> ok(sync.render(conn)));
    }

    public static F.Promise<Result> syncServer() {
        if (!isAuthorized()) {
            return F.Promise.pure(unauthorized());
        }

        return syncService.syncServer().map(r -> ok(r.getStatusText()));
    }

    private static boolean isAuthorized() {
        return session().containsKey(AUTHORIZED) && session().get(AUTHORIZED).contains(USERNAME);
    }
}


