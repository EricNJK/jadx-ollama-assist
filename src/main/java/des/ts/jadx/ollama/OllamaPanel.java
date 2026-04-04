package des.ts.jadx.ollama;

import des.ts.jadx.ollama.config.OllamaConfig;
import des.ts.jadx.ollama.config.OllamaConfigManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * GUI Panel for the Ollama Plugin.
 * Presents the user with an interface to select analysis modes, provide custom instructions,
 * and view the output from the local Ollama daemon interacting with the decompiled code.
 */
public class OllamaPanel extends JDialog {
    private JTextArea outputArea;
    private JTextArea promptArea;
    private JButton analyzeBtn;
    private JComboBox<String> modeBox;
    
    private OllamaConfigManager configManager;
    private OllamaClient client;
    private String codeContext;

    /**
     * Constructs a new OllamaPanel.
     *
     * @param owner The parent frame to attach this dialog to.
     * @param codeContext The decompiled code context to be analyzed by the model.
     */
    public OllamaPanel(Frame owner, String codeContext) {
        super(owner, "Jadx Ollama Assistant", false);
        this.codeContext = codeContext;
        this.client = new OllamaClient();

        try {
            // Load configuration from local directory or default
            Path configPath = Paths.get(System.getProperty("user.home"), ".ollama", "ollama.toml");
            this.configManager = new OllamaConfigManager(configPath);
        } catch (IOException e) {
            System.err.println("Failed to load configs, using defaults. Exception: " + e.getMessage());
        }

        setupUI();
        setSize(900, 700);
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes and positions the UI components.
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel: Settings and Mode
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.add(new JLabel("Analysis Mode:"));
        modeBox = new JComboBox<>(new String[]{
            "Custom Prompt Only (No preset focus)",
            "Advanced AI De-obfuscation",
            "General RE & Malware check",
            "Data Exfiltration",
            "Permission Abuse",
            "Suspicious API Flows"
        });
        modeBox.setSelectedIndex(1); // Default to Advanced AI De-obfuscation
        topPanel.add(modeBox);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Analysis Result"));
        
        // Bottom Panel: Custom Prompt and Action
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        promptArea = new JTextArea(4, 50);
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        promptArea.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane promptScroll = new JScrollPane(promptArea);
        promptScroll.setBorder(BorderFactory.createTitledBorder("Ask a specific question or provide instructions (Ctrl+Enter to send)"));

        // Add a KeyListener to allow submitting with Ctrl+Enter
        promptArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "analyze");
        promptArea.getActionMap().put("analyze", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (analyzeBtn.isEnabled()) {
                    runAnalysis();
                }
            }
        });

        analyzeBtn = new JButton("Analyze Context");
        analyzeBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        analyzeBtn.setPreferredSize(new Dimension(150, 50));
        analyzeBtn.addActionListener(e -> runAnalysis());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(analyzeBtn);

        bottomPanel.add(promptScroll, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Use a JSplitPane to allow resizing between output and prompt areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputScroll, bottomPanel);
        splitPane.setResizeWeight(0.8); // 80% output, 20% input
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Triggers the analysis execution process asynchronously and displays the result or error.
     */
    private void runAnalysis() {
        analyzeBtn.setEnabled(false);
        outputArea.setText("Sending analysis request to Ollama API...\n\n");
        
        OllamaConfig config;
        try {
            config = configManager != null ? configManager.get() : des.ts.jadx.ollama.config.OllamaConfig.defaults();
        } catch (Exception e) {
            config = des.ts.jadx.ollama.config.OllamaConfig.defaults();
        }

        String mode = (String) modeBox.getSelectedItem();
        String customPromptText = promptArea.getText();
        String prompt = PromptBuilder.buildPrompt(config, codeContext, mode, customPromptText);

        client.generate(config, prompt).whenComplete((result, ex) -> {
            SwingUtilities.invokeLater(() -> {
                analyzeBtn.setEnabled(true);
                promptArea.requestFocusInWindow();
                if (ex != null) {
                    outputArea.setText("Error occurred during Ollama communication:\n" + ex.getMessage());
                } else {
                    outputArea.setText(result);
                }
            });
        });
    }
}
