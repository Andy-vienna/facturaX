package org.andy.gui.main.overview_panels.edit_panels.factory;

import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.andy.code.main.StartUp;
import org.andy.code.main.overview.edit.Purchase;
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
	
	private static final String OK = "OK";
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFieldsCol1 = new JTextField[8];
	private JTextField[] txtFieldsCol2a = new JTextField[5];
	private JTextField[] txtFieldsCol2b = new JTextField[2];
	private JLabel lblFileTyp = new JLabel();
	private JButton[] btnFields = new JButton[3];
	
	private String[] sDatum = new String[2];
	private String id = null;
	private boolean file = false;
	
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
					Purchase.actionMouseClick(e, String.valueOf(id));
				}
			}
		});
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				String fName = Purchase.selectFile();
 				txtFieldsCol2b[1].setText(fName);
 				file = true;
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				
 				String[] arrTmp = new String[19];
 				Arrays.fill(arrTmp, null);
 				
 				arrTmp[0] = sDatum[0];
 				for (int i = 0; i < txtFieldsCol1.length; i++) {
 					arrTmp[i + 1] = txtFieldsCol1[i].getText();
 				}
 				for (int i = 0; i < txtFieldsCol2a.length; i++) {
 					if (i ==0) {
 						arrTmp[i + 9] = txtFieldsCol2a[i].getText();
 					}
 					if (i > 0 && i < 5) {
 						arrTmp[i + 9] = txtFieldsCol2a[i].getText().replace(",", ".");
 					}
 				}
 				arrTmp[14] = sDatum[1];
 				arrTmp[15] = txtFieldsCol2b[0].getText();
 				arrTmp[16] = txtFieldsCol2b[1].getText();
 				arrTmp[17] = Purchase.getFilePath();
 				arrTmp[18] = "0";
 				
 				String sResult = Purchase.writeData(arrTmp, id, file);
 				if(sResult.equals(OK)) {
 					JFoverview.actScreen();
 					setIcon();
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
    
    public void setTxtFields(String[] value, String id) {
    	this.id = null;
    	if (id != null && !id.isEmpty()) {
			this.id = id;
		} 
    	if (value[0] == null || value[0].isEmpty()) {
    		this.datePicker[0].setDate(null);
    		this.datePicker[0].setEnabled(true);
    		for (int i = 0; i < this.txtFieldsCol1.length; i++) {
				this.txtFieldsCol1[i].setText("");
				this.txtFieldsCol1[i].setFocusable(true);
			}
    		for (int i = 0; i < this.txtFieldsCol2a.length; i++) {
				this.txtFieldsCol2a[i].setText("");
				this.txtFieldsCol2a[i].setFocusable(true);
			}
    		this.datePicker[1].setDate(null);
    		this.datePicker[1].setEnabled(true);
    		for (int i = 0; i < this.txtFieldsCol2b.length; i++) {
				this.txtFieldsCol2b[i].setText("");
				this.txtFieldsCol2b[0].setFocusable(true);
			}
			return;
		}
		DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate datum1 = LocalDate.parse(value[0], dfDate);
		this.datePicker[0].setDate(datum1);
		this.datePicker[0].setEnabled(false);
		for (int i = 0; i < this.txtFieldsCol1.length; i++) {
			this.txtFieldsCol1[i].setText(value[i + 1]);
			this.txtFieldsCol1[i].setFocusable(false);
		}
		for (int i = 0; i < this.txtFieldsCol2a.length; i++) {
			if (i == 0) {
				this.txtFieldsCol2a[i].setText(value[i + 9]);
			} else {
				this.txtFieldsCol2a[i].setText(value[i + 9].replace(".", ","));
			}
			this.txtFieldsCol2a[i].setFocusable(false);
		}
		LocalDate datum2 = LocalDate.parse(value[14], dfDate);
		this.datePicker[1].setDate(datum2);
		this.datePicker[1].setEnabled(false);
		this.txtFieldsCol2b[0].setText(value[15]);
		this.txtFieldsCol2b[0].setFocusable(false);
		this.txtFieldsCol2b[1].setText(value[16]);
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
