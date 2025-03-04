import slack.Slack;
import util.llm.LLM;
import util.secret.NoEnvExcedption;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws NoEnvExcedption, InterruptedException, IOException {
        LLM llm = new LLM();
        Slack slack = new Slack();

        String aiResult = llm.sendPrompt("meta-llama/Llama-3.3-70B-Instruct-Turbo", "안녕");
        slack.sendMessage(aiResult);
    }
}
