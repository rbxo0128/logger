import slack.Slack;
import util.llm.LLM;
import util.llm.ModelCategory;
import util.logger.MyLogger;
import util.secret.NoEnvExcedption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class Application {
    public static void main(String[] args) throws NoEnvExcedption, InterruptedException, IOException, ExecutionException {
        MyLogger logger = MyLogger.getLogger();
//        Slack slack = new Slack();
        LLM llm = new LLM();

        long startTime = System.currentTimeMillis();
        logger.info("START!");

        String prompt = System.getenv("PROMPT");

        String aiResult = llm.sendPrompt(ModelCategory.LLAMA, prompt);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        LLMTask imageTask = new LLMTask(
                ModelCategory.FLUX,
                "Create thumbnail images for '%s'.".formatted(aiResult)
        );
        Future<String> imageFuture = executor.submit((Callable<String>) imageTask);

        LLMTask reasoningTask = new LLMTask(
                ModelCategory.GEMINI_THINKING,
                "A fairly detailed and complex description of '%s'. Without markdown and escape characters. No more than 500 characters. only use korean character and english character and use korean. Translate all Chinese and Chinese characters that can be translated into Korean if possible, and English if not possible. Finally, review your compliance with the restrictions so far."
                        .formatted(aiResult)
        );
        Future<String> reasoningFuture = executor.submit((Callable<String>) reasoningTask);

        LLMTask reasoningTask2 = new LLMTask(
                ModelCategory.GEMINI_THINKING,
                "Write tips to help you get a job based on '%s'. Without markdown and escape characters. No more than 500 characters. only use korean character and english character and use korean. Translate all Chinese and Chinese characters that can be translated into Korean if possible, and English if not possible. Finally, review your compliance with the restrictions so far."
                        .formatted(aiResult)
        );
        Future<String> reasoningFuture2 = executor.submit((Callable<String>) reasoningTask2);

        String imageResult = imageFuture.get();
        String reasoningResult = reasoningFuture.get();
        String reasoningResult2 = reasoningFuture2.get();

//        slack.sendMessage(reasoningResult, reasoningResult2, imageResult);

        executor.shutdown();

        logger.info("FINISH! %d".formatted(System.currentTimeMillis() - startTime));

        String combinedOutput = "# Generated Results\n\n" +
                "## Image Result\n" +
                imageResult + "\n\n" +
                "## Detailed Description\n" +
                reasoningResult + "\n\n" +
                "## Job Tips\n" +
                reasoningResult2;

        Files.write(Paths.get("docs/output.md"), combinedOutput.getBytes(StandardCharsets.UTF_8));
    }
}

class LLMTask implements Runnable, Callable<String> {
    private final LLM llm;
    private final String prompt;
    private final ModelCategory model;
    private String result;

    LLMTask(ModelCategory model, String prompt) throws NoEnvExcedption {
        llm = new LLM();
        this.prompt = prompt;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            result = llm.sendPrompt(model, prompt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String call() throws Exception {
        run();
        return result;
    }
}