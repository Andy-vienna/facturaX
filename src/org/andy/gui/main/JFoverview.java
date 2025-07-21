package org.andy.gui.main;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.Tools.saveSettingsApp;
import static org.andy.toolbox.sql.Backup.sqlBackup;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.dataExport.ExcelBill;
import org.andy.code.dataExport.ExcelOffer;
import org.andy.code.main.*;
import org.andy.code.main.overview.CalcTaxData;
import org.andy.code.main.overview.CalcUStData;
import org.andy.code.main.overview.LoadOffer;
import org.andy.code.main.overview.LoadSvTax;
import org.andy.code.main.overview.LoadBillOut;
import org.andy.code.main.overview.LoadExpenses;
import org.andy.code.main.overview.LoadBillIn;
import org.andy.code.main.overview.panels.UStPanel;
import org.andy.code.main.overview.panels.TaxPanel;
import org.andy.code.main.overview.panels.TextPanel;
import org.andy.code.misc.Wrapper;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.bill.in.JFeditRe;
import org.andy.gui.bill.in.JFnewRe;
import org.andy.gui.bill.out.JFnewRa;
import org.andy.gui.bill.out.JFstatusRa;
import org.andy.gui.expenses.JFeditEx;
import org.andy.gui.expenses.JFnewEx;
import org.andy.gui.file.JFfileView;
import org.andy.gui.misc.RoundedBorder;
import org.andy.gui.offer.JFconfirmA;
import org.andy.gui.offer.JFnewA;
import org.andy.gui.offer.JFstatusA;
import org.andy.gui.reminder.JFnewReminder;
import org.andy.gui.settings.JFartikel;
import org.andy.gui.settings.JFbank;
import org.andy.gui.settings.JFdbSettings;
import org.andy.gui.settings.JFgwbValues;
import org.andy.gui.settings.JFkunde;
import org.andy.gui.settings.JFowner;
import org.andy.gui.settings.JFpathMgmt;
import org.andy.gui.settings.JFsepaQR;
import org.andy.gui.settings.JFtaxValues;
import org.andy.gui.settings.JFuserMgmt;
import org.andy.gui.svtax.JFeditSvTax;
import org.andy.gui.svtax.JFnewSvTax;
import org.andy.toolbox.misc.*;

