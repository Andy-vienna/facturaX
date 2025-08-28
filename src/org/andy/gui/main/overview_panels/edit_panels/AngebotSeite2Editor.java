package org.andy.gui.main.overview_panels.edit_panels;

import static org.andy.toolbox.misc.CreateObject.createButton;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;

public class AngebotSeite2Editor extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JPanel editorHost = new JPanel(new BorderLayout());
    private final JFXPanel jfxPanel = new JFXPanel(); // Initialisiert das FX-Toolkit
    private HTMLEditor htmlEditor; // JavaFX-Komponente
    private volatile boolean fxReady = false;
    private String pendingHtml; // Puffer
    
    private String html = null;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public AngebotSeite2Editor() {
        super("Liefer- und Leistungsbeschreibung");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        
        editorHost.add(jfxPanel, BorderLayout.CENTER);
        editorHost.setVisible(true);
        add(editorHost, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGetHtml = createButton("<html>OK</html>", "ok.png");
        btnGetHtml.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnGetHtml.setPreferredSize(new Dimension(130, 50));
        btnGetHtml.setEnabled(true);
        south.add(btnGetHtml);
        add(south, BorderLayout.SOUTH);

        // FX-Toolkit ist durch JFXPanel bereits aktiv. Nur noch runLater.
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            htmlEditor = new HTMLEditor();
            htmlEditor.setPrefHeight(400);
            //htmlEditor.setHtmlText(formatText("Leistungsbeschreibung eingeben ..."));
            jfxPanel.setScene(new Scene(new BorderPane(htmlEditor)));
            fxReady = true;
        });

        btnGetHtml.addActionListener(_ -> Platform.runLater(() -> {
            html = htmlEditor.getHtmlText();
            dispose();
        }));
    }
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public void setHtml(String html) {
        pendingHtml = html;
        if (fxReady && htmlEditor != null) {
            Platform.runLater(() -> htmlEditor.setHtmlText(pendingHtml));
        }
    }

    public String getHtml() {
        return html;
    }
    
    public String getStartText(String startText) {
    	String fixedCssHtml = """
        		<html><head><style>
        		  /* Seite ohne Außenränder */
        		  @page { margin: 0; }
        		  html, body { margin:0; padding:0; }

        		  /* Arial global, Größen aus Inline-Styles bleiben erhalten */
        		  body { font-family: Arial, sans-serif; font-size: 10pt; }
        		  * { font-family: inherit !important; }      /* verhindert Font-Wechsel */

        		  /* Überschriften wie Absätze darstellen (Absatz fix) */
        		  h1, h2, h3, h4, h5, h6 {
        		    all: unset;
        		    display: block;
        		    margin: 0 0 8pt 0;
        		    font-family: inherit !important;
        		    font-size: inherit;                       /* übernimmt die gewählte Größe */
        		    font-weight: bold;                        /* optional */
        		  }
        		  p { margin: 0 0 8pt 0; }
        		</style></head><body>""" + startText + "</body></html>";
    	return fixedCssHtml;
    }
}
