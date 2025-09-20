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
        JButton btnGetHtml = createButton("<html>OK</html>", "ok.png", null);
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
            //htmlEditor.setHtmlText(startHtml);
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
    
    public String setStartText(String html) {
    	pendingHtml = html;
        if (fxReady && htmlEditor != null) {
            Platform.runLater(() -> htmlEditor.setHtmlText(pendingHtml));
        }
        return htmlEditor.getHtmlText();
    }
}
