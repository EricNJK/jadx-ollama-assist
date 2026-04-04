package des.ts.jadx.ollama.config;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        return new OllamaConfig(
            toml.getString("ollama.endpoint"),
            toml.getString("ollama.model"),
            toml.getLong("ollama.timeout_ms").intValue(),
            toml.getDouble("ollama.temperature"),
            toml.getString("prompts.base")
        );
    }
}
