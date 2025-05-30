package org.andy.gui.settings;

import static org.andy.toolbox.crypto.Password.checkComplexity;
import static org.andy.toolbox.crypto.Password.hashPwd;
import static org.andy.toolbox.crypto.Password.verifyPwd;
import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Insert.sqlInsert;
import static org.andy.toolbox.sql.Read.sqlReadArrayList;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.toolbox.misc.SetFrameIcon;
import javax.swing.JComboBox;

public class JFuserMgmt extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFuserMgmt.class);

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private static final String TBL_USER = "tblUser";
	private static String sConn;

	private static JButton btnShowPwd = null, btnPwdOK = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFuserMgmt frame = new JFuserMgmt();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.error("error loading user management screen - " + e);
				}
			}
		});
	}

	public JFuserMgmt() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("user.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Benutzerverwaltung");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 446, 186);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

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

		lbl01.setBounds(10, 10, 60, 25);
		lbl02.setBounds(10, 35, 90, 25);
		lbl03.setBounds(10, 60, 90, 25);
		lbl04.setBounds(10, 85, 90, 25);
		lblBenutzerrolle.setBounds(10, 115, 90, 25);

		textUserName.setBounds(100, 10, 220, 25);
		pwdPassOld.setBounds(100, 35, 220, 25);
		pwdPassNew.setBounds(100, 60, 220, 25);
		pwdPassNewR.setBounds(100, 85, 220, 25);
		cmbRoles.setBounds(100, 115, 220, 25);

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
		btnShowPwd.setBounds(320, 35, 25, 75);
		btnPwdOK.setBounds(360, 10, 60, 100);

		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04);
		contentPane.add(lblBenutzerrolle);
		contentPane.add(textUserName);
		contentPane.add(pwdPassOld);
		contentPane.add(pwdPassNew);
		contentPane.add(pwdPassNewR);
		contentPane.add(cmbRoles);
		contentPane.add(btnShowPwd);
		contentPane.add(btnPwdOK);

		//###################################################################################################################################################
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

				String tmpUser = textUserName.getText();

				ArrayList<String> User = new ArrayList<String>();
				String storedUser;
				boolean bCheckComplexity = false, bCheckUser = false, bCheckOld = false;

				bCheckComplexity = checkComplexity(pwdPassNew.getPassword());

				try {
					ArrayList<ArrayList<String>> arrUser = sqlReadArrayList(sConn, TBL_USER, "Id", "*");
					int AnzUser = arrUser.size();
					for(int x = 0; x < AnzUser; x++) {
						User = arrUser.get(x);
						storedUser = User.get(0).toString();
						if(storedUser.equals(tmpUser)) {
							bCheckUser = true;
							break;
						}
					}
				} catch (Exception e1) {
					logger.error("error while checking user - " + e1);
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

						try {

							String sSQLStatement = "INSERT INTO " + TBL_USER + " VALUES ('" + textUserName.getText() + "','" + newPass + "','" + userRole + "')"; //SQL Befehlszeile

							sqlInsert(sConn, sSQLStatement);

						} catch (SQLException | ClassNotFoundException e1) {
							logger.error("error writing new user to database - " + e1);
						} finally {
							Arrays.fill(passwordChars, '\0');
							newPass = null;
						}
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
						bCheckOld = verifyPwd(passwordChars, User.get(1).toString());
						Arrays.fill(passwordChars, '\0');

						if(bCheckOld) {
							char[] newPasswordChars = pwdPassNew.getPassword();
							String changePass = hashPwd(newPasswordChars);

							try {

								String sSQLStatement = "UPDATE " + TBL_USER + " SET [Hash] = '" + changePass + "' WHERE [Id] = '" + textUserName.getText() + "'";

								sqlUpdate(sConn, sSQLStatement);

							} catch (SQLException | ClassNotFoundException e1) {
								logger.error("error updating user to database - " + e1);
							} finally {
								Arrays.fill(newPasswordChars, '\0');
								changePass = null;
							}
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
				dispose();
			}
		});

	}

	public static void setsConn(String sConn) {
		JFuserMgmt.sConn = sConn;
	}
}
