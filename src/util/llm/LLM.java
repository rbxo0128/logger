package util.llm;

import util.logger.MyLogger;
import util.logger.MyLoggerLevel;
import util.secret.MySecret;
import util.secret.NoEnvExcedption;
import util.secret.SecretCategory;
import util.webclient.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LLM {
    private final MyLogger logger;
    private final WebClient webClient;
    private final MySecret secret;
    public LLM() throws NoEnvExcedption {
        logger = MyLogger.getLogger();
        logger.setLevel(MyLoggerLevel.INFO);
        secret = MySecret.getSecret();
        webClient = WebClient.getWebClient();
    }

    public String sendPrompt(String model,String prompt) throws IOException, InterruptedException, NoEnvExcedption {

        Map<String, String> map = new HashMap<>();

        map.put("url", "https://api.together.xyz/v1/chat/completions");
        map.put("method", "POST");
        map.put("headers", "Authorization;Bearer %s;Content-Type;application/json".formatted(secret.getSecret(SecretCategory.TOGETHER_API_KEY.key)));
        map.put("body",
                """
                    {
                    "model": "%s",
                    "messages": [{
                        "role": "user",
                        "content": "%s"
                    }],
                    "max_tokens": %d
                    }
                """.formatted(model, prompt, 2048));

        String result = webClient.sendRequest(webClient.makeRequest(map));
        return result.split("content\": \"")[1].split("\",")[0];
    }
}
