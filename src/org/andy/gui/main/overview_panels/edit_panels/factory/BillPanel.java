package org.andy.gui.main.overview_panels.edit_panels.factory;

import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

import org.andy.code.entity.SQLproductiveData;
import org.andy.code.main.StartUp;
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

public class BillPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(BillPanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFieldsHead = new JTextField[2];
	private JTextField[] txtFieldsPos = new JTextField[12];
	private JTextField[] txtFieldsAnz = new JTextField[12];
	private JTextField[] txtFieldsEP = new JTextField[12];
	private JTextField[] txtFieldsGP = new JTextField[12];
	private JFormattedTextField[] txtFieldsSum = new JFormattedTextField[3];

	private JButton[] btnFields = new JButton[3];
	
	private String[] sDatum = new String[2];
	BigDecimal bdNetto = BigDecimal.ZERO, bdTax = BigDecimal.ZERO, bdBrutto = BigDecimal.ZERO;
	private String id = null;
	private BigDecimal bdTaxRate = BigDecimal.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public BillPanel() {
        super("Rechnungspositionen");
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
	    String[] labelsTop = {"Datum:", "Leistungszeitraum", "Referenz:"};
	    String[] labelsCol = {"Nr:", "Position:", "Anzahl:", "Einzel:", "Summe"};
	    String[] labelsRow = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
	    String[] labelsBtm = {"Netto:", "USt.:", "Brutto:"};
		
	    // Label Arrays
	    JLabel[] lblFieldsTop = new JLabel[labelsTop.length];
	    JLabel[] lblFieldsCol = new JLabel[labelsCol.length];
	    JLabel[] lblFieldsRow = new JLabel[labelsRow.length];
	    JLabel[] lblFieldsBtm = new JLabel[labelsBtm.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labelsTop.length; r++) {
	    	lblFieldsTop[r] = new JLabel(labelsTop[r]);
	    	add(lblFieldsTop[r]);
	    }
	    lblFieldsTop[0].setBounds(10, 20, 50, 25);
    	lblFieldsTop[1].setBounds(230, 20, 120, 25);
    	lblFieldsTop[2].setBounds(570, 20, 70, 25);
    	
	    for (int r = 0; r < labelsCol.length; r++) {
	    	lblFieldsCol[r] = new JLabel(labelsCol[r]);
	    	lblFieldsCol[r].setHorizontalAlignment(SwingConstants.CENTER);
	    	add(lblFieldsCol[r]);
	    }
	    lblFieldsCol[0].setBounds(10, 45, 40, 25);
    	lblFieldsCol[1].setBounds(50, 45, 800, 25);
    	lblFieldsCol[2].setBounds(850, 45, 75, 25);
    	lblFieldsCol[3].setBounds(925, 45, 200, 25);
    	lblFieldsCol[4].setBounds(1125, 45, 200, 25);
    	
    	for (int r = 0; r < labelsRow.length; r++) {
	    	lblFieldsRow[r] = new JLabel(labelsRow[r]);
	    	lblFieldsRow[r].setBounds(10, 70 + r * 25, 40, 25);
	    	lblFieldsRow[r].setHorizontalAlignment(SwingConstants.CENTER);
	    	add(lblFieldsRow[r]);
	    }
    	
    	for (int r = 0; r < labelsBtm.length; r++) {
	    	lblFieldsBtm[r] = new JLabel(labelsBtm[r]);
	    	lblFieldsBtm[r].setBounds(1345, 295 + r * 25, 100, 25);
	    	lblFieldsBtm[r].setFont(new Font("Tahoma", Font.BOLD, 11));
	    	add(lblFieldsBtm[r]);
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
			datePicker[ii].setEnabled(false);
			add(datePicker[ii]);
	    }
		datePicker[0].setBounds(60, 20, 150, 25);
		
		// Textfelder
		txtFieldsHead[0] = makeField(350, 20, 200, 25, true, null);
		txtFieldsHead[1] = makeField(640, 20, 1000, 25, true, null);
	    for (int r = 0; r < txtFieldsHead.length; r++) {
	    	txtFieldsHead[r].setHorizontalAlignment(SwingConstants.LEFT);
	    	txtFieldsHead[r].setFocusable(false);
	    	add(txtFieldsHead[r]);
	    }
	    for (int r = 0; r < txtFieldsPos.length; r++) {
	    	txtFieldsPos[r] = makeField(50, 70 + r * 25, 800, 25, false, null);
	    	txtFieldsAnz[r] = makeField(850, 70 + r * 25, 75, 25, false, null);
	    	txtFieldsEP[r] = makeField(925, 70 + r * 25, 200, 25, false, null);
	    	txtFieldsGP[r] = makeField(1125, 70 + r * 25, 200, 25, false, null);
	    	txtFieldsPos[r].setHorizontalAlignment(SwingConstants.LEFT);
	    	txtFieldsPos[r].setFocusable(false);
	    	txtFieldsAnz[r].setFocusable(false);
	    	txtFieldsEP[r].setFocusable(false);
	    	txtFieldsGP[r].setFocusable(false);
	    	add(txtFieldsPos[r]);
	    	add(txtFieldsAnz[r]);
	    	add(txtFieldsEP[r]);
	    	add(txtFieldsGP[r]);
	    }
	    for (int r = 0; r < txtFieldsSum.length; r++) {
	    	txtFieldsSum[r] = makeFormatField(1445, 295 + r * 25, 150, 25, true, null);
	    	txtFieldsSum[r].setFocusable(false);
	    	add(txtFieldsSum[r]);
	    }
	    
	    // Buttons
		try {
			btnFields[0] = createButton("<html>neu<br>berechnen</html>", "calc.png");
			btnFields[1] = createButton("<html>update</html>", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnFields[0].setEnabled(false);
		btnFields[1].setEnabled(false);
		btnFields[0].setBounds(1625, 260, JFoverview.getButtonx(), JFoverview.getButtony());
		btnFields[1].setBounds(1625, 320, JFoverview.getButtonx(), JFoverview.getButtony());
		add(btnFields[0]);
		add(btnFields[1]);
		
		setPreferredSize(new Dimension(1000, 70 + txtFieldsPos.length * 25 + 20));
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				try {
					calcValue();
					btnFields[1].setEnabled(true);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				updateOffer();
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
    
    private JFormattedTextField makeFormatField(int x, int y, int w, int h, boolean bold, Color bg) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getCurrencyInstance());
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        JFormattedTextField t = new JFormattedTextField(formatter);
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(false);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private void txtFieldsFocusable(boolean b) {
    	this.datePicker[0].setEnabled(b);
    	for (int i = 0; i < this.txtFieldsHead.length; i++) {
			this.txtFieldsHead[i].setFocusable(b);
		}
		for (int i = 0; i < this.txtFieldsPos.length; i++) {
			this.txtFieldsPos[i].setFocusable(b);
			this.txtFieldsAnz[i].setFocusable(b);
			this.txtFieldsEP[i].setFocusable(b);
			this.txtFieldsGP[i].setFocusable(false);
		}
		for (int i = 0; i < this.txtFieldsSum.length; i++) {
			this.txtFieldsSum[i].setFocusable(false);
		}
		btnFields[0].setEnabled(b);
		btnFields[1].setEnabled(false);
    }
    
	//###################################################################################################################################################
    
    private void calcValue() throws ParseException {
    	BigDecimal bdEP = BigDecimal.ZERO; BigDecimal bdAnz = BigDecimal.ZERO; BigDecimal bdGP = BigDecimal.ZERO;
    	bdNetto = BigDecimal.ZERO;
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
			this.txtFieldsGP[i].setText("");
		}
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
	    		if (this.txtFieldsAnz[i].getText().isEmpty() || this.txtFieldsEP[i].getText().isEmpty()) {
	    			JOptionPane.showMessageDialog(null, "Dateneingabe überprüfen ...", "Fehler", JOptionPane.INFORMATION_MESSAGE);
	    			return;
				}
	    		String anz = this.txtFieldsAnz[i].getText();
	    		String ep = this.txtFieldsEP[i].getText();
	    		
	    		bdAnz = new BigDecimal(anz.replace(",", ".").trim()).setScale(2, RoundingMode.HALF_UP);
	    	    bdEP = new BigDecimal(ep.replace(",", ".").trim()).setScale(2, RoundingMode.HALF_UP);
	        	bdGP = bdAnz.multiply(bdEP).setScale(2, RoundingMode.HALF_UP);
	
	        	bdNetto = bdNetto.add(bdGP).setScale(2, RoundingMode.HALF_UP);
    			this.txtFieldsGP[i].setText(bdGP.toString());
    		}
		}
    	txtFieldsSum[0].setValue(Double.parseDouble(bdNetto.toString()));
    	bdTax = bdNetto.multiply(bdTaxRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[1].setValue(Double.parseDouble(bdTax.toString()));
    	bdBrutto = bdNetto.add(bdTax).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[2].setValue(Double.parseDouble(bdBrutto.toString()));	
    }
    
    private void updateOffer() {
    	int iAnzPos = 0;
    	String[] sPosText = new String[13];
    	BigDecimal[] bdAnzahl = new BigDecimal[this.txtFieldsPos.length];
    	BigDecimal[] bdEinzel = new BigDecimal[this.txtFieldsPos.length];
    	
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
				sPosText[i] = this.txtFieldsPos[i].getText();
				bdAnzahl[i] = new BigDecimal(this.txtFieldsAnz[i].getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[i] = new BigDecimal(this.txtFieldsEP[i].getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP);
				iAnzPos = i + 1; // Anzahl der Positionen
    		}
		}
    	Number numberA = (Number) this.txtFieldsSum[0].getValue();
    	double doubleWertA = numberA.doubleValue();
    	String sNetto = String.valueOf(doubleWertA);
    	Number numberB = (Number) this.txtFieldsSum[1].getValue();
    	double doubleWertB = numberB.doubleValue();
    	String sUSt = String.valueOf(doubleWertB);
    	Number numberC = (Number) this.txtFieldsSum[2].getValue();
    	double doubleWertC = numberC.doubleValue();
    	String sBrutto = String.valueOf(doubleWertC);
    	
    	try {
			SQLproductiveData.updateReToDB(id, sDatum[0], txtFieldsHead[0].getText(), txtFieldsHead[1].getText(), iAnzPos, sPosText,
					bdAnzahl, bdEinzel, sNetto, sUSt, sBrutto);
		} catch (ClassNotFoundException | SQLException e1) {
			logger.error("error updating bill to database - " + e1);
		}
    	JFoverview.actScreen();
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
    
    public void setTxtFields(String[] value, String id, BigDecimal bdTaxRate) {
    	this.id = null; this.bdTaxRate = BigDecimal.ZERO;
    	bdNetto = BigDecimal.ZERO;
    	if (bdTaxRate != null) {
    		this.bdTaxRate = bdTaxRate;
    	}
    	if (id != null && !id.isEmpty()) {
			this.id = id;
		}
		this.datePicker[0].setDate(null);
		for (int i = 0; i < this.txtFieldsHead.length; i++) {
			this.txtFieldsHead[i].setText("");
		}
		for (int i = 0; i < this.txtFieldsPos.length; i++) {
			this.txtFieldsPos[i].setText("");
			this.txtFieldsAnz[i].setText("");
			this.txtFieldsEP[i].setText("");
			this.txtFieldsGP[i].setText("");
		}
		for (int i = 0; i < this.txtFieldsSum.length; i++) {
			this.txtFieldsSum[i].setValue(null);
		}
		btnFields[0].setEnabled(false);
		txtFieldsFocusable(false);
		if (value[0] == null || value[0].isEmpty()) {
			return; // Abbruch, wenn kein Wert übergeben wurde
		}
    	DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    	LocalDate datum = LocalDate.parse(value[5], dfDate);
    	this.datePicker[0].setDate(datum);
    	for (int i = 0; i < this.txtFieldsHead.length; i++) {
    		this.txtFieldsHead[i].setText(value[i + 6]);
    	}
    	for (int i = 0; i < Integer.parseInt(value[14]); i++) {
    		BigDecimal bdAnz = new BigDecimal(value[16 + (i * 3)].trim().replace(",", "."));
    		BigDecimal bdEP = new BigDecimal(value[17 + (i * 3)].trim().replace(",", "."));
    		BigDecimal bdGP = bdAnz.multiply(bdEP).setScale(2, RoundingMode.HALF_UP);
    		bdNetto = bdNetto.add(bdGP).setScale(2, RoundingMode.HALF_UP);
			
			this.txtFieldsPos[i].setText(value[15 + (i * 3)]);
			this.txtFieldsAnz[i].setText(value[16 + (i * 3)].replace(".", ","));
			this.txtFieldsEP[i].setText(bdEP.toString());
			this.txtFieldsGP[i].setText(bdGP.toString());
			
		}
    	txtFieldsSum[0].setValue(Double.parseDouble(bdNetto.toString()));
    	bdTax = bdNetto.multiply(bdTaxRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[1].setValue(Double.parseDouble(bdTax.toString()));
    	bdBrutto = bdNetto.add(bdTax).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[2].setValue(Double.parseDouble(bdBrutto.toString()));
    	if (value[1].equals("1") && value[2].equals("0")) {
    		txtFieldsFocusable(true);
		}
    }
}
