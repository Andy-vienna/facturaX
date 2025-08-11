package org.andy.gui.main;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.Tools.saveSettingsApp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.dataExport.ExcelBill;
import org.andy.code.dataExport.ExcelOffer;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.code.main.*;
import org.andy.code.main.overview.result.ZmData;
import org.andy.code.main.overview.result.TaxData;
import org.andy.code.main.overview.result.UStData;
import org.andy.code.main.overview.table.LoadBill;
import org.andy.code.main.overview.table.LoadExpenses;
import org.andy.code.main.overview.table.LoadOffer;
import org.andy.code.main.overview.table.LoadPurchase;
import org.andy.code.main.overview.table.LoadSvTax;
import org.andy.gui.file.JFfileView;
import org.andy.gui.main.overview_panels.SumPanel;
import org.andy.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.gui.main.overview_panels.edit_panels.EditPanelFactory;
import org.andy.gui.main.overview_panels.edit_panels.factory.BillPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.ExpensesPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.OfferPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.PurchasePanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.SvTaxPanel;
import org.andy.gui.main.result_panels.ZmPanel;
import org.andy.gui.main.result_panels.TaxPanel;
import org.andy.gui.main.result_panels.UStPanel;
import org.andy.gui.main.settings_panels.BankPanel;
import org.andy.gui.main.settings_panels.DbPanel;
import org.andy.gui.main.settings_panels.GwbTablePanel;
import org.andy.gui.main.settings_panels.ArtikelPanel;
import org.andy.gui.main.settings_panels.KundePanel;
import org.andy.gui.main.settings_panels.OwnerPanel;
import org.andy.gui.main.settings_panels.PfadPanel;
import org.andy.gui.main.settings_panels.QrPanel;
import org.andy.gui.main.settings_panels.TaxTablePanel;
import org.andy.gui.main.settings_panels.TextPanel;
import org.andy.gui.main.settings_panels.UserPanel;
import org.andy.gui.main.table_panels.CreatePanel;
import org.andy.gui.main.table_panels.CreateTable;
import org.andy.gui.misc.RoundedBorder;
import org.andy.gui.misc.WrapLayout;
import org.andy.gui.offer.JFconfirmA;
import org.andy.gui.reminder.JFreminder;
import org.andy.toolbox.misc.*;

