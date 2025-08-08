package org.andy.gui.main.settings_panels;

import static org.andy.toolbox.misc.CreateObject.applyHighlighting;
import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import org.andy.code.dataStructure.entitiyMaster.Text;
import org.andy.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextPanel extends JPanel  {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(TextPanel.class);
		
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
			gbc.weightx = 0.06;  // Label nimmt 6 % des Platzes
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
			gbc.weightx = 0.89; // Textfeld nimmt 89 % des Platzes
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
    	TextRepository textRepository = new TextRepository();
    	List<Text> textListe = new ArrayList<>();
    	textListe.addAll(textRepository.findAll());
    	
    	for (int i = 0; i < 12; i++) {
    		textAreas.get(i).setText(textListe.get(i).getTextAngebot());
    		labelList.get(i).setText(String.valueOf(textListe.get(i).getId()));
    	}
    	
    	for (int i = 0; i < 5; i++) {
    		textAreas.get(i + 12).setText(textListe.get(i).getTextOrderConfirm());
    		labelList.get(i + 12).setText(String.valueOf(textListe.get(i).getId()));
    	}
		
    	for (int i = 0; i < 2; i++) {
    		textAreas.get(i + 17).setText(textListe.get(i).getTextUst());
    		labelList.get(i + 17).setText(String.valueOf(textListe.get(i).getId()));
    	}
		
    	for (int i = 0; i < 2; i++) {
    		textAreas.get(i + 19).setText(textListe.get(i).getTextZahlZiel());
    		labelList.get(i + 19).setText(String.valueOf(textListe.get(i).getId()));
    	}
		
    	for (int i = 0; i < 10; i++) {
    		textAreas.get(i + 21).setText(textListe.get(i).getTextZahlErin());
    		labelList.get(i + 21).setText(String.valueOf(textListe.get(i).getId()));
    	}
		
    	for (int i = 0; i < 14; i++) {
    		textAreas.get(i + 31).setText(textListe.get(i).getTextMahnung());
    		labelList.get(i + 31).setText(String.valueOf(textListe.get(i).getId()));
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
    	int dataId = Integer.valueOf(lblInf);
    	
    	TextRepository textRepository = new TextRepository();
    	Text text = textRepository.findById(dataId);

		if(label.contains("Angebot")) {
			text.setTextAngebot(txtPane.getText());
		}
		if(label.contains("Auftragsbestätigung")) {
			text.setTextOrderConfirm(txtPane.getText());
		}
		if(label.contains("Umsatzsteuerhinweis")) {
			text.setTextUst(txtPane.getText());
		}
		if(label.contains("Zahlungsziel")) {
			text.setTextZahlZiel(txtPane.getText());
		}
		if(label.contains("Zahlungserinnerung")) {
			text.setTextZahlErin(txtPane.getText());
		}
		if(label.contains("Mahnung")) {
			text.setTextMahnung(txtPane.getText());
		}

		textRepository.update(text); // Datensatz aktualisieren

		texte(); // texte laden und anzeigen

	}
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

}
