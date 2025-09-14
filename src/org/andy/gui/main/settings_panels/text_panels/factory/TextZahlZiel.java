package org.andy.gui.main.settings_panels.text_panels.factory;

import static org.andy.code.misc.ArithmeticHelper.parseStringToIntSafe;
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
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.andy.code.dataStructure.entitiyMaster.Text;
import org.andy.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.gui.main.settings_panels.text_panels.TextPanel;
import org.andy.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextZahlZiel extends TextPanel  {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(TextZahlZiel.class);
		
	private final List<JLabel> labelList = new ArrayList<>();
	private final List<JTextPane> textAreas = new ArrayList<>();
	private final List<JButton> updateButtons = new ArrayList<>();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public TextZahlZiel() {
        super("Textbausteine für Zahlungsziel bearbeiten");
        if (!(getBorder() instanceof TitledBorder)) {
            logger.warn("Kein TitledBorder gesetzt.");
        }
        setLayout(null); // beibehalten, um dein Layout nicht aufzubrechen
        buildUI();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildUI() {
    	
    	setLayout(new GridBagLayout()); // Verwende GridBagLayout für flexible Anordnung
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Arrays für Labels und TextAreas
		String[] labels = {
				"Textbaustein Zahlungsziel A38/a", "Textbaustein Zahlungsziel A38/b", "Textbaustein Zahlungsziel A40",
				"Textbaustein Zahlungsziel A41", "Textbaustein Zahlungsziel A42", "Textbaustein Zahlungsziel ...",
				"Textbaustein Zahlungsziel ...", "Textbaustein Zahlungsziel ...", "Textbaustein Zahlungsziel ...",
				"Textbaustein Zahlungsziel ...", "Textbaustein Zahlungsziel ...", "Textbaustein Zahlungsziel ...",
				"Textbaustein Zahlungsziel ...", "Textbaustein Zahlungsziel ...", "Textbaustein Zahlungsziel ..."};

		for (int i = 0; i < labels.length; i++) {

			String label = labels[i];

			//------------------------------------------------------------------------------
			gbc.gridx = 0; // erste Spalte
			JLabel lbl = new JLabel(label);
			JLabel lblInf = new JLabel();
			lblInf.setVisible(false);
			labelList.add(lblInf); // Label zur Liste hinzufügen)
			gbc.weightx = 0.06;  // Label nimmt 6 % des Platzes
			gbc.weighty = 0;
			add(lbl, gbc);
			add(lblInf, gbc);
			
			//------------------------------------------------------------------------------
			gbc.gridx = 1; // Wechsel zur nächsten Spalte
			JTextPane txtPane = new JTextPane(); // Verwende JTextPane statt JTextArea
			txtPane.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtPane.setEditable(true);

			JScrollPane txtScroll = new JScrollPane(txtPane);
			txtScroll.setBorder(new RoundedBorder(10));
			textAreas.add(txtPane); // TextPane zur Liste hinzufügen
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.87; // Textfeld nimmt 89 % des Platzes
			gbc.weighty = 0.2;
			add(txtScroll, gbc);

			//------------------------------------------------------------------------------
			gbc.gridx = 2; // Wechsel zur nächsten Spalte
			JButton btnUpdateText = null;
			try {
				btnUpdateText = createButton("Ändern", "menu/edit.png", null);
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
			if (btnUpdateText != null) {
				JButton finalBtn = btnUpdateText; // Lokale Kopie des Buttons
				int idx = i;
				btnUpdateText.addActionListener(_ -> handleButtonClick(idx, txtPane));
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
		texte();
    	setPreferredSize(new Dimension(1750, 700));
    }
    
	//###################################################################################################################################################
    
    private void texte() {

    	List<Text> textListe = new TextRepository().findAll();
    	int n = Math.min(textAreas.size(), textListe.size());
    	for (int i = 0; i < n; i++) {
    	    Text tx = textListe.get(i);
    	    textAreas.get(i).setText(tx.getTextZahlZiel());
    	    labelList.get(i).setText(String.valueOf(tx.getId()));
    	}
		
		for (int i = 0; i < textAreas.size(); i++) {
			if (textAreas.get(i).getText().length() > 0) {
				if (textAreas.get(i).getText().contains("{")) {
					String text = textAreas.get(i).getText();
					textAreas.get(i).setText(""); // Zurücksetzen, um doppeltes Styling zu vermeiden
					try {
						applyHighlighting(textAreas.get(i), text);
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
    
    private void handleButtonClick(int index, JTextPane txtPane) {
        int dataId = parseStringToIntSafe(labelList.get(index).getText());
        TextRepository repo = new TextRepository();
        Text text = repo.findById(dataId);
        text.setTextZahlZiel(txtPane.getText());
        repo.update(text);
        texte();
    }

	@Override
	public void initContent() {
		// TODO Auto-generated method stub
	}
    
}
