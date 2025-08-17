package org.andy.gui.main.settings_panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.andy.code.main.LadeEinstellungen;
import org.andy.toolbox.misc.SelectFile;

public class PfadPanel extends JPanel {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	JPanel panel = new JPanel();
	
	// Titel definieren
	String titel = "Pfadverwaltung";

	// Schrift konfigurieren
	Font font = new Font("Tahoma", Font.BOLD, 11);
	Color titleColor = Color.BLUE; // oder z. B. new Color(30, 60, 150);
	
	private JTextField[] txtFields = new JTextField[8];
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public PfadPanel() {
		
        setLayout(null);
        
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), titel);
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT); // optional: Ausrichtung links
        border.setTitlePosition(TitledBorder.TOP);       // optional: Position oben

        setBorder(border);
        
        buildPanel();
    }

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void buildPanel() {
		
		String[] propertyKeys = {
				"templateoffer",
				"templateconfirmation",
				"templatebill",
				"templatereminder",
				"templatemahnung",
				"templatep109a",
				"work",
				"backup"
				};
		
		String labels[] = {
				"Angebot Vorlage (Excel-Vorlage *.xlsx)",
				"Angebotsbestätigung Vorlage (Excel-Vorlage *.xlsx)",
				"Rechnung Vorlage (Excel-Vorlage *.xlsx)",
				"Zahlungserinnerung Vorlage (Excel-Vorlage *.xlsx)",
				"Mahnung Vorlage (Excel-Vorlage *.xlsx",
				"§109a (E/A-Rechnung) Vorlage (Excel-Vorlage *.xlsx",
				"Arbeitsverzeichnis",
				"Sicherungsverzeichnis"
				};
		
		// Label Arrays
	    JLabel[] lblFields = new JLabel[labels.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labels.length; r++) {
	    	lblFields[r] = new JLabel(labels[r]);
	    	lblFields[r].setBounds(10, 20 + r * 25, 300, 25);
	    	add(lblFields[r]);
	    }
		
	    // Textfelder
	    for (int r = 0; r < txtFields.length; r++) {
	    	final int index = r;
	    	txtFields[r] = makeField(310, 20 + r * 25, 585, 25, false, null);
	    	txtFields[r].setText(getters[index].apply(null));
	        txtFields[r].addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	String chosenPath = null;
	                String currentPath = txtFields[index].getText();
	                String defaultPath = currentPath.isEmpty() ? "C:\\" : getters[index].apply(null);

	                if (index < txtFields.length -2) {
	                	chosenPath = SelectFile.chooseFile(defaultPath);
	                } else {
	                	chosenPath = SelectFile.choosePath(defaultPath);
	                }
	                if (chosenPath != null) {
	                    setters[index].accept(chosenPath);
	                    LadeEinstellungen.setPrpAppSettings(propertyKeys[index], chosenPath);
	                    txtFields[index].setText(chosenPath);
	                }
	            }
	        });
	    	add(txtFields[r]);
	    }
		
	    setPreferredSize(new Dimension(900, 20 + labels.length * 25 + 20));
		
	}
	
	//###################################################################################################################################################
	
	// Hilfsfunktion für Textfelder
    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.LEFT);
        t.setFocusable(true);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
    
    // Get-Methoden (als Function<Void, String>)
    @SuppressWarnings("unchecked")
    Function<Void, String>[] getters = new Function[] {
        _ -> LadeEinstellungen.getTplOffer(),
        _ -> LadeEinstellungen.getTplConfirmation(),
        _ -> LadeEinstellungen.getTplBill(),
        _ -> LadeEinstellungen.getTplReminder(),
        _ -> LadeEinstellungen.getTplMahnung(),
        _ -> LadeEinstellungen.getTplP109a(),
        _ -> LadeEinstellungen.getWorkPath(),
        _ -> LadeEinstellungen.getBackupPath()
    };

    // Set-Methoden (als Consumer<String>)
    @SuppressWarnings("unchecked")
    Consumer<String>[] setters = new Consumer[] {
        (Consumer<String>) val -> LadeEinstellungen.setTplOffer(val),
        (Consumer<String>) val -> LadeEinstellungen.setTplConfirmation(val),
        (Consumer<String>) val -> LadeEinstellungen.setTplBill(val),
        (Consumer<String>) val -> LadeEinstellungen.setTplReminder(val),
        (Consumer<String>) val -> LadeEinstellungen.setTplMahnung(val),
        (Consumer<String>) val -> LadeEinstellungen.setTplP109a(val),
        (Consumer<String>) val -> LadeEinstellungen.setWorkPath(val),
        (Consumer<String>) val -> LadeEinstellungen.setBackupPath(val)
    };

}
