package org.andy.gui.main.overview_panels.edit_panels.factory;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.choosePath;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.andy.code.dataStructure.entitiyProductive.Einkauf;
import org.andy.code.dataStructure.repositoryProductive.EinkaufRepository;
import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.gui.file.JFfileView;
import org.andy.gui.main.JFoverview;
import org.andy.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

public class PurchasePanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(PurchasePanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFieldsCol1 = new JTextField[8];
	private JTextField[] txtFieldsCol2a = new JTextField[5];
	private JTextField[] txtFieldsCol2b = new JTextField[2];
	private JLabel lblFileTyp = new JLabel();
	private JButton[] btnFields = new JButton[2];
	
	private String[] sDatum = new String[2];
	private String id = null;
	private boolean file = false;
	private boolean neuBeleg = false;
	
	private EinkaufRepository einkaufRepository = new EinkaufRepository();
	private Einkauf einkauf = new Einkauf();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public PurchasePanel() {
        super("Einkauf");
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
	    String[] labelsCol1 = {
	        "Rechnungsdatum:",
	        "Rechnungsnummer:",
	        "Kreditor Name:",
	        "Kreditor Straße:",
	        "Kreditor PLZ:",
	        "Kreditor Ort:",
	        "Kreditor Land:",
	        "Kreditor UID:",
	        "Währung:"};
	    String[] labelsCol2 = {
	        "USt.-Satz:",
	        "Netto:",
	        "USt.:",
	        "Brutto:",
	        "", // Platzhalter für "Anzahlung:"
	        "Zahlungsziel:",
	        "Zahlungshinweis:",
	        "Datei:"};
		
	    // Label Arrays
	    JLabel[] lblFieldsCol1 = new JLabel[labelsCol1.length];
	    JLabel[] lblFieldsCol2 = new JLabel[labelsCol2.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labelsCol1.length; r++) {
	    	lblFieldsCol1[r] = new JLabel(labelsCol1[r]);
	    	lblFieldsCol1[r].setBounds(10, 20 + r * 25, 200, 25);
	    	add(lblFieldsCol1[r]);
	    }
	    for (int r = 0; r < labelsCol2.length; r++) {
	    	lblFieldsCol2[r] = new JLabel(labelsCol2[r]);
	    	lblFieldsCol2[r].setBounds(630, 20 + r * 25, 200, 25);
	    	add(lblFieldsCol2[r]);
	    }
		
	    // Datepicker für Belegdatum
	    for (int i = 0; i < panelDate.length; i++) {
	    	final int ii = i; // final für Lambda-Ausdruck
	    	panelDate[ii] = new DemoPanel();
		    dateSettings[ii] = new DatePickerSettings();
		    datePicker[ii] = new DatePicker(new DatePickerSettings());
			panelDate[ii].scrollPaneForButtons.setEnabled(false);
			dateSettings[ii].setWeekNumbersDisplayed(true, true);
			dateSettings[ii].setFormatForDatesCommonEra("dd.MM.yyyy");
			datePicker[ii] = new DatePicker(dateSettings[i]);
			datePicker[ii].getComponentDateTextField().setBorder(new RoundedBorder(10));
			datePicker[ii].addDateChangeListener(new DateChangeListener() {
				@Override
				public void dateChanged(DateChangeEvent arg0) {
					LocalDate selectedDate = datePicker[ii].getDate();
					if (selectedDate != null) {
						sDatum[ii] = selectedDate.format(StartUp.getDfdate());
					} else {
						sDatum[ii] = null;
					}
				}
			});
			add(datePicker[ii]);
	    }
		datePicker[0].setBounds(212, 20, 180, 25);
		
		// Textfelder
	    for (int r = 0; r < txtFieldsCol1.length; r++) {
	    	txtFieldsCol1[r] = makeField(210, 45 + r * 25, 400, 25, false, null);
	    	add(txtFieldsCol1[r]);
	    }
	    for (int r = 0; r < txtFieldsCol2a.length; r++) {
	    	txtFieldsCol2a[r] = makeField(830, 20 + r * 25, 400, 25, false, null);
	    	add(txtFieldsCol2a[r]);
	    }
	    txtFieldsCol2a[4].setVisible(false); // Anzahlung wird aktuell nicht benötigt
	    datePicker[1].setBounds(832, 145, 180, 25);
	    for (int r = 0; r < txtFieldsCol2b.length; r++) {
	    	txtFieldsCol2b[r] = makeField(830, 170 + r * 25, 400, 25, false, null);
	    	add(txtFieldsCol2b[r]);
	    }
	    txtFieldsCol2b[1].setFocusable(false);
	    
	    // Anzeige Filetyp
	    lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(1280, 45, 50, 40);
		add(lblFileTyp);
	    
	    btnFields[0] = new JButton();
	    btnFields[0].setToolTipText("");
	    btnFields[0].setBounds(765, 195, 65, 25);
	    add(btnFields[0]);

		try {
			btnFields[1] = createButton("", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnFields[1].setEnabled(true);
		btnFields[1].setBounds(1280, 170, JFoverview.getButtonx(), JFoverview.getButtony());
		add(btnFields[1]);
		
		setPreferredSize(new Dimension(1000, 20 + labelsCol1.length * 25 + 20));
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					String outputPath;
					outputPath = choosePath(LoadData.getWorkPath());
					Path path = Paths.get(outputPath);
					if (outputPath.equals(getNotSelected())) {
						return; // nichts ausgewählt
					}
					try {
						einkaufRepository.exportFileById(einkauf.getId(), path);
					} catch (Exception e1) {
						logger.error("Fehler beim speichern der Datei " + outputPath + ": " + e1.getMessage());
					}
				}
			}
		});
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				String FileNamePath = chooseFile(LoadData.getWorkPath());
 				File fn = new File(FileNamePath);
 				String FileName = fn.getName();
 				txtFieldsCol2b[1].setText(FileName);
 				einkauf.setDateiname(FileName);
 				Path path = Paths.get(FileNamePath);
 				try {
					einkauf.setDatei(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
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
 					String value = null;
 	 				einkauf.setJahr(Integer.parseInt(LoadData.getStrAktGJ()));
 	 				
 	 				boolean bResult = checkInput();
 	 				if (!bResult) {
 	 					JOptionPane.showMessageDialog(null, "Eingaben unvollständig, Beleg kann nicht gespeichert werden", "Belegeingabe", JOptionPane.INFORMATION_MESSAGE);
 	 					return;
 	 				}
 	 				
 	 				einkauf.setReDatum(datePicker[0].getDate());
 	 				einkauf.setId(txtFieldsCol1[0].getText().trim());
 	 				einkauf.setKredName(txtFieldsCol1[1].getText().trim());
 	 				value = (txtFieldsCol1[2].getText() != null) ? txtFieldsCol1[2].getText().trim() : "";
 	 				einkauf.setKredStrasse(value);
 	 				value = (txtFieldsCol1[3].getText() != null) ? txtFieldsCol1[3].getText().trim() : "";
 	 				einkauf.setKredPlz(value);
 	 				value = (txtFieldsCol1[4].getText() != null) ? txtFieldsCol1[4].getText().trim() : "";
 	 				einkauf.setKredOrt(value);
 	 				value = (txtFieldsCol1[5].getText() != null) ? txtFieldsCol1[5].getText().trim() : "";
 	 				einkauf.setKredLand(value);
 	 				value = (txtFieldsCol1[6].getText() != null) ? txtFieldsCol1[6].getText().trim() : "";
 	 				einkauf.setKredUid(value);
 	 				einkauf.setWaehrung(txtFieldsCol1[7].getText().trim());
 	 				einkauf.setSteuersatz(txtFieldsCol2a[0].getText().trim());
 	 				einkauf.setNetto(new BigDecimal(txtFieldsCol2a[1].getText().trim()));
 	 				einkauf.setUst(new BigDecimal(txtFieldsCol2a[2].getText().trim()));
 	 				einkauf.setBrutto(new BigDecimal(txtFieldsCol2a[3].getText().trim()));
 	 				einkauf.setZahlungsziel(datePicker[1].getDate());
 	 				einkauf.setHinweis(txtFieldsCol2b[0].getText().trim());
 	 				einkauf.setAnzahlung(BigDecimal.ZERO); // Feld Anzahlung aktuell nicht verwendet
 	 				
 	 				einkaufRepository.save(einkauf);
 				} else {
 					einkauf = einkaufRepository.findById(id);
 					einkauf.setStatus(1);
 					einkaufRepository.update(einkauf);
 				}
 				neuBeleg = false;
 				JFoverview.actScreen();
 			}
 		});
	}
	
	//###################################################################################################################################################
	
	// Hilfsfunktion für Textfelder
    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(true);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private void txtFieldsFocusable(boolean b) {
    	this.datePicker[0].setEnabled(b);
    	this.datePicker[1].setEnabled(b);
    	for (int i = 0; i < this.txtFieldsCol1.length; i++) {
			this.txtFieldsCol1[i].setFocusable(b);
		}
		for (int i = 0; i < this.txtFieldsCol2a.length; i++) {
			this.txtFieldsCol2a[i].setFocusable(b);
		}
		this.txtFieldsCol2b[0].setFocusable(b);
		this.txtFieldsCol2b[1].setFocusable(false);
    }
    
    private boolean checkInput() {
    	if (datePicker[0].getDate() == null) return false;
    	if (datePicker[1].getDate() == null) return false;
    	System.out.println(txtFieldsCol1[0].getText() + txtFieldsCol1[1].getText() + txtFieldsCol1[7].getText());
    	if (txtFieldsCol1[0].getText() == null || txtFieldsCol1[0].getText().equals("")) return false;
    	if (txtFieldsCol1[1].getText() == null || txtFieldsCol1[1].getText().equals("")) return false;
    	if (txtFieldsCol1[7].getText() == null || txtFieldsCol1[7].getText().equals("")) return false;
    	System.out.println(txtFieldsCol1[0].getText() + txtFieldsCol1[1].getText() + txtFieldsCol1[7].getText());
    	for (int i = 0; i < txtFieldsCol2a.length - 1; i++) {
    		if (txtFieldsCol2a[i].getText() == null || txtFieldsCol2a[i].getText().equals("")) return false;
    	}
    	if (txtFieldsCol2b[0].getText() == null || txtFieldsCol2b[0].getText().equals("")) return false;
    	if (txtFieldsCol2b[1].getText() == null || txtFieldsCol2b[1].getText().equals("")) return false;
    	if (file == false) return false;
    	return true;
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
    
    public void setTxtFields(String id) {
    	this.id = null;
    	if (id != null && !id.isEmpty()) {
			this.id = id;
		} else {
			this.datePicker[0].setDate(null);
    		for (int i = 0; i < this.txtFieldsCol1.length; i++) {
				this.txtFieldsCol1[i].setText("");
			}
    		for (int i = 0; i < this.txtFieldsCol2a.length; i++) {
				this.txtFieldsCol2a[i].setText("");
			}
    		this.datePicker[1].setDate(null);
    		for (int i = 0; i < this.txtFieldsCol2b.length; i++) {
				this.txtFieldsCol2b[i].setText("");
			}
    		txtFieldsFocusable(true); // Bearbeitung freigeben
    		for (int i = 0; i < this.btnFields.length; i++) {
				this.btnFields[i].setEnabled(true);
			}
    		einkauf = new Einkauf();
    		neuBeleg = true;
			return;
		}

    	einkauf = einkaufRepository.findById(id);
    	
		this.datePicker[0].setDate(einkauf.getReDatum());
		this.datePicker[0].setEnabled(false);
		
		this.datePicker[1].setDate(einkauf.getZahlungsziel());
		this.datePicker[1].setEnabled(false);
		
		this.txtFieldsCol1[0].setText(einkauf.getId());
		this.txtFieldsCol1[1].setText(einkauf.getKredName());
		this.txtFieldsCol1[2].setText(einkauf.getKredStrasse());
		this.txtFieldsCol1[3].setText(einkauf.getKredPlz());
		this.txtFieldsCol1[4].setText(einkauf.getKredOrt());
		this.txtFieldsCol1[5].setText(einkauf.getKredLand());
		this.txtFieldsCol1[6].setText(einkauf.getKredUid());
		this.txtFieldsCol1[7].setText(einkauf.getWaehrung());
		
		this.txtFieldsCol2a[0].setText(einkauf.getSteuersatz());
		this.txtFieldsCol2a[1].setText(einkauf.getNetto().toString());
		this.txtFieldsCol2a[2].setText(einkauf.getUst().toString());
		this.txtFieldsCol2a[3].setText(einkauf.getBrutto().toString());
		
		this.txtFieldsCol2b[0].setText(einkauf.getHinweis());
		this.txtFieldsCol2b[1].setText(einkauf.getDateiname());
		
		txtFieldsFocusable(false);
		btnFields[0].setEnabled(false);
		if (einkauf.getStatus() == 0) {
			btnFields[1].setEnabled(true);
		} else {
			btnFields[1].setEnabled(false);
		}
    }

	public void setBtnText(int col, String value) {
		this.btnFields[col].setText(value);
	}
	
	public void setIcon() {
		try {
			JFfileView.setFileIcon(lblFileTyp, txtFieldsCol2b[1].getText());
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
