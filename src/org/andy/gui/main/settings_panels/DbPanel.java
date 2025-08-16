package org.andy.gui.main.settings_panels;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.Tools.saveSettingsDB;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.code.main.LoadData;
import org.andy.gui.main.MainWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(QrPanel.class);
    
    private static JButton btnDBEdit = null, btnDBOK = null;
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public DbPanel() {
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
    	JLabel lbl01 = new JLabel("computer");
		JLabel lbl02 = new JLabel("port");
		JLabel lbl03 = new JLabel("database for MasterData");
		JLabel lbl04 = new JLabel("database for ProductiveData");

		JTextField textDBcomputer = new JTextField(LoadData.getStrDBComputer());
		JTextField textDBport = new JTextField(LoadData.getStrDBPort());
		JTextField textDBnameSource = new JTextField(LoadData.getStrDBNameSource());
		JTextField textDBnameDest = new JTextField(LoadData.getStrDBNameDest());
		JCheckBox chkEncryption = new JCheckBox("encrypt database");
		JCheckBox chkServerCert = new JCheckBox("trust server certificate");

		lbl01.setBounds(10, 20, 180, 25);
		lbl02.setBounds(10, 45, 180, 25);
		lbl03.setBounds(10, 70, 180, 25);
		lbl04.setBounds(10, 95, 180, 25);

		textDBcomputer.setBounds(190, 20, 215, 25);
		textDBport.setBounds(190, 45, 215, 25);
		textDBnameSource.setBounds(190, 70, 215, 25);
		textDBnameDest.setBounds(190, 95, 215, 25);
		chkEncryption.setBounds(190, 120, 155, 25);
		chkServerCert.setBounds(190, 145, 155, 25);

		try {
			btnDBEdit = createButton(null, "edit.png");
			btnDBOK = createButton(null, "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnDBEdit.setEnabled(true);
		btnDBEdit.setBounds(410, 20, MainWindow.getButtonx(), MainWindow.getButtony());
		btnDBOK.setBounds(410, 70, MainWindow.getButtonx(), MainWindow.getButtony());

		add(lbl01);
		add(lbl02);
		add(lbl03);
		add(lbl04);

		add(textDBcomputer);
		add(textDBport);
		add(textDBnameSource);
		add(textDBnameDest);
		add(chkEncryption);
		add(chkServerCert);
		
		add(btnDBEdit);
		add(btnDBOK);

		textDBcomputer.setEnabled(false);
		textDBport.setEnabled(false);
		textDBnameSource.setEnabled(false);
		textDBnameDest.setEnabled(false);
		chkEncryption.setEnabled(false);
		chkServerCert.setEnabled(false);
		btnDBOK.setEnabled(false);

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
		// ActionListener
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
		
		btnDBEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textDBcomputer.setEnabled(true);
				textDBport.setEnabled(true);
				textDBnameSource.setEnabled(true);
				textDBnameDest.setEnabled(true);
				chkEncryption.setEnabled(true);
				chkServerCert.setEnabled(true);
				btnDBEdit.setEnabled(false);
				btnDBOK.setEnabled(true);
			}
		});

		btnDBOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoadData.setStrDBComputer(textDBcomputer.getText());
				LoadData.setStrDBPort(textDBport.getText());
				LoadData.setStrDBNameSource(textDBnameSource.getText());
				LoadData.setStrDBNameDest(textDBnameDest.getText());
				
				LoadData.setPrpDBSettings("computer", LoadData.getStrDBComputer());
				LoadData.setPrpDBSettings("port", LoadData.getStrDBPort());
				LoadData.setPrpDBSettings("names", LoadData.getStrDBNameSource());
				LoadData.setPrpDBSettings("named", LoadData.getStrDBNameDest());
				LoadData.setPrpDBSettings("encrypt", LoadData.getStrDBencrypted());
				LoadData.setPrpDBSettings("cert", LoadData.getStrDBServerCert());

				try {
					saveSettingsDB(LoadData.getPrpDBSettings());
				} catch (IOException e1) {
					logger.error("JFsettings() - " + e1);
				}

				LoadData.LoadProgSettings();

				textDBcomputer.setEnabled(false);
				textDBport.setEnabled(false);
				textDBnameSource.setEnabled(false);
				textDBnameDest.setEnabled(false);
				chkEncryption.setEnabled(false);
				chkServerCert.setEnabled(false);
				btnDBEdit.setEnabled(true);
				btnDBOK.setEnabled(false);
			}
		});
		
		setPreferredSize(new Dimension(550, 185));
	}
}
