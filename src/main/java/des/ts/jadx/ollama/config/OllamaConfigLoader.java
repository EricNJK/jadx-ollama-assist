package des.ts.jadx.ollama.config;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility for loading and parsing the Ollama TOML configuration file.
 */
public class OllamaConfigLoader {
    
    private OllamaConfigLoader() {}

    /**
     * Loads the configuration from the specified TOML file.
     * If the file does not exist, returns the default configuration.
     * 
     * @param path The path to the configuration file.
     * @return The parsed configuration.
     * @throws IOException If the file contains invalid TOML or an I/O error occurs.
     */
    public static OllamaConfig load(Path path) 
throws IOException {
        if (!Files.exists(path)) {
            return OllamaConfig.defaults();
        }

        TomlParseResult toml = Toml.parse(path);

        if (toml.hasErrors()) {
            throw new IOException("Invalid TOML: " + toml.errors());
        }

        OllamaConfig defaults = OllamaConfig.defaults();

        String endpoint = Objects.requireNonNullElse(
                toml.getString("ollama.endpoint"), defaults.endpoint());
        String model = Objects.requireNonNullElse(
                toml.getString("ollama.model"), defaults.model());
        Long timeoutMs = Objects.requireNonNullElse(
                toml.getLong("ollama.timeout_ms"), (long) defaults.timeoutMs());
        Double temperature = Objects.requireNonNullElse(
                toml.getDouble("ollama.temperature"), defaults.temperature());
        String basePrompt = Objects.requireNonNullElse(
                toml.getString("prompts.base"), defaults.basePrompt());

        return new OllamaConfig(
            endpoint,
            model,
            timeoutMs.intValue(),
            temperature,
            basePrompt
        );
    }
}
