package org.andy.gui.misc;

import java.awt.BorderLayout;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.*;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class FxHtmlEditor extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private final JFXPanel fxPanel = new JFXPanel();
    private WebEngine engine;
    private volatile boolean ready = false;
    private String pendingHtml;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public FxHtmlEditor() {
    	
        super(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            engine = webView.getEngine();
            noBrowser(engine); // Öffnen von Websites verhindern
            engine.loadContent(HTML_TEMPLATE);

            ToolBar tb = buildToolbar();
            BorderPane root = new BorderPane(webView);
            root.setTop(tb);
            fxPanel.setScene(new Scene(root));

            engine.getLoadWorker().stateProperty().addListener((_, _, n) -> {
                if (n == Worker.State.SUCCEEDED) {
                    ready = true;
                    if (pendingHtml != null) setHtml(pendingHtml);
                }
            });
        });
    }
    
    //###################################################################################################################################################

    // ---- öffentliche API für Swing ----
    public void setHtml(String html) {
        Platform.runLater(() -> {
            if (engine == null || !ready) { pendingHtml = html; return; }
            engine.executeScript("window.editor.setHtml(`" + escapeTemplate(html) + "`);");
        });
    }

    public String getHtml() {
        if (engine == null || !ready) return "";
        CompletableFuture<String> cf = new CompletableFuture<>();
        Platform.runLater(() -> cf.complete(String.valueOf(
                engine.executeScript("window.editor.getHtml();"))));
        try { return cf.get(2, TimeUnit.SECONDS); }
        catch (TimeoutException te) { return ""; }
        catch (Exception e) { return ""; }
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    // ---- Anzeige von Websites verhindern ----
    private void noBrowser(WebEngine engine) {
    	engine.setCreatePopupHandler(_ -> null);
        engine.getLoadWorker().stateProperty().addListener((_,_,n) -> {
            if (n == javafx.concurrent.Worker.State.SUCCEEDED) {
                engine.executeScript("""
                    (function(){
                      // alle Link-Klicks unterbinden
                      document.addEventListener('click', function(e){
                        const a = e.target.closest('a');
                        if (!a) return;
                        e.preventDefault();
                        // optional: window.java?.onLink && window.java.onLink(a.href);
                      }, true);

                      // Mittelklick blocken
                      document.addEventListener('auxclick', function(e){
                        if (e.button === 1 && e.target.closest('a')) e.preventDefault();
                      }, true);

                      // Kontextmenü auf Links verhindern (optional)
                      document.addEventListener('contextmenu', function(e){
                        if (e.target.closest('a')) e.preventDefault();
                      });
                    })();
                """);
            }
        });
    }
    
    // ---- ToolBar im Editor bauen ----
    private ToolBar buildToolbar() {
        Button undo = new Button("↶");     undo.setOnAction(_ -> exec("undo"));
        Button redo = new Button("↷");     redo.setOnAction(_ -> exec("redo"));

        ToggleButton bold = new ToggleButton("B"); bold.setStyle("-fx-font-weight:bold;"); bold.setOnAction(_ -> exec("bold"));
        ToggleButton italic = new ToggleButton("I"); italic.setStyle("-fx-font-style:italic;"); italic.setOnAction(_ -> exec("italic"));
        ToggleButton underline = new ToggleButton("U"); underline.setStyle("-fx-underline:true;"); underline.setOnAction(_ -> exec("underline"));
        Button clr = new Button("Format löschen");  clr.setOnAction(_ -> exec("removeFormat"));

        Button ul = new Button("• Liste"); ul.setOnAction(_ -> exec("insertUnorderedList"));
        Button ol = new Button("1. Liste"); ol.setOnAction(_ -> exec("insertOrderedList"));
        Button outdent = new Button("⇤"); outdent.setOnAction(_ -> exec("outdent"));
        Button indent  = new Button("⇥"); indent.setOnAction(_ -> exec("indent"));

        ComboBox<String> block = new ComboBox<>();
        block.getItems().addAll("Absatz", "H1", "H2", "Zitat", "Code");
        block.getSelectionModel().select(0);
        block.setOnAction(_ -> {
            switch (block.getValue()) {
                case "H1"   -> exec("formatBlock", "h1");
                case "H2"   -> exec("formatBlock", "h2");
                case "Zitat"-> exec("formatBlock", "blockquote");
                case "Code" -> exec("formatBlock", "pre");
                default     -> exec("formatBlock", "p");
            }
        });

        Button link = new Button("Link");
        link.setOnAction(_ -> {
            TextInputDialog d = new TextInputDialog("https://");
            d.setHeaderText(null); d.setContentText("URL:"); d.setTitle("Link einfügen");
            d.showAndWait().ifPresent(url -> { if (!url.isBlank()) exec("createLink", url); });
        });
        Button mailto = new Button("Mail");
        mailto.setOnAction(_ -> {
            TextInputDialog m = new TextInputDialog("mailto:");
            m.setHeaderText(null); m.setContentText("URL:"); m.setTitle("Mail-Link einfügen");
            m.showAndWait().ifPresent(url -> { if (!url.isBlank()) exec("createLink", url); });
        });
        Button unlink = new Button("Link entfernen"); unlink.setOnAction(_ -> exec("unlink"));

        Button htmlGet = new Button("HTML anzeigen");
        htmlGet.setOnAction(_ -> {
            String html = String.valueOf(engine.executeScript("window.editor.getHtml();"));
            TextArea ta = new TextArea(html); ta.setEditable(false); ta.setPrefRowCount(30); ta.setPrefColumnCount(100);
            Dialog<Void> dlg = new Dialog<>(); dlg.setTitle("HTML");
            dlg.getDialogPane().setContent(ta); dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); dlg.show();
        });

        HBox spacer = new HBox(); spacer.setMinWidth(10);
        return new ToolBar(
                undo, redo, new Separator(),
                bold, italic, underline, clr, new Separator(),
                ul, ol, outdent, indent, new Separator(),
                block, new Separator(),
                link, mailto, unlink, new Separator(),
                htmlGet
        );
    }
    
    //###################################################################################################################################################

    private void exec(String command) {
        engine.executeScript("window.editor.cmd('" + escapeJs(command) + "');");
    }
    private void exec(String command, String value) {
        engine.executeScript("window.editor.cmd('" + escapeJs(command) + "', '" + escapeJs(value) + "');");
    }
    
    //###################################################################################################################################################
    
    private static String escapeJs(String s) { return s.replace("\\","\\\\").replace("'","\\'"); }
    private static String escapeTemplate(String s) { return s.replace("\\","\\\\").replace("`","\\`").replace("${","\\${"); }

    private static final String HTML_TEMPLATE = """
    		<!doctype html>
    		<html lang="de">
    		<head>
    		  <meta charset="UTF-8">
    		  <meta name="viewport" content="width=device-width, initial-scale=1">
    		  <title>Editor</title>
    		  <style>
    		    html, body { height:100%; margin:0; padding:0; }
    		    body { font-family: Arial, sans-serif; font-size:10pt; line-height:1.2; }
    		    #editable { box-sizing: border-box; min-height:100vh; padding:16px; outline:none; line-height:1.5; }
    		    #editable:empty:before { content: attr(data-placeholder); color:#888; }
    		    #editable p { margin: 0; }
    			#editable p + p { margin-top: 0.01em; }
    		    blockquote { border-left:4px solid #ccc; margin:8px 0; padding:8px 12px; color:#ba55d3; font-weight:700; }
    		    pre { background:#f6f8fa; padding:12px; border-radius:6px; overflow:auto; }
    		    h1,h2 { margin:1.2em 0 0.5em; }
    		  </style>
    		</head>
    		<body>
    		  <div id="editable" contenteditable="true" spellcheck="false" data-placeholder="Hier schreiben...">
    		    <p><b>Willkommen.</b> Dies ist ein einfacher HTML-Editor.</p>
    		  </div>

    		  <script>
    		    (function () {
    		      const ed = document.getElementById('editable');

    		      // Standard-Blockelement auf <div> umstellen, reduziert leere <p>
    		      try { document.execCommand('defaultParagraphSeparator', false, 'div'); } catch (e) {}

    		      function cmd(name, value = null) {
    		        ed.focus();
    		        document.execCommand(name, false, value);
    		        ed.dispatchEvent(new Event('input', { bubbles: true }));
    		      }

    		      function getHtml() { return ed.innerHTML; }

    		      function isEmptyPara(el) {
    		        if (!el || el.tagName !== 'P') return false;
    		        const h = el.innerHTML
    		          .replace(/<br\\s*\\/?>/gi, '')
    		          .replace(/&nbsp;/gi, ' ')
    		          .trim();
    		        return h === '';
    		      }

    		      function setHtml(html) {
    		        const tmp = document.createElement('div');
    		        tmp.innerHTML = (html || '').trim();

    		        // führende leere <p> im Input entfernen
    		        while (isEmptyPara(tmp.firstElementChild)) tmp.removeChild(tmp.firstElementChild);

    		        ed.innerHTML = tmp.innerHTML;

    		        // von contenteditable erzeugtes leeres <p> entfernen
    		        while (isEmptyPara(ed.firstElementChild)) ed.removeChild(ed.firstElementChild);

    		        placeCaretEnd(ed);
    		      }

    		      function placeCaretEnd(el) {
    		        el.focus();
    		        const r = document.createRange();
    		        r.selectNodeContents(el);
    		        r.collapse(false);
    		        const s = window.getSelection();
    		        s.removeAllRanges();
    		        s.addRange(r);
    		      }
    		      
    		      // Nur-Text-Einfügen
    		      ed.addEventListener('paste', function (e) {
    		        e.preventDefault();
    		        const text = (e.clipboardData || window.clipboardData).getData('text/plain');
    		        document.execCommand('insertText', false, text);
    		      });

    		      // Tastenkürzel
    		      ed.addEventListener('keydown', function (e) {
    		        if (e.ctrlKey && !e.shiftKey && !e.altKey) {
    		          if (e.key === 'b' || e.key === 'B') { e.preventDefault(); cmd('bold'); }
    		          if (e.key === 'i' || e.key === 'I') { e.preventDefault(); cmd('italic'); }
    		          if (e.key === 'u' || e.key === 'U') { e.preventDefault(); cmd('underline'); }
    		        }
    		      });

    		      // Exponierte API
    		      window.editor = { cmd, getHtml, setHtml };
    		    })();
    		  </script>
    		</body>
    		</html>
    		""";

    //###################################################################################################################################################

}
