import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.PingService;

public class Global extends GlobalSettings {
    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.promise(() -> Results.notFound("404 PAGE NOT FOUND"));
    }

    @Override
    public void onStart(Application application) {
        boolean pingEnabled = Play.application().configuration().getBoolean("pingEnabled", true);
        if (pingEnabled) {
            Logger.info("Starting Ping Service");
            new PingService().run();
        }
    }
}
