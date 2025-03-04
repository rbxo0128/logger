package util.llm;

public enum ModelCategory{
    LLAMA("meta-llama/Llama-3.3-70B-Instruct-Turbo"),
    FLUX("black-forest-labs/FLUX.1-schnell-Free"),
    R1("deepseek-ai/DeepSeek-R1"),
    GEMINI_THINKING("gemini-2.0-flash-thinking-exp-01-21"),;

    public final String name;
    private ModelCategory(String name) {
        this.name = name;
    }
}
