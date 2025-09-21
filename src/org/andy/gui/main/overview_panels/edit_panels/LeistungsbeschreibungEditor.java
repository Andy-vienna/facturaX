package org.andy.gui.main.overview_panels.edit_panels;

import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.andy.gui.misc.FxHtmlEditor;

import javafx.application.Platform;

public class LeistungsbeschreibungEditor extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel editorHost = new JPanel(new BorderLayout());
	private final FxHtmlEditor editor = new FxHtmlEditor();
	
	private volatile boolean fxReady = false;
	private String pendingHtml; // Puffer
    private String html = null;
	
    public LeistungsbeschreibungEditor() {
    	
    	super("Leistungsbeschreibungs Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        editorHost.add(editor, BorderLayout.CENTER);
        editorHost.setVisible(true);
        add(editorHost, BorderLayout.CENTER);
        
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGetHtml = createButton("<html>OK</html>", "ok.png", null);
        btnGetHtml.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnGetHtml.setPreferredSize(new Dimension(130, 50));
        btnGetHtml.setEnabled(true);
        south.add(btnGetHtml);
        add(south, BorderLayout.SOUTH);
    	
    	Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            fxReady = true;
        });
        
        btnGetHtml.addActionListener(_ -> {
            String text = editor.getHtml(); // holt intern via FX-Thread, blockiert kurz
            this.html = text;
            dispose(); // EDT -> ok
        });
    }
    
    //###################################################################################################################################################
  	// Getter und Setter
  	//###################################################################################################################################################

  	public void setText(String html) {
	    pendingHtml = html;
    	if (fxReady && editor != null) {
    		Platform.runLater(() -> editor.setHtml(pendingHtml));
    	}
	}

	public String getText() {
	    return html;
	}
	  
	public String setStartText(String html) {
		pendingHtml = html;
	    if (fxReady && editor != null) {
	        Platform.runLater(() -> editor.setHtml(pendingHtml));
	    }
	    return editor.getHtml();
	}
}

