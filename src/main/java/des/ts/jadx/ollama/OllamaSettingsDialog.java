package des.ts.jadx.ollama;

import des.ts.jadx.ollama.config.OllamaConfig;
import des.ts.jadx.ollama.config.OllamaConfigManager;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for configuring Ollama settings such as the endpoint URL,
 * the model used, and temperature parameters.
 */
public class OllamaSettingsDialog extends JDialog {

    private JTextField endpointField;
    private JTextField modelField;
    private JSpinner tempSpinner;
    private OllamaConfigManager configManager;

    /**
     * Constructs a new Settings Dialog.
     *
     * @param owner The parent frame invoking this dialog.
     * @param configManager The manager handling the Ollama configuration.
     */
    public OllamaSettingsDialog(Frame owner, OllamaConfigManager configManager) {
        super(owner, "Ollama Assist Settings", true);
        this.configManager = configManager;
        setupUI();
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes the user interface components.
     */
    private void setupUI() {
        OllamaConfig config = configManager != null ? configManager.get()
                : des.ts.jadx.ollama.config.OllamaConfig.defaults();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Endpoint URL:"));
        endpointField = new JTextField(config.endpoint(), 25);
        panel.add(endpointField);

        panel.add(new JLabel("Model Name:"));
        modelField = new JTextField(config.model(), 25);
        panel.add(modelField);

        panel.add(new JLabel("Temperature:"));
        tempSpinner = new JSpinner(new SpinnerNumberModel(config.temperature(), 0.0, 2.0, 0.1));
        panel.add(tempSpinner);

        JButton saveBtn = new JButton("Save & Apply");
        saveBtn.addActionListener(e -> saveConfig());
        panel.add(new JLabel("")); // spacer
        panel.add(saveBtn);

        add(panel);
    }

    /**
     * Saves the updated configuration leveraging the injected ConfigManager.
     */
    private void saveConfig() {
        if (configManager == null) {
            JOptionPane.showMessageDialog(this, "Config manager not initialized.");
            return;
        }
        try {
            OllamaConfig oldConfig = configManager.get();
            OllamaConfig newConfig = new OllamaConfig(
                    endpointField.getText().trim(),
                    modelField.getText().trim(),
                    oldConfig.timeoutMs(),
                    ((Number) tempSpinner.getValue()).doubleValue(),
                    oldConfig.basePrompt());
            configManager.save(newConfig);
            JOptionPane.showMessageDialog(this, "Settings saved.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving settings: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
