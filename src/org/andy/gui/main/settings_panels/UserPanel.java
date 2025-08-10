package org.andy.gui.main.settings_panels;

import static org.andy.toolbox.crypto.Password.checkComplexity;
import static org.andy.toolbox.crypto.Password.hashPwd;
import static org.andy.toolbox.crypto.Password.verifyPwd;
import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.code.dataStructure.entitiyMaster.User;
import org.andy.code.dataStructure.repositoryMaster.UserRepository;
import org.andy.code.main.LoadData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(QrPanel.class);
    
    private static JButton btnShowPwd = null, btnPwdOK = null;
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
    private UserRepository userRepository = new UserRepository();
	private List<User> userListe = new ArrayList<>();
	private User storedUser = new User();
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public UserPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "SEPA OR-Code Einstellungen (wirksam nach Neustart)");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	JLabel lbl01 = new JLabel("Username");
		JLabel lbl02 = new JLabel("Kennwort alt");
		JLabel lbl03 = new JLabel("Kennwort neu");
		JLabel lbl04 = new JLabel("Kennw. wiederh.");
		JLabel lblBenutzerrolle = new JLabel("Benutzerrolle");

		JTextField textUserName = new JTextField();
		JPasswordField pwdPassOld = new JPasswordField();
		JPasswordField pwdPassNew = new JPasswordField();
		JPasswordField pwdPassNewR = new JPasswordField();
		
		ArrayList<String> roles = new ArrayList<>();
		roles.add(" ");
		roles.add("user");
		roles.add("superuser");
		roles.add("finacialuser");
		roles.add("admin");
		JComboBox<String> cmbRoles = new JComboBox<>(roles.toArray(new String[0]));

		lbl01.setBounds(10, 20, 60, 25);
		lbl02.setBounds(10, 45, 90, 25);
		lbl03.setBounds(10, 70, 90, 25);
		lbl04.setBounds(10, 95, 90, 25);
		lblBenutzerrolle.setBounds(10, 120, 90, 25);

		textUserName.setBounds(100, 20, 220, 25);
		pwdPassOld.setBounds(100, 45, 220, 25);
		pwdPassNew.setBounds(100, 70, 220, 25);
		pwdPassNewR.setBounds(100, 95, 220, 25);
		cmbRoles.setBounds(100, 120, 220, 25);

		pwdPassOld.setText("");
		pwdPassOld.setEchoChar('*');
		pwdPassNew.setText("");
		pwdPassNew.setEchoChar('*');
		pwdPassNewR.setText("");
		pwdPassNewR.setEchoChar('*');

		try {
			btnShowPwd = createButton("...", null);
			btnPwdOK = createButton(null, "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnShowPwd.setEnabled(true);
		btnPwdOK.setEnabled(true);
		btnShowPwd.setBounds(320, 45, 25, 75);
		btnPwdOK.setBounds(360, 20, JFoverview.getButtonx(), JFoverview.getButtony());

		add(lbl01);
		add(lbl02);
		add(lbl03);
		add(lbl04);
		add(lblBenutzerrolle);
		add(textUserName);
		add(pwdPassOld);
		add(pwdPassNew);
		add(pwdPassNewR);
		add(cmbRoles);
		add(btnShowPwd);
		add(btnPwdOK);

		//###################################################################################################################################################
		// ActionListener
		//###################################################################################################################################################

		pwdPassNewR.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (Arrays.equals(pwdPassNew.getPassword(), pwdPassNewR.getPassword())) {
					pwdPassNew.setBackground(Color.WHITE);
					pwdPassNewR.setBackground(Color.WHITE);
				} else {
					pwdPassNew.setBackground(Color.PINK);
					pwdPassNewR.setBackground(Color.PINK);
				}
			}
		});

		btnShowPwd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pwdPassOld.getEchoChar() == '*') {
					pwdPassOld.setEchoChar((char) 0);
				} else {
					pwdPassOld.setEchoChar('*');
				}
				if (pwdPassNew.getEchoChar() == '*') {
					pwdPassNew.setEchoChar((char) 0);
				} else {
					pwdPassNew.setEchoChar('*');
				}
				if (pwdPassNewR.getEchoChar() == '*') {
					pwdPassNewR.setEchoChar((char) 0);
				} else {
					pwdPassNewR.setEchoChar('*');
				}
			}
		});

		btnPwdOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				boolean bCheckComplexity = false, bCheckUser = false;

				bCheckComplexity = checkComplexity(pwdPassNew.getPassword());
				
				userListe.clear();
				userListe.addAll(userRepository.findAll());

				for(int x = 0; x < userListe.size(); x++) {
					storedUser = userListe.get(x);
					if(storedUser.getId().trim().equals(LoadData.getStrAktUser())) {
						bCheckUser = true; // user exists
						break;
					}
				}

				if(!bCheckUser) { //neuer User

					if(!bCheckComplexity) {
						pwdPassNew.setText("");
						pwdPassNewR.setText("");
						JOptionPane.showMessageDialog(null, "<html>Das Passwort entspricht nicht den Anforderungen ...<br>[>8 Zeichen, a-z, A-Z, 0-9, @#$%^&+=-_!?.]</html>",
								"Usermanagement", JOptionPane.ERROR_MESSAGE);
						return;
					}

					if(Arrays.equals(pwdPassNew.getPassword(), pwdPassNewR.getPassword())) {
						char[] passwordChars = pwdPassNew.getPassword();
						String newPass = hashPwd(passwordChars);
						
						String userRole = cmbRoles.getSelectedItem().toString();
						if(userRole.equals(" ")) {
							JOptionPane.showMessageDialog(null, "Bitte Benutzerrolle auswählen", "Usermanagement", JOptionPane.ERROR_MESSAGE);
							return;
						}

						User newUser = new User();
						
						newUser.setId(textUserName.getText().trim());
						newUser.setHash(newPass);
						newUser.setRoles(userRole);
						
						userRepository.insert(newUser);
						
						Arrays.fill(passwordChars, '\0');
						newPass = null;
						
					} else {
						JOptionPane.showMessageDialog(null, "Passwörter nicht gleich", "Usermanagement", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				if(bCheckUser) { //bekannter User

					if(!bCheckComplexity) {
						pwdPassNew.setText("");
						pwdPassNewR.setText("");
						JOptionPane.showMessageDialog(null, "<html>Das Passwort entspricht nicht den Anforderungen ...<br>[>8 Zeichen, a-z, A-Z, 0-9, @#$%^&+=-_!?.]</html>",
								"Usermanagement", JOptionPane.ERROR_MESSAGE);
						return;
					}

					if(pwdPassOld.getPassword().length != 0 && Arrays.equals(pwdPassNew.getPassword(), pwdPassNewR.getPassword())) {
						char[] passwordChars = pwdPassOld.getPassword();
						boolean bCheckOld = verifyPwd(passwordChars, storedUser.getHash().trim());
						Arrays.fill(passwordChars, '\0');

						if(bCheckOld) {
							char[] newPasswordChars = pwdPassNew.getPassword();
							String changePass = hashPwd(newPasswordChars);

							storedUser.setHash(changePass);
							
							userRepository.update(storedUser);
							
							Arrays.fill(newPasswordChars, '\0');
							changePass = null;
						}else {
							JOptionPane.showMessageDialog(null, "altes Passwort nicht OK", "Usermanagement", JOptionPane.ERROR_MESSAGE);
							return;
						}

					} else {
						JOptionPane.showMessageDialog(null, "Passwörter nicht gleich", "Usermanagement", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				textUserName.setBackground(Color.WHITE);
				pwdPassOld.setBackground(Color.WHITE);
				textUserName.setText("");
				pwdPassOld.setText("");
				pwdPassNew.setText("");
				pwdPassNewR.setText("");
			}
		});
		
		setPreferredSize(new Dimension(500, 165));
	}
}
