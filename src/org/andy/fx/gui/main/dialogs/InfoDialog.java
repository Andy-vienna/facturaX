package org.andy.fx.gui.main.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import javax.imageio.ImageIO;

public final class InfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JButton closeButton = new JButton("Schließen");
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

    public InfoDialog(Window owner, String appName, String appVersion, String[] appBuild) {
        super(owner, "Über " + appName + " (" + appVersion + ")", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent(appName, appVersion, appBuild));
        pack();
        setMinimumSize(new Dimension(250, 475));
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(closeButton);
        bindEscToClose();
        setIconImage(loadImage("/org/resources/icons/icon.png", 32, 32));
    }
    
    // Convenience
    public static void show(Window owner, String appName, String appVersion, String[] appBuild) {
        new InfoDialog(owner, appName, appVersion, appBuild).setVisible(true);
    }

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

    private JPanel buildContent(String appName, String appVersion, String[] appBuild) {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(16, 8, 16, 8));

        // Right: Titel, Untertitel, Lizenztext
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.gridx = 0; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("<html>" +
        		"<span style='font-size:24px; font-weight:bold;'>" + appName + " (" + appVersion + ") " + "</span><br>" +
        		"<span style='font-size:9px; font-weight:bold; color:blue;'></span><br>" +
        		"<span style='font-size:8px; font-weight:bold; color:black;'>build date / time: </span>" +
        		"<span style='font-size:8px; font-weight:bold; color:blue;'>" + appBuild[0] + "</span><br>" +
        		"<span style='font-size:10px; font-weight:bold; color:black;'>Java JDK version : </span>" +
        		"<span style='font-size:10px; font-weight:bold; color:red ;'>" + appBuild[1] + "</span><br>" +
        		"<span style='font-size:10px; font-weight:bold; color:black;'>Datenbank : </span>" +
        		"<span style='font-size:10px; font-weight:bold; color:red ;'>" + appBuild[2] + "</span>" +
        		"</html>");
        //title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(20, 20, 20));
        gc.gridy = 0;
        right.add(title, gc);

        JSeparator sep = new JSeparator();
        gc.gridy = 2;
        right.add(sep, gc);

        JEditorPane license = new JEditorPane("text/html", TEXT_B_HTML);
        license.setEditable(false);
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
