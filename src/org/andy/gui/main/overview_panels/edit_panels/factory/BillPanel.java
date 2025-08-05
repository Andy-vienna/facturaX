package org.andy.gui.main.overview_panels.edit_panels.factory;

import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
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

import org.andy.code.entityProductive.Rechnung;
import org.andy.code.entityProductive.RechnungRepository;
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
 				updateTable();
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
    
    private void updateTable() {
    	
    	BigDecimal anzPos = BigDecimal.ZERO;
    	String[] sPosText = new String[13];
    	BigDecimal[] bdAnzahl = new BigDecimal[this.txtFieldsPos.length];
    	BigDecimal[] bdEinzel = new BigDecimal[this.txtFieldsPos.length];
    	
    	RechnungRepository rechnungRepository = new RechnungRepository();
        Rechnung rechnung = rechnungRepository.findById(id);
    	
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
				sPosText[i] = this.txtFieldsPos[i].getText();
				bdAnzahl[i] = new BigDecimal(this.txtFieldsAnz[i].getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[i] = new BigDecimal(this.txtFieldsEP[i].getText().replace(",", ".")).setScale(2, RoundingMode.HALF_UP);
				anzPos = anzPos.add(BigDecimal.ONE); // Anzahl der Positionen
    		}
		}
    	
    	rechnung.setDatum(datePicker[0].getDate());
    	rechnung.setRef(txtFieldsHead[0].getText()); rechnung.setlZeitr(txtFieldsHead[1].getText());
    	
    	rechnung.setAnzPos(anzPos);
    	rechnung.setArt01(sPosText[0]); rechnung.setMenge01(bdAnzahl[0]); rechnung.setePreis01(bdEinzel[0]);
    	rechnung.setArt02(sPosText[1]); rechnung.setMenge02(bdAnzahl[1]); rechnung.setePreis02(bdEinzel[1]);
    	rechnung.setArt03(sPosText[2]); rechnung.setMenge03(bdAnzahl[2]); rechnung.setePreis03(bdEinzel[2]);
    	rechnung.setArt04(sPosText[3]); rechnung.setMenge04(bdAnzahl[3]); rechnung.setePreis04(bdEinzel[3]);
    	rechnung.setArt05(sPosText[4]); rechnung.setMenge05(bdAnzahl[4]); rechnung.setePreis05(bdEinzel[4]);
    	rechnung.setArt06(sPosText[5]); rechnung.setMenge06(bdAnzahl[5]); rechnung.setePreis06(bdEinzel[5]);
    	rechnung.setArt07(sPosText[6]); rechnung.setMenge07(bdAnzahl[6]); rechnung.setePreis07(bdEinzel[6]);
    	rechnung.setArt08(sPosText[7]); rechnung.setMenge08(bdAnzahl[7]); rechnung.setePreis08(bdEinzel[7]);
    	rechnung.setArt09(sPosText[8]); rechnung.setMenge09(bdAnzahl[8]); rechnung.setePreis09(bdEinzel[8]);
    	rechnung.setArt10(sPosText[9]); rechnung.setMenge10(bdAnzahl[9]); rechnung.setePreis10(bdEinzel[9]);
    	rechnung.setArt11(sPosText[10]); rechnung.setMenge11(bdAnzahl[10]); rechnung.setePreis11(bdEinzel[10]);
    	rechnung.setArt12(sPosText[11]); rechnung.setMenge12(bdAnzahl[11]); rechnung.setePreis12(bdEinzel[11]);
    	
    	Number numberN = (Number) this.txtFieldsSum[0].getValue();
    	double netto = numberN.doubleValue();
    	rechnung.setNetto(BigDecimal.valueOf(netto));
    	
    	Number numberT = (Number) this.txtFieldsSum[1].getValue();
    	double tax = numberT.doubleValue();
    	rechnung.setUst(BigDecimal.valueOf(tax));
    	
    	Number numberB = (Number) this.txtFieldsSum[2].getValue();
    	double brutto = numberB.doubleValue();
    	rechnung.setBrutto(BigDecimal.valueOf(brutto));
    	
    	rechnungRepository.update(rechnung);
    	
    	JFoverview.actScreen();
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
    
    public void setTxtFields(String id, String TaxVal) {
    	
    	ArrayList<String> pos = new ArrayList<>();
    	ArrayList<BigDecimal> anz = new ArrayList<>();
    	ArrayList<BigDecimal> ep = new ArrayList<>();
    	    	
    	RechnungRepository rechnungRepository = new RechnungRepository();
        Rechnung rechnung = rechnungRepository.findById(id);
        
        if (id.isEmpty() || id == null) {
    		return;
    	}
        
        pos.add(rechnung.getArt01()); pos.add(rechnung.getArt02()); pos.add(rechnung.getArt03()); pos.add(rechnung.getArt04());
    	pos.add(rechnung.getArt05()); pos.add(rechnung.getArt06()); pos.add(rechnung.getArt07()); pos.add(rechnung.getArt08());
    	pos.add(rechnung.getArt09()); pos.add(rechnung.getArt10()); pos.add(rechnung.getArt11()); pos.add(rechnung.getArt12());
    	
    	anz.add(rechnung.getMenge01()); anz.add(rechnung.getMenge02()); anz.add(rechnung.getMenge03()); anz.add(rechnung.getMenge04());
    	anz.add(rechnung.getMenge05()); anz.add(rechnung.getMenge06()); anz.add(rechnung.getMenge07()); anz.add(rechnung.getMenge08());
    	anz.add(rechnung.getMenge09()); anz.add(rechnung.getMenge10()); anz.add(rechnung.getMenge11()); anz.add(rechnung.getMenge12());
    	
    	ep.add(rechnung.getePreis01()); ep.add(rechnung.getePreis02()); ep.add(rechnung.getePreis03()); ep.add(rechnung.getePreis04());
    	ep.add(rechnung.getePreis05()); ep.add(rechnung.getePreis06()); ep.add(rechnung.getePreis07()); ep.add(rechnung.getePreis08());
    	ep.add(rechnung.getePreis09()); ep.add(rechnung.getePreis10()); ep.add(rechnung.getePreis11()); ep.add(rechnung.getePreis12());
    	
    	
    	this.id = null; this.bdTaxRate = BigDecimal.ZERO;
    	bdNetto = BigDecimal.ZERO;
    	bdTaxRate = new BigDecimal(TaxVal.trim());

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
		
		this.datePicker[0].setDate(rechnung.getDatum());
		this.txtFieldsHead[0].setText(rechnung.getRef());
		this.txtFieldsHead[1].setText(rechnung.getlZeitr());
		for (int i = 0; i < rechnung.getAnzPos().intValue(); i++) {
    		BigDecimal bdAnz = anz.get(i);
    		BigDecimal bdEP = ep.get(i);
    		BigDecimal bdGP = bdAnz.multiply(bdEP).setScale(2, RoundingMode.HALF_UP);
    		bdNetto = bdNetto.add(bdGP).setScale(2, RoundingMode.HALF_UP);
			
			this.txtFieldsPos[i].setText(pos.get(i));
			this.txtFieldsAnz[i].setText(bdAnz.toString());
			this.txtFieldsEP[i].setText(bdEP.toString());
			this.txtFieldsGP[i].setText(bdGP.toString());
			
		}
		txtFieldsSum[0].setValue(Double.parseDouble(bdNetto.toString()));
    	bdTax = bdNetto.multiply(bdTaxRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[1].setValue(Double.parseDouble(bdTax.toString()));
    	bdBrutto = bdNetto.add(bdTax).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[2].setValue(Double.parseDouble(bdBrutto.toString()));
    	if (rechnung.getState() == 1) { txtFieldsFocusable(true); } // Bearbeitungsmöglichkeit setzen
    }
}
