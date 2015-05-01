package services;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import scala.concurrent.duration.Duration;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PingService {

    private final WSRequestHolder pingRequest;
    private final int frequencyMinutes;

    public PingService() {
        Configuration config = Play.application().configuration();
        String pingRoute = config.getString("pingRoute");
        frequencyMinutes = config.getInt("pingFrequencyMinutes", 50);
        pingRequest = WS.url(pingRoute);
    }

    public void run() {
        schedule(() -> {
            Logger.info("Sending Ping Request: " + Date.from(Instant.now()));
            pingRequest.get();
        });
    }

    private void schedule(Runnable runnable) {
        Akka.system().scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS),
                Duration.create(frequencyMinutes, TimeUnit.MINUTES), runnable, Akka.system().dispatcher());
    }
}
