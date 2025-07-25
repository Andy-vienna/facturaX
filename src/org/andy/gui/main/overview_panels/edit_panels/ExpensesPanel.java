package org.andy.gui.main.overview_panels.edit_panels;

import static org.andy.toolbox.misc.CreateObject.changeKomma;
import static org.andy.toolbox.misc.CreateObject.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import org.andy.code.main.StartUp;
import org.andy.code.main.overview.LoadExpenses;
import org.andy.code.main.overview.WriteExpenses;
import org.andy.gui.main.JFoverview;
import org.andy.gui.main.overview_panels.EditPanel;
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
	private JButton[] btnFields = new JButton[3];
	
	private String sDatum;
	
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
	        "Betrag netto (EUR):",
	        "Steuersatz (%):",
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
		btnFields[1].setBounds(650, 145, JFoverview.getButtonx(), JFoverview.getButtony());
		add(btnFields[1]);
		
		setPreferredSize(new Dimension(1000, 20 + 6 * 25 + 50));
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				String fName = WriteExpenses.selectFile();
 				txtFields[5].setText(fName);
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				
 				String[] arrTmp = new String[8];
 				Arrays.fill(arrTmp, null);
 				
 				arrTmp[0] = sDatum; //txt01.getText();
 				arrTmp[1] = txtFields[0].getText(); // Buchungstext
 				arrTmp[2] = txtFields[1].getText().replace(",", "."); // Betrag netto
 				arrTmp[3] = txtFields[2].getText().replace(",", "."); // Steuersatz
 				arrTmp[4] = txtFields[3].getText().replace(",", "."); // Steuer
 				arrTmp[5] = txtFields[4].getText().replace(",", "."); // Betrag brutto
 				arrTmp[6] = txtFields[5].getText(); // Dateiname
 				arrTmp[7] = WriteExpenses.getFilePath(); // Dateipfad
 				
 				String sResult = WriteExpenses.writeExpense(arrTmp);
 				if(sResult.equals(OK)) {
 					JFoverview.actionAct();
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
    
    public void setTxtFields(int col, String value) {
    	if (value == null || value.isEmpty()) {
    		this.datePicker.setDate(null);
    		for (int i = 0; i < this.txtFields.length; i++) {
				this.txtFields[i].setText("");
			}
			return;
		}
    	if (col == 0) {
    		DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    		LocalDate datum = LocalDate.parse(value, dfDate);
    		this.datePicker.setDate(datum);
		} else {
			this.txtFields[col - 1].setText(value.replace(".", ","));
		}
    }

	public void setBtnText(int col, String value) {
		this.btnFields[col].setText(value);
	}

}
