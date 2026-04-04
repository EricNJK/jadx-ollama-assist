package des.ts.jadx.ollama.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OllamaConfigTest {
    @Test
    public void testDefaults() {
        OllamaConfig config = OllamaConfig.defaults();
        assertEquals("http://localhost:11434", config.endpoint());
        assertEquals("deepseek-coder:6.7b", config.model());
        assertEquals(120000, config.timeoutMs());
        assertEquals(0.2, config.temperature(), 0.001);
        assertEquals("", config.basePrompt());
    }

    @Test
    public void testCustomValues() {
        OllamaConfig config = new OllamaConfig("http://test", "test-model", 1000, 0.5, "test prompt");
        assertEquals("http://test", config.endpoint());
        assertEquals("test-model", config.model());
        assertEquals(1000, config.timeoutMs());
        assertEquals(0.5, config.temperature(), 0.001);
        assertEquals("test prompt", config.basePrompt());
    }
}
