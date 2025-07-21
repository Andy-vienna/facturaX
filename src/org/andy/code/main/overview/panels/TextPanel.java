package org.andy.code.main.overview.panels;

import static org.andy.toolbox.misc.CreateObject.applyHighlighting;
import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.andy.code.sql.SQLmasterData;
import org.andy.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextPanel extends JPanel  {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(TextPanel.class);
	
	private static String sConnMaster;
	
	private static List<JLabel> labelList = new ArrayList<>();
	private static List<JTextPane> textAreas = new ArrayList<>();
	private static List<JButton> updateButtons = new ArrayList<>();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public TextPanel() {
        setLayout(null);
        buildTextPanel();
    }
    
    public static void loadTexte() {
    	TextPanel.texte();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private void buildTextPanel() {
    	
    	setLayout(new GridBagLayout()); // Verwende GridBagLayout für flexible Anordnung
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;


		// Arrays für Labels und TextAreas
		String[] labels = {
				"Textbaustein Angebot A33", "Textbaustein Angebot A36", "Textbaustein Angebot A37",
				"Textbaustein Angebot A39", "Textbaustein Angebot A40", "Textbaustein Angebot A44",
				"Textbaustein Angebot A47", "Textbaustein Angebot QR-Code", "Textbaustein Angebot A12/a",
				"Textbaustein Angebot A12/b", "Textbaustein Angebot A13", "Textbaustein Angebot A14",
				"Textbaustein Auftragsbestätigung A35", "Textbaustein Auftragsbestätigung A38",
				"Textbaustein Auftragsbestätigung A44", "Textbaustein Auftragsbestätigung A47",
				"Textbaustein Auftragsbestätigung QR-Code",
				"Textbaustein Umsatzsteuerhinweis A36/a", "Textbaustein Umsatzsteuerhinweis A36/b",
				"Textbaustein Zahlungsziel A38/a", "Textbaustein Zahlungsziel A38/b",
				"Textbaustein Zahlungserinnerung B16", "Textbaustein Zahlungserinnerung B18/a",
				"Textbaustein Zahlungserinnerung B18/b", "Textbaustein Zahlungserinnerung B18/c",
				"Textbaustein Zahlungserinnerung B20", "Textbaustein Zahlungserinnerung B21",
				"Textbaustein Zahlungserinnerung B22", "Textbaustein Zahlungserinnerung B23",
				"Textbaustein Zahlungserinnerung B26", "Textbaustein Zahlungserinnerung B27",
				"Textbaustein Mahnung B16", "Textbaustein Mahnung B18/a",
				"Textbaustein Mahnung B18/b", "Textbaustein Mahnung B18/c",
				"Textbaustein Mahnung B20/a", "Textbaustein Mahnung B20/b",
				"Textbaustein Mahnung B21/a", "Textbaustein Mahnung B21/b",
				"Textbaustein Mahnung B22/a", "Textbaustein Mahnung B22/b",
				"Textbaustein Mahnung B23/a", "Textbaustein Mahnung B23/b",
				"Textbaustein Mahnung B26", "Textbaustein Mahnung B27"};

		for (int i = 0; i < labels.length; i++) {

			String label = labels[i];

			//------------------------------------------------------------------------------
			gbc.gridx = 0; // erste Spalte
			JLabel lbl = new JLabel(label);
			JLabel lblInf = new JLabel();
			lblInf.setVisible(false);
			labelList.add(lblInf); // Label zur Liste hinzufügen)
			gbc.weightx = 0.08;  // Label nimmt 8 % des Platzes
			add(lbl, gbc);
			add(lblInf, gbc);

			//------------------------------------------------------------------------------
			gbc.gridx = 1; // Wechsel zur nächsten Spalte
			JTextPane txtPane = new JTextPane(); // Verwende JTextPane statt JTextArea
			txtPane.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtPane.setEditable(true);

			JScrollPane txtScroll = new JScrollPane(txtPane);
			txtScroll.setPreferredSize(new Dimension(0, 30));
			txtScroll.setBorder(new RoundedBorder(10));
			textAreas.add(txtPane); // TextPane zur Liste hinzufügen
			gbc.weightx = 0.85; // Textfeld nimmt 92 % des Platzes
			add(txtScroll, gbc);

			//------------------------------------------------------------------------------
			gbc.gridx = 2; // Wechsel zur nächsten Spalte
			JButton btnUpdateText = null;
			try {
				btnUpdateText = createButton("Ändern", "menu/edit.png");
				btnUpdateText.setPreferredSize(new Dimension(0, 30));
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
			if (btnUpdateText != null) {
				JButton finalBtn = btnUpdateText; // Lokale Kopie des Buttons
				int index = i; // Lokale Variable für Lambda (verhindert Probleme mit final/effektiv final)
				btnUpdateText.addActionListener(_ -> handleButtonClick(index, lblInf.getText(), label, txtPane));
				txtPane.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(DocumentEvent e) { finalBtn.setEnabled(true); }
					@Override
					public void removeUpdate(DocumentEvent e) { finalBtn.setEnabled(true); }
					@Override
					public void changedUpdate(DocumentEvent e) { finalBtn.setEnabled(true); }
				});
			}
			updateButtons.add(btnUpdateText); // Button zur Liste hinzufügen
			gbc.weightx = 0.07; // Button nimmt 7 % des Platzes
			add(btnUpdateText, gbc);

			//------------------------------------------------------------------------------
			gbc.gridy++;   // Nächste Zeile
		}
    	
    }
    
	//###################################################################################################################################################
    
    private static void texte() {

		if(SQLmasterData.getArrListText().size()>0) {
			textAreas.get(0).setText(SQLmasterData.getsArrText()[1][4]);
			textAreas.get(1).setText(SQLmasterData.getsArrText()[2][4]);
			textAreas.get(2).setText(SQLmasterData.getsArrText()[3][4]);
			textAreas.get(3).setText(SQLmasterData.getsArrText()[4][4]);
			textAreas.get(4).setText(SQLmasterData.getsArrText()[5][4]);
			textAreas.get(5).setText(SQLmasterData.getsArrText()[6][4]);
			textAreas.get(6).setText(SQLmasterData.getsArrText()[7][4]);
			textAreas.get(7).setText(SQLmasterData.getsArrText()[8][4]);
			textAreas.get(8).setText(SQLmasterData.getsArrText()[9][4]);
			textAreas.get(9).setText(SQLmasterData.getsArrText()[10][4]);
			textAreas.get(10).setText(SQLmasterData.getsArrText()[11][4]);
			textAreas.get(11).setText(SQLmasterData.getsArrText()[12][4]);
			textAreas.get(12).setText(SQLmasterData.getsArrText()[1][6]);
			textAreas.get(13).setText(SQLmasterData.getsArrText()[2][6]);
			textAreas.get(14).setText(SQLmasterData.getsArrText()[3][6]);
			textAreas.get(15).setText(SQLmasterData.getsArrText()[4][6]);
			textAreas.get(16).setText(SQLmasterData.getsArrText()[5][6]);
			textAreas.get(17).setText(SQLmasterData.getsArrText()[1][2]);
			textAreas.get(18).setText(SQLmasterData.getsArrText()[2][2]);
			textAreas.get(19).setText(SQLmasterData.getsArrText()[1][3]);
			textAreas.get(20).setText(SQLmasterData.getsArrText()[2][3]);
			textAreas.get(21).setText(SQLmasterData.getsArrText()[1][5]);
			textAreas.get(22).setText(SQLmasterData.getsArrText()[2][5]);
			textAreas.get(23).setText(SQLmasterData.getsArrText()[3][5]);
			textAreas.get(24).setText(SQLmasterData.getsArrText()[4][5]);
			textAreas.get(25).setText(SQLmasterData.getsArrText()[5][5]);
			textAreas.get(26).setText(SQLmasterData.getsArrText()[6][5]);
			textAreas.get(27).setText(SQLmasterData.getsArrText()[7][5]);
			textAreas.get(28).setText(SQLmasterData.getsArrText()[8][5]);
			textAreas.get(29).setText(SQLmasterData.getsArrText()[9][5]);
			textAreas.get(30).setText(SQLmasterData.getsArrText()[10][5]);
			textAreas.get(31).setText(SQLmasterData.getsArrText()[1][7]);
			textAreas.get(32).setText(SQLmasterData.getsArrText()[2][7]);
			textAreas.get(33).setText(SQLmasterData.getsArrText()[3][7]);
			textAreas.get(34).setText(SQLmasterData.getsArrText()[4][7]);
			textAreas.get(35).setText(SQLmasterData.getsArrText()[5][7]);
			textAreas.get(36).setText(SQLmasterData.getsArrText()[6][7]);
			textAreas.get(37).setText(SQLmasterData.getsArrText()[7][7]);
			textAreas.get(38).setText(SQLmasterData.getsArrText()[8][7]);
			textAreas.get(39).setText(SQLmasterData.getsArrText()[9][7]);
			textAreas.get(40).setText(SQLmasterData.getsArrText()[10][7]);
			textAreas.get(41).setText(SQLmasterData.getsArrText()[11][7]);
			textAreas.get(42).setText(SQLmasterData.getsArrText()[12][7]);
			textAreas.get(43).setText(SQLmasterData.getsArrText()[13][7]);
			textAreas.get(44).setText(SQLmasterData.getsArrText()[14][7]);

			labelList.get(0).setText(SQLmasterData.getsArrText()[1][1]);
			labelList.get(1).setText(SQLmasterData.getsArrText()[2][1]);
			labelList.get(2).setText(SQLmasterData.getsArrText()[3][1]);
			labelList.get(3).setText(SQLmasterData.getsArrText()[4][1]);
			labelList.get(4).setText(SQLmasterData.getsArrText()[5][1]);
			labelList.get(5).setText(SQLmasterData.getsArrText()[6][1]);
			labelList.get(6).setText(SQLmasterData.getsArrText()[7][1]);
			labelList.get(7).setText(SQLmasterData.getsArrText()[8][1]);
			labelList.get(8).setText(SQLmasterData.getsArrText()[9][1]);
			labelList.get(9).setText(SQLmasterData.getsArrText()[10][1]);
			labelList.get(10).setText(SQLmasterData.getsArrText()[11][1]);
			labelList.get(11).setText(SQLmasterData.getsArrText()[12][1]);
			labelList.get(12).setText(SQLmasterData.getsArrText()[1][1]);
			labelList.get(13).setText(SQLmasterData.getsArrText()[2][1]);
			labelList.get(14).setText(SQLmasterData.getsArrText()[3][1]);
			labelList.get(15).setText(SQLmasterData.getsArrText()[4][1]);
			labelList.get(16).setText(SQLmasterData.getsArrText()[5][1]);
			labelList.get(17).setText(SQLmasterData.getsArrText()[1][1]);
			labelList.get(18).setText(SQLmasterData.getsArrText()[2][1]);
			labelList.get(19).setText(SQLmasterData.getsArrText()[1][1]);
			labelList.get(20).setText(SQLmasterData.getsArrText()[2][1]);
			labelList.get(21).setText(SQLmasterData.getsArrText()[1][1]);
			labelList.get(22).setText(SQLmasterData.getsArrText()[2][1]);
			labelList.get(23).setText(SQLmasterData.getsArrText()[3][1]);
			labelList.get(24).setText(SQLmasterData.getsArrText()[4][1]);
			labelList.get(25).setText(SQLmasterData.getsArrText()[5][1]);
			labelList.get(26).setText(SQLmasterData.getsArrText()[6][1]);
			labelList.get(27).setText(SQLmasterData.getsArrText()[7][1]);
			labelList.get(28).setText(SQLmasterData.getsArrText()[8][1]);
			labelList.get(29).setText(SQLmasterData.getsArrText()[9][1]);
			labelList.get(30).setText(SQLmasterData.getsArrText()[10][1]);
			labelList.get(31).setText(SQLmasterData.getsArrText()[1][1]);
			labelList.get(32).setText(SQLmasterData.getsArrText()[2][1]);
			labelList.get(33).setText(SQLmasterData.getsArrText()[3][1]);
			labelList.get(34).setText(SQLmasterData.getsArrText()[4][1]);
			labelList.get(35).setText(SQLmasterData.getsArrText()[5][1]);
			labelList.get(36).setText(SQLmasterData.getsArrText()[6][1]);
			labelList.get(37).setText(SQLmasterData.getsArrText()[7][1]);
			labelList.get(38).setText(SQLmasterData.getsArrText()[8][1]);
			labelList.get(39).setText(SQLmasterData.getsArrText()[9][1]);
			labelList.get(40).setText(SQLmasterData.getsArrText()[10][1]);
			labelList.get(41).setText(SQLmasterData.getsArrText()[11][1]);
			labelList.get(42).setText(SQLmasterData.getsArrText()[12][1]);
			labelList.get(43).setText(SQLmasterData.getsArrText()[13][1]);
			labelList.get(44).setText(SQLmasterData.getsArrText()[14][1]);
		}

		for (int n = 0; n < textAreas.size(); n++) {
			if (textAreas.get(n).getText().length() > 0) {
				if (textAreas.get(n).getText().contains("{")) {
					String text = textAreas.get(n).getText();
					textAreas.get(n).setText(""); // Zurücksetzen, um doppeltes Styling zu vermeiden
					try {
						applyHighlighting(textAreas.get(n), text);
					} catch (BadLocationException e) {
						logger.error("error applying text highlighting - " + e);
					}
				}
			}
		}

		for (int m = 0; m < updateButtons.size(); m++) {
			updateButtons.get(m).setEnabled(false);
		}

	}
    
	//###################################################################################################################################################
    
    private void handleButtonClick(int index, String lblInf, String label, JTextPane txtPane) {

		String sPreSql = "UPDATE tblText SET [{Column}] = '" + txtPane.getText() + "' WHERE [Id] = '{Id}'";

		if(label.contains("Angebot")) {
			sPreSql = sPreSql.replace("{Column}", "TextAngebot");
		}
		if(label.contains("Auftragsbestätigung")) {
			sPreSql = sPreSql.replace("{Column}", "TextOrderConfirm");
		}
		if(label.contains("Umsatzsteuerhinweis")) {
			sPreSql = sPreSql.replace("{Column}", "TextUSt");
		}
		if(label.contains("Zahlungsziel")) {
			sPreSql = sPreSql.replace("{Column}", "TextZahlZiel");
		}
		if(label.contains("Zahlungserinnerung")) {
			sPreSql = sPreSql.replace("{Column}", "TextZahlErin");
		}
		if(label.contains("Mahnung")) {
			sPreSql = sPreSql.replace("{Column}", "TextMahnung");
		}

		String sSQLStatement = sPreSql.replace("{Id}", lblInf);

		try {
			sqlUpdate(sConnMaster, sSQLStatement);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("error updating texts into database - " + e);
		}

		try {
			SQLmasterData.loadBaseData();
		} catch (ClassNotFoundException | SQLException | ParseException e) {
			logger.error("error loading base data from database - " + e);
		}
		texte(); // texte laden und anzeigen

	}
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
    
    public static void setsConnMaster(String sConnMaster) {
		TextPanel.sConnMaster = sConnMaster;
	}

}
