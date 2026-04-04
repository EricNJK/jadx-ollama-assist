package des.ts.jadx.ollama;

import des.ts.jadx.ollama.config.OllamaConfig;

/**
 * Utility for constructing the final prompt string sent to the Ollama model.
 * Injects context, user instructions, and specific analysis logic based on the mode.
 */
public class PromptBuilder {
    
    /**
     * Builds the complete prompt payload.
     * 
     * @param config the configuration defining the base system prompt
     * @param codeContext the decompiled Java/Kotlin source code chunk
     * @param analysisMode the selected scanning mode/focus (e.g. "Advanced AI De-obfuscation")
     * @param customPrompt optional user-defined instructions provided via UI
     * @return a formatted string ready to be parsed by the LLM
     */
    public static String buildPrompt(OllamaConfig config, String codeContext, String analysisMode, String customPrompt) {
        StringBuilder sb = new StringBuilder();
        sb.append(config.basePrompt());
        sb.append("\n\n");
        sb.append("Analysis Focus: ").append(analysisMode).append("\n\n");
        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            sb.append("User Question/Instructions: ").append(customPrompt.trim()).append("\n\n");
        }
        if ("Advanced AI De-obfuscation".equals(analysisMode)) {
            sb.append("This is an advanced AI De-obfuscation task. Your goal is to rewrite the provided obfuscated Java/Kotlin decompiled code to make it as readable as possible.\n");
            sb.append("Instructions:\n");
            sb.append("1. Rename meaningless variable/method/class names (like a, b, c) to contextually logical names.\n");
            sb.append("2. Simplify convoluted control flows, math operations, and conditionals where possible.\n");
            sb.append("3. Decrypt or explain any obvious encoded strings or obfuscated constants.\n");
            sb.append("4. Add inline comments explaining the logic of the code blocks.\n");
            sb.append("5. Output the fully refactored, valid Java code wrapped in a markdown code block, preceded by a brief summary.\n\n");
        } else {
            sb.append("Specifically pay attention to Compose layouts, Kotlin idioms, USB Host permissions/handling, Android background services, and third-party library implications.\n");
        }
        sb.append("Here is the decompiled Android code to analyze:\n\n");
        sb.append("```java\n");
        sb.append(codeContext);
        sb.append("\n```\n");
        return sb.toString();
    }
}
