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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.andy.code.dataStructure.entitiyProductive.Ausgaben;
import org.andy.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.code.main.overview.edit.Expenses;
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

public class ExpensesPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(ExpensesPanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private static final String OK = "OK";
	private TitledBorder border;
	private DatePicker datePicker = new DatePicker();
	private JTextField[] txtFields = new JTextField[6];
	private JLabel lblFileTyp = new JLabel();
	private JButton[] btnFields = new JButton[2];
	
	private String sDatum;
	private int id = 0;
	private boolean file = false;
	private boolean neuBeleg = false;
	
	private AusgabenRepository ausgabenRepository = new AusgabenRepository();
	private Ausgaben ausgaben = new Ausgaben();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public ExpensesPanel() {
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
	        "Steuersatz (%):",
	        "Betrag netto (EUR):",
	        "Betrag Steuer (EUR):",
	        "Betrag brutto (EUR):",
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
		datePicker.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = datePicker.getDate();
				if (selectedDate != null) {
					sDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sDatum = null;
				}
			}
		});
		datePicker.setBounds(212, 20, 180, 25);
		add(datePicker);
		
		// Textfelder
	    for (int r = 0; r < txtFields.length; r++) {
	    	txtFields[r] = makeField(210, 45 + r * 25, 400, 25, false, null);
	    	add(txtFields[r]);
	    }
	    
	    // Anzeige Filetyp
	    lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(660, 45, 50, 40);
		add(lblFileTyp);
	    
	    btnFields[0] = new JButton();
	    btnFields[0].setToolTipText("");
	    btnFields[0].setBounds(145, 170, 65, 25);
	    add(btnFields[0]);

		try {
			btnFields[1] = createButton("", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnFields[1].setEnabled(true);
		btnFields[1].setBounds(660, 145, JFoverview.getButtonx(), JFoverview.getButtony());
		add(btnFields[1]);
		
		setPreferredSize(new Dimension(1000, 20 + 6 * 25 + 50));
	    
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
						ausgabenRepository.exportFileById(ausgaben.getId(), path);
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
 				txtFields[5].setText(FileName);
 				ausgaben.setDateiname(FileName);
 				Path path = Paths.get(FileNamePath);
 				try {
					ausgaben.setDatei(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
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
 					
 					
 					
 					
 					
 					
 					
 					
 					
 					
 					
 					
 					
 				}
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
    	this.datePicker.setEnabled(b);
    	for (int i = 0; i < this.txtFields.length; i++) {
			this.txtFields[i].setFocusable(b);
		}
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
    	this.id = 0;
    	if (id > 0) {
			this.id = id;
		} else {
			this.datePicker.setDate(null);
    		for (int i = 0; i < this.txtFields.length; i++) {
				this.txtFields[i].setText("");
			}
    		txtFieldsFocusable(true); // Bearbeitung freigeben
    		for (int i = 0; i < this.btnFields.length; i++) {
				this.btnFields[i].setEnabled(true);
			}
    		ausgaben = new Ausgaben();
    		neuBeleg = true;
			return;
		}
    	
    	ausgaben = ausgabenRepository.findById(id);
    	
    	this.datePicker.setDate(ausgaben.getDatum());
    	
    	txtFields[0].setText(ausgaben.getArt());
    	txtFields[1].setText(ausgaben.getSteuersatz());
    	txtFields[2].setText(ausgaben.getNetto().toString());
    	txtFields[3].setText(ausgaben.getSteuer().toString());
    	txtFields[4].setText(ausgaben.getBrutto().toString());
    	txtFields[5].setText(ausgaben.getDateiname());
    	
    	txtFieldsFocusable(false);
		btnFields[0].setEnabled(false);
		
    }

	public void setBtnText(int col, String value) {
		this.btnFields[col].setText(value);
	}
	
	public void setIcon() {
		try {
			JFfileView.setFileIcon(lblFileTyp, txtFields[5].getText());
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
