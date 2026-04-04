package des.ts.jadx.ollama;

import jadx.api.JavaNode;
import jadx.api.metadata.ICodeNodeRef;
import jadx.api.plugins.JadxPluginContext;

import java.util.function.Consumer;

/**
 * Handle right-click context menu "Analyze with Ollama" action.
 */
public class OllamaAction implements Consumer<ICodeNodeRef> {

    private final JadxPluginContext context;

    /**
     * Constructs the OllamaAction context menu item.
     * 
     * @param context The JadxPluginContext used to access decompiled nodes.
     */
    public OllamaAction(JadxPluginContext context) {
        this.context = context;
    }

    /**
     * Executes when the user clicks the action in the context menu.
     * 
     * @param nodeRef The reference to the node the user clicked on.
     */
    @Override
    public void accept(ICodeNodeRef nodeRef) {
        String code = "// Unable to resolve context code.";
        try {
            // Jadx 1.5.0 context decompiler usage:
            JavaNode node = context.getDecompiler().getJavaNodeByRef(nodeRef);
            if (node != null && node.getTopParentClass() != null) {
                code = node.getTopParentClass().getCode();
            }
        } catch (Exception ex) {
            System.err.println("Ollama plugin error extracting code: " + ex.getMessage());
        }
        
        // Open our Swing Panel
        OllamaPanel panel = new OllamaPanel(null, code);
        panel.setVisible(true);
    }
}
