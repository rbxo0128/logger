package slack;

import util.logger.MyLogger;
import util.logger.MyLoggerLevel;
import util.secret.MySecret;
import util.secret.NoEnvExcedption;
import util.secret.SecretCategory;
import util.webclient.WebClient;

import java.util.HashMap;

public class Slack {
    private final WebClient webClient;
    private final MySecret secret;
    private final MyLogger logger;

    public Slack() throws NoEnvExcedption {
        logger = MyLogger.getLogger();
        logger.setLevel(MyLoggerLevel.DEBUG);
        secret = MySecret.getSecret();
        webClient = WebClient.getWebClient();
    }

    public void sendMessage(String msg) throws NoEnvExcedption {
        String result = "";
        HashMap<String,String> map = new HashMap<>();
        map.put("url", secret.getSecret(SecretCategory.SLACK_BOT_URL.key));
        map.put("method", "POST");
        map.put("body", """
                {"text": "%s"}
                """.formatted(msg));
        try {
            result = webClient.sendRequest( webClient.makeRequest(map));
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        logger.info(result);
    }
}
