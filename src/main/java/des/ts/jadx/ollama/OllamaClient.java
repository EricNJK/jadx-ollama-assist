package des.ts.jadx.ollama;

import des.ts.jadx.ollama.config.OllamaConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Client for communicating with the local Ollama REST API.
 * Uses standard Java 11+ java.net.http.HttpClient for requests.
 */
public class OllamaClient {
    private final HttpClient client;
    
    public OllamaClient() {
        this.client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }

    /**
     * Generates a response asynchronously from the Ollama engine.
     *
     * @param config the configuration detailing the model, endpoint, and behavior.
     * @param prompt the text prompt to send to the language model.
     * @return a CompletableFuture containing the parsed String output from Ollama.
     */
    public CompletableFuture<String> generate(OllamaConfig config, String prompt) {
        String jsonPayload = String.format(
            "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false, \"options\": {\"temperature\": %s}}",
            escapeJson(config.model()),
            escapeJson(prompt),
            config.temperature()
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(config.endpoint() + "/api/generate"))
            .timeout(Duration.ofMillis(config.timeoutMs()))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Ollama HTTP Error: " + response.statusCode() + " - " + response.body());
                }
                return extractResponse(response.body());
            })
            .exceptionally(ex -> {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                if (cause instanceof java.net.ConnectException || cause instanceof java.net.http.HttpConnectTimeoutException || cause instanceof java.net.http.HttpTimeoutException) {
                    return "⚠️ WARNING: Ollama service is unreachable.\nPlease ensure your Ollama server is running locally or check your network connectivity.\n\nDetails: " + cause.getMessage();
                }
                return "❌ Error communicating with Ollama: " + cause.getMessage();
            });
    }

    /**
     * Escapes standard JSON control characters to prevent malformed payload errors.
     * (Package-private for unit testing)
     */
    String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Naive JSON extraction to find the "response" body from Ollama payload without needing Jackson/Gson.
     * (Package-private for unit testing)
     */
    String extractResponse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "Error: Empty response string from server.";
        }
        int keyIndex = json.indexOf("\"response\"");
        if (keyIndex == -1) return "Error: could not parse Ollama response. Raw: " + json;
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return "Error parsing response colonization.";
        int startIndex = json.indexOf("\"", colonIndex);
        if (startIndex == -1) return "Error parsing response string start.";
        
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = startIndex + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                if (c == 'n') sb.append('\n');
                else if (c == 't') sb.append('\t');
                else if (c == 'r') sb.append('\r');
                else if (c == '"') sb.append('"');
                else if (c == '\\') sb.append('\\');
                // Basic unicode decode \ u XXXX could go here if needed
                else sb.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break; // end of string
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
