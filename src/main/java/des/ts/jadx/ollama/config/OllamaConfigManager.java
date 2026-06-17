package des.ts.jadx.ollama.config;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Manages the Ollama TOML configuration file state.
 * Monitors the file for external modifications using Java NIO WatchService 
 * and handles updates dynamically.
 */
public class OllamaConfigManager {

    private final Path configFile;
    private final AtomicReference<OllamaConfig> current = new AtomicReference<>(OllamaConfig.defaults());

    /**
     * Constructs a new config manager pointing to a specific file.
     * @param configFile The absolute path to the configuration file (typically TOML).
     * @throws IOException If the watch service fails to initialize.
     */
    public OllamaConfigManager(Path configFile) throws IOException {
        this.configFile = configFile;
        if (!Files.exists(configFile.getParent())) {
            Files.createDirectories(configFile.getParent());
        }
        if (!Files.exists(configFile)) {
            Files.write(configFile, ("ollama.endpoint = \"" + OllamaConfig.defaults().endpoint() + "\"\nollama.model = \"" + OllamaConfig.defaults().model() + "\"\nollama.temperature = " + OllamaConfig.defaults().temperature() + "\n").getBytes());
        }
        
        reload();
        watch();
    }

    /**
     * Gets the latest, thread-safe configuration instance.
     * @return the active OllamaConfig.
     */
    public OllamaConfig get() {
        return current.get();
    }

    /**
     * Reloads the configuration file from disk into the current reference.
     */
    private void reload() {
        try {
            OllamaConfig cfg = OllamaConfigLoader.load(configFile);
            current.set(cfg);
            System.out.println("[Ollama] config reloaded from " + configFile);
        } catch (Exception e) {
            System.err.println("[Ollama] config reload failed: " + e.getMessage());
        }
    }

    /**
     * Initiates a background watcher thread to detect modifications to the configuration file.
     * 
     * @throws IOException If configuring the watch service fails.
     */
    private void watch() throws IOException {
        WatchService ws = FileSystems.getDefault().newWatchService();
        Path dir = configFile.getParent();
        dir.register(ws, ENTRY_MODIFY, ENTRY_CREATE);

        Thread watcher = new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = ws.take();
                    for (WatchEvent<?> ev : key.pollEvents()) {
                        Path changed = dir.resolve((Path) ev.context());
                        // Some systems fire multiple empty events during saving
                        if (changed.equals(configFile)) {
                            // slight delay to wait for file writing entirely
                            Thread.sleep(100); 
                            reload();
                        }
                    }
                    key.reset();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    break;
                }
            }
        }, "ollama-config-watcher");

        watcher.setDaemon(true);
        watcher.start();
    }

    /**
     * Safely updates the configuration file on disk while preserving non-managed parameters and comments.
     * @param newConfig The new configuration payload containing endpoint, model, and temperature.
     */
    public void save(OllamaConfig newConfig) {
        try {
            if (!Files.exists(configFile)) {
                Files.write(configFile, "".getBytes());
            }
            String text = new String(Files.readAllBytes(configFile));
            
            text = replaceOrAppend(text, "ollama.endpoint", "\"" + newConfig.endpoint() + "\"");
            text = replaceOrAppend(text, "ollama.model", "\"" + newConfig.model() + "\"");
            text = replaceOrAppend(text, "ollama.temperature", String.valueOf(newConfig.temperature()));
            text = replaceOrAppend(text, "ollama.timeout_ms", String.valueOf(newConfig.timeoutMs()));
            text = replaceOrAppend(text, "prompts.base",  "\"" + newConfig.basePrompt() + "\"");

            Files.write(configFile, text.getBytes());
            current.set(newConfig);
        } catch (IOException e) {
            System.err.println("[Ollama] failed to write config: " + e.getMessage());
        }
    }

    /**
     * Internal utility to replace an existing TOML key-value pair or append it if it doesn't exist.
     * 
     * @param content The original text content of the TOML file.
     * @param key The setting key to replace or append.
     * @param value The value associated with the key.
     * @return The updated text content.
     */
    private String replaceOrAppend(String content, String key, String value) {
        Pattern p = Pattern.compile("(?m)^" + key + "\\s*=\\s*.*$");
        Matcher m = p.matcher(content);
        if (m.find()) {
            return m.replaceAll(key + " = " + value);
        } else {
            return content.endsWith("\n") ? content + key + " = " + value + "\n" : content + "\n" + key + " = " + value + "\n";
        }
    }
}
