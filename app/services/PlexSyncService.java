package services;

import play.Application;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

public class PlexSyncService {

    public static final String PUBLISH_PATH = "/:/prefs?PublishServerOnPlexOnlineKey=true";
    public static final String ACCOUNT_PATH = "/myplex/account";
    public static final String TOKEN_HEADER = "X-Plex-Token";

    private String hostname;
    private final String token;

    @SuppressWarnings("unchecked")
    public PlexSyncService() {
        Application app = play.Play.application();
        String host = app.configuration().getString("plex.host");
        String port = app.configuration().getString("plex.port");
        token = app.configuration().getString("plex.token");
        hostname = host + ":" + port;
    }

    public F.Promise<WSResponse> syncServer() {
        WSRequestHolder request = WS.url(hostname + PUBLISH_PATH);
        return setToken(request).put("");
    }

    public F.Promise<String> isServerConnected() {
        WSRequestHolder request = WS.url(hostname + ACCOUNT_PATH);
        return setToken(request).get().map(r -> r.getBody().contains("publicPort") ? "Connected" : "Disconnected");
    }

    private WSRequestHolder setToken(WSRequestHolder request) {
        return request.setHeader(TOKEN_HEADER, token);
    }
}
