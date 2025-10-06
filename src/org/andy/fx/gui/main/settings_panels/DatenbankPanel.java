package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.code.misc.FileTools.saveSettingsDB;
import static org.andy.fx.gui.misc.CreateButton.createButton;

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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatenbankPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(QrCodePanel.class);
    
    private static JButton btnDBEdit = null, btnDBOK = null;
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public DatenbankPanel() {
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
    	
    	JLabel[] lbl = new JLabel[7];
    	String[] labels = {"database type", "computer", "port", "database for MasterData", "database for ProductiveData", "user", "pass"};
    	String[] cmb = {"Microsoft SQL", "PostgreSQL"};
    	
    	for (int n = 0; n < labels.length; n++) {
    		lbl[n] = new JLabel(labels[n]);
    		lbl[n].setBounds(10, 20 + (n * 25), 180, 25);
    		add(lbl[n]);
    	}
    	
    	JComboBox<String> cmbDBtyp = new JComboBox<>(cmb);
    	cmbDBtyp.setBounds(190, 20, 215, 25);
    	add(cmbDBtyp);
    	

		JTextField textDBcomputer = new JTextField(Einstellungen.getStrDBComputer());
		JTextField textDBport = new JTextField(Einstellungen.getStrDBPort());
		JTextField textDBnameSource = new JTextField(Einstellungen.getStrDBNameSource());
		JTextField textDBnameDest = new JTextField(Einstellungen.getStrDBNameDest());
		JTextField textDBuser = new JTextField(Einstellungen.getStrDBuser());
		JTextField textDBpass = new JTextField(Einstellungen.getStrDBpass());
		JCheckBox chkEncryption = new JCheckBox("encrypt database");
		JCheckBox chkServerCert = new JCheckBox("trust server certificate");

		textDBcomputer.setBounds(190, 45, 215, 25);
		textDBport.setBounds(190, 70, 215, 25);
		textDBnameSource.setBounds(190, 95, 215, 25);
		textDBnameDest.setBounds(190, 120, 215, 25);
		textDBuser.setBounds(190, 145, 215, 25);
		textDBpass.setBounds(190, 170, 215, 25);
		chkEncryption.setBounds(190, 195, 155, 25);
		chkServerCert.setBounds(190, 220, 155, 25);

		try {
			btnDBEdit = createButton(null, ButtonIcon.EDIT.icon(), null);
			btnDBOK = createButton(null, ButtonIcon.OK.icon(), null);
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnDBEdit.setEnabled(true);
		btnDBEdit.setBounds(410, 20, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnDBOK.setBounds(410, 70, HauptFenster.getButtonx(), HauptFenster.getButtony());

		add(textDBcomputer);
		add(textDBport);
		add(textDBnameSource);
		add(textDBnameDest);
		add(textDBuser);
		add(textDBpass);
		add(chkEncryption);
		add(chkServerCert);
		
		add(btnDBEdit);
		add(btnDBOK);
		
		switch(Einstellungen.getStrDBtype()) {
			case "mssql" -> {cmbDBtyp.setSelectedIndex(0); chkEncryption.setVisible(true); chkServerCert.setVisible(true);}
			case "postgre" -> {cmbDBtyp.setSelectedIndex(1); chkEncryption.setVisible(false); chkServerCert.setVisible(false);}
			default -> cmbDBtyp.setSelectedIndex(-1);
		}

		cmbDBtyp.setEnabled(false);
		textDBcomputer.setEnabled(false);
		textDBport.setEnabled(false);
		textDBnameSource.setEnabled(false);
		textDBnameDest.setEnabled(false);
		textDBuser.setEnabled(false);
		textDBpass.setEnabled(false);
		chkEncryption.setEnabled(false);
		chkServerCert.setEnabled(false);
		btnDBOK.setEnabled(false);

		if (Einstellungen.getStrDBencrypted().equals("true")) {
			chkEncryption.setSelected(true);
		} else {
			chkEncryption.setSelected(false);
		}
		if (Einstellungen.getStrDBServerCert().equals("true")) {
			chkServerCert.setSelected(true);
		} else {
			chkServerCert.setSelected(false);
		}

		//###################################################################################################################################################
		// ActionListener
		//###################################################################################################################################################

		cmbDBtyp.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent actionEvent) {
	        	int idx = cmbDBtyp.getSelectedIndex();
	        	if (idx > 0) {
	        		chkEncryption.setVisible(false);
	        		chkServerCert.setVisible(false);
	        	} else {
	        		chkEncryption.setVisible(true);
	        		chkServerCert.setVisible(true);
	        	}
	        }
		});
	        
		chkEncryption.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Einstellungen.setStrDBencrypted("true");
				} else {
					Einstellungen.setStrDBencrypted("false");
				}
			}
		});

		chkServerCert.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Einstellungen.setStrDBServerCert("true");
				} else {
					Einstellungen.setStrDBServerCert("false");
				}
			}
		});
		
		btnDBEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbDBtyp.setEnabled(true);
				textDBcomputer.setEnabled(true);
				textDBport.setEnabled(true);
				textDBnameSource.setEnabled(true);
				textDBnameDest.setEnabled(true);
				textDBuser.setEnabled(true);
				textDBpass.setEnabled(true);
				chkEncryption.setEnabled(true);
				chkServerCert.setEnabled(true);
				btnDBEdit.setEnabled(false);
				btnDBOK.setEnabled(true);
			}
		});

		btnDBOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(cmbDBtyp.getSelectedIndex()) {
					case 0 -> Einstellungen.setStrDBtype("mssql");
					case 1 -> Einstellungen.setStrDBtype("postgre");
				}
				
				Einstellungen.setStrDBComputer(textDBcomputer.getText());
				Einstellungen.setStrDBPort(textDBport.getText());
				Einstellungen.setStrDBNameSource(textDBnameSource.getText());
				Einstellungen.setStrDBNameDest(textDBnameDest.getText());
				Einstellungen.setStrDBuser(textDBuser.getText());
				Einstellungen.setStrDBpass(textDBpass.getText());
				
				Einstellungen.setPrpDBSettings("dbtype", Einstellungen.getStrDBtype());
				Einstellungen.setPrpDBSettings("computer", Einstellungen.getStrDBComputer());
				Einstellungen.setPrpDBSettings("port", Einstellungen.getStrDBPort());
				Einstellungen.setPrpDBSettings("names", Einstellungen.getStrDBNameSource());
				Einstellungen.setPrpDBSettings("named", Einstellungen.getStrDBNameDest());
				Einstellungen.setPrpDBSettings("user", Einstellungen.getStrDBuser());
				Einstellungen.setPrpDBSettings("pass", Einstellungen.getStrDBpass());
				Einstellungen.setPrpDBSettings("encrypt", Einstellungen.getStrDBencrypted());
				Einstellungen.setPrpDBSettings("cert", Einstellungen.getStrDBServerCert());

				try {
					saveSettingsDB(Einstellungen.getPrpDBSettings());
				} catch (IOException e1) {
					logger.error("JFsettings() - " + e1);
				}

				Einstellungen.LoadProgSettings();

				cmbDBtyp.setEnabled(false);
				textDBcomputer.setEnabled(false);
				textDBport.setEnabled(false);
				textDBnameSource.setEnabled(false);
				textDBnameDest.setEnabled(false);
				textDBuser.setEnabled(false);
				textDBpass.setEnabled(false);
				chkEncryption.setEnabled(false);
				chkServerCert.setEnabled(false);
				btnDBEdit.setEnabled(true);
				btnDBOK.setEnabled(false);
			}
		});
		
		setPreferredSize(new Dimension(550, 265));
	}
}
