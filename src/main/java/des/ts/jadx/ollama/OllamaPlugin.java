package des.ts.jadx.ollama;

import jadx.api.plugins.JadxPlugin;
import jadx.api.plugins.JadxPluginContext;
import jadx.api.plugins.JadxPluginInfo;
import jadx.api.plugins.gui.JadxGuiContext;

/**
 * Main entry point for the Jadx Ollama Assist Plugin.
 * Registers GUI actions in Jadx to interact with the Ollama model.
 */
public class OllamaPlugin implements JadxPlugin {

    /**
     * Provides metadata about the plugin to Jadx.
     * 
     * @return JadxPluginInfo object containing the plugin's id, name, and description.
     */
    @Override
    public JadxPluginInfo getPluginInfo() {
        return new JadxPluginInfo("ollama-assist", "Ollama Assist", "Generative AI Assistant for Jadx");
    }

    /**
     * Initializes the plugin, registering necessary context actions in the GUI.
     * 
     * @param context The Jadx plugin context.
     */
    @Override
    public void init(JadxPluginContext context) {
        // Only run if GUI context is present (jadx-gui)
        if (context.getGuiContext() != null) {
            JadxGuiContext guiContext = context.getGuiContext();
            
            guiContext.addMenuAction("Ollama Settings", () -> {
                des.ts.jadx.ollama.config.OllamaConfigManager mgr = null;
                try {
                    java.nio.file.Path configPath = java.nio.file.Paths.get(System.getProperty("user.home"), ".ollama", "ollama.toml");
                    mgr = new des.ts.jadx.ollama.config.OllamaConfigManager(configPath);
                } catch (Exception e) {}
                
                OllamaSettingsDialog dialog = new OllamaSettingsDialog(null, mgr);
                dialog.setVisible(true);
            });

            guiContext.addPopupMenuAction("Analyze with Ollama", nodeRef -> true, null, new OllamaAction(context));
        }
    }
}