public class JFoverview extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(JFoverview.class);

	private static File lock = new File(System.getProperty("user.dir") + "\\.lock");

	private static final int BASEX = 10;
	private static final int BOTTOMY = 5;
	private static final int BUTTONX = 130;
	private static final int BUTTONY = 50;
	private static final int STATEY = 30;

	private static final String[] HEADER_AN = { "AN-Nummer", "Status", "Datum", "Referenz", "Kunde", "Netto" };
	private static final String[] HEADER_RE = { "RE-Nummer", "Status", "Datum", "Leistungszeitraum", "Referenz", "Kunde", "Netto", "USt.", "Brutto" };
	private static final String[] HEADER_PU = {"RE-Datum","RE-Nummer", "Kreditor Name", "Kreditor Land", "Netto", "USt.", "Brutto", "Zahlungsziel", "bezahlt", "Dateiname" };
	private static final String[] HEADER_EX = { "Datum", "Bezeichnung", "Netto (EUR)", "Steuer (EUR)", "Brutto (EUR)", "Dateiname" };
	private static final String[] HEADER_ST = { "Datum", "Zahlungsempfänger", "Bezeichnung", "Zahllast", "Fälligkeit", "bezahlt", "Dateiname" };

	private static String[][] sTempAN = null, sTempRE = null, sTempPU = null, sTempEX = null, sTempST = null;

	private static JFoverview frame;
	private static JTabbedPane tabPanel;
	private static JPanel contentPane, pageOv, pageText, pageErg, pageAdmin, pageSetting;
	private static JPanel pageAN = new JPanel(new BorderLayout());
	private static JPanel pageRE = new JPanel(new BorderLayout());
	private static JPanel pagePU = new JPanel(new BorderLayout());
	private static JPanel pageEX = new JPanel(new BorderLayout());
	private static JPanel pageST = new JPanel(new BorderLayout());
	private static EditPanel offerPanel, billPanel, purchasePanel, svTaxPanel, expensesPanel;
	private static SumPanel infoAN, infoRE, infoPU, infoEX, infoST;
	private static UStPanel panelUSt;
	private static ZmPanel panelZM;
	private static TaxPanel panelP109a;
	private static JScrollPane sPaneText, sPaneErg; //, sPaneSetting;
	private static CreateTable<Object> sPaneAN, sPaneRE, sPanePU, sPaneEX, sPaneST;
	
	private static CreatePanel panelOfferInfo, panelBillInfo;
	
	private static JMenuBar menuBar;
	private static JMenu menu1, menu6, menu9;
	private static JMenuItem logoff, exit, aktualisieren, info;
	
	private static JLabel lblState;
	private static JTextField txtWirtschaftsjahr;

	private static String sLic = null, vZelleAN = null, vStateAN = null, vZelleRE = null, vStateRE = null;
	private static int iLic = 0, iUserRights = 0;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					frame = new JFoverview();
					frame.setVisible(true);

				} catch (Exception  e) {
					//logger.fatal("loadGUI fehlgeschlagen - " + e);
					e.printStackTrace();
				}
			}
		});
	}
	
	//###################################################################################################################################################
	
	public static void actScreen() {
		
		switch (iUserRights) {
			case 1: // User
				pageAN.removeAll();
				pageRE.removeAll();
				doAngebotPanel(false);
				doRechnungPanel(false);
				break;
			case 2: // SuperUser
				pageAN.removeAll();
				pageRE.removeAll();
				pagePU.removeAll();
				pageEX.removeAll();
				doAngebotPanel(false);
				doRechnungPanel(false);
				doEinkaufPanel();
				doAusgabenPanel();
				break;
			case 5: // FinancialUser
				pageST.removeAll();
				//pageOv.removeAll();
				doSvsTaxPanel();
				doJahresergebnis();
				break;
			case 9: // Admin
				TextPanel.loadTexte();
				break;
		}
		
		contentPane.revalidate();
		contentPane.repaint();

		frame.setTitle(StartUp.APP_NAME + StartUp.APP_VERSION + " - Wirtschaftsjahr " + LoadData.getStrAktGJ());
		
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private JFoverview() {
		
		try {
			setIconImage(SetFrameIcon.getFrameIcon("icon.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}
		setMinimumSize(new Dimension(1250, 1000));
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

		//###################################################################################################################################################

		menu1 = new JMenu("Datei");
		menu6 = new JMenu("Ansicht");
		menu9 = new JMenu("Info");

		logoff = new JMenuItem("User abmelden");
		exit = new JMenuItem("Exit");

		aktualisieren = new JMenuItem("Aktualisieren");
		info = new JMenuItem("Info");

		try {

			logoff.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("key.png")));
			exit.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("exit.png")));

			aktualisieren.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("actualize.png")));

			info.setIcon(new ImageIcon(SetMenuIcon.getMenuIcon("info.png")));

		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		menu1.add(logoff);
		menu1.addSeparator();
		menu1.add(exit);
		
		menu6.add(aktualisieren);

		menu9.add(info);

		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);

		menuBar.add(menu1);
		menuBar.add(menu6);
		menuBar.add(menu9);

		contentPane.add(menuBar);

		//###################################################################################################################################################

		if(iLic == 0) { // nicht lizensiert
			menu1.setEnabled(false);
			menu6.setEnabled(false);
		}else if(iLic == 1) { // Demo-Lizenz
			aktualisieren.setEnabled(false);
		}else {
			menu1.setEnabled(true);
		}

		//###################################################################################################################################################

		tabPanel = new JTabbedPane(JTabbedPane.TOP);
		
		doAngebotPanel(false); // TAB - Angebote
		doRechnungPanel(false); // TAB - Ausgangsechnungen
		doEinkaufPanel(); // TAB - Einkaufsrechnungen
		doAusgabenPanel(); // TAB - Ausgaben
		doSvsTaxPanel(); // TAB - SVS und Steuern
		doJahresergebnis(); // TAB - Jahresergebnis
		doEinstellungen(); // TAB - Einstellungen
		//------------------------------------------------------------------------------
		// TAB 8 - Textbausteine
		//------------------------------------------------------------------------------
		if(iUserRights == 9) { // Admin
			pageText = new TextPanel();
			TextPanel.loadTexte();
			
			// ScrollPane für das Panel
			sPaneText = new JScrollPane(pageText);
		}
		//------------------------------------------------------------------------------
		// Tabpanel allgemeines
		//------------------------------------------------------------------------------
		switch (iUserRights) {
		case 1: // User
			tabPanel.addTab("Angebote", pageAN);
			tabPanel.addTab("Rechnungen", pageRE);
			
			tabPanel.setIconAt(0, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/offer.png")));
			tabPanel.setIconAt(1, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/invoice.png")));
			break;
		case 2: // SuperUser
			tabPanel.addTab("Angebote", pageAN);
			tabPanel.addTab("Rechnungen", pageRE);
			tabPanel.addTab("Einkauf", pagePU);
			tabPanel.addTab("Betriebsausgaben", pageEX);
			
			tabPanel.setIconAt(0, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/offer.png")));
			tabPanel.setIconAt(1, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/invoice.png")));
			tabPanel.setIconAt(2, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/purchase.png")));
			tabPanel.setIconAt(3, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/expenses.png")));
			break;
		case 5: // FinancialUser
			tabPanel.addTab("SV und Steuer", pageST);
			tabPanel.addTab("Jahresergebnis", pageOv);
			
			tabPanel.setIconAt(0, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/tax.png")));
			tabPanel.setIconAt(1, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/result.png")));
			break;
		case 9: // Admin
			tabPanel.addTab("Einstellungen", pageAdmin);
			tabPanel.addTab("Textbausteine", sPaneText);
			
			tabPanel.setIconAt(0, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/config.png")));
			tabPanel.setIconAt(1, new ImageIcon(JFoverview.class.getResource("/org/resources/icons/bausteine.png")));
			break;
		default: System.exit(2); // Ende mit Code 2
		};

		// Add the JTabbedPane to the JFrame's content
		tabPanel.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		// TabPanel einbinden und anzeigen
		contentPane.add(tabPanel);
		
		// Statusleiste anlegen und befüllen
		doStatus();
	
		// ------------------------------------------------------------------------------
		// Action Listener für JFrame und JPanel
		// ------------------------------------------------------------------------------
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
		// Action Listener für Menü-Einträge
		// ------------------------------------------------------------------------------
		logoff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				JFmainLogIn.loadLogIn(); // Anmeldefenster einblenden
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		aktualisieren.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actScreen();
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
	// Panels erstellen
	//###################################################################################################################################################
	
	static void doAngebotPanel(boolean use) {
		if (iUserRights != 1 && iUserRights != 2) return; // nur bei user oder superUser
		JButton[] btn = null;
		sTempAN = LoadOffer.loadAngebot(false);
		
		if (use) {
			btn = new JButton[1];
			pageAN.removeAll();
			offerPanel = EditPanelFactory.create("NA");
			try {
				btn[0] = createButton("zurück", "aktualisieren.png");
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
		} else {
			btn = new JButton[3];
			offerPanel = EditPanelFactory.create("AN");
			try {
				btn[0] = createButton("<html>neues<br>Angebot</html>", "new.png");
				btn[1] = createButton("<html>Angebot<br>drucken</html>", "print.png");
				btn[2] = createButton("<html>AB<br>drucken</html>", "print.png");
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
		}
		btn[0].setEnabled(true);
		
		// Tabelle mit ScrollPane anlegen
		sPaneAN = new CreateTable<>(sTempAN, HEADER_AN, new TableANcr());
		sPaneAN.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionClickAN(sPaneAN.getTable(), e); } });
		sPaneAN.setColumnWidths(new int[] {200,200,200,750,300,200});
		
		// InfoPanel anlegen
		infoAN = new SumPanel(new String[] {"Summe offen:", "Summe best.:"}, true);
		setSumAN(); // Summen eintragen
		
		panelOfferInfo = new CreatePanel(sPaneAN, offerPanel, btn, infoAN);
		
		btn = panelOfferInfo.getButtons(); // Button-Instanzen holen für Action Listener
		
		btn[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!use) {
					doAngebotPanel(true);
				} else {
					actScreen();
				}
			}
		});
		if (!use) {
			btn[1].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(vZelleAN == null) {
						return;
					}
					try {
						ExcelOffer.anExport(vZelleAN);
						actScreen();
					} catch (Exception e1) {
						logger.error("actionAN3() - " + e1);
					}
				}
			});
			btn[2].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(vZelleAN == null) {
						return;
					}
					try {
						JFconfirmA.showDialog(vZelleAN);
						actScreen();
					}catch (Exception e1) {
						logger.error("actionAN5() - " + e1);
					}
				}
			});
		}
		pageAN.add(panelOfferInfo);
		pageAN.revalidate();
		pageAN.repaint();
	}
	
	//###################################################################################################################################################
	
	static void doRechnungPanel(boolean use) {
		if (iUserRights != 1 && iUserRights != 2) return; // nur bei user oder superUser
		JButton[] btn = null;
		sTempRE = LoadBill.loadRechnung(false);
		
		if (use) {
			btn = new JButton[1];
			pageRE.removeAll();
			billPanel = EditPanelFactory.create("NR");
			try {
				btn[0] = createButton("zurück", "aktualisieren.png");
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
		} else {
			btn = new JButton[3];
			billPanel = EditPanelFactory.create("RE");
			try {
				btn[0] = createButton("<html>neue<br>Rechnung</html>", "new.png");
				btn[1] = createButton("<html>Rechnung<br>drucken</html>", "print.png");
				btn[2] = createButton("<html>Mahn-<br>verfahren</html>", "print.png");
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
		}
		btn[0].setEnabled(true);
		
		// Tabelle mit ScrollPane anlegen
		sPaneRE = new CreateTable<>(sTempRE, HEADER_RE, new TableREcr());
		sPaneRE.getTable().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) { actionClickBill(sPaneRE.getTable(), e); } });
		sPaneRE.setColumnWidths(new int[] {120,120,120,200,650,200,150,150,150});
		
		// InfoPanel anlegen
		infoRE = new SumPanel(new String[] {"Summe offen:", "Summe bez.:"}, true);
		setSumRE(); // Summen eintragen
		
		panelBillInfo = new CreatePanel(sPaneRE, billPanel, btn, infoRE);
		
		btn = panelBillInfo.getButtons(); // Button-Instanzen holen für Action Listener
		
		btn[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!use) {
					doRechnungPanel(true);
				} else {
					actScreen();
				}
			}
		});
		if (!use) {
			btn[1].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(vZelleRE == null) {
						return;
					}
					try {
						ExcelBill.reExport(vZelleRE);
						actScreen();
					} catch (Exception e1) {
						logger.error("actionRE3() - " + e1);
					}
				}
			});
			btn[2].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(vZelleRE == null) {
						return;
					}
					JFreminder.showGUI(vZelleRE);
					actScreen();
				}
			});
		}
		pageRE.add(panelBillInfo);
		pageRE.revalidate();
		pageRE.repaint();
	}
	
	//###################################################################################################################################################
	
	static void doEinkaufPanel() {
		if(iUserRights == 2) { // SuperUser
			sTempPU = LoadPurchase.loadEinkaufsRechnung(false);
			
			purchasePanel = EditPanelFactory.create("PU");
			if (purchasePanel instanceof PurchasePanel pup) {
				pup.setsTitel("neuen Einkaufsbeleg erfassen");
				pup.setBtnText(0, "..."); pup.setBtnText(1, "save");
			}
			
			// Tabelle mit ScrollPane anlegen
			sPanePU = new CreateTable<>(sTempPU, HEADER_PU, new TablePUcr());
			sPanePU.getTable().addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) { actionClickPU(sPanePU.getTable(), e); } });
			sPanePU.setColumnWidths(new int[] {100,150,400,80,100,100,100,150,80,400});
			
			// InfoPanel anlegen
			infoPU = new SumPanel(new String[] {"Netto:", "Brutto:"}, false);
			setSumPU();
			
			CreatePanel panel = new CreatePanel(sPanePU, purchasePanel, null, infoPU);
			pagePU.add(panel);
			pagePU.revalidate();
			pagePU.repaint();
		}
	}
	
	//###################################################################################################################################################
	
	static void doAusgabenPanel() {
		if(iUserRights == 2) { // SuperUser
			sTempEX = LoadExpenses.loadAusgaben(false);
			
			expensesPanel = EditPanelFactory.create("EX");
			if (expensesPanel instanceof ExpensesPanel ep) {
				ep.setsTitel("neuen Beleg erfassen");
				ep.setBtnText(0, "..."); ep.setBtnText(1, "save");
			}
			
			// Tabelle mit ScrollPane anlegen
			sPaneEX = new CreateTable<>(sTempEX, HEADER_EX, new TableEXcr());
			sPaneEX.getTable().addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) { actionClickEX(sPaneEX.getTable(), e); } });
			sPaneEX.setColumnWidths(new int[] {100,650,150,150,150,650});
			
			// InfoPanel anlegen
			infoEX = new SumPanel(new String[] {"Netto:", "Brutto:"}, false);
			setSumEX();
	
			CreatePanel panel = new CreatePanel(sPaneEX, expensesPanel, null, infoEX);
			pageEX.add(panel);
			pageEX.revalidate();
			pageEX.repaint();
		}
	}
	
	//###################################################################################################################################################
	
	static void doSvsTaxPanel() {
		if(iUserRights == 5) { // FinancialUser
			sTempST = LoadSvTax.loadSvTax(false);
			
			svTaxPanel = EditPanelFactory.create("SVT");
			if (svTaxPanel instanceof SvTaxPanel svt) {
				svt.setsTitel("neuen Beleg anlegen");
				svt.setBtnText(0, "..."); svt.setBtnText(1, "save");
			}
			
			// Tabelle mit ScrollPane anlegen
			sPaneST = new CreateTable<>(sTempST, HEADER_ST, new TableSTcr());
			sPaneST.getTable().addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) { actionClickST(sPaneST.getTable(), e); } });
			sPaneST.setColumnWidths(new int[] {120,450,450,120,120,80,500});
			
			// InfoPanel anlegen
			infoST = new SumPanel(new String[] {"SV:", "Steuer:"}, false);
			setSumST();
			
			CreatePanel panel = new CreatePanel(sPaneST, svTaxPanel, null, infoST);
			pageST.add(panel);
			pageST.revalidate();
			pageST.repaint();
		}
	}
	
	//###################################################################################################################################################
	
	static void doJahresergebnis() {
		if(iUserRights == 5) { // FinancialUser
			panelUSt = new UStPanel();
			panelUSt.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			panelZM = new ZmPanel();
			panelZM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
			panelP109a = new TaxPanel();
			panelP109a.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
			UStData.setValuesUVA(panelUSt);
			ZmData.RecState(panelZM);
			TaxData.setValuesTax(panelP109a);
			
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
			pageOv.revalidate();
			pageOv.repaint();
		}
	}
	
	//###################################################################################################################################################
	
	static void doEinstellungen() {
		if(iUserRights == 9) { // Admin
			
			String[] select = { "", "Eigentümerdaten", "Bankdaten", "Stammdatenverwaltung", "Pfadverwaltung", "Benutzerverwaltung", "Steuerdaten", "SEPA QR-Code", "Datenbank" };

			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
			border.setTitleJustification(TitledBorder.LEFT);
			border.setTitlePosition(TitledBorder.TOP);

			pageAdmin = new JPanel(null);
			pageAdmin.setBorder(border);

			// Nutze BorderLayout, dann kannst du zentral das pageSetting platzieren
			pageSetting = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
			pageSetting.setBounds(5, 50, 1200, 800); // Beispielgröße

			JComboBox<String> cmbSelect = new JComboBox<>(select);
			cmbSelect.setBounds(10, 10, 500, 25);

			pageAdmin.add(cmbSelect);
			pageAdmin.add(pageSetting);

			cmbSelect.addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent actionEvent) {
			        // Panels aus pageSetting entfernen
			        pageSetting.removeAll();
			        int idx = cmbSelect.getSelectedIndex();
			        switch (idx) {
			            case 1:
			                OwnerPanel ownerPanel = new OwnerPanel();
			                pageSetting.add(ownerPanel);
			                break;
			            case 2:
			            	BankPanel bankPanel = new BankPanel();
			            	pageSetting.add(bankPanel);
			            	break;
			            case 3:
			                KundePanel kundePanel = new KundePanel();
			                ArtikelPanel itemPanel = new ArtikelPanel();
			                pageSetting.add(kundePanel);
			                pageSetting.add(itemPanel);
			                break;
			            case 4:
			                PfadPanel pathPanel = new PfadPanel();
			                pageSetting.add(pathPanel);
			                break;
			            case 5:
			            	UserPanel userPanel = new UserPanel();
			            	pageSetting.add(userPanel);
			            	break;
			            case 6:
			                TaxTablePanel taxTablePanel = new TaxTablePanel();
			                GwbTablePanel gwbTablePanel = new GwbTablePanel();
			                pageSetting.add(taxTablePanel);
			                pageSetting.add(gwbTablePanel);
			                break;
			            case 7:
			            	QrPanel qrPanel = new QrPanel();
			            	pageSetting.add(qrPanel);
			            	break;
			            case 8:
			            	DbPanel dbPanel = new DbPanel();
			            	pageSetting.add(dbPanel);
			            	break;
			            default: break;
			        }
			        pageSetting.revalidate();
			        pageSetting.repaint();
			    }
			});
		}
	}
	
	//###################################################################################################################################################
	// Summen bilden
	//###################################################################################################################################################

	static void setSumAN() {
		double dOpen = LoadOffer.getSumOpen().doubleValue();
		double dOrdered = LoadOffer.getSumOrdered().doubleValue();
		infoAN.setTxtSum(0, dOpen);
		infoAN.setTxtSum(1, dOrdered);
		infoAN.setProgressBar(prozent(LoadOffer.getSumOpen(), LoadOffer.getSumOrdered()));
	}

	static void setSumRE() {
		double dOpen = LoadBill.getSumOpen().doubleValue();
		double dPayed = LoadBill.getSumPayed().doubleValue();
		infoRE.setTxtSum(0, dOpen);
		infoRE.setTxtSum(1, dPayed);
		infoRE.setProgressBar(prozent(LoadBill.getSumOpen(), LoadBill.getSumPayed()));
	}

	static void setSumPU() {
		double dNetto = LoadPurchase.getBdNetto().doubleValue();
		double dBrutto = LoadPurchase.getBdBrutto().doubleValue();
		infoPU.setTxtSum(0, dNetto);
		infoPU.setTxtSum(1, dBrutto);
		infoPU.setProgressBar(prozent(LoadExpenses.getBdNetto(), LoadExpenses.getBdBrutto()));
	}

	static void setSumEX() {
		double dNetto = LoadExpenses.getBdNetto().doubleValue();
		double dBrutto = LoadExpenses.getBdBrutto().doubleValue();
		infoEX.setTxtSum(0, dNetto);
		infoEX.setTxtSum(1, dBrutto);
		infoEX.setProgressBar(prozent(LoadExpenses.getBdNetto(), LoadExpenses.getBdBrutto()));
	}
	
	static BigDecimal setSumST() {
		Double dSv = LoadSvTax.getBdSV().doubleValue();
		Double dSteuer = LoadSvTax.getBdSteuer().doubleValue();
		infoST.setTxtSum(0, dSv);
		infoST.setTxtSum(1, dSteuer);
		infoST.setProgressBar(prozent(LoadSvTax.getBdSV(), LoadSvTax.getBdSteuer()));
		return null;
	}
	
	//###################################################################################################################################################
	
	static void doStatus() {

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
				actScreen();
			}
		});
		txtWirtschaftsjahr.setBackground(new Color(176, 224, 230));
		txtWirtschaftsjahr.setHorizontalAlignment(SwingConstants.CENTER);
		txtWirtschaftsjahr.setFont(new Font("Tahoma", Font.BOLD, 12));
		txtWirtschaftsjahr.setForeground(Color.BLACK);
		contentPane.add(txtWirtschaftsjahr);
	}

	//###################################################################################################################################################
	// ActionListner
	//###################################################################################################################################################
	
	private void resizeGUI(Dimension xy) {
		int x = xy.width;
		int y = xy.height;

		int iStateTop = y - BOTTOMY - STATEY;

		menuBar.setBounds(0, 0, x, STATEY - 10);

		tabPanel.setLocation(10, STATEY);
		tabPanel.setSize(x - 20, y - 80);

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
	
	private static void actionClickAN(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) == null) {
					if (offerPanel instanceof OfferPanel anp) {
						anp.setsTitel("Angebotspositionen");
						anp.setTxtFields(null, null);
					}
					return;
				} else {
					if (offerPanel instanceof OfferPanel anp) {
						anp.setsTitel("Angebotspositionen (Angebots-Nr. = " + table.getValueAt(row, 0).toString() + ")");
						Kunde kunde = searchKundeAll(table.getValueAt(row, 4).toString());
						anp.setTxtFields(table.getValueAt(row, 0).toString(), kunde.getTaxvalue());
						vZelleAN = table.getValueAt(row, 0).toString();
						vStateAN = table.getValueAt(row, 1).toString();
						if(panelOfferInfo instanceof CreatePanel cp) {
							JButton[] btn = cp.getButtons();
							switch(vStateAN) {
							case "erstellt":
								btn[1].setEnabled(true);
								btn[2].setEnabled(false);
								break;
							case "bestellt":
								btn[1].setEnabled(false);
								btn[2].setEnabled(true);
								break;
							default:
								btn[1].setEnabled(false);
								btn[2].setEnabled(false);
								break;
							}
						}
					}
				}
			}
		}
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) != null) {
					String nr = table.getValueAt(row, 0).toString();
					Kunde kunde = searchKundeAll(table.getValueAt(row, 4).toString());
					actionFile(nr, kunde);
				}
			}
		}
	}
	
	//###################################################################################################################################################
	
	private static void actionClickBill(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) == null) {
					if (billPanel instanceof BillPanel rep) {
						rep.setsTitel("Rechnungspositionen");
						rep.setTxtFields(null, null);
					}
					return;
				} else {
					if (billPanel instanceof BillPanel rep) {
						rep.setsTitel("Rechnungspositionen (Rechnung-Nr. = " + table.getValueAt(row, 0).toString() + ")");
						Kunde kunde = searchKundeAll(table.getValueAt(row, 5).toString());
						rep.setTxtFields(table.getValueAt(row, 0).toString(), kunde.getTaxvalue());
						vZelleRE = table.getValueAt(row, 0).toString();
						vStateRE = table.getValueAt(row, 1).toString();
						if(panelBillInfo instanceof CreatePanel cp) {
							JButton[] btn = cp.getButtons();
							switch(vStateRE) {
							case "erstellt":
								btn[1].setEnabled(true);
								btn[2].setEnabled(false);
								break;
							case "gedruckt", "Zahlungserinnerung", "Mahnstufe 1", "Mahnstufe 2":
								btn[1].setEnabled(false);
								btn[2].setEnabled(true);
								break;
							default:
								btn[1].setEnabled(false);
								btn[2].setEnabled(false);
								break;
							}
						}
					}
				}
			}
		}
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) != null) {
					String nr = table.getValueAt(row, 0).toString();
					Kunde kunde = searchKundeAll(table.getValueAt(row, 5).toString());
					actionFile(nr, kunde);
				}
			}
		}
	}
	
	//###################################################################################################################################################

	private static void actionClickPU(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) == null) {
					if (purchasePanel instanceof PurchasePanel pup) {
						pup.setsTitel("neuen Einkaufsbeleg erfassen");
						pup.setBtnText(1, "save");
						pup.setTxtFields(null);
						pup.setIcon();
						pup.setFile(false);
					}
					return;
				} else {
					if (purchasePanel instanceof PurchasePanel pup) {
						pup.setsTitel("vorhandenen Einkaufsbeleg (Rechnungs-Nr. = " + table.getValueAt(row, 1).toString() + ")  bearbeiten");
						pup.setBtnText(1, "<html>bezahlt<br>setzen</html>");
						pup.setTxtFields(table.getValueAt(row, 1).toString()); // Rechnungsnummer
						pup.setIcon();
						pup.setFile(false);
					}
					return;
				}
			}
		}
	}
	
	//###################################################################################################################################################

	private static void actionClickEX(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			int[] belegID = LoadExpenses.getBelegID();
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) == null) {
					if (expensesPanel instanceof ExpensesPanel ep) {
						ep.setsTitel("neuen Beleg erfassen");
						ep.setTxtFields(0);
						ep.setIcon();
						ep.setFile(false);
					}
					return;
				} else {
					if (expensesPanel instanceof ExpensesPanel ep) {
						ep.setsTitel("vorhandenen Beleg bearbeiten");
						ep.setTxtFields(belegID[row]);
						ep.setIcon();
						ep.setFile(false);
					}
					return;
				}
			}
		}
	}
	
	//###################################################################################################################################################

	private static void actionClickST(JTable table, MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			int row = table.rowAtPoint(e.getPoint());
			int column = table.columnAtPoint(e.getPoint());
			// Hier die gewünschte Aktion bei Klick ausführen
			int[] belegID = LoadSvTax.getBelegID();
			if (row != -1 && column != -1) {
				if (table.getValueAt(row, column) == null) {
					if (svTaxPanel instanceof SvTaxPanel svp) {
						svp.setsTitel("neuen Bescheid/Vorschreibung erfassen");
						svp.setBtnText(1, "save");
						svp.setTxtFields(0);
						svp.setIcon();
						svp.setFile(false);
					}
					return;
				} else {
					if (svTaxPanel instanceof SvTaxPanel svp) {
						svp.setsTitel("vorhandenen Bescheid/Vorschreibung bearbeiten");
						svp.setBtnText(1, "<html>bezahlt<br>setzen</html>");
						svp.setTxtFields(belegID[row]);
						svp.setIcon();
						svp.setFile(false);
					}
					return;
				}
			}
		}
	}
	
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

	private static void actionFile(String value, Kunde kunde) {
		if(value == null || kunde == null) {
			return;
		}
		JFfileView.loadGUI(value, kunde);
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

	static Kunde searchKundeAll(String sKdName) {
		KundeRepository kundeRepository = new KundeRepository();
	    List<Kunde> kundeListe = new ArrayList<>();
	    kundeListe.addAll(kundeRepository.findAll());

		for (int kd = 0; kd < kundeListe.size(); kd++) {
			Kunde kunde = kundeListe.get(kd);

			// Prüfen, ob die Kunde-Liste null oder zu kurz ist
			if (kunde.getName() == null) {
				continue; // Überspringe ungültige Einträge
			}

			if (kunde.getName().equals(sKdName)) {
				return kunde; // Gib die Kundendaten zurück
			}
		}
		return null;

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

	public static CreatePanel getPanelBillInfo() {
		return panelBillInfo;
	}

	public static String[][] getsTempAN() {
		return sTempAN;
	}

	public static String[][] getsTempRE() {
		return sTempRE;
	}

	public static String[][] getsTempPU() {
		return sTempPU;
	}

	public static String[][] getsTempEX() {
		return sTempEX;
	}

	public static String[][] getsTempST() {
		return sTempST;
	}
	
}

//###################################################################################################################################################
// Klassen für Cell Rendering der Tabellen
//###################################################################################################################################################

class TableANcr extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
		String[][] s = JFoverview.getsTempAN();
		switch(s[row][1]) {
			case "storniert" -> setBackground(Color.PINK);
			case "gedruckt" -> setBackground(new Color(175,238,238)); // hellblau
			case "bestellt" -> setBackground(new Color(37, 204, 196)); // türkis
			case "bestätigt" -> setBackground(new Color(152, 251, 152)); // hellgrün
			}
		return label;
	}
}

