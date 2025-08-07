package org.andy.gui.main.create_panels;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.Tools.FormatIBAN;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

import org.andy.code.entityMaster.Artikel;
import org.andy.code.entityMaster.ArtikelRepository;
import org.andy.code.entityMaster.Bank;
import org.andy.code.entityMaster.BankRepository;
import org.andy.code.entityMaster.Kunde;
import org.andy.code.entityMaster.KundeRepository;
import org.andy.code.entityProductive.Angebot;
import org.andy.code.entityProductive.AngebotRepository;
import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;
import org.andy.org.eclipse.wb.swing.FocusTraversalOnArray;

public class CreateOfferPanel extends JPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(CreateBillPanel.class);
	
	JPanel panel = new JPanel(null);
	private Border b;
	
	@SuppressWarnings("unused")
	private TitledBorder border;

	private static JLabel[] lblPos = new JLabel[13];
	@SuppressWarnings("unchecked")
	private static JComboBox<String>[] cbPos = new JComboBox[13];
	private static JTextField[] txtAnz = new JTextField[13];
	private static JTextField[] txtEP = new JTextField[13];
	private static JTextField[] txtGP = new JTextField[13];

	private static BigDecimal[] bdAnzahl = new BigDecimal[13];
	private static BigDecimal[] bdEinzel = new BigDecimal[13];
	private static BigDecimal[] bdSumme = new BigDecimal[13];
	private static String[] sPosText = new String[13];

	private static boolean bKundeSel = false;
	private static boolean bBankSel = false;
	private static boolean bArtSel = false;
	
	private static KundeRepository kundeRepository = new KundeRepository();
	private static List<Kunde> kundeListe = new ArrayList<>();
    private static Kunde kundeLeer = new Kunde();
    private static Kunde kunde;
    private static BankRepository bankRepository = new BankRepository();
	private static List<Bank> bankListe = new ArrayList<>();
	private static Bank bankLeer = new Bank();
	private static Bank bank;
	private static ArtikelRepository artikelRepository = new ArtikelRepository();
	private static List<Artikel> artikelListe = new ArrayList<>();
    private static Artikel artikelLeer = new Artikel();
    private static Artikel artikel;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public CreateOfferPanel() {
    	setLayout(null);
        initContent();
    }

	public void initContent() {
		b = getBorder();
	    if (b instanceof TitledBorder) {
	        this.border = (TitledBorder) b;
	    } else {
	        logger.warn("Kein TitledBorder vorhanden – setsTitel() wird nicht funktionieren.");
	    }
	    
	    fillVector();
		buildPanel();
	}

	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private void buildPanel() {

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

		String[] kundeTexte = kundeListe.stream()
                .map(Kunde::getName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
		String[] bankTexte = bankListe.stream()
                .map(Bank::getBankName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
		String[] artikelTexte = artikelListe.stream()
                .map(Artikel::getText)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
		
		JComboBox<String> cmbKunde = new JComboBox<>(kundeTexte);
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
		JComboBox<String> cmbBank = new JComboBox<>(bankTexte);
		JTextField textBank = new JTextField();
		JTextField textIBAN = new JTextField();
		JTextField textBIC = new JTextField();
		JTextField textNummer = new JTextField(setAnNummer());
		JCheckBox chkPage2 = new JCheckBox("Angebot mit Anlage (Beschreibung)");
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
		chkPage2.setBounds(600, 320, 230, 23);
		textReferenz.setBounds(450, 350, 385, 25);
		btnDoExport.setBounds(850, 305, JFoverview.getButtonx(), JFoverview.getButtony());

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
			add(lblPos[x]);
			cbPos[x] = new JComboBox<>(artikelTexte);
			cbPos[x].setBounds(345,  30 + ((x - 1) * 20), 440, 20);
			cbPos[x].setSelectedIndex(0);
			add(cbPos[x]);
			txtAnz[x] = new JTextField();
			txtAnz[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtAnz[x].setBounds(785, 30 + ((x - 1) * 20), 70, 20);
			txtAnz[x].setEnabled(false);
			add(txtAnz[x]);
			txtEP[x] = new JTextField();
			txtEP[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtEP[x].setBounds(855, 30 + ((x - 1) * 20), 70, 20);
			txtEP[x].setEditable(false);
			add(txtEP[x]);
			txtGP[x] = new JTextField();
			txtGP[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtGP[x].setBounds(925, 30 + ((x - 1) * 20), 70, 20);
			txtGP[x].setEditable(false);
			add(txtGP[x]);
		}

		add(lbl01);
		add(lbl02);
		add(lbl03);
		add(lbl04);
		add(lbl05);
		add(lbl06);
		add(lbl07);
		add(lbl08);
		add(lbl09);
		add(lbl10);
		add(lbl11);
		add(lbl12);
		add(lbl13);
		add(lbl14);
		add(lbl15);
		add(lbl16);
		add(lbl17);
		add(lbl18);
		add(lbl20);
		add(lbl21);
		add(lbl22);
		add(lbl23);
		add(lbl24);
		add(lbl25);
		add(lbl26);
		add(lbl29);
		add(separator1);
		add(separator2);
		add(separator3);
		add(cmbKunde);
		add(textKdNr);
		add(textKdName);
		add(textKdStrasse);
		add(textKdPLZ);
		add(textKdOrt);
		add(textKdLand);
		add(textKdPronom);
		add(textKdDuty);
		add(textKdUID);
		add(textKdUSt);
		add(textKdRabatt);
		add(textKdZahlZiel);
		add(chkRevCharge);
		add(cmbBank);
		add(textBank);
		add(textIBAN);
		add(textBIC);
		add(textNummer);
		add(chkPage2);
		add(textReferenz);
		add(btnDoExport);

		add(datePicker);

		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{cmbKunde, cmbBank, btnDoExport}));

		//###################################################################################################################################################

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
					bKundeSel = false;
				} else {
					int idx = cmbKunde.getSelectedIndex();
		            kunde = kundeListe.get(idx);
					textKdNr.setText(kunde.getId());
					textKdName.setText(kunde.getName());
					textKdStrasse.setText(kunde.getStrasse());
					textKdPLZ.setText(kunde.getPlz());
					textKdOrt.setText(kunde.getOrt());
					textKdLand.setText(kunde.getLand());
					textKdPronom.setText(kunde.getPronomen());
					textKdDuty.setText(kunde.getPerson());
					textKdUID.setText(kunde.getUstid());
					textKdUSt.setText(kunde.getTaxvalue());
					textKdRabatt.setText(kunde.getDeposit());
					textKdZahlZiel.setText(kunde.getZahlungsziel());
					if(textKdUSt.getText().equals("0")) {
						chkRevCharge.setVisible(true);
					}else {
						chkRevCharge.setVisible(false);
					}
					bKundeSel = true;
				}
			}
		});
		cmbBank.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (cmbBank.getSelectedIndex() == 0) {
					textBank.setText("");
					textIBAN.setText("");
					textBIC.setText("");
					bBankSel = false;
				} else {
					int idx = cmbBank.getSelectedIndex();
		            bank = bankListe.get(idx);
					textBank.setText(bank.getBankName());
					textIBAN.setText(FormatIBAN(bank.getIban()));
					textBIC.setText(bank.getBic());
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

		btnDoExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AngebotRepository angebotRepository = new AngebotRepository();
				Angebot angebot = new Angebot();
				BigDecimal summe = BigDecimal.ZERO;

				if(bKundeSel) {
					if(bBankSel) {
						if(bArtSel) {
							if(textReferenz.getText().equals("")) {
								JOptionPane.showMessageDialog(null, "Kundenreferenz fehlt ...", "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							
							angebot.setIdNummer(setAnNummer());
							angebot.setJahr(Integer.valueOf(LoadData.getStrAktGJ()));
							angebot.setDatum(datePicker.getDate());
							angebot.setIdKunde(kunde.getId());
							angebot.setIdBank(bank.getId());
							angebot.setRef(textReferenz.getText());
							angebot.setRevCharge(chkRevCharge.isSelected() ? 1 : 0);
							angebot.setPage2(chkPage2.isSelected() ? 1 : 0);
							
							angebot.setAnzPos(BigDecimal.valueOf(setNum()));
							
							angebot.setArt01(sPosText[1]); angebot.setMenge01(bdAnzahl[1]); angebot.setePreis01(bdEinzel[1]);
							angebot.setArt02(sPosText[2]); angebot.setMenge02(bdAnzahl[2]); angebot.setePreis02(bdEinzel[2]);
							angebot.setArt03(sPosText[3]); angebot.setMenge03(bdAnzahl[3]); angebot.setePreis03(bdEinzel[3]);
							angebot.setArt04(sPosText[4]); angebot.setMenge04(bdAnzahl[4]); angebot.setePreis04(bdEinzel[4]);
							angebot.setArt05(sPosText[5]); angebot.setMenge05(bdAnzahl[5]); angebot.setePreis05(bdEinzel[5]);
							angebot.setArt06(sPosText[6]); angebot.setMenge06(bdAnzahl[6]); angebot.setePreis06(bdEinzel[6]);
							angebot.setArt07(sPosText[7]); angebot.setMenge07(bdAnzahl[7]); angebot.setePreis07(bdEinzel[7]);
							angebot.setArt08(sPosText[8]); angebot.setMenge08(bdAnzahl[8]); angebot.setePreis08(bdEinzel[8]);
							angebot.setArt09(sPosText[9]); angebot.setMenge09(bdAnzahl[9]); angebot.setePreis09(bdEinzel[9]);
							angebot.setArt10(sPosText[10]); angebot.setMenge10(bdAnzahl[10]); angebot.setePreis10(bdEinzel[10]);
							angebot.setArt11(sPosText[11]); angebot.setMenge11(bdAnzahl[11]); angebot.setePreis11(bdEinzel[11]);
							angebot.setArt12(sPosText[12]); angebot.setMenge12(bdAnzahl[12]); angebot.setePreis12(bdEinzel[12]);
							
							for (int i = 0; i < setNum(); i++) {
								summe = summe.add(bdSumme[i + 1]);
							}
							angebot.setNetto(summe);
							
							angebot.setUst(new BigDecimal("0.00")); // nicht benötigt
							angebot.setBrutto(new BigDecimal("0.00")); // nicht benötigt
							angebot.setlZeitr(" "); // nicht benötigt
							
							angebot.setState(1); // Status: erstellt
							
							angebotRepository.save(angebot); // Angebot in DB schreiben

							JFoverview.actScreen();
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
	// ActionListener
	//###################################################################################################################################################

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
					int idx = cbPos.getSelectedIndex();
		            artikel = artikelListe.get(idx);
					sPosText[iNr] = artikel.getText();
					bdEinzel[iNr] = artikel.getWert();
					txtEP.setText(bdEinzel[iNr].toPlainString());
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
			bdAnzahl[iNr] = new BigDecimal(txtAnz.getText().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
			bdEinzel[iNr] = new BigDecimal(txtEP.getText().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
			bdSumme[iNr] = bdEinzel[iNr].multiply(bdAnzahl[iNr]).setScale(2, RoundingMode.HALF_UP);
			txtGP.setText(String.format(Locale.GERMANY, "%.2f", bdSumme[iNr]));
			txtAnz.setBackground(Color.WHITE);
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Eingabe inkorrekt ...", "Rechnung erstellen", JOptionPane.ERROR_MESSAGE);
			txtAnz.setText("");
		}
	
	}
	
	public static void EPActionA(final int iNr, final JComboBox<?> cbPos, final JTextField txtAnz, final JTextField txtEP, final JTextField txtGP) {
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
	// private Teil
	//###################################################################################################################################################
	
	private static void fillVector() {
		
		kundeListe.clear();
        kundeListe.add(kundeLeer); // falls du immer einen Dummy-Eintrag vorne willst        
        kundeListe.addAll(kundeRepository.findAll());
		
		bankListe.clear();
        bankListe.add(bankLeer); // falls du immer einen Dummy-Eintrag vorne willst        
        bankListe.addAll(bankRepository.findAll());
        
        artikelListe.clear();
        artikelListe.add(artikelLeer); // falls du immer einen Dummy-Eintrag vorne willst        
        artikelListe.addAll(artikelRepository.findAll());
        
	}
	
	private static String setAnNummer() {
		AngebotRepository angebotRepository = new AngebotRepository();
		int maxAnNummer = angebotRepository.findMaxNummerByJahr(Integer.parseInt(LoadData.getStrAktGJ()));
		
		return "AN-" + LoadData.getStrAktGJ() + "-" + String.format("%04d", maxAnNummer + 1);
	}

	private static int setNum() {
		int Num = 1;
		while(cbPos[Num].getSelectedIndex() > 0) {
			Num = Num + 1;
		}
		return Num - 1;
	}

}
