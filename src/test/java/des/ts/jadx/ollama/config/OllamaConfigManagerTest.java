package des.ts.jadx.ollama.config;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class OllamaConfigManagerTest {

    @Test
    public void testManagerInitializationAndGet() throws IOException {
        Path path = Files.createTempFile("test-manager", ".toml");
        Files.delete(path); // Let manager create it

        OllamaConfigManager manager = new OllamaConfigManager(path);
        OllamaConfig config = manager.get();
        assertEquals("http://localhost:11434", config.endpoint());

        assertTrue(Files.exists(path));
        
        Files.delete(path); // Cleanup
    }

    @Test
    public void testManagerSaveAndUpdate() throws IOException {
        Path path = Files.createTempFile("test-manager-save", ".toml");
        
        OllamaConfigManager manager = new OllamaConfigManager(path);
        OllamaConfig newConfig = new OllamaConfig("http://new-endpoint", "new-model", 5000, 0.9, "");
        manager.save(newConfig);

        OllamaConfig updated = manager.get();
        assertEquals("http://new-endpoint", updated.endpoint());
        assertEquals("new-model", updated.model());
        assertEquals(0.9, updated.temperature(), 0.001);

        // Check if file content was actually modified
        String content = new String(Files.readAllBytes(path));
        assertTrue(content.contains("endpoint = \"http://new-endpoint\""));
        
        Files.delete(path); // Cleanup
    }
}