//###################################################################################################################################################

class TableREcr extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
		String[][] s = JFoverview.getsTempRE();
		switch(s[row][1]) {
			case "storniert" -> setBackground(Color.PINK);
			case "gedruckt" -> setBackground(new Color(175,238,238)); // hellblau
			case "Zahlungserinnerung" -> setBackground(Color.YELLOW);
			case "Mahnstufe 1" -> setBackground(Color.MAGENTA);
			case "Mahnstufe 2" -> setBackground(Color.RED);
			case "bezahlt" -> setBackground(new Color(152, 251, 152)); // hellgrün
			}
		return label;
	}
}

//###################################################################################################################################################

class TablePUcr extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TablePUcr.class);
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(column == 0 || column == 1 || column == 3 || column == 7 || column == 8) {
			label.setHorizontalAlignment(SwingConstants.CENTER);
		} else if(column == 4 || column == 5 || column == 6 || column == 9){
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		if (row % 2 < 1) {
			setBackground(new Color(10, 10, 10, 10));
		} else {
			setBackground(Color.WHITE);
		}
		String[][] s = JFoverview.getsTempPU();
		if(s[row][0] != null) {
			DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
			LocalDate datePay = LocalDate.parse(s[row][7], inputFormat);
			long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
			int daysPayable = 0;
			try {
				daysPayable = Math.toIntExact(daysBetween);
				if (s[row][8].equals("nein")) {
					if(daysPayable < 0) {
						setBackground(Color.RED); // rot
					}
					if(daysPayable >= 0 && daysPayable < 3) {
						setBackground(Color.PINK); // rot
					}
				}
			} catch (Exception e3) {
				logger.error("error in converting long to integer - " + e3);
			}
			if(s[row][8].equals("ja")) {
				setBackground(new Color(152, 251, 152)); // hellgrün
			}
		} else {
			setBackground(new Color(238,210,238)); // leerzeile für Eingabe kennzeichnen
		}
		return label;
	}
}

