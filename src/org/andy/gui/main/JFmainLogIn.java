package org.andy.gui.main;

import static main.java.toolbox.crypto.Password.verifyPwd;
import static main.java.toolbox.misc.CreateObject.createButton;
import static main.java.toolbox.sql.Read.sqlReadArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.FlatIntelliJLaf;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.misc.ImagePanel;
import org.andy.gui.misc.MyFlatTabbedPaneUI;

public class JFmainLogIn {

	private static final Logger logger = LogManager.getLogger(JFmainLogIn.class);

	private static final String TBL_USER = "tblUser";
	private static final String SORT_ID = "Id";

	private static String sConn;

	private static JFrame frame;

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static void loadLogIn() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				FlatIntelliJLaf.setup();

				try {
					UIManager.setLookAndFeel(new FlatIntelliJLaf());
					UIManager.put("Button.arc", 10);
					UIManager.put("Component.arc", 10);
					UIManager.put("TextComponent.arc", 10);
					UIManager.put("ProgressBar.arc",  10);
					UIManager.put("TabbedPaneUI", MyFlatTabbedPaneUI.class.getName());
					UIManager.put("TabbedPane.tabType", "card");
					UIManager.put("TabbedPane.cardTabSelectionHeight", 0);
					UIManager.put("MenuBar.selectionBackground", Color.LIGHT_GRAY);
					UIManager.put("MenuBar.hoverBackground", Color.LIGHT_GRAY);
					UIManager.put("MenuBar.underlineSelectionColor", Color.LIGHT_GRAY);
					UIManager.put("MenuBar.underlineSelectionBackground", Color.LIGHT_GRAY);
					UIManager.put("MenuItem.selectionBackground", Color.LIGHT_GRAY);
					UIManager.put("MenuItem.hoverBackground", Color.LIGHT_GRAY);
					UIManager.put("MenuItem.underlineSelectionColor", Color.LIGHT_GRAY);
					UIManager.put("MenuItem.underlineSelectionBackground", Color.LIGHT_GRAY);
					UIManager.put("TableHeader.background", new Color(255,248,220));
					UIManager.put("TableHeader.foreground", Color.BLACK);
				} catch( Exception ex ) {
					logger.fatal("cannot load FlatIntelliJLaf theme for UI - " + ex);
				}

