package org.andy.fx.gui.main.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.andy.fx.code.dataStructure.entityJSON.JsonAI;
import org.andy.fx.code.googleServices.CheckEnvAI;
import org.andy.fx.code.misc.App;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.imageio.ImageIO;

public final class InfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JButton closeButton = new JButton("Schließen");
	private static final String TEXT_A_HTML =
			"<html>"
			+ "<div style='font-size:24px;font-weight:bold;'>%s(%s)</div>"
			+ "<table style='font-size:10px;font-weight:bold;' cellspacing='0' cellpadding='0'>"
			+ "<tr><td width='140' style='padding-right:8px;white-space:nowrap;'></td>"
			+ "<td><span style='color:blue;'></span></td></tr>"
			+ "<tr><td style='padding-right:8px;white-space:nowrap;'>built date / time :</td>"
			+ "<td><span style='color:blue;'>%s</span></td></tr>"
			+ "<tr><td style='padding-right:8px;white-space:nowrap;'>Java JDK version :</td>"
			+ "<td><span style='color:red;'>%s</span></td></tr>"
			+ "<tr><td style='padding-right:8px;white-space:nowrap;'>Database-Server :</td>"
			+ "<td><span style='color:red;'>%s</span></td></tr>"
			+ "<tr><td style='padding-right:8px;white-space:nowrap;'>Google AI Features :</td>"
			+ "<td><span style='color:green;'>%s</span></td></tr></table>"
			+ "</html>";
	private static final String TEXT_B_HTML =
            """
            <html style='font-family:sans-serif;'>
              Copyright &copy; 2024-2025 Andreas Fischer<br><br>
              Licensed under the Apache License, Version 2.0 (the "License");<br>
              you may not use this file except in compliance with the License.<br>
              You may obtain a copy of the License at<br><br>
              <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a><br><br>
              Unless required by applicable law or agreed to in writing, software
			  distributed under the License is distributed on an "AS IS" BASIS,
			  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
			  See the License for the specific language governing permissions and
			  limitations under the License.
            </html>
            """;
	
	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

    public InfoDialog(Window owner, App a) {
        super(owner, "Über " + a.NAME + " (" + a.VERSION + ")", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent(a));
        pack();
        setMinimumSize(new Dimension(250, 510));
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(closeButton);
        bindEscToClose();
        setIconImage(loadImage("/org/resources/icons/icon.png", 32, 32));
    }
    
    // Convenience
    public static void show(Window owner, App a) {
        new InfoDialog(owner, a).setVisible(true);
    }

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

    private JPanel buildContent(App a) {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(16, 8, 16, 8));
        
        JsonAI ai = CheckEnvAI.getSettingsAI();
        String aiState = "not enabled";
        boolean aiAny = ai.isOAuth2Login || ai.isGeminiAPI || ai.isDocumentAI;
        if (aiAny) aiState = "partially enabled";
        if (ai.isOAuth2Login && ai.isGeminiAPI && ai.isDocumentAI) aiState = "fully enabled";

        // Right: Titel, Untertitel, Lizenztext
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.gridx = 0; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;

        String html = String.format(TEXT_A_HTML, a.NAME, a.VERSION, a.TIME, a.JDK, App.DB, aiState);
		JLabel title = new JLabel(html);
        title.setForeground(new Color(20, 20, 20));
        gc.gridy = 0;
        right.add(title, gc);

        JSeparator sep = new JSeparator();
        gc.gridy = 2;
        right.add(sep, gc);

        JEditorPane license = new JEditorPane("text/html", TEXT_B_HTML);
        license.setEditable(false);
        license.setFocusable(false);
        license.setOpaque(false);
        license.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        license.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try { Desktop.getDesktop().browse(e.getURL().toURI()); } catch (Exception ignored) {}
            }
        });

        JScrollPane scroll = new JScrollPane(license);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(400, 180));
        gc.gridy = 3; gc.fill = GridBagConstraints.BOTH; gc.weighty = 1;
        right.add(scroll, gc);

        root.add(right, BorderLayout.CENTER);

        // Bottom: Buttonzeile
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        closeButton.addActionListener(_ -> dispose());
        buttons.add(closeButton);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }
    
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

    private void bindEscToClose() {
        JRootPane rp = getRootPane();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rp.getActionMap();
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "CLOSE");
        am.put("CLOSE", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { dispose(); }});
    }

    private static Image loadImage(String path, int w, int h) {
        try (InputStream is = InfoDialog.class.getResourceAsStream(path)) {
            if (is == null) return null;
            Image src = ImageIO.read(is);
            return src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

}
