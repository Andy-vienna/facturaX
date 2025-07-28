package org.andy.gui.main;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.Tools.saveSettingsApp;
import static org.andy.toolbox.sql.Backup.sqlBackup;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.dataExport.ExcelBill;
import org.andy.code.dataExport.ExcelOffer;
import org.andy.code.main.*;
import org.andy.code.main.overview.result.TaxData;
import org.andy.code.main.overview.result.UStData;
import org.andy.code.main.overview.result.RecStateData;
import org.andy.code.main.overview.table.LoadPurchase;
import org.andy.code.main.overview.table.LoadBillOut;
import org.andy.code.main.overview.table.LoadExpenses;
import org.andy.code.main.overview.table.LoadOffer;
import org.andy.code.main.overview.table.LoadSvTax;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.bill.out.JFnewRa;
import org.andy.gui.bill.out.JFstatusRa;
import org.andy.gui.file.JFfileView;
import org.andy.gui.main.overview_panels.SumPanel;
import org.andy.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.gui.main.overview_panels.edit_panels.EditPanelFactory;
import org.andy.gui.main.overview_panels.edit_panels.ExpensesPanel;
import org.andy.gui.main.overview_panels.edit_panels.OfferPanel;
import org.andy.gui.main.overview_panels.edit_panels.PurchasePanel;
import org.andy.gui.main.overview_panels.edit_panels.SvTaxPanel;
import org.andy.gui.main.result_panels.RecStatePanel;
import org.andy.gui.main.result_panels.TaxPanel;
import org.andy.gui.main.result_panels.UStPanel;
import org.andy.gui.main.settings_panels.TextPanel;
import org.andy.gui.main.table_panels.CreatePanel;
import org.andy.gui.main.table_panels.CreateTable;
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
import org.andy.toolbox.misc.*;

