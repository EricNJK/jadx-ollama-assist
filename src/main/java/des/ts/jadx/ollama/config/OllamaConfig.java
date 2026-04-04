package des.ts.jadx.ollama.config;

/**
 * Immutable configuration entity containing settings for the Ollama connection.
 */
public class OllamaConfig {
    private final String endpoint;
    private final String model;
    private final int timeoutMs;
    private final double temperature;
    private final String basePrompt;

    /**
     * Constructs a new Ollama configuration instance.
     *
     * @param endpoint    The Ollama API endpoint (e.g. http://localhost:11434).
     * @param model       The name of the model to use (e.g. deepseek-coder).
     * @param timeoutMs   The timeout in milliseconds for HTTP calls.
     * @param temperature The sampling temperature to use for generations.
     * @param basePrompt  The baseline context prompt to be injected.
     */
    public OllamaConfig(String endpoint, String model, int timeoutMs, double temperature, String basePrompt) {
        this.endpoint = endpoint;
        this.model = model;
        this.timeoutMs = timeoutMs;
        this.temperature = temperature;
        this.basePrompt = basePrompt;
    }

    /**
     * @return The Ollama API endpoint.
     */
    public String endpoint() { return endpoint; }

    /**
     * @return The Ollama model name.
     */
    public String model() { return model; }

    /**
     * @return Connection timeout in milliseconds.
     */
    public int timeoutMs() { return timeoutMs; }

    /**
     * @return The language model's sampling temperature.
     */
    public double temperature() { return temperature; }

    /**
     * @return The base prompt prefix.
     */
    public String basePrompt() { return basePrompt; }

    /**
     * Creates a default configuration if none is present or parsing fails.
     * 
     * @return A standard defaults payload.
     */
    public static OllamaConfig defaults() {
        return new OllamaConfig(
                "http://localhost:11434",
                "deepseek-coder:6.7b",
                120_000,
                0.2,
                "");
    }
}