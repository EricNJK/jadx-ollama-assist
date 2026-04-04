package des.ts.jadx.ollama.config;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class OllamaConfigLoaderTest {

    @Test
    public void testLoadNonExistentFileReturnsDefaults() throws IOException {
        Path path = Files.createTempFile("test-nonexistent", ".toml");
        Files.delete(path); // ensure it doesn't exist

        OllamaConfig config = OllamaConfigLoader.load(path);
        assertNotNull(config);
        assertEquals("http://localhost:11434", config.endpoint());
    }

    @Test
    public void testLoadValidToml() throws IOException {
        Path path = Files.createTempFile("test", ".toml");
        String tomlContent = 
            "[ollama]\n" +
            "endpoint = \"http://remote:11434\"\n" +
            "model = \"llama3\"\n" +
            "timeout_ms = 60000\n" +
            "temperature = 0.8\n" +
            "[prompts]\n" +
            "base = \"base context\"\n";
        Files.write(path, tomlContent.getBytes());

        OllamaConfig config = OllamaConfigLoader.load(path);
        assertEquals("http://remote:11434", config.endpoint());
        assertEquals("llama3", config.model());
        assertEquals(60000, config.timeoutMs());
        assertEquals(0.8, config.temperature(), 0.001);
        assertEquals("base context", config.basePrompt());

        Files.delete(path);
    }
}