public class JFoverview extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(JFoverview.class);

	private static File lock = new File(System.getProperty("user.dir") + "\\.lock");

	private static String sConn;

	private static final int BASEX = 10;
	private static final int BOTTOMY = 5;
	private static final int BUTTONX = 130;
	private static final int BUTTONY = 50;
	private static final int STATEY = 30;

	private static final String[] HEADER_AN = { "AN-Nummer", "Status", "Datum", "Referenz", "Kunde", "Netto (EUR)" };
	private static final String[] HEADER_RE = { "RE-Nummer", "Status", "Datum", "Leistungszeitraum", "Referenz", "Kunde", "Netto (EUR)",
			"USt. (EUR)", "Brutto (EUR)" };
	private static final String[] HEADER_PU = {"RE-Datum","RE-Nummer",  "Kreditor Name", "Kreditor Strasse", "Kreditor PLZ", "Kreditor Ort",
			"Kreditor Land", "Kreditor UID", "Waehrung", "Steuersatz", "Netto", "USt.", "Brutto", "Anzahlung", "Zahlungsziel", "Hinweis", "Dateiname" };
	private static final String[] HEADER_EX = { "Id", "Datum", "Bezeichnung", "Netto (EUR)", "Steuersatz (%)", "Steuer (EUR)", "Brutto (EUR)", "Dateiname" };
	private static final String[] HEADER_ST = { "Datum", "Zahlungsempfänger", "Bezeichnung", "Zahllast", "Fälligkeit", "Dateiname" };

	private static String[][] arrYearAN = new String[100][60], arrYearRE = new String[100][60], arrYearPU = new String[100][20], arrYearEX = new String[100][9], arrYearST = new String[100][9];
	private static String[][] sTempAN = new String [100][6], sTempRE = new String [100][9], sTempPU = new String [100][17], sTempEX = new String [100][8], sTempST = new String [30][6];
	private static boolean[] bActiveAN = new boolean[100], bPrintAN = new boolean[100], bOrderAN = new boolean[100], bActiveRE = new boolean[100], bPrintRE = new boolean[100],
			bMoneyRE = new boolean[100], bPayedPU = new boolean[100], bPayedST = new boolean[30];
	private static int AnzYearAN, AnzYearRE, AnzYearPU, AnzYearEX, AnzYearST;

	private static JFoverview frame;
	private static JTabbedPane tabPanel;
	private static JPanel contentPane, pageAN, pageRE, pagePU, pageEX, pageST, pageOv, pageText, pageErg, pageSetting;
	private static EditPanel offerPanel, billOutPanel, purchasePanel, svTaxPanel, expensesPanel;
	private static SumPanel infoAN, infoRE, infoPU, infoEX, infoST;
	private static UStPanel panelUSt;
	private static RecStatePanel panelZM;
	private static TaxPanel panelP109a;
	private static JScrollPane sPaneText, sPaneErg, sPaneSetting;
	private static CreateTable<Object> sPaneAN, sPaneRE, sPanePU, sPaneEX, sPaneST;
	
	private static JMenuBar menuBar;
	private static JMenu menu1, menu2, menu3, menu5, menu6, menu9;
	private static JMenuItem logoff, backup, exit, newAN, editAN, printAN, stateAN, printAB, newREa, editREa, printREa, stateREa, printRErem,
	userMgmt, pathMgmt, qrCodeSetup, dbSettings, editArtikel, editBank, editKunde, editOwner, editTax, editGwb, aktualisieren, info;

	public static JButton btnNewAN, btnPrintAN, btnStateAN, btnPrintAB, btnNewREa, btnPrintREa, btnStateREa, btnPrintRem;
	
	private static JLabel lblState;
	private static JTextField txtWirtschaftsjahr;

	private static String sLic = null, vZelleRa = null, vZelleA = null;
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
		sTempAN = LoadOffer.loadAngebot(false);
		
		offerPanel = EditPanelFactory.create("AN");
		//offerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Tabelle mit ScrollPane anlegen
		sPaneAN = new CreateTable<>(sTempAN, HEADER_AN, new TableANCellRenderer());
		sPaneAN.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionDblClickOfferBill(sPaneAN.getTable(), e); } });
		sPaneAN.setColumnWidths(new int[] {200,200,200,750,200,200});

		// Buttons anlegen
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
		
		// InfoPanel anlegen
		infoAN = new SumPanel(new String[] {"Summe offen:", "Summe best.:"}, true);
		
		pageAN = new CreatePanel(sPaneAN, offerPanel, new JButton[] {btnNewAN, btnPrintAN, btnStateAN, btnPrintAB}, infoAN);
		
		//------------------------------------------------------------------------------
		// TAB 2 - Ausgangsechnungen
		//------------------------------------------------------------------------------
		sTempRE = LoadBillOut.loadAusgangsRechnung(false);
		
		billOutPanel = EditPanelFactory.create("REa");
		//billOutPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Tabelle mit ScrollPane anlegen
		sPaneRE = new CreateTable<>(sTempRE, HEADER_RE, new TableRECellRenderer());
		sPaneRE.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionDblClickOfferBill(sPaneRE.getTable(), e); } });
		sPaneRE.setColumnWidths(new int[] {120,120,120,200,650,200,150,150,150});

		// Buttons anlegen
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
		
		// InfoPanel anlegen
		infoRE = new SumPanel(new String[] {"Summe offen:", "Summe bez.:"}, true);
		
		pageRE = new CreatePanel(sPaneRE, billOutPanel, new JButton[] {btnNewREa, btnPrintREa, btnStateREa, btnPrintRem},	infoRE);

		//------------------------------------------------------------------------------
		// TAB 3 - Einkaufsrechnungen
		//------------------------------------------------------------------------------
		sTempPU = LoadPurchase.loadEinkaufsRechnung(false);
		
		purchasePanel = EditPanelFactory.create("PU");
		if (purchasePanel instanceof PurchasePanel pup) {
			pup.setsTitel("neuen Beleg anlegen");
			pup.setBtnText(0, "..."); pup.setBtnText(1, "save");
		}
		
		// Tabelle mit ScrollPane anlegen
		sPanePU = new CreateTable<>(sTempPU, HEADER_PU, new TablePUCellRenderer());
		sPanePU.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionClickPU(sPanePU.getTable(), e); } });
		sPanePU.setColumnWidths(new int[] {100,80,150,150,80,150,80,100,60,60,70,70,70,70,100,250,200});
		
		// InfoPanel anlegen
		infoPU = new SumPanel(new String[] {"Netto:", "Brutto:"}, false);
		
		pagePU = new CreatePanel(sPanePU, purchasePanel, null, infoPU);
		
		//------------------------------------------------------------------------------
		// TAB 4 - Ausgaben
		//------------------------------------------------------------------------------
		sTempEX = LoadExpenses.loadAusgaben(false);
		
		expensesPanel = EditPanelFactory.create("EX");
		if (expensesPanel instanceof ExpensesPanel ep) {
			ep.setsTitel("neuen Beleg anlegen");
			ep.setBtnText(0, "..."); ep.setBtnText(1, "save");
		}
		
		// Tabelle mit ScrollPane anlegen
		sPaneEX = new CreateTable<>(sTempEX, HEADER_EX, new TableEXCellRenderer());
		sPaneEX.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionClickEX(sPaneEX.getTable(), e); } });
		sPaneEX.setColumnWidths(new int[] {50,100,650,100,100,100,100,650});
		
		// InfoPanel anlegen
		infoEX = new SumPanel(new String[] {"Netto:", "Brutto:"}, false);

		pageEX = new CreatePanel(sPaneEX, expensesPanel, null, infoEX);

		//------------------------------------------------------------------------------
		// TAB 5 - SVS und Steuern
		//------------------------------------------------------------------------------
		sTempST = LoadSvTax.loadSvTax(false, null);
		
		svTaxPanel = EditPanelFactory.create("SVT");
		if (svTaxPanel instanceof SvTaxPanel svt) {
			svt.setsTitel("neuen Beleg anlegen");
			svt.setBtnText(0, "..."); svt.setBtnText(1, "save");
		}
		
		// Tabelle mit ScrollPane anlegen
		sPaneST = new CreateTable<>(sTempST, HEADER_ST, new TableSTCellRenderer());
		sPaneST.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionClickST(sPaneST.getTable(), e); } });
		sPaneST.setColumnWidths(new int[] {120,500,500,120,120,500});
		
		// InfoPanel anlegen
		infoST = new SumPanel(new String[] {"SV:", "Steuer:"}, false);
		
		pageST = new CreatePanel(sPaneST, svTaxPanel, null, infoST);

		//------------------------------------------------------------------------------
		// TAB 6 - Jahresergebnis
		//------------------------------------------------------------------------------
		panelUSt = new UStPanel();
		panelUSt.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		panelZM = new RecStatePanel();
		panelZM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panelP109a = new TaxPanel();
		panelP109a.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		pageErg = new JPanel(new GridBagLayout());
		GridBagConstraints erg = new GridBagConstraints();
		erg.gridx = 0; erg.weightx = 1.0; erg.fill = GridBagConstraints.HORIZONTAL;
		
		// Erste Zeile
		erg.gridy = 0;
		erg.weighty = 0;
		pageErg.add(panelUSt, erg);

		// Zweite Zeile
		erg.gridy = 1;
		erg.weighty = 0;
		pageErg.add(panelZM, erg);
		
		// Dritte Zeile
		erg.gridy = 2;
		erg.weighty = 0;
		pageErg.add(panelP109a, erg);
		
		erg.gridy = 2;
		erg.weighty = 1.0;
		erg.fill = GridBagConstraints.VERTICAL;
		pageErg.add(Box.createVerticalGlue(), erg);

		sPaneErg = new JScrollPane(pageErg);
		pageOv = new JPanel(new BorderLayout());
		pageOv.add(sPaneErg, BorderLayout.CENTER);

		//------------------------------------------------------------------------------
		// TAB 7 - Einstellungen
		//------------------------------------------------------------------------------
		pageSetting = new JPanel();
		
		sPaneSetting = new JScrollPane(pageSetting);
		
		//------------------------------------------------------------------------------
		// TAB 8 - Textbausteine
		//------------------------------------------------------------------------------
		pageText = new TextPanel();
		TextPanel.loadTexte();
		
		// ScrollPane für das Panel
		sPaneText = new JScrollPane(pageText);

		//------------------------------------------------------------------------------
		// Tabpanel allgemeines
		//------------------------------------------------------------------------------
		
		if(iUserRights > 0) { // User oder SuperUser oder FinancialUser oder Admin
			tabPanel.addTab("Angebote", pageAN);
			tabPanel.addTab("Rechnungen", pageRE);
			
			tabPanel.setIconAt(0, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/offer.png")));
			tabPanel.setIconAt(1, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/invoice.png")));
		}
		if(iUserRights > 1) { // SuperUser oder FinancialUser oder Admin
			tabPanel.addTab("Einkauf", pagePU);
			tabPanel.addTab("Betriebsausgaben", pageEX);
			
			tabPanel.setIconAt(2, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/purchase.png")));
			tabPanel.setIconAt(3, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/expenses.png")));
		}
		if(iUserRights > 4) { // FinancialUser oder Admin
			tabPanel.addTab("SV und Steuer", pageST);
			tabPanel.addTab("Jahresergebnis", pageOv);
			
			tabPanel.setIconAt(4, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/tax.png")));
			tabPanel.setIconAt(5, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/result.png")));
		}
		if(iUserRights > 8) { // Admin
			tabPanel.addTab("Einstellungen", sPaneSetting);
			tabPanel.addTab("Textbausteine", sPaneText);
			
			tabPanel.setIconAt(6, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/config.png")));
			tabPanel.setIconAt(7, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/bausteine.png")));
		}

		// Add the JTabbedPane to the JFrame's content
		tabPanel.setFont(new Font("Tahoma", Font.BOLD, 12));
	
		// ------------------------------------------------------------------------------
		// Action Listener für TabPanel
		// ------------------------------------------------------------------------------
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
					setSumRE(); // Summen-Infos Ausgangsrechnungen
				}
				if(selectedIndex == 2) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					setSumPU(); // Summen-Infos Eingangsrechnungen
				}
				if(selectedIndex == 3) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					setSumEX(); // Summen-Infos Ausgaben
				}
				if(selectedIndex == 4) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					setSumST(); // Summen-Infos SV und Steuern
				}
				if(selectedIndex == 5) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					
					BigDecimal bdExNetto = setSumEX(); // Summen-Infos Ausgaben
					BigDecimal bdReNetto = setSumPU(); // Summen-Infos Eingangsrechnungen
					UStData.setValuesUVA(panelUSt, AnzYearPU, AnzYearRE, AnzYearEX, arrYearPU, arrYearRE, arrYearEX);
					RecStateData.RecState(panelZM, AnzYearRE, arrYearRE);
					LoadSvTax.loadSvTax(false, panelP109a);
					TaxData.setValuesTax(panelP109a, AnzYearRE, arrYearRE, bdExNetto, bdReNetto);
					
				}
				if(selectedIndex == 6) {
					menu2.setEnabled(false);
					menu3.setEnabled(false);
					
				}
				if(selectedIndex == 7) {
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
	
	public static void actScreen() {
		actionAct();
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	private void resizeGUI(Dimension xy) {
		int x = xy.width;
		int y = xy.height;

		int iStateTop = y - BOTTOMY - STATEY;

		menuBar.setBounds(0, 0, x, STATEY - 10);

		tabPanel.setLocation(10, STATEY);
		tabPanel.setSize(x - 20, y - 80);
		
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
		
		BigDecimal bdReNetto = BigDecimal.ZERO, bdExNetto = BigDecimal.ZERO;

		try {
			SQLmasterData.loadBaseData();
			SQLmasterData.loadNummernkreis();
		} catch (SQLException | ParseException e1) {
			logger.error("actionAct() - " + e1);
		} catch (IOException e2) {
			logger.error("actionAct() - " + e2);
		} catch (ClassNotFoundException e3) {
			logger.error("actionAct() - " + e3);
		}
		
		if(iUserRights > 0) { // User oder SuperUser oder FinancialUser oder Admin
			LoadOffer.loadAngebot(false);
			LoadBillOut.loadAusgangsRechnung(false);
			setSumAN(); // Summen-Infos Angebote
			setSumRE(); // Summen-Infos Ausgangsrechnungen
		}
		if(iUserRights > 1) { // SuperUser oder FinancialUser oder Admin
			LoadPurchase.loadEinkaufsRechnung(false);
			LoadExpenses.loadAusgaben(false);
			setSumPU(); // Summen-Infos Eingangsrechnungen
			bdReNetto = setSumPU(); // Summen-Infos Eingangsrechnungen
			bdExNetto = setSumEX(); // Summen-Infos Ausgaben
		}
		if(iUserRights > 4) { // FinancialUser oder Admin
			UStData.setValuesUVA(panelUSt, AnzYearPU, AnzYearRE, AnzYearEX, arrYearPU, arrYearRE, arrYearEX);
			RecStateData.RecState(panelZM, AnzYearRE, arrYearRE);
			LoadSvTax.loadSvTax(false, panelP109a);
			TaxData.setValuesTax(panelP109a, AnzYearRE, arrYearRE, bdExNetto, bdReNetto);
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

		BigDecimal bdOpen = new BigDecimal("0.00"), bdClosed = new BigDecimal("0.00");
		BigDecimal bdTmpOpen, bdTmpClosed;

		try {
			if(AnzYearAN > 0) {
				for(int x = 1; (x - 1) < AnzYearAN; x++) {
					String sTmp = arrYearAN[x][5].trim();
					String sValue = arrYearAN[x][10].trim();
					if(sTmp.equals(JFstatusA.getWritten()) || sTmp.equals(JFstatusA.getPrinted())) {
						bdTmpOpen = new BigDecimal(sValue);
						bdOpen = bdOpen.add(bdTmpOpen);
					}
					if(sTmp.equals(JFstatusA.getOrdered()) || sTmp.equals(JFstatusA.getConfirmed())) {
						bdTmpClosed = new BigDecimal(sValue);
						bdClosed = bdClosed.add(bdTmpClosed);
					}
				}
			}
			Double dOpen = bdOpen.doubleValue();
			Double dClosed = bdClosed.doubleValue();
			infoAN.setTxtSum(0, dOpen);
			infoAN.setTxtSum(1, dClosed);

		} catch (NullPointerException e1){
			logger.error("error in calculating offer sum - " + e1);
		}
		infoAN.setProgressBar(prozent(bdOpen, bdClosed));
	}

	static void setSumRE() {

		BigDecimal bdOpen = new BigDecimal("0.00"), bdClosed = new BigDecimal("0.00");
		BigDecimal bdTmpOpen, bdTmpClosed;
		
		try {
			if(AnzYearRE > 0) {
				for(int x = 1; (x - 1) < AnzYearRE; x++) {
					String sTmp = arrYearRE[x][5].trim();
					String sValue = arrYearRE[x][12].trim();
					if(sTmp.equals(JFstatusRa.getWritten()) || sTmp.equals(JFstatusRa.getPrinted()) || sTmp.equals(JFstatusRa.getRemprinted())) {
						bdTmpOpen = new BigDecimal(sValue);
						bdOpen = bdOpen.add(bdTmpOpen);
					}
					if(sTmp.equals(JFstatusRa.getPayed())) {
						bdTmpClosed = new BigDecimal(sValue);
						bdClosed = bdClosed.add(bdTmpClosed);
					}
				}
			}
			Double dOpen = bdOpen.doubleValue();
			Double dClosed = bdClosed.doubleValue();
			infoRE.setTxtSum(0, dOpen);
			infoRE.setTxtSum(1, dClosed);
		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}
		infoRE.setProgressBar(prozent(bdOpen, bdClosed));
	}

	static BigDecimal setSumPU() {

		BigDecimal bdNetto = new BigDecimal("0.00"), bdBrutto = new BigDecimal("0.00");

		try {
			if(AnzYearPU >0) {
				for(int x = 1; (x - 1) < AnzYearPU; x++) {
					String sNetto = arrYearPU[x][11].trim();
					String sBrutto = arrYearPU[x][13].trim();

					bdNetto = bdNetto.add(new BigDecimal(sNetto));
					bdBrutto = bdBrutto.add(new BigDecimal(sBrutto));
				}
			}
			Double dOpen = bdNetto.doubleValue();
			Double dClosed = bdBrutto.doubleValue();
			infoPU.setTxtSum(0, dOpen);
			infoPU.setTxtSum(1, dClosed);
		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}
		infoPU.setProgressBar(prozent(bdNetto, bdBrutto));
		return bdNetto;
	}

	static BigDecimal setSumEX() {

		BigDecimal bdNetto = new BigDecimal("0.00"), bdBrutto = new BigDecimal("0.00");

		try {
			if(AnzYearEX > 0) {
				for(int x = 1; (x - 1) < AnzYearEX; x++) {
					String sNetto = arrYearEX[x][3].trim();
					String sBrutto = arrYearEX[x][6].trim();

					bdNetto = bdNetto.add(new BigDecimal(sNetto));
					bdBrutto = bdBrutto.add(new BigDecimal(sBrutto));
				}
			}
			Double dOpen = bdNetto.doubleValue();
			Double dClosed = bdBrutto.doubleValue();
			infoEX.setTxtSum(0, dOpen);
			infoEX.setTxtSum(1, dClosed);
		} catch (NullPointerException e1){
			logger.error("error in calculatin expenses sum - " + e1);
		}
		infoEX.setProgressBar(prozent(bdNetto, bdBrutto));
		return bdNetto;
	}
	
	static BigDecimal setSumST() {
		
		BigDecimal bdSv = new BigDecimal("0.00"), bdTax = new BigDecimal("0.00");
		
		try {
			if(AnzYearST > 0) {
				for(int x = 1; (x - 1) < AnzYearST; x++) {
					if (arrYearST[x][3].contains("Sozialversicherung")) {
						bdSv = bdSv.add(new BigDecimal(arrYearST[x][5].trim()));
					}
					if (arrYearST[x][3].contains("Finanzamt")) {
						bdTax = bdTax.add(new BigDecimal(arrYearST[x][5].trim()));
					}

				}
			}
			Double dSv = bdSv.doubleValue();
			Double dTax = bdTax.doubleValue();
			infoST.setTxtSum(0, dSv);
			infoST.setTxtSum(1, dTax);
		} catch (NullPointerException e1){
			logger.error("error in calculating sv tax sum - " + e1);
		}
		infoST.setProgressBar(prozent(bdSv, bdTax));
		return null;
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################

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

	static class TableANCellRenderer extends DefaultTableCellRenderer {
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
						if(sTempAN[row][1].equals(JFstatusA.getNotactive())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(false);
							printAB.setEnabled(false);
						}
						if(sTempAN[row][1].equals(JFstatusA.getWritten())) {
							btnPrintAN.setEnabled(true);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(true);
							printAN.setEnabled(true);
							stateAN.setEnabled(false);
							printAB.setEnabled(false);
						}
						if(sTempAN[row][1].equals(JFstatusA.getPrinted())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(true);
							btnPrintAB.setEnabled(false);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(true);
							printAB.setEnabled(false);
						}
						if(sTempAN[row][1].equals(JFstatusA.getOrdered())) {
							btnPrintAN.setEnabled(false);
							btnStateAN.setEnabled(false);
							btnPrintAB.setEnabled(true);
							editAN.setEnabled(false);
							printAN.setEnabled(false);
							stateAN.setEnabled(false);
							printAB.setEnabled(true);
						}
						if(sTempAN[row][1].equals(JFstatusA.getConfirmed())) {
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
			if(AnzYearAN == 0) {
				return label;
			}
			if(bPrintAN[row] == true) {
				setBackground(new Color(175,238,238));  // hellblau
			}
			if(bOrderAN[row] == true) {
				setBackground(new Color(37, 204, 196)); // türkis
			}
			if(bActiveAN[row] == false) {
				setBackground(Color.PINK);
			}
			if(sTempAN[row][1] != null) {
				if(sTempAN[row][1].equals(JFstatusA.getConfirmed())) {
					setBackground(new Color(152, 251, 152)); // hellgrün
				}
			}
			return label;
		}
	}

	static class TableRECellRenderer extends DefaultTableCellRenderer {
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
						if(sTempRE[row][1].equals(JFstatusRa.getNotactive())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(false);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(false);
							printRErem.setEnabled(false);
						}
						if(sTempRE[row][1].equals(JFstatusRa.getWritten())) {
							btnPrintREa.setEnabled(true);
							btnStateREa.setEnabled(false);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(true);
							printREa.setEnabled(true);
							stateREa.setEnabled(false);
							printRErem.setEnabled(false);
						}
						if(sTempRE[row][1].equals(JFstatusRa.getPrinted())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(true);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(true);
						}
						if(sTempRE[row][1].equals(JFstatusRa.getRemprinted())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(true);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(true);
						}
						if(sTempRE[row][1].equals(JFstatusRa.getMahnprinted1())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(true);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(true);
						}
						if(sTempRE[row][1].equals(JFstatusRa.getMahnprinted2())) {
							btnPrintREa.setEnabled(false);
							btnStateREa.setEnabled(true);
							btnPrintRem.setEnabled(false);
							editREa.setEnabled(false);
							printREa.setEnabled(false);
							stateREa.setEnabled(true);
							printRErem.setEnabled(false);
						}
						if(sTempRE[row][1].equals(JFstatusRa.getPayed())) {
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
			if(AnzYearRE == 0) {
				return label;
			}
			if(bPrintRE[row] == true) {
				setBackground(new Color(175,238,238)); // hellblau
			}
			if(bMoneyRE[row] == true) {
				setBackground(new Color(152, 251, 152)); // hellgrün
			}
			if(bActiveRE[row] == false) {
				setBackground(Color.PINK);
			}
			if(sTempRE[row][1] != null) {
				if(sTempRE[row][1].equals(JFstatusRa.getRemprinted())) {
					setBackground(Color.YELLOW); // rot
				}
				if(sTempRE[row][1].equals(JFstatusRa.getMahnprinted1())) {
					setBackground(Color.MAGENTA); // rot
				}
				if(sTempRE[row][1].equals(JFstatusRa.getMahnprinted2())) {
					setBackground(Color.RED); // rot
				}
			}
			return label;
		}
	}

	static class TablePUCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
			if(AnzYearPU == 0) {
				return label;
			}

			if(sTempPU[row][0] != null) {
				DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
				LocalDate datePay = LocalDate.parse(arrYearPU[row + 1][15], inputFormat);
				long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
				int daysPayable = 0;

				try {
					daysPayable = Math.toIntExact(daysBetween);
				} catch (Exception e3) {
					logger.error("error in converting long to integer - " + e3);
				}

				if(daysPayable < 0 && bPayedPU[row] == false) {
					setBackground(Color.RED); // rot
				}
				if(daysPayable >= 0 && daysPayable < 3 && bPayedPU[row] == false) {
					setBackground(Color.PINK); // rot
				}

				if(bPayedPU[row] == true) {
					setBackground(new Color(152, 251, 152)); // hellgrün
				}
			}

			return label;
		}
	}

	static class TableEXCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
			if(AnzYearEX == 0) {
				return label;
			}
			if(sTempEX[row][0] != null && sTempEX[row][6] != null){
				setBackground(new Color(152, 251, 152)); // hellgrün
			}else if(sTempEX[row][0] != null && sTempEX[row][6] == null){
				setBackground(Color.PINK);
			}
			
			return label;
		}
	}

	static class TableSTCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
			if(AnzYearST == 0) {
				return label;
			}

			if(sTempST[row][0] != null) {
				DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
				LocalDate datePay = LocalDate.parse(arrYearST[row + 1][6], inputFormat);
				long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
				int daysPayable = 0;

				try {
					daysPayable = Math.toIntExact(daysBetween);
				} catch (Exception e3) {
					logger.error("error in converting long to integer - " + e3);
				}

				if(daysPayable < 0 && bPayedST[row] == false) {
					setBackground(Color.RED); // rot
				}
				if(daysPayable >= 0 && daysPayable < 3 && bPayedST[row] == false) {
					setBackground(Color.PINK); // rot
				}
				if(bPayedST[row] == true) {
					setBackground(new Color(152, 251, 152)); // hellgrün
				}
			}

			return label;
		}
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private void actionDblClickOfferBill(JTable table, MouseEvent e) {
		String[] arrTmp = new String[51];
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				Arrays.fill(arrTmp, null);
				if (table.getValueAt(row, column) == null) {
					if (offerPanel instanceof OfferPanel anp) {
						anp.setsTitel("Angebotspositionen");
						anp.setTxtFields(arrTmp, null);
					}
					return;
				} else {
					if (offerPanel instanceof OfferPanel anp) {
						anp.setsTitel("Angebotspositionen (Angebots-Nr. = " + table.getValueAt(row, 0).toString() + ")");
						for (int i = 0; i < arrTmp.length; i++) {
							arrTmp[i] = arrYearAN[row + 1][i + 1];
						}
						anp.setTxtFields(arrTmp, table.getValueAt(row, 0).toString());
					}
				}
			}
		}
	}

	private void actionClickPU(JTable table, MouseEvent e) {
		String[] arrTmp = new String[19];
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				Arrays.fill(arrTmp, null);
				if (table.getValueAt(row, column) == null) {
					if (purchasePanel instanceof PurchasePanel pup) {
						pup.setsTitel("neuen Einkaufsbeleg erfassen");
						pup.setBtnText(1, "save");
						pup.setTxtFields(arrTmp, null);
						pup.setIcon();
						pup.setFile(false);
					}
					return;
				} else {
					if (purchasePanel instanceof PurchasePanel pup) {
						pup.setsTitel("vorhandenen Einkaufsbeleg (Rechnungs-Nr. = " + table.getValueAt(row, 1).toString() + ")  bearbeiten");
						pup.setBtnText(1, "<html>bezahlt<br>setzen</html>");
						for (int i = 0; i < arrTmp.length; i++) {
							arrTmp[i] = arrYearPU[row + 1][i + 1];
						}
						pup.setTxtFields(arrTmp, table.getValueAt(row, 1).toString());
						pup.setIcon();
						pup.setFile(false);
					}
					return;
				}
			}
		}
	}

	private void actionClickEX(JTable table, MouseEvent e) {
		String[] arrTmp = new String[7];
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				Arrays.fill(arrTmp, null);
				if (table.getValueAt(row, column) == null) {
					if (expensesPanel instanceof ExpensesPanel ep) {
						ep.setsTitel("neuen Beleg erfassen");
						ep.setTxtFields(arrTmp, 0);
						ep.setIcon();
						ep.setFile(false);
					}
					return;
				} else {
					if (expensesPanel instanceof ExpensesPanel ep) {
						ep.setsTitel("vorhandenen Beleg (Beleg-Nr. = " + table.getValueAt(row, 0).toString() + ")  bearbeiten");
						for (int i = 0; i < arrTmp.length; i++) {
							arrTmp[i] = arrYearEX[row + 1][i + 1];
						}
						ep.setTxtFields(arrTmp, Integer.parseInt(table.getValueAt(row, 0).toString()));
						ep.setIcon();
						ep.setFile(false);
					}
					return;
				}
			}
		}
	}

	private void actionClickST(JTable table, MouseEvent e) {
		String[] arrTmp = new String[8];
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				Arrays.fill(arrTmp, null);
				if (table.getValueAt(row, column) == null) {
					if (svTaxPanel instanceof SvTaxPanel svp) {
						svp.setsTitel("neuen Bescheid/Vorschreibung erfassen");
						svp.setBtnText(1, "save");
						svp.setTxtFields(arrTmp, 0);
						svp.setIcon();
						svp.setFile(false);
					}
					return;
				} else {
					if (svTaxPanel instanceof SvTaxPanel svp) {
						svp.setsTitel("vorhandenen Bescheid/Vorschreibung (Id-Nr. = " + arrYearST[row + 1][1] + ")  bearbeiten");
						svp.setBtnText(1, "<html>bezahlt<br>setzen</html>");
						for (int i = 0; i < arrTmp.length; i++) {
							arrTmp[i] = arrYearST[row + 1][i + 2];
						}
						svp.setTxtFields(arrTmp, Integer.parseInt(arrYearST[row + 1][1]));
						svp.setIcon();
						svp.setFile(false);
					}
					return;
				}
			}
		}
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################

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
			logger.error("actionAN1() - " + e);
		}

	}

	private void actionAN2() {
		if(vZelleA == null) {
			return;
		}
		try {
			if(!vZelleA.isEmpty() && iRowA >= 0) {
				String[] tmpArray = new String[47];
				for(int x = 1; (x - 1) < AnzYearAN; x++) {
					if(arrYearAN[x][1].equals(vZelleA)) {
						for(int y = 1; y < 48; y++) {
							tmpArray[y-1] = arrYearAN[x][y];
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
				JFstatusA.showDialog(vZelleA, sTempAN[iRowA][1]);
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
				for(int x = 1; (x - 1) < AnzYearRE; x++) {
					if(arrYearRE[x][1].equals(vZelleRa)) {
						for(int y = 1; y < 52; y++) {
							tmpArray[y-1] = arrYearRE[x][y];
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
				JFstatusRa.showDialog(vZelleRa, sTempRE[iRowRa][1]);
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
	
	//###################################################################################################################################################
	
	private static int prozent(BigDecimal open, BigDecimal closed) {
		BigDecimal bdSumA = BigDecimal.ZERO, bdTmpA = BigDecimal.ZERO, bdTmpA1 = BigDecimal.ZERO, bdProzA = BigDecimal.ZERO;
		int iProzA = 0;
		bdSumA = open.add(closed);
		try {
			bdSumA = open.add(closed);
			bdTmpA = new BigDecimal("100.00");
			if(bdSumA.intValue() > 0) {
				bdTmpA1 = bdTmpA.divide(bdSumA, 8, RoundingMode.HALF_UP);
				bdProzA = bdTmpA1.multiply(open);
			}else {
				bdTmpA1 = new BigDecimal("0.00");
				bdProzA = new BigDecimal("0.00");
			}
		}catch (Exception e2){
			logger.error("error in calculating percentage - " + e2);
			iProzA = -99; // Fehlerwert
		}finally {
			iProzA = bdProzA.intValue();
		}
		return iProzA;
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

	public static String[][] getArrYearAN() {
		return arrYearAN;
	}

	public static String[][] getArrYearRE() {
		return arrYearRE;
	}

	public static int getAnzYearEX() {
		return AnzYearEX;
	}

	public static int getAnzYearST() {
		return AnzYearST;
	}

	public static void setsConn(String sConn) {
		JFoverview.sConn = sConn;
	}

	public static void setbActiveAN(int idx, boolean bActiveAN) {
		JFoverview.bActiveAN[idx] = bActiveAN;
	}

	public static void setbPrintAN(int idx, boolean bPrintAN) {
		JFoverview.bPrintAN[idx] = bPrintAN;
	}

	public static void setbOrderAN(int idx, boolean bOrderAN) {
		JFoverview.bOrderAN[idx] = bOrderAN;
	}

	public static void setAnzYearAN(int anzYearAN) {
		AnzYearAN = anzYearAN;
	}

	public static void setbActiveRE(int idx, boolean bActiveRE) {
		JFoverview.bActiveRE[idx] = bActiveRE;
	}

	public static void setbPrintRE(int idx, boolean bPrintRE) {
		JFoverview.bPrintRE[idx] = bPrintRE;
	}

	public static void setbMoneyRE(int idx, boolean bMoneyRE) {
		JFoverview.bMoneyRE[idx] = bMoneyRE;
	}

	public static void setAnzYearRE(int anzYearRE) {
		AnzYearRE = anzYearRE;
	}

	public static void setbPayedPU(int idx, boolean bPayedPU) {
		JFoverview.bPayedPU[idx] = bPayedPU;
	}

	public static void setAnzYearPU(int anzYearPU) {
		AnzYearPU = anzYearPU;
	}

	public static void setArrYearAN(String[][] arrYearAN) {
		JFoverview.arrYearAN = arrYearAN;
	}

	public static void setArrYearRE(String[][] arrYearRE) {
		JFoverview.arrYearRE = arrYearRE;
	}

	public static void setArrYearPU(String[][] arrYearPU) {
		JFoverview.arrYearPU = arrYearPU;
	}

	public static void setArrYearEX(String[][] arrYearEX) {
		JFoverview.arrYearEX = arrYearEX;
	}

	public static void setArrYearST(String[][] arrYearST) {
		JFoverview.arrYearST = arrYearST;
	}

	public static void setAnzYearEX(int anzYearEX) {
		AnzYearEX = anzYearEX;
	}

	public static void setbPayedST(int idx, boolean bPayedST) {
		JFoverview.bPayedST[idx] = bPayedST;
	}

	public static void setAnzYearST(int anzYearST) {
		AnzYearST = anzYearST;
	}
}
