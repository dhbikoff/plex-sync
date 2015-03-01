import play.GlobalSettings;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public class Global extends GlobalSettings {
    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.promise(() -> Results.notFound("404 PAGE NOT FOUND"));
    }
}