				try {
					new JFmainLogIn();
				} catch (Exception e) {
					logger.fatal("loadLogIn fehlgeschlagen - " + e);
				}

			}
		});
	}

	public JFmainLogIn() {

		ImageIcon icon = null;
		try (InputStream is = JFmainLogIn.class.getResourceAsStream("/icons/icon.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			icon = new ImageIcon(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("LogIn");
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(icon.getImage());
		frame.getContentPane().add(createMainPanel());
		frame.setSize(850, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.revalidate();  // Validiert das Layout neu
		frame.repaint();     // Zeichnet das Layout neu
	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	private JPanel createMainPanel() {

		Image image = null;
		try (InputStream is = JFinfo.class.getResourceAsStream("/icons/hintergrund.jpg")) {
			if (is == null) {
				throw new RuntimeException("Hintergrund nicht gefunden!");
			}
			image = ImageIO.read(is);
		} catch (IOException e) {
			logger.error("error loading backgroung image - " + e);
		}

		// ... und jetzt das mainPanel aufbauen
		ImagePanel mainPanel = new ImagePanel(new BorderLayout());

		mainPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				JFoverview.loadGUI();
				frame.dispose();
			}
		});
		mainPanel.setImage(image, false); //hier kann man einstellen, ob das Bild im Original oder eingepasst ausgegeben werden soll (true/false)
		mainPanel.setLayout(null);

		//------------------------------------------------------------------------------
		//Felddefinitionen
		//------------------------------------------------------------------------------
		JLabel lblNewLabel = new JLabel(StartUp.APP_NAME + StartUp.APP_VERSION);
		lblNewLabel.setForeground(Color.ORANGE);
		lblNewLabel.setFont(new Font("Tempus Sans ITC", Font.BOLD, 50));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(20, 10, 810, 70);
		mainPanel.add(lblNewLabel);

		String sLic = StartUp.getAPP_LICENSE();
		JLabel lblLicense = new JLabel(sLic);
		lblLicense.setForeground(Color.RED);
		if(StartUp.getAPP_MODE() == 1) {
			lblLicense.setForeground(Color.ORANGE);
		}
		if(StartUp.getAPP_MODE() == 2) {
			lblLicense.setForeground(Color.GREEN);
		}
		lblLicense.setHorizontalAlignment(SwingConstants.LEFT);
		lblLicense.setFont(new Font("Arial", Font.BOLD, 14));
		lblLicense.setBounds(20, 450, 295, 40);
		mainPanel.add(lblLicense);

		JTextField textLIUser = new JTextField();
		textLIUser.setFont(new Font("Tahoma", Font.BOLD, 12));
		textLIUser.setColumns(10);
		textLIUser.setBounds(338, 390, 175, 25);
		mainPanel.add(textLIUser);

		JPasswordField pwdLIPasswort = new JPasswordField();
		pwdLIPasswort.setFont(new Font("Tahoma", Font.BOLD, 12));
		pwdLIPasswort.setBounds(338, 418, 175, 25);
		pwdLIPasswort.setEchoChar('*');
		mainPanel.add(pwdLIPasswort);

		//------------------------------------------------------------------------------
		// Schaltflächen anzeigen
		//------------------------------------------------------------------------------
		JButton btnOK = null, btnExit = null, btnShowPwd = null;
		try {
			btnOK = createButton(null, "ok.png");
			btnExit = createButton(null, "exit.png");
			btnShowPwd = createButton("...", null);
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnOK.setBounds(385, 450, 80, 40);
		btnOK.setEnabled(true);
		btnExit.setBounds(780, 450, 60, 40);
		btnExit.setEnabled(true);
		btnShowPwd.setBounds(513, 418, 25, 25);
		btnShowPwd.setEnabled(true);

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Anmeldedaten erfassen und auswerten
				//------------------------------------------------------------------------------
				LoadData.setStrAktUser(textLIUser.getText());
				if(LoadData.getStrAktUser().equals("admin")){
					JOptionPane.showMessageDialog(null, "user nicht zur Anmeldung freigegeben", "Anmeldefehler", JOptionPane.ERROR_MESSAGE);
					LoadData.setStrAktUser(null);
					textLIUser.setText("");
					pwdLIPasswort.setText("");
					return;
				}
				ArrayList<String> User = new ArrayList<String>();
				String storedUser;
				boolean bCheckUser = false, bLogIn = false;
				try {
					ArrayList<ArrayList<String>> arrUser = sqlReadArrayList(sConn, TBL_USER, SORT_ID, "*");

					int AnzUser = arrUser.size();
					for(int x = 0; x < AnzUser; x++) {
						User = arrUser.get(x);
						storedUser = User.get(0).toString();
						if(storedUser.equals(LoadData.getStrAktUser())) {
							if(!(LoadData.getStrAktUser().equals("admin"))) {
								bCheckUser = true;
								storedUser = null;
								break;
							}
						}
					}
					if(bCheckUser) {
						char[] passwordChars = pwdLIPasswort.getPassword();
						pwdLIPasswort.setText("");
						bLogIn = verifyPwd(passwordChars, User.get(1).toString());
						Arrays.fill(passwordChars, '\0');
					}
				} catch (Exception e1) {
					logger.error("error while checking user for login - " + e1);
				}

				if(bLogIn) {
					try {
						SQLmasterData.loadBaseData();
					} catch (SQLException | ParseException | ClassNotFoundException e1) {
						logger.error("createMainPanel() - " + e1);
					}
					mainPanel.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(null, "User oder Passwort falsch ...", "Anmeldefehler", JOptionPane.ERROR_MESSAGE);
					LoadData.setStrAktUser(null);
					textLIUser.setText("");
					pwdLIPasswort.setText("");
					return;
				}
			}
		});
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnShowPwd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pwdLIPasswort.getEchoChar() == '*') {
					pwdLIPasswort.setEchoChar((char) 0);
				}else {
					pwdLIPasswort.setEchoChar('*');
				}
			}
		});

		mainPanel.add(btnOK);
		mainPanel.add(btnExit);
		mainPanel.add(btnShowPwd);
		//------------------------------------------------------------------------------
		return mainPanel;
	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFmainLogIn.sConn = sConn;
	}

}
