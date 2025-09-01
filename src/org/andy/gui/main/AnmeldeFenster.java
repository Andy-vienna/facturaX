package org.andy.gui.main;

import static org.andy.toolbox.crypto.Password.verifyPwd;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.andy.code.dataStructure.entitiyMaster.User;
import org.andy.code.dataStructure.repositoryMaster.UserRepository;

public final class AnmeldeFenster {

    public interface AuthCallback {
        void onSuccess(User user);
        void onCancel();
    }

    private final JFrame frame = new JFrame("Anmeldung");
    private final JTextField userField = new JTextField(18);
    private final JPasswordField passField = new JPasswordField(18);
    private final JButton loginBtn = new JButton("OK");
    private final JButton cancelBtn = new JButton("Cancel");

    private final UserRepository userRepository;
    private final AuthCallback callback;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public AnmeldeFenster(UserRepository repo, AuthCallback cb) {
        this.userRepository = Objects.requireNonNull(repo);
        this.callback = Objects.requireNonNull(cb);

        BufferedImage bg = loadImage("/icons/hintergrund_450.jpg");
        JPanel root = new BackgroundPanel(bg);

        root.setLayout(new GridBagLayout());
        JPanel form = buildFormPanel();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; 
        c.gridy = 0;
        c.weightx = 1; 
        c.weighty = 1;                 // Platz oberhalb aufnehmen
        c.anchor  = GridBagConstraints.SOUTH;   // Panel nach unten
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(0, 0, 25, 0);    // Abstand zum unteren Rand
        root.add(form, c);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(root);
        frame.setSize(450, 265); //(850, 500);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        setAppIcon(frame, "/icons/icon.png");
        JRootPane rootPane = frame.getRootPane();
        rootPane.setDefaultButton(loginBtn);
        wireActions(rootPane);
    }

    public void show() {
        frame.setVisible(true);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 6, 1, 6);
        gc.anchor = GridBagConstraints.SOUTH;
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Benutzeranmeldung", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(Color.WHITE);

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        p.add(title, gc);

        gc.gridwidth = 1;

        gc.gridy = 1; gc.gridx = 0; //p.add(new JLabel("Benutzer"), gc);
        gc.gridx = 1; p.add(userField, gc);

        gc.gridy = 2; gc.gridx = 0; //p.add(new JLabel("Passwort"), gc);
        gc.gridx = 1; p.add(passField, gc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttons.setOpaque(false);
        buttons.add(loginBtn);
        buttons.add(cancelBtn);

        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        p.add(buttons, gc);

        return p;
    }

    private void wireActions(JRootPane root) {
        // Enter = Login, Esc = Cancel
    	InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    	ActionMap am = root.getActionMap();
    	im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "CANCEL");
    	am.put("CANCEL", new AbstractAction() {
    	    @Override public void actionPerformed(java.awt.event.ActionEvent e) { doCancel(); }
    	});

        loginBtn.addActionListener(_ -> doLogin());
        cancelBtn.addActionListener(_ -> doCancel());
    }

    private void doLogin() {
        loginBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

        final String userId = userField.getText().trim();
        final char[] pwd = passField.getPassword();

        new SwingWorker<User, Void>() {
            @Override protected User doInBackground() {
                try {
                    User u = userRepository.findById(userId);
                    if (u == null) return null;
                    String hash = u.getHash();
                    boolean ok = verifyPwd(pwd, hash);
                    return ok ? u : null;
                } finally {
                    Arrays.fill(pwd, '\0');
                }
            }
            @Override protected void done() {
                try {
                    User u = get();
                    if (u != null) {
                        frame.dispose();
                        callback.onSuccess(u);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Benutzer oder Passwort falsch.",
                                "Anmeldung",
                                JOptionPane.ERROR_MESSAGE);
                        passField.setText("");
                        loginBtn.setEnabled(true);
                        cancelBtn.setEnabled(true);
                        passField.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Fehler bei der Anmeldung.",
                            "Anmeldung",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println(ex);
                    passField.setText("");
                    loginBtn.setEnabled(true);
                    cancelBtn.setEnabled(true);
                    passField.requestFocusInWindow();
                    callback.onCancel();
                }
            }
        }.execute();
    }

    private void doCancel() {
        frame.dispose();
        callback.onCancel();
    }

    private static void setAppIcon(Window w, String path) {
        BufferedImage img = loadImage(path);
        if (img != null) w.setIconImage(img);
    }

    private static BufferedImage loadImage(String path) {
        try (InputStream is = AnmeldeFenster.class.getResourceAsStream(path)) {
            return is != null ? ImageIO.read(is) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    // Panel mit skaliertem Hintergrund
    private static final class BackgroundPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Image bg;
        BackgroundPanel(Image bg) { this.bg = bg; }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                int w = getWidth(), h = getHeight();
                g.drawImage(bg, 0, 0, w, h, this);
            }
        }
    }
}
