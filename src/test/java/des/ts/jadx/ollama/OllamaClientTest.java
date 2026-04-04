package des.ts.jadx.ollama;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OllamaClientTest {

    private final OllamaClient client = new OllamaClient();

    @Test
    public void testEscapeJson() {
        assertEquals("test\\\\quote\\\"", client.escapeJson("test\\quote\""));
        assertEquals("newline\\nand\\rtab\\t", client.escapeJson("newline\nand\rtab\t"));
        assertEquals("", client.escapeJson(null));
    }

    @Test
    public void testExtractResponseValid() {
        String json = "{\"model\":\"llama3\",\"created_at\":\"time\",\"response\":\"Hello World!\",\"done\":true}";
        String extracted = client.extractResponse(json);
        assertEquals("Hello World!", extracted);
    }

    @Test
    public void testExtractResponseWithEscapes() {
        String json = "{\"response\":\"Line1\\nLine2\\\"Tabbed\\\"\"}";
        String extracted = client.extractResponse(json);
        assertEquals("Line1\nLine2\"Tabbed\"", extracted);
    }

    @Test
    public void testExtractResponseEmptyOrMissing() {
        assertEquals("Error: Empty response string from server.", client.extractResponse(""));
        assertEquals("Error: Empty response string from server.", client.extractResponse("   "));
        assertTrue(client.extractResponse("{\"done\":true}").startsWith("Error: could not parse Ollama response."));
    }
}