//###################################################################################################################################################

class TableEXcr extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(column == 0) {
			label.setHorizontalAlignment(SwingConstants.CENTER);
		} else if(column == 2 || column == 3 || column == 4 || column == 5){
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		if (row % 2 < 1) {
			setBackground(new Color(176,226,255, 100));
		} else {
			setBackground(Color.WHITE);
		}
		String[][] s = JFoverview.getsTempEX();
		if(s[row][0] != null) {
			
		} else {
			setBackground(new Color(238,210,238)); // leerzeile für Eingabe kennzeichnen
		}
		return label;
	}
}

//###################################################################################################################################################

class TableSTcr extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TableSTcr.class);
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(column == 0 || column == 4 || column == 5) {
			label.setHorizontalAlignment(SwingConstants.CENTER);
		} else if(column == 3 || column == 6){
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		if (row % 2 < 1) {
			setBackground(new Color(10, 10, 10, 10));
		} else {
			setBackground(Color.WHITE);
		}
		String[][] s = JFoverview.getsTempST();
		if(s[row][0] != null) {
			DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
			LocalDate datePay = LocalDate.parse(s[row][4], inputFormat);
			long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
			int daysPayable = 0;

			try {
				daysPayable = Math.toIntExact(daysBetween);
			} catch (Exception e3) {
				logger.error("error in converting long to integer - " + e3);
			}
			if (s[row][5].equals("nein")) {
				if(daysPayable < 0) {
					setBackground(Color.RED); // rot
				}
				if(daysPayable >= 0 && daysPayable < 3) {
					setBackground(Color.PINK); // rot
				}
			}
			if (s[row][5].equals("ja")) {
				setBackground(new Color(152, 251, 152)); // hellgrün
			}
		} else {
			setBackground(new Color(238,210,238)); // leerzeile für Eingabe kennzeichnen
		}
		return label;
	}
}
