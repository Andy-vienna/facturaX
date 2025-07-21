package org.andy.gui.bill.out;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.Tools.FormatIBAN;
import static org.andy.toolbox.misc.Tools.cutBack2;
import static org.andy.toolbox.sql.Insert.sqlInsert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.code.main.overview.LoadBillOut;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;

public class JFnewRa extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(JFnewRa.class);

	private static String sConnSource;
	private static String sConnDest;
	private static final String TBL_BILL_OUT = "tbl_reOUT";

	private JPanel contentPanel = new JPanel();
	private static final String VON = "Leistungszr. von";
	private static final String BIS = "Leistungszr. bis";

	private static JLabel[] lblPos = new JLabel[13];
	@SuppressWarnings("unchecked")
	private static JComboBox<String>[] cbPos = new JComboBox[13];
	private static JTextField[] txtAnz = new JTextField[13];
	private static JTextField[] txtEP = new JTextField[13];
	private static JTextField[] txtGP = new JTextField[13];

	private static String sReNummer = null;
	private static String sReDatum = StartUp.getDtNow();
	private static String sReReferenz = null;
	private static String sDatumVon = null;
	private static String sDatumBis = null;

	private static String[][] arrArtikel = new String[100][5];
	private static String[][] arrBank = new String[20][6];
	private static String[][] arrKunde = new String[100][16];

	private static List<String> ARdata = new ArrayList<>();
	private static List<String> BKdata = new ArrayList<>();
	private static List<String> KDdata = new ArrayList<>();

	private static BigDecimal bdNetto;
	private static BigDecimal bdUstSatz;
	private static BigDecimal bdUSt;
	private static BigDecimal bdBrutto;

	private static BigDecimal[] bdAnzahl = new BigDecimal[13];
	private static BigDecimal[] bdEinzel = new BigDecimal[13];
	private static BigDecimal[] bdSumme = new BigDecimal[13];

	private static String[] sPosText = new String[13];
	private static String[] arrWriteR = new String[51];

	private static int iSelKunde;
	private static int iSelBank;
	private static int iNumFrame;
	private static BigDecimal bdRabatt;
	private static boolean bKundeSel = false;
	private static boolean bBankSel = false;
	private static boolean bArtSel = false;
	private static boolean bRevCharge = false;

	private static String sTsTp;
	private static String sTsRk;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void showGUI(String sDate, String sRE) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					fillVector();
					JFnewRa frame = new JFnewRa(sDate, sRE);
					frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e1) {
					logger.fatal("showGUI(String sDate, String sRE) fehlgeschlagen - " + e1);
				}
			}
		});
	}

	public JFnewRa(String sDate, String sRE) {

		try (InputStream is = JFnewRa.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setResizable(false);
		setTitle("Rechnung erstellen");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFcreateRa.class.getResource("/main/resources/icons/edit.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1021, 428);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(null);
		getContentPane().add(contentPanel);
		setLocationRelativeTo(null);

		JLabel lbl01 = new JLabel("Kundennummer");
		JLabel lbl02 = new JLabel("Kundenname");
		JLabel lbl03 = new JLabel("Strasse");
		JLabel lbl04 = new JLabel("PLZ");
		JLabel lbl05 = new JLabel("Ort");
		JLabel lbl06 = new JLabel("Land");
		JLabel lbl07 = new JLabel("Anrede");
		JLabel lbl08 = new JLabel("Ansprechpartner");
		JLabel lbl09 = new JLabel("UID");
		JLabel lbl10 = new JLabel("USt.-Satz");
		JLabel lbl11 = new JLabel("%");
		JLabel lbl12 = new JLabel("Rabattschlüssel");
		JLabel lbl13 = new JLabel("%");
		JLabel lbl14 = new JLabel("Zahlungsziel");
		JLabel lbl15 = new JLabel("Tage");
		JLabel lbl16 = new JLabel("Bank");
		JLabel lbl17 = new JLabel("IBAN");
		JLabel lbl18 = new JLabel("BIC");
		JLabel lbl20 = new JLabel("Nr.");
		JLabel lbl21 = new JLabel("Position");
		JLabel lbl22 = new JLabel("Anz.");
		JLabel lbl23 = new JLabel("Einzel");
		JLabel lbl24 = new JLabel("Summe");
		JLabel lbl25 = new JLabel("Rechnungsnummer:");
		JLabel lbl26 = new JLabel("Rechnungsdatum:");
		JLabel lbl27 = new JLabel(VON);
		JLabel lbl28 = new JLabel(BIS);
		JLabel lbl29 = new JLabel("Referenz");

		JComboBox<String> cmbKunde = new JComboBox<>(KDdata.toArray(new String[0]));
		JTextField textKdNr = new JTextField();
		JTextField textKdName = new JTextField();
		JTextField textKdStrasse = new JTextField();
		JTextField textKdPLZ = new JTextField();
		JTextField textKdOrt = new JTextField();
		JTextField textKdLand = new JTextField();
		JTextField textKdPronom = new JTextField();
		JTextField textKdDuty = new JTextField();
		JTextField textKdUID = new JTextField();
		JTextField textKdUSt = new JTextField();
		JTextField textKdRabatt = new JTextField();
		JTextField textKdZahlZiel = new JTextField();
		JCheckBox chkRevCharge = new JCheckBox("ReverseCharge");
		JComboBox<String> cmbBank = new JComboBox<>(BKdata.toArray(new String[0]));
		JTextField textBank = new JTextField();
		JTextField textIBAN = new JTextField();
		JTextField textBIC = new JTextField();
		JTextField textNummer = new JTextField(sRE);
		JTextField textReferenz = new JTextField();
		JButton btnDoExport = null;
		try {
			btnDoExport = createButton("<html>Rechnung<br>erstellen</html>", "edit.png");
		} catch (RuntimeException e) {
			logger.error("error creating button - " + e);
		}
		btnDoExport.setEnabled(true);


		DemoPanel panelDate = new DemoPanel();
		panelDate.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettings = new DatePickerSettings();
		dateSettings.setWeekNumbersDisplayed(true, true);
		dateSettings.setFormatForDatesCommonEra("dd.MM.yyyy");
		DatePicker datePicker = new DatePicker(dateSettings);
		datePicker.setDate(StartUp.getDateNow());
		datePicker.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePicker.getComponentDateTextField().setFont(new Font("Tahoma", Font.BOLD, 14));
		datePicker.getComponentDateTextField().setForeground(Color.BLUE);
		datePicker.getComponentDateTextField().setHorizontalAlignment(SwingConstants.CENTER);
		datePicker.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = datePicker.getDate();
				if (selectedDate != null) {
					sReDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sReDatum = null;
				}
			}
		});

		DemoPanel panelVon = new DemoPanel();
		panelVon.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsVon = new DatePickerSettings();
		dateSettingsVon.setWeekNumbersDisplayed(true, true);
		dateSettingsVon.setFormatForDatesCommonEra("dd.MM.yyyy");
		DatePicker datePickerVon = new DatePicker(dateSettingsVon);
		datePickerVon.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePickerVon.getComponentDateTextField().setFont(new Font("Tahoma", Font.BOLD, 14));
		datePickerVon.getComponentDateTextField().setForeground(Color.BLUE);
		datePickerVon.getComponentDateTextField().setHorizontalAlignment(SwingConstants.CENTER);
		datePickerVon.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = datePickerVon.getDate();
				if (selectedDate != null) {
					sDatumVon = selectedDate.format(StartUp.getDfdate());
				} else {
					sDatumVon = null;
				}
			}
		});

		DemoPanel panelBis = new DemoPanel();
		panelBis.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsBis = new DatePickerSettings();
		dateSettingsBis.setWeekNumbersDisplayed(true, true);
		dateSettingsBis.setFormatForDatesCommonEra("dd.MM.yyyy");
		DatePicker datePickerBis = new DatePicker(dateSettingsBis);
		datePickerBis.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePickerBis.getComponentDateTextField().setFont(new Font("Tahoma", Font.BOLD, 14));
		datePickerBis.getComponentDateTextField().setForeground(Color.BLUE);
		datePickerBis.getComponentDateTextField().setHorizontalAlignment(SwingConstants.CENTER);
		datePickerBis.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = datePickerBis.getDate();
				if (selectedDate != null) {
					sDatumBis = selectedDate.format(StartUp.getDfdate());
				} else {
					sDatumBis = null;
				}
			}
		});


		JSeparator separator1 = new JSeparator();
		JSeparator separator2 = new JSeparator();
		JSeparator separator3 = new JSeparator();

		lbl01.setBounds(10, 30, 110, 20);
		lbl02.setBounds(10, 50, 110, 20);
		lbl03.setBounds(10, 70, 110, 20);
		lbl04.setBounds(10, 90, 110, 20);
		lbl05.setBounds(10, 110, 110, 20);
		lbl06.setBounds(10, 130, 110, 20);
		lbl07.setBounds(10, 150, 110, 20);
		lbl08.setBounds(10, 170, 110, 20);
		lbl09.setBounds(10, 190, 110, 20);
		lbl10.setBounds(10, 210, 110, 20);
		lbl11.setBounds(155, 210, 30, 20);
		lbl12.setBounds(10, 230, 110, 20);
		lbl13.setBounds(155, 230, 30, 20);
		lbl14.setBounds(10, 250, 110, 20);
		lbl15.setBounds(155, 250, 50, 20);
		lbl16.setBounds(10, 310, 110, 20);
		lbl17.setBounds(10, 330, 110, 20);
		lbl18.setBounds(10, 350, 110, 20);
		lbl20.setBounds(320, 10, 25, 20);
		lbl21.setBounds(345, 10, 440, 20);
		lbl22.setBounds(785, 10, 70, 20);
		lbl23.setBounds(855, 10, 70, 20);
		lbl24.setBounds(925, 10, 70, 20);
		lbl25.setBounds(330, 290, 125, 25);
		lbl26.setBounds(330, 320, 125, 25);
		lbl27.setBounds(600, 290, 100, 25);
		lbl28.setBounds(600, 320, 100, 25);
		lbl29.setBounds(330, 350, 60, 25);

		cmbKunde.setBounds(10, 10, 300, 20);
		textKdNr.setBounds(110, 30, 200, 20);
		textKdName.setBounds(110, 50, 200, 20);
		textKdStrasse.setBounds(110, 70, 200, 20);
		textKdPLZ.setBounds(110, 90, 200, 20);
		textKdOrt.setBounds(110, 110, 200, 20);
		textKdLand.setBounds(110, 130, 200, 20);
		textKdPronom.setBounds(110, 150, 200, 20);
		textKdDuty.setBounds(110, 170, 200, 20);
		textKdUID.setBounds(110, 190, 200, 20);
		textKdUSt.setBounds(110, 210, 40, 20);
		textKdRabatt.setBounds(110, 230, 40, 20);
		textKdZahlZiel.setBounds(110, 250, 40, 20);
		chkRevCharge.setBounds(200, 210, 110, 20);
		cmbBank.setBounds(10, 290, 300, 20);
		textBank.setBounds(110, 310, 200, 20);
		textIBAN.setBounds(110, 330, 200, 20);
		textBIC.setBounds(110, 350, 200, 20);
		textNummer.setBounds(450, 290, 140, 25);
		textReferenz.setBounds(450, 350, 385, 25);
		btnDoExport.setBounds(850, 305, 120, 50);

		datePicker.setBounds(452, 320, 140, 25);
		datePickerVon.setBounds(692, 290, 139, 25);
		datePickerBis.setBounds(692, 320, 139, 25);

		separator1.setBounds(10, 280, 300, 2);
		separator1.setOrientation(SwingConstants.HORIZONTAL);
		separator2.setBounds(315, 10, 2, 370);
		separator2.setOrientation(SwingConstants.VERTICAL);
		separator3.setBounds(320, 280, 675, 2);
		separator3.setOrientation(SwingConstants.HORIZONTAL);

		lbl01.setForeground(Color.GRAY);
		lbl02.setForeground(Color.GRAY);
		lbl03.setForeground(Color.GRAY);
		lbl04.setForeground(Color.GRAY);
		lbl05.setForeground(Color.GRAY);
		lbl06.setForeground(Color.GRAY);
		lbl07.setForeground(Color.GRAY);
		lbl08.setForeground(Color.GRAY);
		lbl09.setForeground(Color.GRAY);
		lbl10.setForeground(Color.GRAY);
		lbl11.setForeground(Color.GRAY);
		lbl12.setForeground(Color.GRAY);
		lbl13.setForeground(Color.GRAY);
		lbl14.setForeground(Color.GRAY);
		lbl15.setForeground(Color.GRAY);
		lbl16.setForeground(Color.GRAY);
		lbl17.setForeground(Color.GRAY);
		lbl18.setForeground(Color.GRAY);
		lbl20.setHorizontalAlignment(SwingConstants.CENTER);
		lbl21.setHorizontalAlignment(SwingConstants.CENTER);
		lbl22.setHorizontalAlignment(SwingConstants.CENTER);
		lbl23.setHorizontalAlignment(SwingConstants.CENTER);
		lbl24.setHorizontalAlignment(SwingConstants.CENTER);

		textKdNr.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdNr.setEditable(false);
		textKdName.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdName.setEditable(false);
		textKdStrasse.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdStrasse.setEditable(false);
		textKdPLZ.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdPLZ.setEditable(false);
		textKdOrt.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdOrt.setEditable(false);
		textKdLand.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdLand.setEditable(false);
		textKdPronom.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdPronom.setEditable(false);
		textKdDuty.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdDuty.setEditable(false);
		textKdUID.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdUID.setEditable(false);
		textKdUSt.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdUSt.setEditable(false);
		textKdRabatt.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdRabatt.setEditable(false);
		textKdZahlZiel.setFont(new Font("Tahoma", Font.BOLD, 11));
		textKdZahlZiel.setEditable(false);
		chkRevCharge.setVisible(false);
		textBank.setFont(new Font("Tahoma", Font.BOLD, 11));
		textBank.setEditable(false);
		textIBAN.setFont(new Font("Tahoma", Font.BOLD, 11));
		textIBAN.setEditable(false);
		textBIC.setFont(new Font("Tahoma", Font.BOLD, 11));
		textBIC.setEditable(false);
		textNummer.setForeground(Color.BLUE);
		textNummer.setFont(new Font("Tahoma", Font.BOLD, 14));
		textNummer.setHorizontalAlignment(SwingConstants.CENTER);
		textNummer.setEditable(false);
		textReferenz.setForeground(Color.BLUE);
		textReferenz.setBackground(Color.PINK);
		textReferenz.setFont(new Font("Tahoma", Font.BOLD, 11));

		for(int x = 1; x < 13; x++) {
			lblPos[x] = new JLabel(String.valueOf(x));
			lblPos[x].setHorizontalAlignment(SwingConstants.CENTER);
			lblPos[x].setBounds(320, 30 + ((x - 1) * 20), 20, 20);
			contentPanel.add(lblPos[x]);
			cbPos[x] = new JComboBox<>(ARdata.toArray(new String[0]));
			cbPos[x].setBounds(345,  30 + ((x - 1) * 20), 440, 20);
			cbPos[x].setSelectedIndex(0);
			contentPanel.add(cbPos[x]);
			txtAnz[x] = new JTextField();
			txtAnz[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtAnz[x].setBounds(785, 30 + ((x - 1) * 20), 70, 20);
			txtAnz[x].setEnabled(false);
			contentPanel.add(txtAnz[x]);
			txtEP[x] = new JTextField();
			txtEP[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtEP[x].setBounds(855, 30 + ((x - 1) * 20), 70, 20);
			txtEP[x].setEditable(false);
			contentPanel.add(txtEP[x]);
			txtGP[x] = new JTextField();
			txtGP[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtGP[x].setBounds(925, 30 + ((x - 1) * 20), 70, 20);
			txtGP[x].setEditable(false);
			contentPanel.add(txtGP[x]);
		}

		btnDoExport.setIconTextGap(10);
		btnDoExport.setIcon(new ImageIcon(JFnewRa.class.getResource("/org/resources/icons/edit.png")));

		contentPanel.add(lbl01);
		contentPanel.add(lbl02);
		contentPanel.add(lbl03);
		contentPanel.add(lbl04);
		contentPanel.add(lbl05);
		contentPanel.add(lbl06);
		contentPanel.add(lbl07);
		contentPanel.add(lbl08);
		contentPanel.add(lbl09);
		contentPanel.add(lbl10);
		contentPanel.add(lbl11);
		contentPanel.add(lbl12);
		contentPanel.add(lbl13);
		contentPanel.add(lbl14);
		contentPanel.add(lbl15);
		contentPanel.add(lbl16);
		contentPanel.add(lbl17);
		contentPanel.add(lbl18);
		contentPanel.add(lbl20);
		contentPanel.add(lbl21);
		contentPanel.add(lbl22);
		contentPanel.add(lbl23);
		contentPanel.add(lbl24);
		contentPanel.add(lbl25);
		contentPanel.add(lbl26);
		contentPanel.add(lbl27);
		contentPanel.add(lbl28);
		contentPanel.add(lbl29);
		contentPanel.add(separator1);
		contentPanel.add(separator2);
		contentPanel.add(separator3);
		contentPanel.add(cmbKunde);
		contentPanel.add(textKdNr);
		contentPanel.add(textKdName);
		contentPanel.add(textKdStrasse);
		contentPanel.add(textKdPLZ);
		contentPanel.add(textKdOrt);
		contentPanel.add(textKdLand);
		contentPanel.add(textKdPronom);
		contentPanel.add(textKdDuty);
		contentPanel.add(textKdUID);
		contentPanel.add(textKdUSt);
		contentPanel.add(textKdRabatt);
		contentPanel.add(textKdZahlZiel);
		contentPanel.add(chkRevCharge);
		contentPanel.add(cmbBank);
		contentPanel.add(textBank);
		contentPanel.add(textIBAN);
		contentPanel.add(textBIC);
		contentPanel.add(textNummer);
		contentPanel.add(textReferenz);
		contentPanel.add(btnDoExport);

		contentPanel.add(datePicker);
		contentPanel.add(datePickerVon);
		contentPanel.add(datePickerBis);

		contentPanel.setFocusTraversalPolicy(new org.andy.org.eclipse.wb.swing.FocusTraversalOnArray(new Component[]{cmbKunde, cmbBank, datePickerVon, datePickerBis, textReferenz, btnDoExport}));

		//###################################################################################################################################################
		//###################################################################################################################################################

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				sReNummer = null;
				sReDatum = null;
				sReReferenz = null;
				iSelKunde = 0;
				iSelBank = 0;
				iNumFrame = 0;
				bKundeSel = false;
				bBankSel = false;
				bArtSel = false;
				for (int x = 0; x < bdAnzahl.length; x++) {
					bdAnzahl[x] = new BigDecimal("0.00");
					bdEinzel[x] = new BigDecimal("0.00");
					bdSumme[x] = new BigDecimal("0.00");
					sPosText[x] = null;
				}

				cmbKunde.setSelectedIndex(0);
				cmbBank.setSelectedIndex(0);
				textKdNr.setText("");
				textKdName.setText("");
				textReferenz.setText("");

				for (int x = 1; x < 13; x++) {
					cbPos[x].setSelectedIndex(0);
					txtAnz[x].setText("");
					txtEP[x].setText("");
					txtGP[x].setText("");
				}

				dispose();
				Runtime.getRuntime().gc();

			}
		});

		cmbKunde.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (cmbKunde.getSelectedIndex() == 0) {
					textKdNr.setText("");
					textKdName.setText("");
					textKdStrasse.setText("");
					textKdPLZ.setText("");
					textKdOrt.setText("");
					textKdLand.setText("");
					textKdPronom.setText("");
					textKdDuty.setText("");
					textKdUID.setText("");
					textKdUSt.setText("");
					textKdRabatt.setText("");
					textKdZahlZiel.setText("");
					chkRevCharge.setVisible(false);
					iSelKunde = cmbKunde.getSelectedIndex();
					bKundeSel = false;
				} else {
					textKdNr.setText(arrKunde[cmbKunde.getSelectedIndex()][1]);
					textKdName.setText(arrKunde[cmbKunde.getSelectedIndex()][2]);
					textKdStrasse.setText(arrKunde[cmbKunde.getSelectedIndex()][3]);
					textKdPLZ.setText(arrKunde[cmbKunde.getSelectedIndex()][4]);
					textKdOrt.setText(arrKunde[cmbKunde.getSelectedIndex()][5]);
					textKdLand.setText(arrKunde[cmbKunde.getSelectedIndex()][6]);
					textKdPronom.setText(arrKunde[cmbKunde.getSelectedIndex()][7]);
					textKdDuty.setText(arrKunde[cmbKunde.getSelectedIndex()][8]);
					textKdUID.setText(arrKunde[cmbKunde.getSelectedIndex()][9]);
					textKdUSt.setText(arrKunde[cmbKunde.getSelectedIndex()][10]);
					textKdRabatt.setText(arrKunde[cmbKunde.getSelectedIndex()][11]);
					bdRabatt = new BigDecimal(arrKunde[cmbKunde.getSelectedIndex()][11]);
					textKdZahlZiel.setText(arrKunde[cmbKunde.getSelectedIndex()][12]);
					if(textKdUSt.getText().equals("0")) {
						chkRevCharge.setVisible(true);
					}else {
						chkRevCharge.setVisible(false);
					}
					iSelKunde = cmbKunde.getSelectedIndex();
					bKundeSel = true;
				}
			}
		});
		chkRevCharge.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					bRevCharge = true;
				}else{
					bRevCharge = false;
				};
			}
		});
		cmbBank.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (cmbBank.getSelectedIndex() == 0) {
					textBank.setText("");
					textIBAN.setText("");
					textBIC.setText("");
					iSelBank = cmbBank.getSelectedIndex();
					bBankSel = false;
				} else {
					textBank.setText(arrBank[cmbBank.getSelectedIndex()][2]);
					textIBAN.setText(FormatIBAN(arrBank[cmbBank.getSelectedIndex()][3]));
					textBIC.setText(arrBank[cmbBank.getSelectedIndex()][4]);
					iSelBank = cmbBank.getSelectedIndex();
					bBankSel = true;
				}
			}
		});
		cbPos[1].addActionListener(cbPosListenerR(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]));
		cbPos[2].addActionListener(cbPosListenerR(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]));
		cbPos[3].addActionListener(cbPosListenerR(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]));
		cbPos[4].addActionListener(cbPosListenerR(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]));
		cbPos[5].addActionListener(cbPosListenerR(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]));
		cbPos[6].addActionListener(cbPosListenerR(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]));
		cbPos[7].addActionListener(cbPosListenerR(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]));
		cbPos[8].addActionListener(cbPosListenerR(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]));
		cbPos[9].addActionListener(cbPosListenerR(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]));
		cbPos[10].addActionListener(cbPosListenerR(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]));
		cbPos[11].addActionListener(cbPosListenerR(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]));
		cbPos[12].addActionListener(cbPosListenerR(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]));
		txtAnz[1].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtAnz[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtAnz[2].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtAnz[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtAnz[3].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtAnz[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtAnz[4].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtAnz[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtAnz[5].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtAnz[5].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtAnz[6].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtAnz[6].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtAnz[7].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtAnz[7].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtAnz[8].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtAnz[8].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtAnz[9].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtAnz[9].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtAnz[10].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtAnz[10].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtAnz[11].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtAnz[11].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtAnz[12].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionR(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		txtAnz[12].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionR(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		txtEP[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtEP[1].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtEP[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtEP[2].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtEP[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtEP[3].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtEP[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtEP[4].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtEP[5].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtEP[5].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtEP[6].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtEP[6].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtEP[7].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtEP[7].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtEP[8].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtEP[8].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtEP[9].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtEP[9].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtEP[10].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtEP[10].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtEP[11].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtEP[11].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtEP[12].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionR(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		txtEP[12].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionR(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		textReferenz.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(textReferenz.getText().isEmpty()) {
					textReferenz.setBackground(Color.PINK);
				}else {
					textReferenz.setBackground(Color.WHITE);
				}
			}
		});

		btnDoExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();

				if(bKundeSel) {
					if(bBankSel) {
						if(bArtSel) {
							if(textReferenz.getText().equals("") || sDatumVon == null || sDatumBis == null) {
								JOptionPane.showMessageDialog(null, "Kundenreferenz oder Leistungszeitraum fehlt ...", "Rechnung erstellen", JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							sReNummer = textNummer.getText();
							sReReferenz = textReferenz.getText();
							iNumFrame = setNum();

							String[] tmpArrR = new String[51];
							tmpArrR = writeRE();

							for (String str : tmpArrR) {
								sb.append(str).append("','");
							}
							String sValues= sb.substring(0, sb.length() - 2);

							try {

								String tblName = TBL_BILL_OUT.replace("_", LoadData.getStrAktGJ());
								String sSQLStatementA = "INSERT INTO " + tblName + " VALUES ('" + sValues + ")";

								sqlInsert(sConnDest, sSQLStatementA);

								String sSQLStatementB = "INSERT INTO [tblRE] VALUES ('" + tmpArrR[0] + "')";

								sqlInsert(sConnSource, sSQLStatementB);

							} catch (SQLException | ClassNotFoundException e1) {
								logger.error("error creating new outgoing bill - " + e1);
							}

							try {
								SQLmasterData.loadNummernkreis();
							} catch (ClassNotFoundException | SQLException | IOException e2) {
								logger.error("error creating new outgoing bill - " + e2);
							}
							LoadBillOut.loadAusgangsRechnung(false);
							JFoverview.btnPrintREa.setEnabled(false);
							JFoverview.btnStateREa.setEnabled(false);

							dispose();
							Runtime.getRuntime().gc();
						}else {
							JOptionPane.showMessageDialog(null, "keine Artikel ausgewählt ...", "Rechnung erstellen", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}else {
						JOptionPane.showMessageDialog(null, "Bank nicht ausgewählt ...", "Rechnung erstellen", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(null, "Kunde nicht ausgewählt ...", "Rechnung erstellen", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		});
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static void fillVector() {

		arrArtikel = SQLmasterData.getsArrArtikel();
		arrBank = SQLmasterData.getsArrBank();
		arrKunde = SQLmasterData.getsArrKunde();

		ARdata.clear();
		BKdata.clear();
		KDdata.clear();
		ARdata.add(" ");
		for (int x = 1; (x - 1) < SQLmasterData.getAnzArtikel(); x++) {
			ARdata.add(arrArtikel[x][2]);
		}
		BKdata.add(" ");
		for (int x = 1; (x - 1) < SQLmasterData.getAnzBank(); x++) {
			BKdata.add(arrBank[x][2]);
		}
		KDdata.add(" ");
		for (int x = 1; (x - 1) < SQLmasterData.getAnzKunde(); x++) {
			KDdata.add(arrKunde[x][2]);
		}
	}

	private static BigDecimal multi(BigDecimal a, BigDecimal b) {
		BigDecimal product = a.multiply(b).setScale(2, RoundingMode.HALF_UP);
		return (product); // Ergebnis zurückliefern
	}

	public static String[] writeRE() {
		Arrays.fill(arrWriteR, "");
		bdNetto = new BigDecimal("0.00");
		int x = 1;
		int y = 1;

		arrWriteR[0] = sReNummer; // Rechnungsnummer
		arrWriteR[1] = "1"; // active
		arrWriteR[2] = "0"; // printed
		arrWriteR[3] = "0"; // payed
		arrWriteR[4] = JFstatusRa.getWritten();
		arrWriteR[5] = sReDatum; // Rechnungsdatum
		arrWriteR[6] = sDatumVon + "-" + sDatumBis; // Leistungszeitraum
		arrWriteR[7] = sReReferenz; // Kundenreferenz
		arrWriteR[8] = arrKunde[iSelKunde][1];
		arrWriteR[9] = String.valueOf(JFnewRa.bRevCharge);
		arrWriteR[10] = arrBank[iSelBank][1]; // Index der Bankverbindung

		while(y < (JFnewRa.getiNumFrame() * 3)){
			arrWriteR[y + 14] = JFnewRa.sPosText[x];
			arrWriteR[y + 15] = JFnewRa.bdAnzahl[x].toPlainString();
			arrWriteR[y + 16] = JFnewRa.bdEinzel[x].toPlainString();
			BigDecimal bdTmp = multi(JFnewRa.bdAnzahl[x], JFnewRa.bdEinzel[x]);
			bdNetto = bdNetto.add(bdTmp);
			x = x + 1;
			y = y + 3;
		}

		String sTmp = arrKunde[iSelKunde][10];
		BigDecimal bdTmpA = new BigDecimal(sTmp).setScale(2, RoundingMode.HALF_UP);
		BigDecimal bdA = new BigDecimal("100").setScale(2, RoundingMode.HALF_UP);
		BigDecimal bdTmpB = bdTmpA.divide(bdA).setScale(2, RoundingMode.HALF_UP);
		bdUstSatz = bdTmpB;
		bdUSt = bdNetto.multiply(bdUstSatz).setScale(2, RoundingMode.HALF_UP);
		bdBrutto = bdNetto.add(bdUSt).setScale(2, RoundingMode.HALF_UP);

		arrWriteR[11] = bdNetto.toPlainString(); // Netto
		arrWriteR[12] = bdUSt.toPlainString(); // USt.
		arrWriteR[13] = bdBrutto.toPlainString(); // Brutto
		arrWriteR[14] = String.valueOf(JFnewRa.getiNumFrame()); // Anzahl Positionen

		return arrWriteR;
	}

	public static ActionListener cbPosListenerR(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
		ActionListener cbPosAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cbPos.getSelectedIndex() == 0) {
					txtAnz.setText("");
					txtEP.setText("");
					txtGP.setText("");
					txtAnz.setEnabled(false);
					txtEP.setEditable(false);
					txtAnz.setBackground(Color.WHITE);
					txtEP.setBackground(Color.WHITE);
					bArtSel = false;
				} else {
					if(bKundeSel && bBankSel) {

					}else {
						JOptionPane.showMessageDialog(null, "Kunde oder Bank nicht ausgewählt ...", "Rechnung erstellen", JOptionPane.INFORMATION_MESSAGE);
						cbPos.setSelectedIndex(0);
						return;
					}
					JFnewRa.sPosText[iNr] = arrArtikel[cbPos.getSelectedIndex()][2];
					if(cbPos.getSelectedItem().toString().length() > 14) {
						sTsTp = arrArtikel[cbPos.getSelectedIndex()][2].substring(arrArtikel[cbPos.getSelectedIndex()][2].length() - 14);
						boolean bSpace = arrArtikel[cbPos.getSelectedIndex()][2].indexOf(" ", arrArtikel[cbPos.getSelectedIndex()][2].indexOf(" ")) != -1;
						if(bSpace) {
							try {
								sTsRk = cutBack2(arrArtikel[cbPos.getSelectedIndex()][2], " ", 1);
							} catch (IOException e1) {
								logger.error("cbPosListenerR(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP)" + e1);
							}
							if(sTsRk.equals("Reisekosten")) {
								txtEP.setEditable(true);
								txtEP.setToolTipText("Eingaben mit ENTER abschließen");
							}else {
								txtEP.setEditable(false);
								txtEP.setToolTipText(null);
							}
							if(sTsTp.equals("Tagespauschale")) {
								String sTmp = arrArtikel[cbPos.getSelectedIndex()][3];
								BigDecimal bdTmpA = new BigDecimal(sTmp).setScale(2, RoundingMode.HALF_UP);
								BigDecimal bdA = new BigDecimal("1");
								BigDecimal bdB = new BigDecimal("100");
								BigDecimal bdTmpB = JFnewRa.bdRabatt.divide(bdB);
								bdTmpB = bdA.subtract(bdTmpB);
								bdEinzel[iNr] = bdTmpA.multiply(bdTmpB).setScale(2, RoundingMode.HALF_UP);
								txtEP.setText(JFnewRa.bdEinzel[iNr].toPlainString());
								txtEP.setBackground(new Color(152,251,152));
								txtEP.setToolTipText("Rabattierter Preis");
							}else {
								String sTmp = arrArtikel[cbPos.getSelectedIndex()][3];
								BigDecimal bdTmpA = new BigDecimal(sTmp).setScale(2, RoundingMode.HALF_UP);
								bdEinzel[iNr] = bdTmpA;
								txtEP.setText(JFnewRa.bdEinzel[iNr].toPlainString());
								txtEP.setBackground(Color.WHITE);
								txtEP.setToolTipText(null);
							}
						}
					}else {
						String sTmp = arrArtikel[cbPos.getSelectedIndex()][3];
						BigDecimal bdTmpA = new BigDecimal(sTmp).setScale(2, RoundingMode.HALF_UP);
						bdEinzel[iNr] = bdTmpA;
						txtEP.setText(bdEinzel[iNr].toPlainString());
					}
					txtAnz.setEnabled(true);
					txtAnz.setBackground(Color.PINK);
					bArtSel = true;
				}
			}
		};
		return cbPosAction;
	}

	public static void AnzahlActionR(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
		if(txtEP.getText().isEmpty() || txtAnz.getText().isEmpty()) {
			txtAnz.setBackground(Color.PINK);
			return;
		}
		try {
			bdAnzahl[iNr] = new BigDecimal(txtAnz.getText().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
			bdEinzel[iNr] = new BigDecimal(txtEP.getText().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
			bdSumme[iNr] = bdEinzel[iNr].multiply(bdAnzahl[iNr]).setScale(2, RoundingMode.HALF_UP);
			txtGP.setText(String.format(Locale.GERMANY, "%.2f", JFnewRa.bdSumme[iNr]));
			txtAnz.setBackground(Color.WHITE);
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Eingabe inkorrekt ...", "Rechnung erstellen", JOptionPane.ERROR_MESSAGE);
			txtAnz.setText("");
		}

	}

	public static void EPActionR(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
		if(txtEP.getText().isEmpty()) {
			return;
		}
		try {
			bdEinzel[iNr] = new BigDecimal(txtEP.getText().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Eingabe inkorrekt ...", "Rechnung erstellen", JOptionPane.ERROR_MESSAGE);
			txtEP.setText("");
		}

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static int setNum() {
		int Num = 1;
		while(cbPos[Num].getSelectedIndex() > 0) {
			Num = Num + 1;
		}
		return Num - 1;
	}

	public static int getiNumFrame() {
		return iNumFrame;
	}

	public static void setsConnDest(String sConnDest) {
		JFnewRa.sConnDest = sConnDest;
	}

	public static void setsConnSource(String sConnSource) {
		JFnewRa.sConnSource = sConnSource;
	}

}
