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
import org.andy.code.main.overview.edit.SvTax;
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

public class SvTaxPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(SvTaxPanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private static final String OK = "OK";
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFields = new JTextField[3];
	private JTextField txtFile = new JTextField();
	private JLabel lblFileTyp = new JLabel();
	private JButton[] btnFields = new JButton[3];
	
	private String[] sDatum = new String[2];
	private int id = 0;
	private boolean file = false;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public SvTaxPanel() {
        super("Steuer und Sozialversicherung");
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
	        "Eingangsdatum:",
	        "Organisation:",
	        "Bezeichnung:",
	        "Zahllast:",
	        "Zahlungsziel:",
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
	    for (int r = 0; r < txtFields.length; r++) {
	    	txtFields[r] = makeField(210, 45 + r * 25, 400, 25, false, null);
	    	add(txtFields[r]);
	    }
	    datePicker[1].setBounds(212, 120, 180, 25);
	    
	    txtFile = makeField(210, 145, 400, 25, false, null);
	    txtFile.setFocusable(false);
	    add(txtFile);
	    
	    // Anzeige Filetyp
	    lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(660, 45, 50, 40);
		add(lblFileTyp);
	    
	    btnFields[0] = new JButton();
	    btnFields[0].setToolTipText("");
	    btnFields[0].setBounds(145, 145, 65, 25);
	    add(btnFields[0]);

		try {
			btnFields[1] = createButton("", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnFields[1].setEnabled(true);
		btnFields[1].setBounds(660, 120, JFoverview.getButtonx(), JFoverview.getButtony());
		add(btnFields[1]);
		
		setPreferredSize(new Dimension(1000, 20 + 5 * 25 + 50));
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					SvTax.actionMouseClick(e, String.valueOf(id));
				}
			}
		});
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				String fName = SvTax.selectFile();
 				txtFile.setText(fName);
 				file = true;
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				
 				String[] arrTmp = new String[7];
 				Arrays.fill(arrTmp, null);
 				
 				arrTmp[0] = sDatum[0]; // Belegdatum
 				arrTmp[1] = txtFields[0].getText(); // Organisation
 				arrTmp[2] = txtFields[1].getText(); // Bezeichnung
 				arrTmp[3] = txtFields[2].getText().replace(",", "."); // Zahllast
 				arrTmp[4] = sDatum[1]; // Zahlungsziel
 				arrTmp[5] = txtFile.getText(); // Dateiname
 				arrTmp[6] = SvTax.getFilePath(); // Dateipfad
 				
 				String sResult = SvTax.writeData(arrTmp, id, file);
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
    
    public void setTxtFields(String[] value, int id) {
    	this.id = 0;
    	if (id > 0) {
			this.id = id;
		} 
    	if (value[0] == null || value[0].isEmpty()) {
    		this.datePicker[0].setDate(null);
    		for (int i = 0; i < this.txtFields.length; i++) {
				this.txtFields[i].setText("");
			}
    		this.datePicker[1].setDate(null);
    		this.txtFile.setText("");
			return;
		}
    	DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDate datumA = LocalDate.parse(value[0], dfDate);
    	this.datePicker[0].setDate(datumA);
    	for (int i = 0; i < this.txtFields.length; i++) {
    		if (i == 2) {
    			this.txtFields[i].setText(value[i + 1].replace(".", ","));
    		} else {
    			this.txtFields[i].setText(value[i + 1]);
    		}
    	}
    	LocalDate datumB = LocalDate.parse(value[4], dfDate);
    	this.datePicker[1].setDate(datumB);
    	txtFile.setText(value[5]);
    }

	public void setBtnText(int col, String value) {
		this.btnFields[col].setText(value);
	}
	
	public void setIcon() {
		try {
			JFfileView.setFileIcon(lblFileTyp, txtFile.getText());
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
