package services;

import com.ning.http.client.Response;
import play.Application;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.libs.ws.ning.NingWSResponse;

public class PlexSyncService {

    public static final String PUBLISH_PATH = "/:/prefs?PublishServerOnPlexOnlineKey=true";
    public static final String ACCOUNT_PATH = "/myplex/account";
    public static final String TOKEN_HEADER = "X-Plex-Token";

    private String hostname;
    private final String token;

    public PlexSyncService() {
        Application app = play.Play.application();
        String host = app.configuration().getString("plex.host");
        String port = app.configuration().getString("plex.port");
        token = app.configuration().getString("plex.token");
        hostname = host + ":" + port;
    }

    public F.Promise<WSResponse> syncServer() {
        WSRequestHolder request = WS.url(hostname + PUBLISH_PATH).setTimeout(5000);
        Logger.info("Syncing server");
        return setToken(request).put("").recover(t -> {
            Logger.error("Sync failed. Server offline", t);
            return new FailureResponse();
        });
    }

    public F.Promise<String> serverConnectionStatus() {
        WSRequestHolder request = WS.url(hostname + ACCOUNT_PATH).setTimeout(5000);
        Logger.info("Checking Server Connection Status");
        return setToken(request).get()
                .map(r -> r.getBody().contains("publicPort") ? "Connected" : "Disconnected")
                .recover(t -> {
                    Logger.error("Server status failed. Server offline", t);
                    return "Offline";
                });
    }

    private WSRequestHolder setToken(WSRequestHolder request) {
        return request.setHeader(TOKEN_HEADER, token);
    }

    private static class FailureResponse extends NingWSResponse {

        public FailureResponse() {
            super(null);
        }

        @Override
        public String getStatusText() {
            return "Failed";
        }
    }
}
