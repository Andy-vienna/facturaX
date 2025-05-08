package org.andy.gui.bill.in;

import static org.andy.toolbox.misc.CreateObject.changeKomma;
import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
import static org.andy.toolbox.sql.Insert.sqlInsert;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.xml.xpath.XPathExpressionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;

public class JFnewRe extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFnewRe.class);

	private static final long serialVersionUID = 1L;
	private static final String TBL_BILL_IN = "tbl_reIN";

	private static String sReDatum;
	private static String sZZDatum;
	private static String sConn;
	private static String FilePath;

	private JPanel contentPane;
	private static JTextField txtItem1;
	private static DatePicker dateRechnung = new DatePicker();
	private static JTextField txtItem3;
	private static JTextField txtItem4;
	private static JTextField txtItem5;
	private static JTextField txtItem6;
	private static JTextField txtItem7;
	private static JTextField txtItem8;
	private static JTextField txtItem9;
	private static JTextField txtItem10;
	private static JTextField txtItem11;
	private static JTextField txtItem12;
	private static JTextField txtItem13;
	private static JTextField txtItem14;
	private static DatePicker dateZahlZiel = new DatePicker();
	private static JTextField txtItem16;
	private static JTextField txtItem17;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFnewRe frame = new JFnewRe();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFnewRe() {

		try (InputStream is = JFnewRe.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setTitle("neue Eingangsrechnung anlegen");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFnewRe.class.getResource("/edit_color.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 665, 610);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblItem1 = new JLabel("Rechnungsnummer");
		lblItem1.setBounds(10, 10, 130, 25);
		contentPane.add(lblItem1);

		JLabel lblItem2 = new JLabel("Rechnungsdatum");
		lblItem2.setBounds(10, 35, 130, 25);
		contentPane.add(lblItem2);

		JLabel lblItem3 = new JLabel("Kreditor Name");
		lblItem3.setBounds(10, 70, 130, 25);
		contentPane.add(lblItem3);

		JLabel lblItem4 = new JLabel("Kreditor Straße");
		lblItem4.setBounds(10, 95, 130, 25);
		contentPane.add(lblItem4);

		JLabel lblItem5 = new JLabel("Kreditor PLZ");
		lblItem5.setBounds(10, 120, 130, 25);
		contentPane.add(lblItem5);

		JLabel lblItem6 = new JLabel("Kreditor Ort");
		lblItem6.setBounds(10, 145, 130, 25);
		contentPane.add(lblItem6);

		JLabel lblItem7 = new JLabel("Kreditor Ländercode");
		lblItem7.setBounds(10, 170, 130, 25);
		contentPane.add(lblItem7);

		JLabel lblItem8 = new JLabel("Kreditor UID");
		lblItem8.setBounds(10, 195, 130, 25);
		contentPane.add(lblItem8);

		JLabel lblItem9 = new JLabel("Währung");
		lblItem9.setBounds(10, 230, 130, 25);
		contentPane.add(lblItem9);

		JLabel lblItem10 = new JLabel("USt. Satz");
		lblItem10.setBounds(10, 255, 130, 25);
		contentPane.add(lblItem10);

		JLabel lblItem11 = new JLabel("Netto");
		lblItem11.setBounds(10, 290, 130, 25);
		contentPane.add(lblItem11);

		JLabel lblItem12 = new JLabel("Anzahlung");
		lblItem12.setBounds(10, 315, 130, 25);
		contentPane.add(lblItem12);

		JLabel lblItem13 = new JLabel("USt.");
		lblItem13.setBounds(10, 340, 130, 25);
		contentPane.add(lblItem13);

		JLabel lblItem14 = new JLabel("Brutto");
		lblItem14.setBounds(10, 365, 130, 25);
		contentPane.add(lblItem14);

		JLabel lblItem15 = new JLabel("Zahlungsziel");
		lblItem15.setBounds(10, 400, 130, 25);
		contentPane.add(lblItem15);

		JLabel lblItem16 = new JLabel("Zahlungshinweis");
		lblItem16.setBounds(10, 425, 130, 25);
		contentPane.add(lblItem16);

		JLabel lblItem17 = new JLabel("Dateianhang:");
		lblItem17.setBounds(10, 460, 70, 25);
		contentPane.add(lblItem17);

		txtItem1 = new JTextField();
		txtItem1.setBounds(140, 10, 500, 25);
		contentPane.add(txtItem1);
		txtItem1.setColumns(10);

		DemoPanel panelDateR = new DemoPanel();
		panelDateR.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsR = new DatePickerSettings();
		dateSettingsR.setWeekNumbersDisplayed(true, true);
		dateSettingsR.setFormatForDatesCommonEra("dd.MM.yyyy");
		dateRechnung = new DatePicker(dateSettingsR);
		dateRechnung.getComponentDateTextField().setBorder(new RoundedBorder(10));
		dateRechnung.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = dateRechnung.getDate();
				if (selectedDate != null) {
					sReDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sReDatum = null;
				}
			}
		});
		dateRechnung.setBounds(142, 35, 180, 25);
		contentPane.add(dateRechnung);

		txtItem3 = new JTextField();
		txtItem3.setColumns(10);
		txtItem3.setBounds(140, 70, 500, 25);
		contentPane.add(txtItem3);

		txtItem4 = new JTextField();
		txtItem4.setColumns(10);
		txtItem4.setBounds(140, 95, 500, 25);
		contentPane.add(txtItem4);

		txtItem5 = new JTextField();
		txtItem5.setColumns(10);
		txtItem5.setBounds(140, 120, 500, 25);
		contentPane.add(txtItem5);

		txtItem6 = new JTextField();
		txtItem6.setColumns(10);
		txtItem6.setBounds(140, 145, 500, 25);
		contentPane.add(txtItem6);

		txtItem7 = new JTextField();
		txtItem7.setColumns(10);
		txtItem7.setBounds(140, 170, 500, 25);
		contentPane.add(txtItem7);

		txtItem8 = new JTextField();
		txtItem8.setColumns(10);
		txtItem8.setBounds(140, 195, 500, 25);
		contentPane.add(txtItem8);

		txtItem9 = new JTextField();
		txtItem9.setColumns(10);
		txtItem9.setBounds(140, 230, 500, 25);
		contentPane.add(txtItem9);

		txtItem10 = new JTextField();
		txtItem10.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem10.setText(changeKomma(txtItem10)));
			}
		});
		txtItem10.setColumns(10);
		txtItem10.setBounds(140, 255, 500, 25);
		contentPane.add(txtItem10);

		txtItem11 = new JTextField();
		txtItem11.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem11.setText(changeKomma(txtItem11)));
			}
		});
		txtItem11.setColumns(10);
		txtItem11.setBounds(140, 290, 500, 25);
		contentPane.add(txtItem11);

		txtItem12 = new JTextField();
		txtItem12.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem12.setText(changeKomma(txtItem12)));
			}
		});
		txtItem12.setColumns(10);
		txtItem12.setBounds(140, 315, 500, 25);
		contentPane.add(txtItem12);

		txtItem13 = new JTextField();
		txtItem13.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem13.setText(changeKomma(txtItem13)));
			}
		});
		txtItem13.setColumns(10);
		txtItem13.setBounds(140, 340, 500, 25);
		contentPane.add(txtItem13);

		txtItem14 = new JTextField();
		txtItem14.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem14.setText(changeKomma(txtItem14)));
			}
		});
		txtItem14.setColumns(10);
		txtItem14.setBounds(140, 365, 500, 25);
		contentPane.add(txtItem14);

		DemoPanel panelDateZ = new DemoPanel();
		panelDateZ.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsZ = new DatePickerSettings();
		dateSettingsZ.setWeekNumbersDisplayed(true, true);
		dateSettingsZ.setFormatForDatesCommonEra("dd.MM.yyyy");
		dateZahlZiel = new DatePicker(dateSettingsZ);
		dateZahlZiel.getComponentDateTextField().setBorder(new RoundedBorder(10));
		dateZahlZiel.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = dateZahlZiel.getDate();
				if (selectedDate != null) {
					sZZDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sZZDatum = null;
				}
			}
		});
		dateZahlZiel.setBounds(142, 400, 180, 25);
		contentPane.add(dateZahlZiel);

		txtItem16 = new JTextField();
		txtItem16.setColumns(10);
		txtItem16.setBounds(140, 425, 500, 25);
		contentPane.add(txtItem16);

		txtItem17 = new JTextField("------------");
		txtItem17.setEditable(false);
		txtItem17.setBounds(140, 460, 500, 25);
		contentPane.add(txtItem17);

		JButton btnImportZUGFeRD = null, btnImportXML = null, btnOK = null;
		try {
			btnImportZUGFeRD = createButton("<html>import<br>ZUGFeRD</html>", "import.png");
			btnImportXML = createButton("<html>import<br>XML</html>", "import.png");
			btnOK = createButton("OK", "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnImportZUGFeRD.setBounds(10, 510, 130, 50);
		btnImportZUGFeRD.setEnabled(true);

		btnImportXML.setBounds(150, 510, 130, 50);
		btnImportXML.setEnabled(true);

		btnOK.setBounds(510, 510, 130, 50);
		btnOK.setEnabled(true);

		JButton btnSelect = new JButton("...");
		btnSelect.setToolTipText("");
		btnSelect.setIconTextGap(10);
		btnSelect.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSelect.setBounds(80, 460, 60, 25);

		//###################################################################################################################################################
		//###################################################################################################################################################

		// ------------------------------------------------------------------------------
		// Action Listener für JFrame
		// ------------------------------------------------------------------------------
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				dispose();
			}
		});

		// ------------------------------------------------------------------------------
		// Action Listener für Buttons
		// ------------------------------------------------------------------------------
		btnImportZUGFeRD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importPDF();
			}
		});

		btnImportXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importPDF(); // aktuell alles über diesen Importer
				importXML();
			}
		});

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				writeNewREe();
				JFoverview.loadEingangsRechnung(false);
				dispose();
			}
		});

		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fName = selectFile();
				txtItem17.setText(fName);
			}
		});

		contentPane.add(btnImportZUGFeRD);
		contentPane.add(btnImportXML);
		contentPane.add(btnOK);
		contentPane.add(btnSelect);

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static String selectFile() {
		String FileNamePath = chooseFile(LoadData.getWorkPath());
		if(FileNamePath == getNotSelected()) {
			return getNotSelected();
		}
		File fn = new File(FileNamePath);
		FilePath = fn.getPath();
		String FileName = fn.getName();
		return FileName;
	}

	private static void writeNewREe() {

		BigDecimal Tmp09, Tmp10, Tmp11, Tmp12, Tmp13 = new BigDecimal("0.00");
		int Tmp18 = 0;
		String[] arrTmp = new String[19];
		Arrays.fill(arrTmp, null);

		try {
			arrTmp[0] = txtItem1.getText();
			arrTmp[1] = sReDatum; //txtItem2.getText();
			arrTmp[2] = txtItem3.getText();
			arrTmp[3] = txtItem4.getText();
			arrTmp[4] = txtItem5.getText();
			arrTmp[5] = txtItem6.getText();
			arrTmp[6] = txtItem7.getText();
			arrTmp[7] = txtItem8.getText();
			arrTmp[8] = txtItem9.getText();

			Tmp09 = new BigDecimal(txtItem10.getText());
			Tmp10 = new BigDecimal(txtItem11.getText());
			Tmp11 = new BigDecimal(txtItem12.getText());
			Tmp12 = new BigDecimal(txtItem13.getText());
			Tmp13 = new BigDecimal(txtItem14.getText());

			arrTmp[14] = sZZDatum; //txtItem15.getText();
			arrTmp[15] = txtItem16.getText();
			arrTmp[16] = txtItem17.getText();
			arrTmp[17] = FilePath;

			Tmp18 = 0;

			for(int x = 0; x < 18; x++) {
				if(arrTmp == null) {
					JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Eingangsrechnung erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Eingangsrechnung erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
			return;
		}


		String tblName = TBL_BILL_IN.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "INSERT INTO " + tblName + " ([Id],[re_datum],[kred_name],[kred_strasse],[kred_plz],[kred_ort],[kred_land],[kred_uid]"
				+ ",[waehrung],[steuersatz],[netto],[anzahlung],[ust],[brutto],[zahlungsziel],[hinweis],[dateiname],[datei],[status]) VALUES ('"
				+ arrTmp[0] + "','" + arrTmp[1]	+ "','" + arrTmp[2] + "','" + arrTmp[3] + "','" + arrTmp[4] + "','" + arrTmp[5] + "','" + arrTmp[6] + "','"
				+ arrTmp[7] + "','" + arrTmp[8] + "',"	+ Tmp09 + "," + Tmp10 + "," + Tmp11 + "," + Tmp12 + "," + Tmp13 + ",'" + arrTmp[14] + "','"
				+ arrTmp[15] + "','" + arrTmp[16] + "'," + "(SELECT * FROM OPENROWSET(BULK '" + arrTmp[17] + "', SINGLE_BLOB) AS DATA),"
				+ Tmp18 + ")";

		try {
			sqlInsert(sConn, sSQLStatement);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error writing new incoming invoice - " + e);
		}

	}

	private static void importPDF() {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		String[] tmp = new String[19];
		Arrays.fill(tmp, null);

		String sGetFile = chooseFile("");
		Invoice xe = null;

		try {
			ZUGFeRDImporter zu=new ZUGFeRDImporter(sGetFile);

			try {
				xe = zu.extractInvoice();
			} catch (XPathExpressionException | ParseException e1) {
				logger.error("error importing ZUGFeRD-Invoice - " + e1);
			}

			byte[] xmlContent = zu.getRawXML();

			Pattern pattern = Pattern.compile("<ram:RateApplicablePercent>(\\d+(\\.\\d+)?)</ram:RateApplicablePercent>");

			char[] charArray = new String(xmlContent, StandardCharsets.UTF_8).toCharArray();
			CharSequence charSequence = CharBuffer.wrap(charArray);

			Matcher matcher = pattern.matcher(charSequence);

			tmp[0] = zu.getInvoiceID();
			tmp[1] = zu.getIssueDate();
			tmp[2] = xe.getRecipient().getName();
			tmp[3] = xe.getRecipient().getStreet();
			tmp[4] = xe.getRecipient().getZIP();
			tmp[5] = xe.getRecipient().getLocation();
			tmp[6] = xe.getRecipient().getCountry();
			tmp[7] = xe.getRecipient().getVatID();
			tmp[8] = xe.getCurrency();
			if (matcher.find()) {
				tmp[9] = matcher.group(1);
			}
			tmp[10] = zu.getLineTotalAmount();
			tmp[11] = zu.getPaidAmount();
			tmp[12] = zu.getTaxTotalAmount();
			tmp[13] = zu.getAmount();
			tmp[14] = zu.getDueDate();
			tmp[15] = xe.getPaymentTermDescription();

			txtItem1.setText(tmp[0]); // Rechnungsnummer

			LocalDate dateR = LocalDate.parse(formatDate(tmp[1]), formatter);
			dateRechnung.setDate(dateR);

			txtItem3.setText(tmp[2]); // Rechnungssteller Name
			txtItem4.setText(tmp[3]); // Anschrift Zeile 1
			txtItem5.setText(tmp[4]); // Anschrift PLZ
			txtItem6.setText(tmp[5]); // Anschrift Ort
			txtItem7.setText(tmp[6]); // Länderkennung
			txtItem8.setText(tmp[7]); // Rechnungsempfänger USt. ID
			txtItem9.setText(tmp[8]); // Währungskennung
			txtItem10.setText(formatValue(tmp[9])); // Steuersatz
			txtItem11.setText(formatValue(tmp[10])); // Rechnungssumme netto
			txtItem12.setText(formatValue(tmp[11])); // Bezahlter Teilbetrag
			txtItem13.setText(formatValue(tmp[12])); // Rechnungssumme enthaltende USt.
			txtItem14.setText(formatValue(tmp[13])); // Rechnungssumme brutto

			LocalDate dateZ = LocalDate.parse(formatDate(tmp[14]), formatter);
			dateZahlZiel.setDate(dateZ);

			txtItem16.setText(tmp[15]); // Zahlungshinweis

			File fn = new File(sGetFile);
			FilePath = fn.getPath();
			String FileName = fn.getName();

			txtItem17.setText(FileName);
		} catch (Exception e) {
			logger.error("error importing xml contents for incoming e-bill - " + e);
			return;
		}


	}

	private static void importXML() {

	}

	private static String formatDate(String date) {
		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDate datum = LocalDate.parse(date, inputFormat);
		return datum.format(formatter);
	}

	private static String formatValue(String value) {
		DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
		BigDecimal bdtmp = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
		return df.format(bdtmp);
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFnewRe.sConn = sConn;
	}

}
