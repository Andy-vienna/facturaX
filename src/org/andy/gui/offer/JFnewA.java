package org.andy.gui.offer;

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
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;
import org.andy.org.eclipse.wb.swing.FocusTraversalOnArray;

public class JFnewA extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(JFnewA.class);

	private static String sConnSource;
	private static String sConnDest;
	private static final String TBL_OFFER = "tbl_an";

	private JPanel contentPanel = new JPanel();

	private static JLabel[] lblPos = new JLabel[13];
	@SuppressWarnings("unchecked")
	private static JComboBox<String>[] cbPos = new JComboBox[13];
	private static JTextField[] txtAnz = new JTextField[13];
	private static JTextField[] txtEP = new JTextField[13];
	private static JTextField[] txtGP = new JTextField[13];

	private static String sAnNummer = null;
	private static String sAnDatum = StartUp.getDtNow();
	private static String sAnReferenz = null;

	private static String[][] arrArtikel = new String[100][5];
	private static String[][] arrBank = new String[20][6];
	private static String[][] arrKunde = new String[100][16];

	private static List<String> ARdata = new ArrayList<>();
	private static List<String> BKdata = new ArrayList<>();
	private static List<String> KDdata = new ArrayList<>();

	private static double[] dAnzahl = new double[13];
	private static double[] dEinzel = new double[13];
	private static double[] dSumme = new double[13];
	private static String[] sPosText = new String[13];
	private static String sOptional1 = "false";
	private static String[] arrWriteA = new String[48];

	private static int iSelKunde;
	private static int iSelBank;
	private static int iNumFrame;
	private static double dRabatt;
	private static double dNetto = 0;
	private static boolean bKundeSel = false;
	private static boolean bBankSel = false;
	private static boolean bArtSel = false;

	private static String sTsTp;
	private static String sTsRk;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void showGUI(String sDate, String sAN) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					fillVector();
					JFnewA frame = new JFnewA(sDate, sAN);
					frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e1) {
					logger.fatal("showGUI(String sDate, String sAN) fehlgeschlagen - " + e1);
				}
			}
		});
	}

	public JFnewA(String sDate, String sAN) {

		try (InputStream is = JFnewA.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setResizable(false);
		setTitle("Angebot erstellen");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFcreateA.class.getResource("/main/resources/icons/edit.png")));
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
		JLabel lbl25 = new JLabel("Angebotsnummer:");
		JLabel lbl26 = new JLabel("Angebotsdatum:");
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
		JTextField textNummer = new JTextField(sAN);
		JCheckBox chkOptional1 = new JCheckBox("Angebot mit Anlage (Beschreibung)");
		JTextField textReferenz = new JTextField();
		JButton btnDoExport = null;
		try {
			btnDoExport = createButton("<html>Angebot<br>erstellen</html>", "edit.png");
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
					sAnDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sAnDatum = null;
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
		chkOptional1.setBounds(600, 320, 230, 23);
		textReferenz.setBounds(450, 350, 385, 25);
		btnDoExport.setBounds(850, 305, 120, 50);

		datePicker.setBounds(452, 320, 140, 25);

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
		btnDoExport.setIcon(new ImageIcon(JFnewA.class.getResource("/org/resources/icons/edit.png")));

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
		contentPanel.add(chkOptional1);
		contentPanel.add(textReferenz);
		contentPanel.add(btnDoExport);

		contentPanel.add(datePicker);

		contentPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{cmbKunde, cmbBank, btnDoExport}));

		//###################################################################################################################################################
		//###################################################################################################################################################

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				sAnNummer = null;
				sAnDatum = null;
				sAnReferenz = null;
				iSelKunde = 0;
				iSelBank = 0;
				iNumFrame = 0;
				dRabatt = 0.0;
				bKundeSel = false;
				bBankSel = false;
				bArtSel = false;
				for (int x = 0; x < dAnzahl.length; x++) {
					dAnzahl[x] = 0.0;
					dEinzel[x] = 0.0;
					dSumme[x] = 0.0;
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
					dRabatt = Double.parseDouble(arrKunde[cmbKunde.getSelectedIndex()][11]);
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
				}else{
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
		cbPos[1].addActionListener(cbPosListenerA(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]));
		cbPos[2].addActionListener(cbPosListenerA(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]));
		cbPos[3].addActionListener(cbPosListenerA(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]));
		cbPos[4].addActionListener(cbPosListenerA(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]));
		cbPos[5].addActionListener(cbPosListenerA(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]));
		cbPos[6].addActionListener(cbPosListenerA(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]));
		cbPos[7].addActionListener(cbPosListenerA(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]));
		cbPos[8].addActionListener(cbPosListenerA(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]));
		cbPos[9].addActionListener(cbPosListenerA(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]));
		cbPos[10].addActionListener(cbPosListenerA(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]));
		cbPos[11].addActionListener(cbPosListenerA(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]));
		cbPos[12].addActionListener(cbPosListenerA(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]));
		txtAnz[1].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtAnz[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtAnz[2].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtAnz[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtAnz[3].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtAnz[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtAnz[4].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtAnz[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtAnz[5].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtAnz[5].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtAnz[6].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtAnz[6].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtAnz[7].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtAnz[7].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtAnz[8].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtAnz[8].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtAnz[9].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtAnz[9].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtAnz[10].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtAnz[10].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtAnz[11].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtAnz[11].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtAnz[12].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				AnzahlActionA(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		txtAnz[12].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnzahlActionA(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		txtEP[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtEP[1].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(1, cbPos[1], txtAnz[1], txtEP[1], txtGP[1]);
			}
		});
		txtEP[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtEP[2].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(2, cbPos[2], txtAnz[2], txtEP[2], txtGP[2]);
			}
		});
		txtEP[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtEP[3].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(3, cbPos[3], txtAnz[3], txtEP[3], txtGP[3]);
			}
		});
		txtEP[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtEP[4].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(4, cbPos[4], txtAnz[4], txtEP[4], txtGP[4]);
			}
		});
		txtEP[5].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtEP[5].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(5, cbPos[5], txtAnz[5], txtEP[5], txtGP[5]);
			}
		});
		txtEP[6].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtEP[6].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(6, cbPos[6], txtAnz[6], txtEP[6], txtGP[6]);
			}
		});
		txtEP[7].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtEP[7].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(7, cbPos[7], txtAnz[7], txtEP[7], txtGP[7]);
			}
		});
		txtEP[8].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtEP[8].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(8, cbPos[8], txtAnz[8], txtEP[8], txtGP[8]);
			}
		});
		txtEP[9].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtEP[9].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(9, cbPos[9], txtAnz[9], txtEP[9], txtGP[9]);
			}
		});
		txtEP[10].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtEP[10].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(10, cbPos[10], txtAnz[10], txtEP[10], txtGP[10]);
			}
		});
		txtEP[11].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtEP[11].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(11, cbPos[11], txtAnz[11], txtEP[11], txtGP[11]);
			}
		});
		txtEP[12].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EPActionA(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
			}
		});
		txtEP[12].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				EPActionA(12, cbPos[12], txtAnz[12], txtEP[12], txtGP[12]);
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

		chkOptional1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					sOptional1 = "true";
				} else {
					sOptional1 = "false";
				}
				;
			}
		});

		btnDoExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();

				if(bKundeSel) {
					if(bBankSel) {
						if(bArtSel) {
							if(textReferenz.getText().equals("")) {
								JOptionPane.showMessageDialog(null, "Kundenreferenz fehlt ...", "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							sAnNummer = textNummer.getText();
							sAnReferenz = textReferenz.getText();
							iNumFrame = setNum();

							String[] tmpArrA = new String[48];
							tmpArrA = writeAN();

							for (String str : tmpArrA) {
								sb.append(str).append("','");
							}
							String sValues= sb.substring(0, sb.length() - 2);

							try {

								String tblName = TBL_OFFER.replace("_", LoadData.getStrAktGJ());
								String sSQLStatementA = "INSERT INTO " + tblName + " VALUES ('" + sValues + ")"; //SQL Befehlszeile

								sqlInsert(sConnDest, sSQLStatementA);

								String sSQLStatementB = "INSERT INTO [tblAN] VALUES ('" + tmpArrA[0] + "')";

								sqlInsert(sConnSource, sSQLStatementB);

							} catch (SQLException | ClassNotFoundException e1) {
								logger.error("error creating new offer - " + e1);
							}

							try {
								SQLmasterData.loadNummernkreis();
							} catch (ClassNotFoundException | SQLException | IOException e2) {
								logger.error("error creating new offer - " + e2);
							}

							JFoverview.loadAngebot(false);
							dispose();
							Runtime.getRuntime().gc();
						}else {
							JOptionPane.showMessageDialog(null, "keine Artikel ausgewählt ...", "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}else {
						JOptionPane.showMessageDialog(null, "Bank nicht ausgewählt ...", "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}else {
					JOptionPane.showMessageDialog(null, "Kunde nicht ausgewählt ...", "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
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

	private static double multi(double a, double b) {
		return (a * b); // Ergebnis zurückliefern
	}

	public static String[] writeAN() {
		Arrays.fill(arrWriteA, "");
		dNetto = 0;
		int x = 1;
		int y = 1;

		arrWriteA[0] = sAnNummer; // Angebotsnummer
		arrWriteA[1] = "1"; // active
		arrWriteA[2] = "0"; // printed
		arrWriteA[3] = "0"; // ordered
		arrWriteA[4] = JFstatusA.getWritten();
		arrWriteA[5] = sAnDatum; // Angebotsdatum
		arrWriteA[6] = sAnReferenz; // Kundenreferenz
		arrWriteA[7] = arrKunde[iSelKunde][1];
		arrWriteA[8] = arrBank[iSelBank][1]; // Index der Bankverbindung

		while(y < (JFnewA.getiNumFrame() * 3)){
			arrWriteA[y + 10] = sPosText[x];
			arrWriteA[y + 11] = Double.toString(dAnzahl[x]);
			arrWriteA[y + 12] = Double.toString(dEinzel[x]);
			dNetto = dNetto + multi(dAnzahl[x], dEinzel[x]);
			x = x + 1;
			y = y + 3;
		}

		arrWriteA[9] = String.valueOf(dNetto); // Netto
		arrWriteA[10] = String.valueOf(iNumFrame);

		arrWriteA[47] = sOptional1;

		return arrWriteA;
	}

	public static ActionListener cbPosListenerA(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
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
						JOptionPane.showMessageDialog(null, "Kunde oder Bank nicht ausgewählt ...", "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
						cbPos.setSelectedIndex(0);
						return;
					}
					sPosText[iNr] = arrArtikel[cbPos.getSelectedIndex()][2];
					if(cbPos.getSelectedItem().toString().length() > 14) {
						sTsTp = arrArtikel[cbPos.getSelectedIndex()][2].substring(arrArtikel[cbPos.getSelectedIndex()][2].length() - 14);
						boolean bSpace = arrArtikel[cbPos.getSelectedIndex()][2].indexOf(" ", arrArtikel[cbPos.getSelectedIndex()][2].indexOf(" ")) != -1;
						if(bSpace) {
							try {
								sTsRk = cutBack2(arrArtikel[cbPos.getSelectedIndex()][2], " ", 1);
							} catch (IOException e1) {
								logger.error("cbPosListenerA(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP)" + e1);
							}
							if(sTsRk.contains("Reisekosten")) {
								txtEP.setEditable(true);
								txtEP.setToolTipText("Eingaben mit ENTER abschließen");
							}else {
								txtEP.setEditable(false);
								txtEP.setToolTipText(null);
							}
							if(sTsTp.contains("Tagespauschale")) {
								dEinzel[iNr] = Double.parseDouble(arrArtikel[cbPos.getSelectedIndex()][3]) * (1 - (dRabatt / 100));
								txtEP.setText(String.format(Locale.GERMANY, "%.2f", dEinzel[iNr]));
								txtEP.setBackground(new Color(152,251,152));
								txtEP.setToolTipText("Rabattierter Preis");
							}else {
								dEinzel[iNr] = Double.parseDouble(arrArtikel[cbPos.getSelectedIndex()][3]);
								txtEP.setText(String.format(Locale.GERMANY, "%.2f", dEinzel[iNr]));
								txtEP.setBackground(Color.WHITE);
								txtEP.setToolTipText(null);
							}
						}
					}else {
						dEinzel[iNr] = Double.parseDouble(arrArtikel[cbPos.getSelectedIndex()][3]);
						txtEP.setText(String.format(Locale.GERMANY, "%.2f", dEinzel[iNr]));
					}
					txtAnz.setEnabled(true);
					txtAnz.setBackground(Color.PINK);
					bArtSel = true;
				}
			}
		};
		return cbPosAction;
	}

	public static void AnzahlActionA(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
		if(txtEP.getText().isEmpty() || txtAnz.getText().isEmpty()) {
			txtAnz.setBackground(Color.PINK);
			return;
		}
		try {
			dAnzahl[iNr] = Double.parseDouble(txtAnz.getText().replace(',', '.'));
			dSumme[iNr] = dEinzel[iNr] * dAnzahl[iNr];
			txtGP.setText(String.format(Locale.GERMANY, "%.2f", dSumme[iNr]));
			txtAnz.setBackground(Color.WHITE);
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Eingabe inkorrekt ...", "Angebot erstellen", JOptionPane.ERROR_MESSAGE);
			txtAnz.setText("");
		}

	}

	public static void EPActionA(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
		if(txtEP.getText().isEmpty()) {
			return;
		}
		try {
			dEinzel[iNr] = Double.parseDouble(txtEP.getText().replace(',', '.'));
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Eingabe inkorrekt ...", "Angebot erstellen", JOptionPane.ERROR_MESSAGE);
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
		JFnewA.sConnDest = sConnDest;
	}

	public static void setsConnSource(String sConnSource) {
		JFnewA.sConnSource = sConnSource;
	}

}
