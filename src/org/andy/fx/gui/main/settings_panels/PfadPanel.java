package org.andy.fx.gui.main.settings_panels;

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

import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.FileSelect;

public class PfadPanel extends JPanel {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	JPanel panel = new JPanel();
	
	// Titel definieren
	String titel = "Pfadverwaltung";

	// Schrift konfigurieren
	Font font = new Font("Tahoma", Font.BOLD, 11);
	Color titleColor = Color.BLUE; // oder z. B. new Color(30, 60, 150);
	
	private JTextField[] txtFields = new JTextField[13];
	
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
				"templateofferrev",
				"templatedescription",
				"templatedescriptionbase",
				"templatedescriptionstyle",
				"templateconfirmation",
				"templatebill",
				"templatereminder",
				"templatemahnung",
				"templatebestellung",
				"templatelieferschein",
				"templatep109a",
				"work",
				};
		
		String labels[] = {
				"Angebot Vorlage (Excel-Vorlage *.xlsx)",
				"Angebotsrevision Vorlage (Excel-Vorlage *.xlsx)",
				"Leistungsbeschreibung Vorlage (pdf-Vorlage *.pdf)",
				"Leistungsbeschreibung Basistext (html-Vorlage *.html)",
				"Leistungsbeschreibung StyleSheet (css-Struktur *.css)",
				"Auftragsbestätigung Vorlage (Excel-Vorlage *.xlsx)",
				"Rechnung Vorlage (Excel-Vorlage *.xlsx)",
				"Zahlungserinnerung Vorlage (Excel-Vorlage *.xlsx)",
				"Mahnung Vorlage (Excel-Vorlage *.xlsx",
				"Bestellung Vorlage (Excel-Vorlage *.xlsx)",
				"Lieferschein Vorlage (Excel-Vorlage *.xlsx)",
				"§109a (E/A-Rechnung) Vorlage (Excel-Vorlage *.xlsx",
				"Arbeitsverzeichnis",
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
	    	txtFields[r] = makeField(310, 20 + r * 25, 800, 25, false, null);
	    	txtFields[r].setText(getters[index].apply(null));
	        txtFields[r].addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	String chosenPath = null;
	                String currentPath = txtFields[index].getText();
	                String defaultPath = currentPath.isEmpty() ? "C:\\" : getters[index].apply(null);

	                if (index < txtFields.length -1) {
	                	chosenPath = FileSelect.chooseFile(defaultPath);
	                } else {
	                	chosenPath = FileSelect.choosePath(defaultPath);
	                }
	                if (chosenPath != null) {
	                    setters[index].accept(chosenPath);
	                    Einstellungen.setPrpAppSettings(propertyKeys[index], chosenPath);
	                    txtFields[index].setText(chosenPath);
	                }
	            }
	        });
	    	add(txtFields[r]);
	    }
		
	    setPreferredSize(new Dimension(1120, 20 + labels.length * 25 + 20));
		
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
        _ -> Einstellungen.getTplOffer(),
        _ -> Einstellungen.getTplOfferRev(),
        _ -> Einstellungen.getTplDescription(),
        _ -> Einstellungen.getTplDescriptionBase(),
        _ -> Einstellungen.getTplDescriptionStyle(),
        _ -> Einstellungen.getTplConfirmation(),
        _ -> Einstellungen.getTplBill(),
        _ -> Einstellungen.getTplReminder(),
        _ -> Einstellungen.getTplMahnung(),
        _ -> Einstellungen.getTplBestellung(),
        _ -> Einstellungen.getTplLieferschein(),
        _ -> Einstellungen.getTplP109a(),
        _ -> Einstellungen.getWorkPath()
    };

    // Set-Methoden (als Consumer<String>)
    @SuppressWarnings("unchecked")
    Consumer<String>[] setters = new Consumer[] {
        (Consumer<String>) val -> Einstellungen.setTplOffer(val),
        (Consumer<String>) val -> Einstellungen.setTplOfferRev(val),
        (Consumer<String>) val -> Einstellungen.setTplDescription(val),
        (Consumer<String>) val -> Einstellungen.setTplDescriptionBase(val),
        (Consumer<String>) val -> Einstellungen.setTplDescriptionStyle(val),
        (Consumer<String>) val -> Einstellungen.setTplConfirmation(val),
        (Consumer<String>) val -> Einstellungen.setTplBill(val),
        (Consumer<String>) val -> Einstellungen.setTplReminder(val),
        (Consumer<String>) val -> Einstellungen.setTplMahnung(val),
        (Consumer<String>) val -> Einstellungen.setTplBestellung(val),
        (Consumer<String>) val -> Einstellungen.setTplLieferschein(val),
        (Consumer<String>) val -> Einstellungen.setTplP109a(val),
        (Consumer<String>) val -> Einstellungen.setWorkPath(val)
    };

}
