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



    public String sendPrompt(ModelCategory model,String prompt) throws IOException, InterruptedException, NoEnvExcedption {

        Map<String, String> map = new HashMap<>();
        String result = "";

        switch (model) {
            case R1:
            case LLAMA:
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
                        """.formatted(model.name, prompt, 2048));

                result = webClient.sendRequest(webClient.makeRequest(map));
                System.out.println("result = " + result);
                String content = result.split("content")[1].split("tool_calls")[0].substring(4);
                content = content.substring(0, content.length() - 1).strip();
                content = content.substring(0, content.length() - 2); // ",이라 2개를 제거해야 한다
                if (model.equals(ModelCategory.R1)) {
                    logger.debug(content);
                    content = content.split("/think")[1].substring(1).strip();
                }
                return content.replace("\\n", "");

            case FLUX:
                map.put("url", "https://api.together.xyz/v1/images/generations");
                map.put("method", "POST");
                map.put("headers", "Authorization;Bearer %s;Content-Type;application/json".formatted(secret.getSecret(SecretCategory.TOGETHER_API_KEY.key)));
                map.put("body",
                        """
                            {
                            "width": 1024,
                            "height": 1024,
                            "model": "%s",
                            "prompt": "%s"
                            }
                        """.formatted(model.name, prompt));
                result = webClient.sendRequest(webClient.makeRequest(map));
                return result.split("url\": \"")[1].split("\",")[0];
            case GEMINI_THINKING:
                map.put("url", "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s".formatted(model.name, secret.getSecret(SecretCategory.GEMINI_API_KEY.key)));
                map.put("method", "POST");
                map.put("headers", "Content-Type;application/json");
                map.put("body", """
                        {
                          "contents": [
                            {
                              "role": "user",
                              "parts": [
                                {
                                  "text": "%s"
                                }
                              ]
                            }
                          ],
                        }
                        """.formatted(prompt));
                result = webClient.sendRequest(webClient.makeRequest(map));
                System.out.println("content = " + result);
                return result.split("text\": \"")[1].split("\"")[0];
default:
    throw new RuntimeException("Unknown model: " + model);
}



}
}
