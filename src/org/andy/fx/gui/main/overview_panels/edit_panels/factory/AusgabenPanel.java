package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToIntSafe;
import static org.andy.fx.code.misc.FileSelect.chooseFile;
import static org.andy.fx.code.misc.FileSelect.choosePath;
import static org.andy.fx.code.misc.FileSelect.getNotSelected;
import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CodeListen;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.dialogs.DateianzeigeDialog;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

public class AusgabenPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(AusgabenPanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private CodeListen cl = new CodeListen();
	private JComboBox<String> cmbLand = new JComboBox<>();
	private JComboBox<String> cmbCurr = new JComboBox<>();
	private String iso2code; private String currency3code;
	
	private TitledBorder border;
	private DatePicker datePicker = new DatePicker();
	private JTextField[] txtFields = new JTextField[8];
	private JLabel lblFileTyp = new JLabel();
	private JButton[] btnFields = new JButton[2];
	
	private boolean file = false;
	private boolean neuBeleg = false;
	
	private AusgabenRepository ausgabenRepository = new AusgabenRepository();
	private Ausgaben a = new Ausgaben();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public AusgabenPanel() {
        super("Betriebsausgaben");
        initContent();
    }

	@Override
	public void initContent() {
		b = getBorder();
	    if (b instanceof TitledBorder) {
	        this.border = (TitledBorder) b;
	    } else {
	        logger.warn("Kein TitledBorder vorhanden – setsTitel() wird nicht funktionieren.");
	    }
	    
		buildPanel();
	}

	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void buildPanel() {
		
		// Überschriften und Feldbeschriftungen
	    String[] labels = {
	        "Belegdatum:",
	        "Buchungstext des Beleges:",
	        "Ursprungsland (2-Zeichen):",
	        "Währung:",
	        "USt.-Satz:",
	        "Netto:",
	        "USt.:",
	        "Brutto:",
	        "Dateianhang:"};
		
	    // Label Arrays
	    JLabel[] lblFields = new JLabel[labels.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labels.length; r++) {
	    	lblFields[r] = new JLabel(labels[r]);
	    	lblFields[r].setBounds(10, 20 + r * 25, 200, 25);
	    	add(lblFields[r]);
	    }
		
	    // Datepicker für Belegdatum
	    DemoPanel panelDate = new DemoPanel();
		panelDate.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettings = new DatePickerSettings();
		dateSettings.setWeekNumbersDisplayed(true, true);
		dateSettings.setFormatForDatesCommonEra("dd.MM.yyyy");
		datePicker = new DatePicker(dateSettings);
		datePicker.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePicker.setBounds(212, 20, 180, 25);
		add(datePicker);
		
		cmbLand = new JComboBox<>(cl.getCountries().toArray(new String[0]));
		cmbLand.setBounds(210, 70, 200, 25);
		cmbLand.addActionListener(_ -> doCountry());
		cmbLand.setEnabled(false);
		add(cmbLand);
		
		cmbCurr = new JComboBox<>(cl.getCurrencies().toArray(new String[0]));
		cmbCurr.setBounds(210, 95, 200, 25);
		cmbCurr.addActionListener(_ -> doCurrency());
		cmbCurr.setEnabled(false);
		add(cmbCurr);
		
		// Textfelder
	    for (int r = 0; r < txtFields.length; r++) {
	    	if (r == 1 || r == 2) {
	    		txtFields[r] = makeField(410, 45 + r * 25, 200, 25, false, null);
	    	}else {
	    		txtFields[r] = makeField(210, 45 + r * 25, 400, 25, false, null);
	    	}
	    	txtFields[r].setFocusable(false);
	    	add(txtFields[r]);
	    }
	    attachCommaToDot(txtFields[4]);
	    txtFields[4].getDocument().addDocumentListener(new DocumentListener() {
	    	@Override
	        public void insertUpdate(DocumentEvent e) { calcFields(); }
	        @Override
	        public void removeUpdate(DocumentEvent e) { calcFields(); }
	        @Override
	        public void changedUpdate(DocumentEvent e) { calcFields(); }
	    });
        attachCommaToDot(txtFields[5]);
        attachCommaToDot(txtFields[6]);
	    
	    // Anzeige Filetyp
	    lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(660, 45, 50, 40);
		add(lblFileTyp);
	    
	    btnFields[0] = new JButton();
	    btnFields[0].setToolTipText("");
	    btnFields[0].setBounds(145, 220, 65, 25);
	    add(btnFields[0]);

		btnFields[1] = createButton("", ButtonIcon.SAVE.icon(), null);
		btnFields[1].setBounds(660, 195, HauptFenster.getButtonx(), HauptFenster.getButtony());
		add(btnFields[1]);
		
		txtFieldsFocusable(false);
		btnFields[0].setEnabled(false);
		setPreferredSize(new Dimension(1000, 20 + 8 * 25 + 50));
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					String outputPath;
					outputPath = choosePath(Einstellungen.getWorkPath());
					Path path = Paths.get(outputPath);
					if (outputPath.equals(getNotSelected())) {
						return; // nichts ausgewählt
					}
					try {
						ausgabenRepository.exportFileById(a.getId(), path);
					} catch (Exception e1) {
						logger.error("Fehler beim speichern der Datei " + outputPath + ": " + e1.getMessage());
					}
				}
			}
		});
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				String FileNamePath = chooseFile(Einstellungen.getWorkPath());
 				File fn = new File(FileNamePath);
 				String FileName = fn.getName();
 				txtFields[7].setText(FileName);
 				a.setDateiname(FileName);
 				Path path = Paths.get(FileNamePath);
 				try {
					a.setDatei(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
				} catch (IOException e1) {
					logger.error("Fehler laden der Datei " + FileName + ": " + e1.getMessage());
				}
 				file = true;
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				if (neuBeleg) {
 					
 					a.setJahr(parseStringToIntSafe(Einstellungen.getStrAktGJ()));
 	 				
 	 				boolean bResult = checkInput();
 	 				if (!bResult) {
 	 					JOptionPane.showMessageDialog(null, "Eingaben unvollständig, Beleg kann nicht gespeichert werden", "Belegeingabe", JOptionPane.INFORMATION_MESSAGE);
 	 					return;
 	 				}
 					
 					a.setDatum(datePicker.getDate());
 					a.setArt(txtFields[0].getText());
 					a.setLand(iso2code);
 					a.setWaehrung(currency3code);
 					a.setSteuersatz(txtFields[3].getText());
 					a.setNetto(parseStringToBigDecimalSafe(txtFields[4].getText(), LocaleFormat.AUTO));
 					a.setSteuer(parseStringToBigDecimalSafe(txtFields[5].getText(), LocaleFormat.AUTO));
 					a.setBrutto(parseStringToBigDecimalSafe(txtFields[6].getText(), LocaleFormat.AUTO));
 					
 					ausgabenRepository.save(a);
 					
 					neuBeleg = false;
 	 				HauptFenster.actScreen();
 				}
 			}
 		});
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
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
    
    private void txtFieldsFocusable(boolean b) {
    	this.datePicker.setEnabled(b);
    	this.cmbLand.setEnabled(b); this.cmbCurr.setEnabled(b);
    	this.cmbLand.setSelectedIndex(0); this.cmbCurr.setSelectedIndex(0);
    	for (int i = 0; i < this.txtFields.length; i++) {
			this.txtFields[i].setFocusable(b);
		}
    	this.txtFields[1].setFocusable(false);
    	this.txtFields[2].setFocusable(false);
    	this.txtFields[7].setFocusable(false);
    }
    
    private boolean checkInput() {
    	if (datePicker.getDate() == null) return false;
    	for (int i = 0; i < txtFields.length - 1; i++) {
    		if (txtFields[i].getText() == null || txtFields[i].getText().equals("")) return false;
    	}
    	if (file == false) return false;
    	return true;
    }
    
    private void calcFields() {
    	BigDecimal tax = BD.ZERO; BigDecimal netto = BD.ZERO; BigDecimal ust = BD.ZERO;
    	if (!txtFields[3].isFocusable()) return; // keine Prüfung, wenn Feld nicht fokussierbar
    	try {
    	    int value = Integer.parseInt(txtFields[3].getText().trim()); // Steuersatz als Ganzzahl eingegeben
    	    if (value >= 0 && value <= 99) {
    	        tax = new BigDecimal(value).divide(BD.HUNDRED); // Steuersatz
    	        netto = new BigDecimal(txtFields[4].getText().trim()).setScale(2, RoundingMode.HALF_UP);
    	        ust = netto.multiply(tax).setScale(2, RoundingMode.HALF_UP); txtFields[5].setText(ust.toString());
    	        txtFields[6].setText(netto.add(ust).setScale(2, RoundingMode.HALF_UP).toString());
    	    } else {
    	    	JOptionPane.showMessageDialog(null, "Steuersatz ungültig", "Belegeingabe", JOptionPane.INFORMATION_MESSAGE);
    	    }
    	} catch (NumberFormatException e) {
    	    // keine Zahl eingegeben
    	}
    }
    
    private void doCountry() {
    	if (cmbLand.getSelectedIndex() == 0) return;
    	iso2code = cmbLand.getSelectedItem().toString().substring(0,2);
    	String ctry = cl.getCountryFromCode(iso2code);
    	
    	this.txtFields[1].setText(ctry);
    }
    
    private void doCurrency() {
    	if (cmbCurr.getSelectedIndex() == 0) return;
    	currency3code = cmbCurr.getSelectedItem().toString().substring(0,3);
    	
    	this.txtFields[2].setText(currency3code);
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
    
    public void setsTitel(String sTitel) {
    	if (b instanceof TitledBorder) {
	        this.border = (TitledBorder) b;
	        this.border.setTitle(sTitel);
	    	this.border.setTitleColor(Color.BLUE);
	    	this.border.setTitleFont(new Font("Tahoma", Font.BOLD, 12));
	    	this.repaint();  // wichtig, damit es sichtbar wird
	    }
	}
    
    public void setTxtFields(int id) {
    	if (id <= 0) {
			this.datePicker.setDate(null);
    		for (int i = 0; i < this.txtFields.length; i++) {
				this.txtFields[i].setText("");
			}
    		txtFieldsFocusable(true); // Bearbeitung freigeben
    		for (int i = 0; i < this.btnFields.length; i++) {
				this.btnFields[i].setEnabled(true);
			}
    		a = new Ausgaben();
    		neuBeleg = true;
			return;
		}
    	
    	a = ausgabenRepository.findById(id);
    	
    	this.datePicker.setDate(a.getDatum());
    	
    	this.txtFields[0].setText(a.getArt());
    	this.txtFields[1].setText(cl.getCountryFromCode(a.getLand()));
    	this.txtFields[2].setText(a.getWaehrung());
    	this.txtFields[3].setText(a.getSteuersatz());
    	this.txtFields[4].setText(a.getNetto().toString());
    	this.txtFields[5].setText(a.getSteuer().toString());
    	this.txtFields[6].setText(a.getBrutto().toString());
    	this.txtFields[7].setText(a.getDateiname());
    	
    	txtFieldsFocusable(false);
    	neuBeleg = false;
    	for (int i = 0; i < this.btnFields.length; i++) {
			this.btnFields[i].setEnabled(false);
		}
    }

	public void setBtnText(int col, String value) {
		this.btnFields[col].setText(value);
	}
	
	public void setIcon() {
		try {
			DateianzeigeDialog.setFileIcon(lblFileTyp, txtFields[6].getText());
			lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}

	public void setFile(boolean file) {
		this.file = false;
		this.file = file;
	}

}
