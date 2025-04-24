package org.andy.gui.settings;

import static main.java.toolbox.misc.CreateObject.createButton;
import static main.java.toolbox.misc.Tools.saveSettingsDB;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.main.LoadData;
import main.java.toolbox.crypto.Caesar;
import main.java.toolbox.misc.SetFrameIcon;

public class JFdbSettings extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFsepaQR.class);

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private static JButton btnDBOK = null, btnShowDBPwd = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI(boolean bStart) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFdbSettings frame = new JFdbSettings(bStart);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("fatal error loading gui for editing data - " + e);
				}
			}
		});
	}

	public JFdbSettings(boolean bStart) {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("database.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("DB Einstellungen");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 515, 283);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel lbl01 = new JLabel("computer");
		JLabel lbl02 = new JLabel("port");
		JLabel lbl03 = new JLabel("database for MasterData");
		JLabel lbl04 = new JLabel("database for ProductiveData");
		JLabel lbl05 = new JLabel("user");
		JLabel lbl06 = new JLabel("password");
		JLabel lbl07 = new JLabel("SQL-Server Instanz");

		JTextField textDBcomputer = new JTextField();
		JTextField textDBport = new JTextField(LoadData.getStrDBPort());
		JTextField textDBnameSource = new JTextField(LoadData.getStrDBNameSource());
		JTextField textDBnameDest = new JTextField(LoadData.getStrDBNameDest());
		JTextField textDBuser = new JTextField(LoadData.getStrDBUser());
		JPasswordField pwdDBpass = new JPasswordField(LoadData.getStrDBPass());
		JCheckBox chkEncryption = new JCheckBox("encrypt database");
		JCheckBox chkServerCert = new JCheckBox("trust server certificate");
		JComboBox<String> serverDropdown = new JComboBox<>();
		serverDropdown.addItem("Lade Server...");

		lbl01.setBounds(10, 10, 180, 25);
		lbl02.setBounds(10, 35, 180, 25);
		lbl03.setBounds(10, 60, 180, 25);
		lbl04.setBounds(10, 85, 180, 25);
		lbl05.setBounds(10, 110, 180, 25);
		lbl06.setBounds(10, 135, 180, 25);
		lbl07.setBounds(10, 210, 180, 25);

		textDBcomputer.setBounds(190, 10, 215, 25);
		textDBport.setBounds(190, 35, 215, 25);
		textDBnameSource.setBounds(190, 60, 215, 25);
		textDBnameDest.setBounds(190, 85, 215, 25);
		textDBuser.setBounds(190, 110, 215, 25);
		pwdDBpass.setBounds(190, 135, 215, 25);
		pwdDBpass.setEchoChar('*');
		chkEncryption.setBounds(190, 160, 155, 25);
		chkServerCert.setBounds(190, 185, 155, 25);
		serverDropdown.setBounds(190, 210, 215, 25);

		try {
			btnDBOK = createButton(null, "ok.png");
			btnShowDBPwd = createButton("...", null);
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnDBOK.setBounds(410, 10, 80, 40);
		btnShowDBPwd.setBounds(400, 135, 25, 25);

		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04);
		contentPane.add(lbl05);
		contentPane.add(lbl06);
		contentPane.add(lbl07);

		contentPane.add(btnDBOK);
		contentPane.add(btnShowDBPwd);
		contentPane.add(textDBcomputer);
		contentPane.add(textDBport);
		contentPane.add(textDBnameSource);
		contentPane.add(textDBnameDest);
		contentPane.add(textDBuser);
		contentPane.add(pwdDBpass);
		contentPane.add(chkEncryption);
		contentPane.add(chkServerCert);
		contentPane.add(serverDropdown);

		textDBcomputer.setEnabled(false);
		textDBport.setEnabled(false);
		textDBnameSource.setEnabled(false);
		textDBnameDest.setEnabled(false);
		textDBuser.setEnabled(false);
		pwdDBpass.setEnabled(false);
		chkEncryption.setEnabled(false);
		chkServerCert.setEnabled(false);
		serverDropdown.setEnabled(false);
		btnDBOK.setEnabled(false);
		btnShowDBPwd.setEnabled(false);

		if (LoadData.getStrDBencrypted().equals("true")) {
			chkEncryption.setSelected(true);
		} else {
			chkEncryption.setSelected(false);
		}
		if (LoadData.getStrDBServerCert().equals("true")) {
			chkServerCert.setSelected(true);
		} else {
			chkServerCert.setSelected(false);
		}

		//###################################################################################################################################################
		//###################################################################################################################################################

		chkEncryption.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LoadData.setStrDBencrypted("true");
				} else {
					LoadData.setStrDBencrypted("false");
				}
			}
		});

		chkServerCert.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LoadData.setStrDBServerCert("true");
				} else {
					LoadData.setStrDBServerCert("false");
				}
			}
		});

		serverDropdown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				serverDropdown.setEnabled(true);
				textDBcomputer.setEnabled(true);
				textDBport.setEnabled(true);
				textDBnameSource.setEnabled(true);
				textDBnameDest.setEnabled(true);
				textDBuser.setEnabled(true);
				pwdDBpass.setEnabled(true);
				btnShowDBPwd.setEnabled(true);
				chkEncryption.setEnabled(true);
				chkServerCert.setEnabled(true);
				btnDBOK.setEnabled(true);
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LoadData.setStrDBservice(serverDropdown.getSelectedItem().toString());
					if(serverDropdown.getSelectedIndex() > 0) {

						String[] parts = serverDropdown.getSelectedItem().toString().split("\\\\");

						if (parts.length == 2) {
							textDBcomputer.setText(parts[0]); // Computername eintragen
							LoadData.setStrDBComputer(parts[0]); // Computername eintragen
							LoadData.setStrDBservice(parts[1]); // Instanzname eintragen
						}

					}
				}
			}
		});

		btnDBOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoadData.setStrDBPort(textDBport.getText());
				LoadData.setStrDBNameSource(textDBnameSource.getText());
				LoadData.setStrDBNameDest(textDBnameDest.getText());
				LoadData.setStrDBUser(textDBuser.getText());
				LoadData.setStrDBPass(String.valueOf(pwdDBpass.getPassword()));
				String tmpPW = Caesar.encryptString(LoadData.getStrDBPass(), 13);

				LoadData.setPrpDBSettings("service", LoadData.getStrDBservice());
				LoadData.setPrpDBSettings("computer", LoadData.getStrDBComputer());
				LoadData.setPrpDBSettings("port", LoadData.getStrDBPort());
				LoadData.setPrpDBSettings("names", LoadData.getStrDBNameSource());
				LoadData.setPrpDBSettings("named", LoadData.getStrDBNameDest());
				LoadData.setPrpDBSettings("user", LoadData.getStrDBUser());
				LoadData.setPrpDBSettings("pass", tmpPW);
				LoadData.setPrpDBSettings("encrypt", LoadData.getStrDBencrypted());
				LoadData.setPrpDBSettings("cert", LoadData.getStrDBServerCert());

				try {
					saveSettingsDB(LoadData.getPrpDBSettings());
				} catch (IOException e1) {
					logger.error("JFsettings() - " + e1);
				}

				LoadData.LoadProgSettings();

				LoadData.setbFinished(true);
				dispose();
			}
		});

		btnShowDBPwd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pwdDBpass.getEchoChar() == '*') {
					pwdDBpass.setEchoChar((char) 0);
				} else {
					pwdDBpass.setEchoChar('*');
				}
			}
		});

		// In neuem Thread Server suchen (damit GUI nicht hängt)
		new Thread(() -> {
			List<String> serverList = findSqlServers();
			SwingUtilities.invokeLater(() -> {
				serverDropdown.removeAllItems();
				if (serverList.isEmpty()) {
					serverDropdown.addItem("Keine Server gefunden");
				} else {
					for (String server : serverList) {
						serverDropdown.addItem("");
						serverDropdown.addItem(server);
					}
				}
			});
		}).start();

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static List<String> findSqlServers() {
		List<String> servers = new ArrayList<>();
		try {
			ProcessBuilder builder = new ProcessBuilder("sqlcmd", "-L");
			Process process = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			boolean reading = false;

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("Server:") || line.startsWith("Servers:")) {
					reading = true;
					continue;
				}
				if (reading && !line.isEmpty()) {
					servers.add(line);
				}
			}

			process.waitFor();
		} catch (Exception e) {
			servers.add("Fehler bei Server-Erkennung: " + e.getMessage());
		}
		return servers;
	}

}
