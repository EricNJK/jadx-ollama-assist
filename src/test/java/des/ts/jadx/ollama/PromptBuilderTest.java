package des.ts.jadx.ollama;

import des.ts.jadx.ollama.config.OllamaConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PromptBuilderTest {

    @Test
    public void testBuildPromptGeneralMode() {
        OllamaConfig config = OllamaConfig.defaults();
        String result = PromptBuilder.buildPrompt(config, "class Test {}", "General RE", null);
        
        assertTrue(result.contains(config.basePrompt()));
        assertTrue(result.contains("Analysis Focus: General RE"));
        assertTrue(result.contains("class Test {}"));
        assertFalse(result.contains("User Question/Instructions:"));
    }

    @Test
    public void testBuildPromptWithCustomInstruction() {
        OllamaConfig config = OllamaConfig.defaults();
        String result = PromptBuilder.buildPrompt(config, "code()", "Analysis", "Find bugs");
        
        assertTrue(result.contains("User Question/Instructions: Find bugs"));
    }

    @Test
    public void testBuildPromptDeobfuscationMode() {
        OllamaConfig config = OllamaConfig.defaults();
        String result = PromptBuilder.buildPrompt(config, "public void a() {}", "Advanced AI De-obfuscation", "");
        
        assertTrue(result.contains("This is an advanced AI De-obfuscation task."));
        assertTrue(result.contains("1. Rename meaningless variable"));
    }
}