public class JFoverview extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(JFoverview.class);

	private static File lock = new File(System.getProperty("user.dir") + "\\.lock");

	private static String sConn;

	private static final int BASEX = 10;
	private static final int BASEY = 25;
	private static final int BOTTOMY = 5;
	private static final int BUTTONX = 130;
	private static final int BUTTONY = 50;
	private static final int STATEY = 30;

	private static final String[] HEADER_A = { "AN-Nummer", "Status", "Datum", "Referenz", "Kunde", "Netto (EUR)" };
	private static final String[] HEADER_Ra = { "RE-Nummer", "Status", "Datum", "Leistungszeitraum", "Referenz", "Kunde", "Netto (EUR)",
			"USt. (EUR)", "Brutto (EUR)" };
	private static final String[] HEADER_Re = {"RE-Nummer", "RE-Datum", "Kreditor Name", "Kreditor Strasse", "Kreditor PLZ", "Kreditor Ort",
			"Kreditor Land", "Kreditor UID", "Waehrung", "Steuersatz", "Netto", "Anzahlung", "USt.", "Brutto", "Zahlungsziel", "Hinweis", "Dateiname" };
	private static final String[] HEADER_E = { "Id", "Datum", "Bezeichnung", "Netto (EUR)", "Steuersatz (%)", "Steuer (EUR)", "Brutto (EUR)", "Dateiname" };
	private static final String[] HEADER_SVTAX = { "Datum", "Zahlungsempfänger", "Bezeichnung", "Zahllast", "Fälligkeit", "Dateiname" };

	private static String[][] arrYearOffer = new String[100][60], arrYearBillOut = new String[100][60], arrYearBillIn = new String[100][20], arrExpenses = new String[100][9], arrSvTax = new String[100][9];
	private static String[][] sTempA = new String [100][6], sTempRa = new String [100][9], sTempRe = new String [100][17], sTempE = new String [100][8], sTempSvTax = new String [100][6];
	private static boolean[] bActiveA = new boolean[100], bPrintA = new boolean[100], bOrderA = new boolean[100], bActiveRa = new boolean[100], bPrintRa = new boolean[100],
			bMoneyRa = new boolean[100], bPayedRe = new boolean[100], bPayedSvTax = new boolean[100];
	private static int AnzYearOffer, AnzYearBillOut, AnzYearBillIn, AnzExpenses, AnzSvTax;

	private static JFoverview frame;
	private static JPanel contentPane;

	private static JTabbedPane tabPanel;
	private static JPanel pageA, pageRa, pageRe, pageE, pageSvTax, pageOv, pageText;
	
	private static UStPanel panelUSt;
	private static TaxPanel panelP109a;
	private static JScrollPane sPaneUSt, sPaneP109a;
	
	private static JMenu menu1, menu2, menu3, menu5, menu6, menu9;
	private static JMenuItem logoff, backup, exit, newAN, editAN, printAN, stateAN, printAB, newREa, editREa, printREa, stateREa, printRErem,
	userMgmt, pathMgmt, qrCodeSetup, dbSettings, editArtikel, editBank, editKunde, editOwner, editTax, editGwb, aktualisieren, info;

	private static JMenuBar menuBar;
	private static JTable tableA, tableRa, tableRe, tableE, tableSvTax;
	private static JScrollPane sPaneA, sPaneRa, sPaneRe, sPaneE, sPaneSvTax, sPaneText;

	public static JButton btnNewAN, btnPrintAN, btnStateAN, btnPrintAB, btnNewREa, btnPrintREa, btnStateREa, btnPrintRem,
		btnNewREe, btnStateREe, btnNewEx, btnEditEx, btnNewSvTax, btnEditSvTax;

	private static JLabel lblState, lblANopen, lblANclosed, lblREaOpen, lblREaClosed, lblREeOpen, lblREeClosed, lblExNetto, lblExBrutto, lblSvTaxOpen, lblSvTaxClosed;
	
	private static JTextField txtANopen, txtANclosed, txtREaOpen, txtREaClosed, txtREeOpen, txtREeClosed, txtExNetto, txtExBrutto, txtSvTaxOpen, txtSvTaxClosed;
	private static JTextField txtWirtschaftsjahr;
	
	private static JProgressBar progBarA, progBarRa, progBarRe;
	private static JLabel lblProgBarA, lblProgBarRa, lblProgBarRe;

	private static String sLic = null, vZelleRa = null, vZelleRe = null, vZelleA = null, vZelleSvTax = null;
	private static Wrapper<String> vZelleE = new Wrapper<>("");
	private static int iLic = 0, iUserRights = 0, iRowRa, iRowA;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {

					frame = new JFoverview();
					frame.setVisible(true);

					SQLmasterData.loadBaseData();
					SQLmasterData.loadNummernkreis();

				} catch (Exception  e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}

			}
		});
	}

	public JFoverview() {

		sLic = StartUp.getAPP_LICENSE();
		iLic = StartUp.getAPP_MODE();
		
		if(JFmainLogIn.getUserRights().equals("user")) {
			iUserRights = 1; // User
		} else if(JFmainLogIn.getUserRights().equals("superuser")) {
			iUserRights = 2; // SuperUser
		} else if(JFmainLogIn.getUserRights().equals("financialuser")) {
			iUserRights = 5; // FinancialUser
		} else if(JFmainLogIn.getUserRights().equals("admin")) {
			iUserRights = 9; // Admin
		} else {
			iUserRights = 0; // kein User angemeldet
		}

		try {
			setIconImage(SetFrameIcon.getFrameIcon("icon.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}
		setMinimumSize(new Dimension(1000, 700));
		setTitle(StartUp.APP_NAME + StartUp.APP_VERSION + " - Wirtschaftsjahr " + LoadData.getStrAktGJ() + " - " + sLic);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(LoadData.getsSizeX().equals("1920")) {
			setExtendedState(JFoverview.MAXIMIZED_BOTH);
		}else {
			setBounds(100, 100, Integer.valueOf(LoadData.getsSizeX()), Integer.valueOf(LoadData.getsSizeY()));
		}
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//###################################################################################################################################################
		//###################################################################################################################################################

		menu1 = new JMenu("Datei");
		menu2 = new JMenu("Angebot");
		menu3 = new JMenu("Ausgangsrechnung");
		menu5 = new JMenu("Einstellungen");
		menu6 = new JMenu("Ansicht");
		menu9 = new JMenu("Info");

		logoff = new JMenuItem("User abmelden");
		backup = new JMenuItem("Datenbank sichern");
		exit = new JMenuItem("Exit");

		newAN = new JMenuItem("neu");
		editAN = new JMenuItem("bearbeiten");
		printAN = new JMenuItem("drucken");
		stateAN = new JMenuItem("Status ändern");
		printAB = new JMenuItem("Auftragsbestätiung");

		newREa = new JMenuItem("neu");
		editREa = new JMenuItem("bearbeiten");
		printREa = new JMenuItem("drucken");
		stateREa = new JMenuItem("Status ändern");
		printRErem = new JMenuItem("Mahnverfahren");

		userMgmt = new JMenuItem("Benutzerverwaltung");
		pathMgmt = new JMenuItem("Pfadverwaltung");
		qrCodeSetup = new JMenuItem("SEPA QR Setup");
		dbSettings = new JMenuItem("Datenbank Einstellungen");
		editArtikel = new JMenuItem("Artikel bearbeiten");
		editBank = new JMenuItem("Bank bearbeiten");
		editKunde = new JMenuItem("Kunde bearbeiten");
		editOwner = new JMenuItem("Eigene Daten bearbeiten");
		editTax = new JMenuItem("Steuertabelle bearbeiten");
		editGwb = new JMenuItem("Gewinnfreibetragstabelle bearbeiten");

		aktualisieren = new JMenuItem("Aktualisieren");
		info = new JMenuItem("Info");

		try {

			logoff.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("key.png")));
			backup.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("database.png")));
			exit.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("exit.png")));

			newAN.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("new.png")));
			editAN.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			printAN.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("print.png")));
			stateAN.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("state.png")));
			printAB.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("print.png")));

			newREa.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("new.png")));
			editREa.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			printREa.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("print.png")));
			stateREa.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("state.png")));
			printRErem.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("print.png")));

			userMgmt.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("team.png")));
			pathMgmt.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("folder.png")));
			qrCodeSetup.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("qrcode.png")));
			dbSettings.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("database.png")));
			editArtikel.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			editBank.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			editKunde.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			editOwner.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			editTax.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));
			editGwb.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("edit.png")));

			aktualisieren.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("actualize.png")));

			info.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("info.png")));

		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}


		menu1.add(logoff);
		if(iUserRights > 1) {
			menu1.add(backup);
		}
		menu1.addSeparator();
		menu1.add(exit);

		menu2.add(newAN);
		menu2.add(editAN);
		menu2.add(printAN);
		menu2.add(stateAN);
		menu2.add(printAB);

		menu3.add(newREa);
		menu3.add(editREa);
		menu3.add(printREa);
		menu3.add(stateREa);
		menu3.add(printRErem);

		if(iUserRights > 8) {
			menu5.add(userMgmt);
			menu5.add(pathMgmt);
			menu5.add(qrCodeSetup);
			menu5.add(dbSettings);
			menu5.addSeparator();
		}
		if(iUserRights > 1){
			menu5.add(editArtikel);
			menu5.add(editBank);
			menu5.add(editKunde);
		}
		if(iUserRights > 8) {
			menu5.addSeparator();
			menu5.add(editOwner);
			menu5.addSeparator();
			menu5.add(editTax);
			menu5.add(editGwb);
		}
		
		menu6.add(aktualisieren);

		menu9.add(info);

		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);

		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar.add(menu3);
		menuBar.add(menu5);
		menuBar.add(menu6);
		menuBar.add(menu9);

		contentPane.add(menuBar);

		//###################################################################################################################################################
		//###################################################################################################################################################

		if(iLic == 0) { // nicht lizensiert
			backup.setEnabled(false);
			menu2.setEnabled(false);
			menu3.setEnabled(false);
			menu5.setEnabled(false);
		}else if(iLic == 1) { // Demo-Lizenz
			backup.setEnabled(false);
			menu5.setEnabled(false);
			aktualisieren.setEnabled(false);
		}else {
			menu1.setEnabled(true);
			menu2.setEnabled(true);
			menu3.setEnabled(false);
			menu5.setEnabled(true);
		}

		editAN.setEnabled(false);
		printAN.setEnabled(false);
		stateAN.setEnabled(false);
		printAB.setEnabled(false);
		editREa.setEnabled(false);
		printREa.setEnabled(false);
		stateREa.setEnabled(false);
		printRErem.setEnabled(false);

		//###################################################################################################################################################
		//###################################################################################################################################################

		tabPanel = new JTabbedPane(JTabbedPane.TOP);

		//------------------------------------------------------------------------------
		// TAB 1 - Angebote
		//------------------------------------------------------------------------------
		pageA = new JPanel();
		pageA.setLayout(null);
		
		sTempA = LoadOffer.loadAngebot(false);

		tableA = new JTable(sTempA, HEADER_A);
		tableA.setDefaultEditor(Object.class, null);
		tableA.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionDblClickOfferBill(tableA, e);
			}
		});

		sPaneA = new JScrollPane(tableA);
		tableA.setRowSelectionAllowed(false);
		tableA.setDefaultRenderer(Object.class, new TableACellRenderer());
		sPaneA.setAutoscrolls(true);
		pageA.add(sPaneA);

		try {
			btnNewAN = createButton("<html>neues<br>Angebot</html>", "new.png");
			btnPrintAN = createButton("<html>Angebot<br>drucken</html>", "print.png");
			btnStateAN = createButton("<html>AN-Status<br>ändern</html>", "trafficlight.png");
			btnPrintAB = createButton("<html>AB<br>drucken</html>", "print.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		if(iUserRights != 5) { // FinancialUser
			btnNewAN.setEnabled(true);
		}
		
		pageA.add(btnNewAN);
		pageA.add(btnPrintAN);
		pageA.add(btnStateAN);
		pageA.add(btnPrintAB);

		//------------------------------------------------------------------------------
		// TAB 2 - Ausgangsechnungen
		//------------------------------------------------------------------------------
		pageRa = new JPanel();
		pageRa.setLayout(null);

		sTempRa = LoadBillOut.loadAusgangsRechnung(false);
		
		tableRa = new JTable(sTempRa, HEADER_Ra);
		tableRa.setDefaultEditor(Object.class, null);
		tableRa.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionDblClickOfferBill(tableRa, e);
			}
		});

		sPaneRa = new JScrollPane(tableRa);
		tableRa.setRowSelectionAllowed(false);
		tableRa.setDefaultRenderer(Object.class, new TableRaCellRenderer());
		sPaneRa.setAutoscrolls(true);
		pageRa.add(sPaneRa);

		try {
			btnNewREa = createButton("<html>neue<br>Rechnung</html>", "new.png");
			btnPrintREa = createButton("<html>Rechnung<br>drucken</html>", "print.png");
			btnStateREa = createButton("<html>RE-Status<br>ändern</html>", "trafficlight.png");
			btnPrintRem = createButton("<html>Mahn-<br>verfahren</html>", "print.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		if(iUserRights != 5) { // FinancialUser
			btnNewREa.setEnabled(true);
		}

		pageRa.add(btnNewREa);
		pageRa.add(btnPrintREa);
		pageRa.add(btnStateREa);
		pageRa.add(btnPrintRem);

		//------------------------------------------------------------------------------
		// TAB 3 - Eingangsechnungen
		//------------------------------------------------------------------------------
		pageRe = new JPanel();
		pageRe.setLayout(null);
		
		sTempRe = LoadBillIn.loadEingangsRechnung(false);

		tableRe = new JTable(sTempRe, HEADER_Re);
		tableRe.setDefaultEditor(Object.class, null);
		tableRe.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionDblClickInBill(tableRe, e);
			}
		});

		sPaneRe = new JScrollPane(tableRe);
		tableRe.setRowSelectionAllowed(false);
		tableRe.setDefaultRenderer(Object.class, new TableReCellRenderer());
		sPaneRe.setAutoscrolls(true);
		pageRe.add(sPaneRe);

		try {
			btnNewREe = createButton("<html>neue<br>Rechnung</html>", "new.png");
			btnStateREe = createButton("<html>RE-Status<br>ändern</html>", "trafficlight.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnNewREe.setEnabled(true);

		pageRe.add(btnNewREe);
		pageRe.add(btnStateREe);

		//------------------------------------------------------------------------------
		// TAB 4 - Ausgaben
		//------------------------------------------------------------------------------
		pageE = new JPanel();
		pageE.setLayout(null);
		
		sTempE = LoadExpenses.loadAusgaben(false);

		tableE = new JTable(sTempE, HEADER_E);
		tableE.setDefaultEditor(Object.class, null);
		tableE.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionDblClickExpenses(tableE, e);
			}
		});

		sPaneE = new JScrollPane(tableE);
		tableE.setRowSelectionAllowed(false);
		tableE.setDefaultRenderer(Object.class, new TableECellRenderer());
		sPaneE.setAutoscrolls(true);
		pageE.add(sPaneE);

		try {
			btnNewEx = createButton("<html>neue<br>Ausgabe</html>", "new.png");
			btnEditEx = createButton("<html>Ausgabe<br>editieren</html>", "edit.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnNewEx.setEnabled(true);

		pageE.add(btnNewEx);
		pageE.add(btnEditEx);

		//------------------------------------------------------------------------------
		// TAB 5 - SVS und Steuern
		//------------------------------------------------------------------------------
		pageSvTax = new JPanel();
		pageSvTax.setLayout(null);
		
		sTempSvTax = LoadSvTax.loadSvTax(false, null);

		tableSvTax = new JTable(sTempSvTax, HEADER_SVTAX);
		tableSvTax.setDefaultEditor(Object.class, null);
		tableSvTax.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionDblClickSvTax(tableSvTax, e);
			}
		});

		sPaneSvTax = new JScrollPane(tableSvTax);
		tableSvTax.setRowSelectionAllowed(false);
		tableSvTax.setDefaultRenderer(Object.class, new TableSvTaxCellRenderer());
		sPaneSvTax.setAutoscrolls(true);
		pageSvTax.add(sPaneSvTax);

		try {
			btnNewSvTax = createButton("<html>neue<br>Abgabe</html>", "new.png");
			btnEditSvTax = createButton("<html>Status<br>ändern</html>", "trafficlight.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnNewSvTax.setEnabled(true);

		pageSvTax.add(btnNewSvTax);
		pageSvTax.add(btnEditSvTax);
		
		//------------------------------------------------------------------------------
		// TAB 6 - Jahresergebnis
		//------------------------------------------------------------------------------
		pageOv = new JPanel(null);
		
		panelUSt = new UStPanel(); // Erstelle das Panel für die Umsatzsteuerdaten
		
		sPaneUSt = new JScrollPane(panelUSt); // ScrollPane für das Panel
		sPaneUSt.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sPaneUSt.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		panelP109a = new TaxPanel();
		
		sTempSvTax = LoadSvTax.loadSvTax(false, panelP109a);
		
		sPaneP109a = new JScrollPane(panelP109a); // ScrollPane für das Panel
		sPaneP109a.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sPaneP109a.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
		pageOv.add(sPaneUSt); // Füge das ScrollPane zum Panel hinzu
		pageOv.add(sPaneP109a); // Füge das ScrollPane zum Panel hinzu

		//------------------------------------------------------------------------------
		// TAB 7 - Textbausteine
		//------------------------------------------------------------------------------
		pageText = new TextPanel();
		TextPanel.loadTexte();
		
		// ScrollPane für das Panel
		sPaneText = new JScrollPane(pageText);

		//------------------------------------------------------------------------------
		// Tabpanel allgemeines
		//------------------------------------------------------------------------------
		
		if(iUserRights > 0) { // User oder SuperUser oder FinancialUser oder Admin
			tabPanel.addTab("Angebote", pageA);
			tabPanel.addTab("Ausgangsrechnungen", pageRa);
			
			tabPanel.setIconAt(0, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/offer.png")));
			tabPanel.setIconAt(1, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/invoice.png")));
			
			createSumInfoA(); // Summen-Infos Angebote
			createSumInfoRa(); // Summen-Infos Ausgangsrechnungen
		}
		if(iUserRights > 1) { // SuperUser oder FinancialUser oder Admin
			tabPanel.addTab("Eingangsrechnungen", pageRe);
			tabPanel.addTab("Betriebsausgaben", pageE);
			
			tabPanel.setIconAt(2, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/cost.png")));
			tabPanel.setIconAt(3, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/expenses.png")));
			
			createSumInfoRe(); // Summen-Infos Eingangsrechnungen
			createSumInfoE(); // Summen-Infos Ausgaben
		}
		if(iUserRights > 4) { // FinancialUser oder Admin
			tabPanel.addTab("SV und Steuer", pageSvTax);
			tabPanel.addTab("Jahresergebnis", pageOv);
			
			tabPanel.setIconAt(4, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/tax.png")));
			tabPanel.setIconAt(5, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/result.png")));
			
			createSumInfoSvTax(); // Summen-Infos SV und Steuern
		}
		if(iUserRights > 8) { // Admin
			tabPanel.addTab("Textbausteine", sPaneText);
			
			tabPanel.setIconAt(6, new ImageIcon(JFeditAnRe.class.getResource("/org/resources/icons/bausteine.png")));
		}

		// Add the JTabbedPane to the JFrame's content
		tabPanel.setFont(new Font("Tahoma", Font.BOLD, 12));
	
		tabPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = tabPanel.getSelectedIndex();
				if(selectedIndex == 0) {
					menu2.setEnabled(true);
					menu3.setEnabled(false);
					setSumAN(); // Summen-Infos Angebote
				}
				if(selectedIndex == 1) {
					menu2.setEnabled(false);
					menu3.setEnabled(true);
					setSumREa(); // Summen-Infos Ausgangsrechnungen
				}
				if(selectedIndex == 2) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					setSumREe(); // Summen-Infos Eingangsrechnungen
				}
				if(selectedIndex == 3) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					setSumEX(); // Summen-Infos Ausgaben
				}
				if(selectedIndex == 4) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					//setSumSvTax(); // Summen-Infos SV und Steuern
				}
				if(selectedIndex == 5) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					
					setSumEX(); // Summen-Infos Ausgaben
					LoadSvTax.loadSvTax(false, panelP109a);
					CalcUStData.setValuesUVA(panelUSt, AnzYearBillIn, AnzYearBillOut, AnzExpenses, arrYearBillIn, arrYearBillOut, arrExpenses);
					CalcTaxData.setValuesTax(panelP109a, AnzYearBillOut, arrYearBillOut);
				}
				if(selectedIndex == 6) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					TextPanel.loadTexte(); // Textbausteine neu laden
				}
				//actionAct(); auskommentiert wegen Performance-Problemen bei TAB-Wechsel
			}
		});
		contentPane.add(tabPanel);

		createStatus(); // Statuszeile
		setSumAN(); // Summen-Infos Angebote

		// ------------------------------------------------------------------------------
		// Action Listener für JFrame und JPanel
		// ------------------------------------------------------------------------------
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				logger.info("facturaX wird beendet - Version: " + StartUp.APP_VERSION);
				dispose();
			}
		});
		contentPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeGUI(contentPane.getSize());
			}
			@Override
			public void componentShown(ComponentEvent e) {
				resizeGUI(contentPane.getSize());
			}
		});

		// ------------------------------------------------------------------------------
		// Action Listener für Buttons
		// ------------------------------------------------------------------------------
		btnNewAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN1();
			}
		});
		btnPrintAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN3();
			}
		});
		btnStateAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN4();
			}
		});
		btnPrintAB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN5();
			}
		});

		btnNewREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE1();
			}
		});
		btnPrintREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE3();
			}
		});
		btnStateREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE4();
			}
		});
		btnPrintRem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE5();
			}
		});

		btnNewREe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNewREe();
			}
		});
		btnStateREe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionEditREe(vZelleRe);
			}
		});

		btnNewEx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNewEx();
			}
		});
		btnEditEx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionEditEx(vZelleE);
			}
		});

		btnNewSvTax.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNewSvTax();
			}
		});
		btnEditSvTax.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionEditSvTax(vZelleSvTax);
			}
		});

		// ------------------------------------------------------------------------------
		// Action Listener für Menü-Einträge
		// ------------------------------------------------------------------------------
		logoff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				JFmainLogIn.loadLogIn(); // Anmeldefenster einblenden
			}
		});
		backup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sqlBackup(sConn, LoadData.getBackupPath());
				} catch (ClassNotFoundException | SQLException | IOException | InterruptedException e1) {
					Thread.currentThread().interrupt();
					logger.error("error while doing database backup - " + e1);
				}

			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		newAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN1();
			}
		});
		editAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN2();
			}
		});
		printAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN3();
			}
		});
		stateAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN4();
			}
		});
		printAB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAN5();
			}
		});

		newREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE1();
			}
		});
		editREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE2();
			}
		});
		printREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE3();
			}
		});
		stateREa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE4();
			}
		});
		printRErem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRE5();
			}
		});

		userMgmt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFuserMgmt.loadGUI();
			}
		});
		pathMgmt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFpathMgmt.loadGUI();
			}
		});
		qrCodeSetup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFsepaQR.loadGUI(false);
			}
		});
		dbSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFdbSettings.loadGUI(false);
			}
		});
		editArtikel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFartikel.loadGUI();
			}
		});
		editBank.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFbank.loadGUI();
			}
		});
		editKunde.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFkunde.loadGUI();
			}
		});
		editOwner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFowner.loadGUI();
			}
		});
		editTax.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFtaxValues.loadGUI();
			}
		});
		editGwb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFgwbValues.loadGUI();
			}
		});

		aktualisieren.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAct();
			}
		});
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFinfo.loadFrame();
			}
		});

		try {
			lock.createNewFile();
		} catch (IOException e1) {
			logger.error("JFoverview() - " + e1);
		}
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	private void resizeGUI(Dimension xy) {
		int x = xy.width;
		int y = xy.height;

		int iStateTop = y - BOTTOMY - STATEY;
		int iTblAHeight = y - 185;
		int iButtonATop = BASEY + iTblAHeight - 10;
		int iTblRaHeight = y - 185;
		int iButtonRaTop = BASEY + iTblRaHeight - 10;
		int iTblReHeight = y - 185;
		int iButtonReTop = BASEY + iTblRaHeight - 10;
		int iTblEHeight = y - 185;
		int iButtonETop = BASEY + iTblEHeight - 10;
		int iTblSvTaxHeight = y - 185;
		int iButtonSvTaxTop = BASEY + iTblSvTaxHeight - 10;

		menuBar.setBounds(0, 0, x, STATEY - 10);

		tabPanel.setLocation(10, STATEY);
		tabPanel.setSize(x - 20, y - 80);

		//#############################################################################################################
		// Angebote
		
		if(iUserRights > 0) { // User oder SuperUser oder FinancialUser oder Admin
			sPaneA.setBounds(0, 10, x - 20, iTblAHeight);
			tableA.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			int col_RefA = ((x-20)-710);
			setColumnWidths(tableA,120,120,120,col_RefA,200,150);

			btnNewAN.setBounds( 0 * BUTTONX, iButtonATop, BUTTONX, BUTTONY);
			btnPrintAN.setBounds( 1 * BUTTONX, iButtonATop, BUTTONX, BUTTONY);
			btnStateAN.setBounds( 2 * BUTTONX, iButtonATop, BUTTONX, BUTTONY);
			btnPrintAB.setBounds( 3 * BUTTONX, iButtonATop, BUTTONX, BUTTONY);

			lblANopen.setBounds(  4 * BUTTONX, iButtonRaTop + 5, 90, STATEY - 10);
			lblANclosed.setBounds(4 * BUTTONX, iButtonRaTop + 25, 90, STATEY - 10);

			txtANopen.setBounds(  BASEX + (4 * BUTTONX) + 95, iButtonRaTop + 5, 110, STATEY - 10);
			txtANclosed.setBounds(BASEX + (4 * BUTTONX) + 95, iButtonRaTop + 25, 110, STATEY - 10);

			progBarA.setBounds(BASEX + (4 * BUTTONX) + 210, iButtonRaTop, BUTTONX / 2, BUTTONY);
			lblProgBarA.setBounds((BASEX + (4 * BUTTONX) + 210) + (BUTTONX / 2), iButtonRaTop, BUTTONX - 50, BUTTONY);
		}

		//#############################################################################################################
		// Ausgangsrechnungen
		
		if(iUserRights > 0) { // User oder SuperUser oder FinancialUser oder Admin
			sPaneRa.setBounds(0, 10, x - 20, iTblRaHeight);
			tableRa.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			int col_RefRa = ((x-20)-1210);
			setColumnWidths(tableRa,120,120,120,200,col_RefRa,200,150,150,150);

			btnNewREa.setBounds( 0 * BUTTONX, iButtonRaTop, BUTTONX, BUTTONY);
			btnPrintREa.setBounds( 1 * BUTTONX, iButtonRaTop, BUTTONX, BUTTONY);
			btnStateREa.setBounds( 2 * BUTTONX, iButtonRaTop, BUTTONX, BUTTONY);
			btnPrintRem.setBounds( 3 * BUTTONX, iButtonRaTop, BUTTONX, BUTTONY);

			lblREaOpen.setBounds(  4 * BUTTONX, iButtonRaTop + 5, 90, STATEY - 10);
			lblREaClosed.setBounds(4 * BUTTONX, iButtonRaTop + 25, 90, STATEY - 10);

			txtREaOpen.setBounds(  BASEX + (4 * BUTTONX) + 95, iButtonRaTop + 5, 110, STATEY - 10);
			txtREaClosed.setBounds(BASEX + (4 * BUTTONX) + 95, iButtonRaTop + 25, 110, STATEY - 10);

			progBarRa.setBounds(BASEX + (4 * BUTTONX) + 210, iButtonRaTop, BUTTONX / 2, BUTTONY);
			lblProgBarRa.setBounds((BASEX + (4 * BUTTONX) + 210) + (BUTTONX / 2), iButtonRaTop, BUTTONX - 50, BUTTONY);
		}

		//#############################################################################################################
		// Eingangsrechnungen
		
		if(iUserRights > 1) { // SuperUser oder FinancialUser oder Admin
			sPaneRe.setBounds(0, 10, x - 20, iTblReHeight);
			tableRe.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			//int col_RefRe = ((x-20)-1210);
			setColumnWidths(tableRe,100,80,150,150,80,150,80,100,60,60,70,70,70,70,100,250,250);

			btnNewREe.setBounds( 0 * BUTTONX, iButtonReTop, BUTTONX, BUTTONY);
			btnStateREe.setBounds( 1 * BUTTONX, iButtonReTop, BUTTONX, BUTTONY);

			lblREeOpen.setBounds(  2 * BUTTONX, iButtonReTop + 5, 90, STATEY - 10);
			lblREeClosed.setBounds(2 * BUTTONX, iButtonReTop + 25, 90, STATEY - 10);

			txtREeOpen.setBounds(  BASEX + (2 * BUTTONX) + 95, iButtonReTop + 5, 110, STATEY - 10);
			txtREeClosed.setBounds(BASEX + (2 * BUTTONX) + 95, iButtonReTop + 25, 110, STATEY - 10);

			progBarRe.setBounds(BASEX + (2 * BUTTONX) + 210, iButtonReTop, BUTTONX / 2, BUTTONY);
			lblProgBarRe.setBounds((BASEX + (2 * BUTTONX) + 210) + (BUTTONX / 2), iButtonReTop, BUTTONX - 50, BUTTONY);
		}

		//#############################################################################################################
		// Ausgaben
		
		if(iUserRights > 1) { // SuperUser oder FinancialUser oder Admin
			sPaneE.setBounds(0, 10, x - 20, iTblEHeight);
			tableE.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			int col_dyn = ((x-20)-450)/2;
			setColumnWidths(tableE,50,100,col_dyn,100,100,100,100,col_dyn);

			btnNewEx.setBounds( 0 * BUTTONX, iButtonETop, BUTTONX, BUTTONY);
			btnEditEx.setBounds( 1 * BUTTONX, iButtonETop, BUTTONX, BUTTONY);

			lblExNetto.setBounds(  2 * BUTTONX, iButtonETop + 5, 90, STATEY - 10);
			lblExBrutto.setBounds(2 * BUTTONX, iButtonETop + 25, 90, STATEY - 10);

			txtExNetto.setBounds(  BASEX + (2 * BUTTONX) + 95, iButtonETop + 5, 110, STATEY - 10);
			txtExBrutto.setBounds(BASEX + (2 * BUTTONX) + 95, iButtonETop + 25, 110, STATEY - 10);
		}

		//#############################################################################################################
		// Sozialversicherung und Steuer
		
		if(iUserRights > 4) { // FinancialUser oder Admin
			sPaneSvTax.setBounds(0, 10, x - 20, iTblSvTaxHeight);
			tableSvTax.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			//int col_dyn = ((x-20)-450)/2;
			setColumnWidths(tableSvTax,80,400,400,100,80,400);

			btnNewSvTax.setBounds( 0 * BUTTONX, iButtonSvTaxTop, BUTTONX, BUTTONY);
			btnEditSvTax.setBounds( 1 * BUTTONX, iButtonSvTaxTop, BUTTONX, BUTTONY);

			lblSvTaxOpen.setBounds(  2 * BUTTONX, iButtonSvTaxTop + 5, 90, STATEY - 10);
			lblSvTaxClosed.setBounds(2 * BUTTONX, iButtonSvTaxTop + 25, 90, STATEY - 10);

			txtSvTaxOpen.setBounds(  BASEX + (2 * BUTTONX) + 95, iButtonSvTaxTop + 5, 110, STATEY - 10);
			txtSvTaxClosed.setBounds(BASEX + (2 * BUTTONX) + 95, iButtonSvTaxTop + 25, 110, STATEY - 10);
		}

		//#############################################################################################################
		// Jahresergebnis
		
		if(iUserRights > 4) { // FinancialUser oder Admin
			sPaneUSt.setBounds(0, 20, x - 20, 280); // Sichtbarer Bereich im Hauptpanel
			sPaneP109a.setBounds(0, 320, x - 20, 415); // Sichtbarer Bereich im Hauptpanel
		}
		
		//#############################################################################################################

		lblState.setBounds(BASEX, iStateTop+1, x - 100, STATEY-4);
		txtWirtschaftsjahr.setBounds(x - 90, iStateTop-1, 80, STATEY);

		LoadData.setsSizeX(String.valueOf(x));
		LoadData.setsSizeY(String.valueOf(y));

		LoadData.setPrpAppSettings("screenx", LoadData.getsSizeX());
		LoadData.setPrpAppSettings("screeny", LoadData.getsSizeY());
		try {
			saveSettingsApp(LoadData.getPrpAppSettings());
		} catch (IOException e1) {
			logger.error("error saving settings - " + e1);
		}

	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################

	private static void actionAct() {

		try {
			SQLmasterData.loadBaseData();
			SQLmasterData.loadNummernkreis();
		} catch (SQLException | ParseException e1) {
			logger.error("txtWirtschaftsjahr.addActionListener(new ActionListener() - " + e1);
		} catch (IOException e1) {
			logger.error("txtWirtschaftsjahr.addActionListener(new ActionListener() - " + e1);
		} catch (ClassNotFoundException e) {
			logger.error("txtWirtschaftsjahr.addActionListener(new ActionListener() - " + e);
		}
		
		if(iUserRights > 0) { // User oder SuperUser oder FinancialUser oder Admin
			LoadOffer.loadAngebot(false);
			LoadBillOut.loadAusgangsRechnung(false);
		}
		if(iUserRights > 1) { // SuperUser oder FinancialUser oder Admin
			LoadBillIn.loadEingangsRechnung(false);
			LoadExpenses.loadAusgaben(false);
		}
		if(iUserRights > 4) { // FinancialUser oder Admin
			LoadSvTax.loadSvTax(false, panelP109a);
			CalcUStData.setValuesUVA(panelUSt, AnzYearBillIn, AnzYearBillOut, AnzExpenses, arrYearBillIn, arrYearBillOut, arrExpenses);
			CalcTaxData.setValuesTax(panelP109a, AnzYearBillOut, arrYearBillOut);
		}
		if(iUserRights > 8) { // Admin
			TextPanel.loadTexte();
		}

		actualizeWindow();

		frame.setTitle(StartUp.APP_NAME + StartUp.APP_VERSION + " - Wirtschaftsjahr " + LoadData.getStrAktGJ());
		
	}
	
	static void actualizeWindow() {
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################

	static void setSumAN() {

		BigDecimal bdOpenA = new BigDecimal("0.00"), bdClosedA = new BigDecimal("0.00");
		BigDecimal bdTmpOpen, bdTmpClosed;
		BigDecimal bdSumA;
		BigDecimal bdTmpA;
		BigDecimal bdTmpA1;
		BigDecimal bdProzA;

		String sOpenA = null, sClosedA = null;

		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

		try {
			if(AnzYearOffer > 0) {
				for(int x = 1; (x - 1) < AnzYearOffer; x++) {
					String sTmp = arrYearOffer[x][5].trim();
					String sValue = arrYearOffer[x][10].trim();
					if(sTmp.equals(JFstatusA.getWritten()) || sTmp.equals(JFstatusA.getPrinted())) {
						bdTmpOpen = new BigDecimal(sValue);
						bdOpenA = bdOpenA.add(bdTmpOpen);
					}
					if(sTmp.equals(JFstatusA.getOrdered()) || sTmp.equals(JFstatusA.getConfirmed())) {
						bdTmpClosed = new BigDecimal(sValue);
						bdClosedA = bdClosedA.add(bdTmpClosed);
					}
				}
			}

			sOpenA = decimalFormat.format(bdOpenA);
			sClosedA = decimalFormat.format(bdClosedA);

		} catch (NullPointerException e1){
			logger.error("error in calculating offer sum - " + e1);
		}


		try {
			txtANopen.setText(sOpenA + "  EUR");
			txtANclosed.setText(sClosedA + "  EUR");

			bdSumA = bdOpenA.add(bdClosedA);
			bdTmpA = new BigDecimal("100.00");
			if(bdSumA.intValue() > 0) {
				bdTmpA1 = bdTmpA.divide(bdSumA, 8, RoundingMode.HALF_UP);
				bdProzA = bdTmpA1.multiply(bdOpenA);
			}else {
				bdTmpA1 = new BigDecimal("0.00");
				bdProzA = new BigDecimal("0.00");
			}

			int iProzA = bdProzA.intValue();

			progBarA.setValue(iProzA);

			if(iProzA > 30) {
				progBarA.setForeground(Color.RED);
				lblProgBarA.setForeground(Color.RED);
			}else {
				progBarA.setForeground(Color.PINK);
				lblProgBarA.setForeground(Color.BLACK);
			}

			if(iProzA > 0) {
				lblProgBarA.setText("<html>" + decimalFormat.format(bdProzA) + " %<br>offen</html>");
			}else {
				lblProgBarA.setText("<html>nichts<br>offen</html>");
			}

		}catch (Exception e2){
			logger.error("error in calculating offer sum - " + e2);
		}

	}

	static void setSumREa() {

		BigDecimal bdOpenR = new BigDecimal("0.00"), bdClosedR = new BigDecimal("0.00");
		BigDecimal bdTmpOpen, bdTmpClosed;
		BigDecimal bdSumR;
		BigDecimal bdTmpR;
		BigDecimal bdTmpR1;
		BigDecimal bdProzR;

		String sOpenR = null, sClosedR = null;

		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

		try {
			if(AnzYearBillOut > 0) {
				for(int x = 1; (x - 1) < AnzYearBillOut; x++) {
					String sTmp = arrYearBillOut[x][5].trim();
					String sValue = arrYearBillOut[x][12].trim();
					if(sTmp.equals(JFstatusRa.getWritten()) || sTmp.equals(JFstatusRa.getPrinted()) || sTmp.equals(JFstatusRa.getRemprinted())) {
						bdTmpOpen = new BigDecimal(sValue);
						bdOpenR = bdOpenR.add(bdTmpOpen);
					}
					if(sTmp.equals(JFstatusRa.getPayed())) {
						bdTmpClosed = new BigDecimal(sValue);
						bdClosedR = bdClosedR.add(bdTmpClosed);
					}
				}
			}

			sOpenR = decimalFormat.format(bdOpenR);
			sClosedR = decimalFormat.format(bdClosedR);

		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}


		try {
			txtREaOpen.setText(sOpenR + "  EUR");
			txtREaClosed.setText(sClosedR + "  EUR");

			bdSumR = bdOpenR.add(bdClosedR);
			bdTmpR = new BigDecimal("100.00");
			if(bdSumR.intValue() > 0) {
				bdTmpR1 = bdTmpR.divide(bdSumR, 8, RoundingMode.HALF_UP);
				bdProzR = bdTmpR1.multiply(bdOpenR);
			}else {
				bdTmpR1 = new BigDecimal("0.00");
				bdProzR = new BigDecimal("0.00");
			}

			int iProzR = bdProzR.intValue();

			progBarRa.setValue(iProzR);

			if(iProzR > 30) {
				progBarRa.setForeground(Color.RED);
				lblProgBarRa.setForeground(Color.RED);
			}else {
				progBarRa.setForeground(Color.PINK);
				lblProgBarRa.setForeground(Color.BLACK);
			}

			if(iProzR > 0) {
				lblProgBarRa.setText("<html>" + decimalFormat.format(bdProzR) + " %<br>offen</html>");
			}else {
				lblProgBarRa.setText("<html>nichts<br>offen</html>");
			}

		}catch (Exception e){
			logger.error("error in calculating revenue sum - " + e);
		}

	}

	static void setSumREe() {

		BigDecimal bdOpenR = new BigDecimal("0.00"), bdClosedR = new BigDecimal("0.00");
		BigDecimal bdTmpOpen, bdTmpClosed;
		BigDecimal bdSumR;
		BigDecimal bdTmpR;
		BigDecimal bdTmpR1;
		BigDecimal bdProzR;

		String sOpenR = null, sClosedR = null;

		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

		try {
			if(AnzYearBillIn >0) {
				for(int x = 1; (x - 1) < AnzYearBillIn; x++) {
					int iTmp = Integer.parseInt(arrYearBillIn[x][19]);
					String sValue = arrYearBillIn[x][14].trim();
					if(iTmp == 0) {
						bdTmpOpen = new BigDecimal(sValue);
						bdOpenR = bdOpenR.add(bdTmpOpen);
					}
					if(iTmp == 1) {
						bdTmpClosed = new BigDecimal(sValue);
						bdClosedR = bdClosedR.add(bdTmpClosed);
					}
				}
			}

			sOpenR = decimalFormat.format(bdOpenR);
			sClosedR = decimalFormat.format(bdClosedR);

		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}


		try {
			txtREeOpen.setText(sOpenR + "  EUR");
			txtREeClosed.setText(sClosedR + "  EUR");

			bdSumR = bdOpenR.add(bdClosedR);
			bdTmpR = new BigDecimal("100.00");
			if(bdSumR.intValue() > 0) {
				bdTmpR1 = bdTmpR.divide(bdSumR, 8, RoundingMode.HALF_UP);
				bdProzR = bdTmpR1.multiply(bdOpenR);
			}else {
				bdTmpR1 = new BigDecimal("0.00");
				bdProzR = new BigDecimal("0.00");
			}

			int iProzR = bdProzR.intValue();

			progBarRe.setValue(iProzR);

			if(iProzR > 30) {
				progBarRe.setForeground(Color.RED);
				lblProgBarRe.setForeground(Color.RED);
			}else {
				progBarRe.setForeground(Color.PINK);
				lblProgBarRe.setForeground(Color.BLACK);
			}

			if(iProzR > 0) {
				lblProgBarRe.setText("<html>" + decimalFormat.format(bdProzR) + " %<br>offen</html>");
			}else {
				lblProgBarRe.setText("<html>nichts<br>offen</html>");
			}

		}catch (Exception e){
			logger.error("error in calculating revenue sum - " + e);
		}

	}

	static void setSumEX() {

		BigDecimal bdNetto = new BigDecimal("0.00"), bdBrutto = new BigDecimal("0.00");
		BigDecimal bdTmpNetto, bdTmpBrutto;

		String sNetto = null, sBrutto = null;

		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

		try {
			if(AnzExpenses > 0) {
				for(int x = 1; (x - 1) < AnzExpenses; x++) {
					String sValueNetto = arrExpenses[x][3].trim();
					String sValueBrutto = arrExpenses[x][6].trim();

					bdTmpNetto = new BigDecimal(sValueNetto);
					bdNetto = bdNetto.add(bdTmpNetto);

					bdTmpBrutto = new BigDecimal(sValueBrutto);
					bdBrutto = bdBrutto.add(bdTmpBrutto);
				}
			}

			sNetto = decimalFormat.format(bdNetto);
			sBrutto = decimalFormat.format(bdBrutto);
			
			CalcTaxData.setBdExpNetto(bdNetto);

		} catch (NullPointerException e1){
			logger.error("error in calculatin expenses sum - " + e1);
		}

		try {
			txtExNetto.setText(sNetto + "  EUR");
			txtExBrutto.setText(sBrutto + "  EUR");

		}catch (Exception e){
			logger.error("error in calculatin expenses sum - " + e);
		}


	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################

	static void createSumInfoA() {
		lblANopen = new JLabel("Summe offen:");
		lblANopen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageA.add(lblANopen);

		lblANclosed = new JLabel("Summe best.:");
		lblANclosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageA.add(lblANclosed);

		txtANopen = new JTextField("--");
		txtANopen.setEditable(false);
		txtANopen.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtANopen.setForeground(Color.BLUE);
		txtANopen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageA.add(txtANopen);
		txtANopen.setColumns(10);

		txtANclosed = new JTextField("--");
		txtANclosed.setEditable(false);
		txtANclosed.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtANclosed.setForeground(Color.BLUE);
		txtANclosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageA.add(txtANclosed);
		txtANclosed.setColumns(10);

		progBarA = new JProgressBar();
		progBarA.setOrientation(SwingConstants.VERTICAL);
		progBarA.setOpaque(true);
		pageA.add(progBarA);

		lblProgBarA = new JLabel();
		lblProgBarA.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblProgBarA.setHorizontalAlignment(SwingConstants.CENTER);
		pageA.add(lblProgBarA);
	}

	static void createSumInfoRa() {
		lblREaOpen = new JLabel("Summe offen:");
		lblREaOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRa.add(lblREaOpen);

		lblREaClosed = new JLabel("Summe bez.:");
		lblREaClosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRa.add(lblREaClosed);

		txtREaOpen = new JTextField("--");
		txtREaOpen.setEditable(false);
		txtREaOpen.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtREaOpen.setForeground(Color.BLUE);
		txtREaOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRa.add(txtREaOpen);
		txtREaOpen.setColumns(10);

		txtREaClosed = new JTextField("--");
		txtREaClosed.setEditable(false);
		txtREaClosed.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtREaClosed.setForeground(Color.BLUE);
		txtREaClosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRa.add(txtREaClosed);
		txtREaClosed.setColumns(10);

		progBarRa = new JProgressBar();
		progBarRa.setOrientation(SwingConstants.VERTICAL);
		progBarRa.setOpaque(true);
		pageRa.add(progBarRa);

		lblProgBarRa = new JLabel();
		lblProgBarRa.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblProgBarRa.setHorizontalAlignment(SwingConstants.CENTER);
		pageRa.add(lblProgBarRa);
	}

	static void createSumInfoRe() {
		lblREeOpen = new JLabel("Summe offen:");
		lblREeOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRe.add(lblREeOpen);

		lblREeClosed = new JLabel("Summe bez.:");
		lblREeClosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRe.add(lblREeClosed);

		txtREeOpen = new JTextField("--");
		txtREeOpen.setEditable(false);
		txtREeOpen.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtREeOpen.setForeground(Color.BLUE);
		txtREeOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRe.add(txtREeOpen);
		txtREeOpen.setColumns(10);

		txtREeClosed = new JTextField("--");
		txtREeClosed.setEditable(false);
		txtREeClosed.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtREeClosed.setForeground(Color.BLUE);
		txtREeClosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageRe.add(txtREeClosed);
		txtREeClosed.setColumns(10);

		progBarRe = new JProgressBar();
		progBarRe.setOrientation(SwingConstants.VERTICAL);
		progBarRe.setOpaque(true);
		pageRe.add(progBarRe);

		lblProgBarRe = new JLabel();
		lblProgBarRe.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblProgBarRe.setHorizontalAlignment(SwingConstants.CENTER);
		pageRe.add(lblProgBarRe);
	}

	static void createSumInfoE() {
		lblExNetto = new JLabel("Summe netto:");
		lblExNetto.setHorizontalAlignment(SwingConstants.RIGHT);
		pageE.add(lblExNetto);

		lblExBrutto = new JLabel("Summe brutto:");
		lblExBrutto.setHorizontalAlignment(SwingConstants.RIGHT);
		pageE.add(lblExBrutto);

		txtExNetto = new JTextField("--");
		txtExNetto.setEditable(false);
		txtExNetto.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtExNetto.setForeground(Color.BLUE);
		txtExNetto.setHorizontalAlignment(SwingConstants.RIGHT);
		pageE.add(txtExNetto);
		txtExNetto.setColumns(10);

		txtExBrutto = new JTextField("--");
		txtExBrutto.setEditable(false);
		txtExBrutto.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtExBrutto.setForeground(Color.BLUE);
		txtExBrutto.setHorizontalAlignment(SwingConstants.RIGHT);
		pageE.add(txtExBrutto);
		txtExBrutto.setColumns(10);
	}

	static void createSumInfoSvTax() {
		lblSvTaxOpen = new JLabel("Summe offen:");
		lblSvTaxOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageSvTax.add(lblSvTaxOpen);

		lblSvTaxClosed = new JLabel("Summe erledigt:");
		lblSvTaxClosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageSvTax.add(lblSvTaxClosed);

		txtSvTaxOpen = new JTextField("--");
		txtSvTaxOpen.setEditable(false);
		txtSvTaxOpen.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtSvTaxOpen.setForeground(Color.BLUE);
		txtSvTaxOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		pageSvTax.add(txtSvTaxOpen);
		txtSvTaxOpen.setColumns(10);

		txtSvTaxClosed = new JTextField("--");
		txtSvTaxClosed.setEditable(false);
		txtSvTaxClosed.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtSvTaxClosed.setForeground(Color.BLUE);
		txtSvTaxClosed.setHorizontalAlignment(SwingConstants.RIGHT);
		pageSvTax.add(txtSvTaxClosed);
		txtSvTaxClosed.setColumns(10);
	}
	
	static void createStatus() {

		String sStatus = "<html>"
				+ "<b>" + StartUp.getDtNow() + "</b> | " + sLic
				+ " | Angemeldeter Benutzer: <font color='blue'><b>" + LoadData.getStrAktUser() + "</b> (" + JFmainLogIn.getUserRights() + ")</font>"
				+ " | Master-DB: <font color='blue'><b>" + LoadData.getStrDBNameSource() + "</b></font>"
				+ " | Produktiv-DB: <font color='blue'><b>" + LoadData.getStrDBNameDest() + "</b></font>"
				+ "</html>";

		lblState = new JLabel(sStatus);
		lblState.setBorder(new RoundedBorder(10));
		lblState.setHorizontalAlignment(SwingConstants.LEFT);
		lblState.setOpaque(true);

		if(iLic == 1) {
			lblState.setBackground(new Color(255, 246, 143));
		} else if(iLic == 2) {
			lblState.setBackground(new Color(152, 251, 152));
		}else {
			lblState.setBackground(Color.PINK);
		}
		lblState.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblState);

		txtWirtschaftsjahr = new JTextField(LoadData.getStrAktGJ());
		txtWirtschaftsjahr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoadData.setStrAktGJ(txtWirtschaftsjahr.getText());
				LoadData.setPrpAppSettings("year", LoadData.getStrAktGJ());
				try {
					saveSettingsApp(LoadData.getPrpAppSettings());
				} catch (IOException e1) {
					logger.error("error writing financial year " + e1);
				}
				actionAct();
			}
		});
		txtWirtschaftsjahr.setBackground(new Color(176, 224, 230));
		txtWirtschaftsjahr.setHorizontalAlignment(SwingConstants.CENTER);
		txtWirtschaftsjahr.setFont(new Font("Tahoma", Font.BOLD, 12));
		txtWirtschaftsjahr.setForeground(Color.BLACK);
		contentPane.add(txtWirtschaftsjahr);
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################

	static ArrayList<String> searchKundeAll(String sKdNr) {
		List<ArrayList<String>> kundenListe = SQLmasterData.getArrListKunde();

		for (int kd = 0; kd < kundenListe.size(); kd++) {
			ArrayList<String> kunde = kundenListe.get(kd);

			// Prüfen, ob die Kunde-Liste null oder zu kurz ist
			if (kunde == null || kunde.size() < 2 || kunde.get(0) == null) {
				continue; // Überspringe ungültige Einträge
			}

			if (kunde.get(0).equals(sKdNr)) {
				return kunde; // Gib den Kundennamen zurück
			}
		}
		return null;

	}

	static class TableACellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			try {
				if(hasFocus && column == 0) {
					vZelleRa = null;
					vZelleA = value.toString();
					iRowA = row;
					if(iLic == 2 && iUserRights != 5) { // nur bei Lizenz 2 und nicht FinancialUser
						if(sTempA[row][1].equals(JFstatusA.getNotactive())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(false);
							printAB.setEnabled(false);
						}
						if(sTempA[row][1].equals(JFstatusA.getWritten())) {
							btnPrintAN.setEnabled(true);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(true);
							printAN.setEnabled(true);
							stateAN.setEnabled(false);
							printAB.setEnabled(false);
						}
						if(sTempA[row][1].equals(JFstatusA.getPrinted())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(true);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(true);
							printAB.setEnabled(false);
						}
						if(sTempA[row][1].equals(JFstatusA.getOrdered())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(true);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(false);
							printAB.setEnabled(true);
						}
						if(sTempA[row][1].equals(JFstatusA.getConfirmed())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(false);
							printAB.setEnabled(false);
						}
						btnPrintREa.setEnabled(false);
						btnStateREa.setEnabled(false);
						editREa.setEnabled(false);
						printREa.setEnabled(false);
						stateREa.setEnabled(false);
						printRErem.setEnabled(false);
					}

				}else if(hasFocus && column != 0) {
					vZelleA = null;
					iRowA = -1;
					btnPrintAN.setEnabled(false);
					btnStateAN.setEnabled(false);
					btnPrintAB.setEnabled(false);
					editAN.setEnabled(false);
					printAN.setEnabled(false);
					stateAN.setEnabled(false);
					printAB.setEnabled(false);
				}
			}catch (NullPointerException e) {
				vZelleA = null;
				iRowA = -1;
				btnPrintAN.setEnabled(false);
				btnStateAN.setEnabled(false);
				btnPrintAB.setEnabled(false);
				editAN.setEnabled(false);
				printAN.setEnabled(false);
				stateAN.setEnabled(false);
				printAB.setEnabled(false);
			}
			if(column == 0 || column == 1 || column == 2) {
				label.setHorizontalAlignment(SwingConstants.CENTER);
			} else if(column == 5){
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (row % 2 < 1) {
				setBackground(new Color(10, 10, 10, 10));
			} else {
				setBackground(Color.WHITE);
			}
			if(AnzYearOffer == 0) {
				return label;
			}
			if(bPrintA[row] == true) {
				setBackground(new Color(175,238,238));  // hellblau
			}
			if(bOrderA[row] == true) {
				setBackground(new Color(37, 204, 196)); // türkis
			}
			if(bActiveA[row] == false) {
				setBackground(Color.PINK);
			}
			if(sTempA[row][1] != null) {
				if(sTempA[row][1].equals(JFstatusA.getConfirmed())) {
					setBackground(new Color(152, 251, 152)); // hellgrün
				}
			}
			return label;
		}
	}

	static class TableRaCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			try {
				if(hasFocus && column == 0) {
					vZelleRa = value.toString();
					vZelleA = null;
					iRowRa = row;
					if(iLic == 2 && iUserRights != 5) { // nur bei Lizenz 2 und nicht FinancialUser
						if(sTempRa[row][1].equals(JFstatusRa.getNotactive())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(false);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(false);
							printRErem.setEnabled(false);
						}
						if(sTempRa[row][1].equals(JFstatusRa.getWritten())) {
							btnPrintREa.setEnabled(true);
							btnStateREa.setEnabled(false);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(true);
							printREa.setEnabled(true);
							stateREa.setEnabled(false);
							printRErem.setEnabled(false);
						}
						if(sTempRa[row][1].equals(JFstatusRa.getPrinted())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(true);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(true);
						}
						if(sTempRa[row][1].equals(JFstatusRa.getRemprinted())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(true);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(true);
						}
						if(sTempRa[row][1].equals(JFstatusRa.getMahnprinted1())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(true);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(true);
						}
						if(sTempRa[row][1].equals(JFstatusRa.getMahnprinted2())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(false);
						}
						if(sTempRa[row][1].equals(JFstatusRa.getPayed())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(false);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(false);
							printRErem.setEnabled(false);
						}
						btnPrintAN.setEnabled(false);
						btnStateAN.setEnabled(false);
						btnPrintAB.setEnabled(false);
					}

				}else if(hasFocus && column != 0) {
					vZelleRa = null;
					iRowRa = -1;
					btnPrintREa.setEnabled(false);
					btnStateREa.setEnabled(false);
					btnPrintRem.setEnabled(false);
					editREa.setEnabled(false);
					printREa.setEnabled(false);
					stateREa.setEnabled(false);
					printRErem.setEnabled(false);
				}
			}catch (NullPointerException e) {
				vZelleRa = null;
				iRowRa = -1;
				btnPrintREa.setEnabled(false);
				btnStateREa.setEnabled(false);
				btnPrintRem.setEnabled(false);
				editREa.setEnabled(false);
				printREa.setEnabled(false);
				stateREa.setEnabled(false);
				printRErem.setEnabled(false);
			}
			if(row == 0 && column == 9) {
			}
			if(column == 0 || column == 1 || column == 2 || column == 3) {
				label.setHorizontalAlignment(SwingConstants.CENTER);
			} else if(column == 6 || column == 7 || column == 8){
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (row % 2 < 1) {
				setBackground(new Color(10, 10, 10, 10));
			} else {
				setBackground(Color.WHITE);
			}
			if(AnzYearBillOut == 0) {
				return label;
			}
			if(bPrintRa[row] == true) {
				setBackground(new Color(175,238,238)); // hellblau
			}
			if(bMoneyRa[row] == true) {
				setBackground(new Color(152, 251, 152)); // hellgrün
			}
			if(bActiveRa[row] == false) {
				setBackground(Color.PINK);
			}
			if(sTempRa[row][1] != null) {
				if(sTempRa[row][1].equals(JFstatusRa.getRemprinted())) {
					setBackground(Color.YELLOW); // rot
				}
				if(sTempRa[row][1].equals(JFstatusRa.getMahnprinted1())) {
					setBackground(Color.MAGENTA); // rot
				}
				if(sTempRa[row][1].equals(JFstatusRa.getMahnprinted2())) {
					setBackground(Color.RED); // rot
				}
			}
			return label;
		}
	}

	static class TableReCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			try {
				if(hasFocus && column == 0) {
					vZelleRe = value.toString();
					if(sTempRe[row][0] == null) {
						btnStateREe.setEnabled(false);
					}else if(sTempRe[row][0] != null) {
						btnStateREe.setEnabled(true);
					}
				}else if(hasFocus && column != 0) {
					vZelleRe = null;
					btnStateREe.setEnabled(false);
				}
			}catch (NullPointerException e) {
				vZelleRe = null;
				btnStateREe.setEnabled(false);
			}

			if(column == 0 || column == 1 || column == 6 || column == 8 || column == 14) {
				label.setHorizontalAlignment(SwingConstants.CENTER);
			} else if(column == 7 || column == 9 || column == 10 || column == 11 || column == 12 || column == 13){
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (row % 2 < 1) {
				setBackground(new Color(10, 10, 10, 10));
			} else {
				setBackground(Color.WHITE);
			}
			if(AnzYearBillIn == 0) {
				return label;
			}

			if(sTempRe[row][0] != null) {
				DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
				LocalDate datePay = LocalDate.parse(arrYearBillIn[row + 1][15], inputFormat);
				long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
				int daysPayable = 0;

				try {
					daysPayable = Math.toIntExact(daysBetween);
				} catch (Exception e3) {
					logger.error("error in converting long to integer - " + e3);
				}

				if(daysPayable < 0 && bPayedRe[row] == false) {
					setBackground(Color.RED); // rot
				}
				if(daysPayable >= 0 && daysPayable < 3 && bPayedRe[row] == false) {
					setBackground(Color.PINK); // rot
				}

				if(bPayedRe[row] == true) {
					setBackground(new Color(152, 251, 152)); // hellgrün
				}
			}

			return label;
		}
	}

	static class TableECellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			try {
				if(hasFocus && column == 0) {
					vZelleE.value = value.toString();
					if(sTempE[row][0] == null) {
						btnEditEx.setEnabled(false);
					}else if(sTempE[row][0] != null) {
						btnEditEx.setEnabled(true);
					}
				}else if(hasFocus && column != 0) {
					vZelleE = null;
					btnEditEx.setEnabled(false);
				}
			}catch (NullPointerException e) {
				vZelleE = null;
				btnEditEx.setEnabled(false);
			}
			if(column == 0 || column == 1 || column == 4) {
				label.setHorizontalAlignment(SwingConstants.CENTER);
			} else if(column == 3 || column == 5 || column == 6){
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (row % 2 < 1) {
				setBackground(new Color(10, 10, 10, 10));
			} else {
				setBackground(Color.WHITE);
			}
			if(AnzExpenses == 0) {
				return label;
			}
			if(sTempE[row][0] != null && sTempE[row][6] != null){
				setBackground(new Color(152, 251, 152)); // hellgrün
			}else if(sTempE[row][0] != null && sTempE[row][6] == null){
				setBackground(Color.PINK);
			}
			return label;
		}
	}

	static class TableSvTaxCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			try {
				if(hasFocus && column == 0) {
					vZelleSvTax = value.toString();
					if(sTempSvTax[row][0] == null) {
						btnEditSvTax.setEnabled(false);
					}else if(sTempE[row][0] != null) {
						btnEditSvTax.setEnabled(true);
					}
				}else if(hasFocus && column != 0) {
					vZelleSvTax = null;
					btnEditSvTax.setEnabled(false);
				}
			}catch (NullPointerException e) {
				vZelleSvTax = null;
				btnEditSvTax.setEnabled(false);
			}

			if(column == 0 || column == 4) {
				label.setHorizontalAlignment(SwingConstants.CENTER);
			} else if(column == 3){
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (row % 2 < 1) {
				setBackground(new Color(10, 10, 10, 10));
			} else {
				setBackground(Color.WHITE);
			}
			if(AnzSvTax == 0) {
				return label;
			}

			if(sTempSvTax[row][0] != null) {
				DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
				LocalDate datePay = LocalDate.parse(arrSvTax[row + 1][6], inputFormat);
				long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
				int daysPayable = 0;

				try {
					daysPayable = Math.toIntExact(daysBetween);
				} catch (Exception e3) {
					logger.error("error in converting long to integer - " + e3);
				}

				if(daysPayable < 0 && bPayedSvTax[row] == false) {
					setBackground(Color.RED); // rot
				}
				if(daysPayable >= 0 && daysPayable < 3 && bPayedSvTax[row] == false) {
					setBackground(Color.PINK); // rot
				}
				if(bPayedSvTax[row] == true) {
					setBackground(new Color(152, 251, 152)); // hellgrün
				}
			}

			return label;
		}
	}

	static void setColumnWidths(JTable table, int... widths) {
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < widths.length; i++) {
			if (i < columnModel.getColumnCount()) {
				columnModel.getColumn(i).setMaxWidth(widths[i]);
			} else {
				break;
			}
		}
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private void actionDblClickOfferBill(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {

		}
		// Prüfen, ob es ein Doppelklick war
		if (e.getClickCount() == 2 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());

			// Hier die gewünschte Aktion bei Doppelklick ausführen
			if (row != -1 && column != -1) {
				String value = null;
				ArrayList<String> kunde = null;
				try {
					value = table.getValueAt(row, column).toString();
					if(value.contains("AN")) {
						kunde = searchKundeAll(arrYearOffer[row + 1][8]);
					}
					if(value.contains("RE")) {
						kunde = searchKundeAll(arrYearBillOut[row + 1][9]);
					}
					actionFile(value, kunde);
				} catch (Exception e1) {
					logger.debug("actionDblMouseClick(JTable table, MouseEvent e) - Doppelklick: leere Zeile ...");
				}
			}
		}
	}

	private void actionDblClickInBill(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {

		}
		// Prüfen, ob es ein Doppelklick mit BUTTON1(links) war
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Doppelklick ausführen
			if (row != -1 && column != -1) {
				String value = table.getValueAt(row, column).toString();
				if(column == 0) {
					actionEditREe(value);
				}
			}
		}
	}

	private void actionDblClickExpenses(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {

		}
		// Prüfen, ob es ein Doppelklick mit BUTTON1(links) war
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Doppelklick ausführen
			if (row != -1 && column != -1) {
				Wrapper<String> value = new Wrapper<>("");
				value.value = table.getValueAt(row, column).toString();
				if(column == 0) {
					actionEditEx(value);
				}
			}
		}
	}

	private void actionDblClickSvTax(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {

		}
		// Prüfen, ob es ein Doppelklick mit BUTTON1(links) war
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Doppelklick ausführen
			if (row != -1 && column != -1) {
				String value = arrSvTax[row + 1][1];
				if(column == 0) {
					actionEditSvTax(value);
				}
			}
		}
	}

	private void actionFile(String value, ArrayList<String> kunde) {
		if(value == null || kunde == null) {
			return;
		}
		JFfileView.loadGUI(value, kunde);
	}

	private void actionAN1() {
		try {
			JFnewA.showGUI(StartUp.getDtNow(), SQLmasterData.getStrAktAnNr());
		}catch (Exception e) {
			logger.error("actionAN12() - " + e);
		}

	}

	private void actionAN2() {
		if(vZelleA == null) {
			return;
		}
		try {
			if(!vZelleA.isEmpty() && iRowA >= 0) {
				String[] tmpArray = new String[47];
				for(int x = 1; (x - 1) < AnzYearOffer; x++) {
					if(arrYearOffer[x][1].equals(vZelleA)) {
						for(int y = 1; y < 48; y++) {
							tmpArray[y-1] = arrYearOffer[x][y];
						}
					}
				}
				JFeditAnRe.showDialog(tmpArray, "Angebot");
				btnPrintAN.setEnabled(false);
				btnStateAN.setEnabled(false);
				btnPrintAB.setEnabled(false);
			}
			Runtime.getRuntime().gc();
		}catch (Exception e) {
			logger.error("actionAN2() - " + e);
		}

	}

	private void actionAN3() {
		try {
			ExcelOffer.anExport(vZelleA);
		} catch (Exception e1) {
			logger.error("actionAN3() - " + e1);
		}

		String tblName = LoadOffer.getDatatbl().replace("_", LoadData.getStrAktGJ());
		String sStatement = "UPDATE " + tblName + " SET [printState] = '1', [Status] = '" + JFstatusA.getPrinted() + "' WHERE [IdNummer] = '" + vZelleA + "'";

		try {
			sqlUpdate(sConn, sStatement);
		} catch (SQLException | ClassNotFoundException e1) {
			logger.error("error updating offer state to database - " + e1);
		}

		LoadOffer.loadAngebot(false);
		btnPrintAN.setEnabled(false);
		btnStateAN.setEnabled(false);
		btnPrintAB.setEnabled(false);
		Runtime.getRuntime().gc();
	}

	private void actionAN4() {
		if(vZelleA == null) {
			return;
		}
		try {
			if(!vZelleA.isEmpty() && iRowA >= 0) {
				JFstatusA.showDialog(vZelleA, sTempA[iRowA][1]);
				btnPrintAN.setEnabled(false);
				btnStateAN.setEnabled(false);
				btnPrintAB.setEnabled(false);
			}
			Runtime.getRuntime().gc();
		}catch (Exception e) {
			logger.error("actionAN4() - " + e);
		}

	}

	private void actionAN5() {
		try {
			JFconfirmA.showDialog(vZelleA);
			btnPrintAN.setEnabled(false);
			btnStateAN.setEnabled(false);
			btnPrintAB.setEnabled(false);
			Runtime.getRuntime().gc();
		}catch (Exception e) {
			logger.error("actionAN5() - " + e);
		}

	}

	private void actionRE1() {
		try {
			JFnewRa.showGUI(StartUp.getDtNow(), SQLmasterData.getStrAktReNr());
		}catch (Exception e) {
			logger.error("actionRE1() - " + e);
		}

	}

	private void actionRE2() {
		if(vZelleRa == null) {
			return;
		}
		try {
			if(!vZelleRa.isEmpty() && iRowRa >= 0) {
				String[] tmpArray = new String[51];
				for(int x = 1; (x - 1) < AnzYearBillOut; x++) {
					if(arrYearBillOut[x][1].equals(vZelleRa)) {
						for(int y = 1; y < 52; y++) {
							tmpArray[y-1] = arrYearBillOut[x][y];
						}
					}
				}
				JFeditAnRe.showDialog(tmpArray, "Rechnung");
				btnPrintREa.setEnabled(false);
				btnStateREa.setEnabled(false);
			}
			Runtime.getRuntime().gc();
		}catch (Exception e) {
			logger.error("actionRE2() - " + e);
		}

	}

	private void actionRE3() {
		try {
			ExcelBill.reExport(vZelleRa);
		} catch (Exception e1) {
			logger.error("actionRE3() - " + e1);
		}

		String tblName = LoadBillOut.getDatatbl().replace("_", LoadData.getStrAktGJ());
		String sStatement = "UPDATE " + tblName + " SET [printState] = '1', [Status] = 'gedruckt' WHERE [IdNummer] = '" + vZelleRa + "'";

		try {
			sqlUpdate(sConn, sStatement);
		} catch (SQLException | ClassNotFoundException e1) {
			logger.error("error updating bill state to database - " + e1);
		}

		LoadBillOut.loadAusgangsRechnung(false);
		btnPrintREa.setEnabled(false);
		btnStateREa.setEnabled(false);
		Runtime.getRuntime().gc();
	}

	private void actionRE4() {
		if(vZelleRa == null) {
			return;
		}
		try{
			if(!vZelleRa.isEmpty() && iRowRa >= 0) {
				JFstatusRa.showDialog(vZelleRa, sTempRa[iRowRa][1]);
				btnPrintREa.setEnabled(false);
				btnStateREa.setEnabled(false);
			}
			Runtime.getRuntime().gc();
		}catch (Exception e) {
			logger.error("actionRE4() - " + e);
		}

	}

	private void actionRE5() {
		if(vZelleRa == null) {
			return;
		}
		JFnewReminder.showGUI(vZelleRa);
	}

	private void actionNewREe() {
		JFnewRe.loadGUI();
	}

	private void actionEditREe(String sID) {
		JFeditRe.loadGUI(sID);
	}

	private void actionNewEx() {
		JFnewEx.loadGUI();
	}

	private void actionEditEx(Wrapper<String> sId) {
		JFeditEx.loadGUI(sId);
	}

	private void actionNewSvTax() {
		JFnewSvTax.loadGUI();
	}

	private void actionEditSvTax(String sId) {
		JFeditSvTax.loadGUI(sId);
	}

	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
	
	public static File getLock() {
		return lock;
	}

	public static int getButtonx() {
		return BUTTONX;
	}

	public static int getButtony() {
		return BUTTONY;
	}

	public static String[][] getArrYearOffer() {
		return arrYearOffer;
	}

	public static String[][] getArrYearBillOut() {
		return arrYearBillOut;
	}

	public static int getAnzExpenses() {
		return AnzExpenses;
	}

	public static int getAnzSvTax() {
		return AnzSvTax;
	}

	public static void setsConn(String sConn) {
		JFoverview.sConn = sConn;
	}

	public static void setbActiveA(int idx, boolean bActiveA) {
		JFoverview.bActiveA[idx] = bActiveA;
	}

	public static void setbPrintA(int idx, boolean bPrintA) {
		JFoverview.bPrintA[idx] = bPrintA;
	}

	public static void setbOrderA(int idx, boolean bOrderA) {
		JFoverview.bOrderA[idx] = bOrderA;
	}

	public static void setAnzYearOffer(int anzYearOffer) {
		AnzYearOffer = anzYearOffer;
	}

	public static void setbActiveRa(int idx, boolean bActiveRa) {
		JFoverview.bActiveRa[idx] = bActiveRa;
	}

	public static void setbPrintRa(int idx, boolean bPrintRa) {
		JFoverview.bPrintRa[idx] = bPrintRa;
	}

	public static void setbMoneyRa(int idx, boolean bMoneyRa) {
		JFoverview.bMoneyRa[idx] = bMoneyRa;
	}

	public static void setAnzYearBillOut(int anzYearBillOut) {
		AnzYearBillOut = anzYearBillOut;
	}

	public static void setbPayedRe(int idx, boolean bPayedRe) {
		JFoverview.bPayedRe[idx] = bPayedRe;
	}

	public static void setAnzYearBillIn(int anzYearBillIn) {
		AnzYearBillIn = anzYearBillIn;
	}

	public static void setArrYearOffer(String[][] arrYearOffer) {
		JFoverview.arrYearOffer = arrYearOffer;
	}

	public static void setArrYearBillOut(String[][] arrYearBillOut) {
		JFoverview.arrYearBillOut = arrYearBillOut;
	}

	public static void setArrYearBillIn(String[][] arrYearBillIn) {
		JFoverview.arrYearBillIn = arrYearBillIn;
	}

	public static void setArrExpenses(String[][] arrExpenses) {
		JFoverview.arrExpenses = arrExpenses;
	}

	public static void setArrSvTax(String[][] arrSvTax) {
		JFoverview.arrSvTax = arrSvTax;
	}

	public static void setAnzExpenses(int anzExpenses) {
		AnzExpenses = anzExpenses;
	}

	public static void setbPayedSvTax(int idx, boolean bPayedSvTax) {
		JFoverview.bPayedSvTax[idx] = bPayedSvTax;
	}

	public static void setAnzSvTax(int anzSvTax) {
		AnzSvTax = anzSvTax;
	}

}
