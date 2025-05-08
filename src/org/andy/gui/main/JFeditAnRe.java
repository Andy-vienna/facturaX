package org.andy.gui.main;

import static org.andy.toolbox.misc.CreateObject.changeKomma;
import static org.andy.toolbox.misc.CreateObject.createButton;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

import org.andy.code.main.StartUp;
import org.andy.code.sql.SQLmasterData;
import org.andy.code.sql.SQLproductiveData;
import org.andy.gui.misc.RoundedBorder;

public class JFeditAnRe extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFeditAnRe.class);
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private static DemoPanel panelDate;
	private static DatePicker datePicker;
	private static JLabel lblLZ = new JLabel("Leistungs-Zr.:");
	private static JTextField txtLZ = new JTextField();
	private static JTextField txtRef = new JTextField();
	private static JLabel[] lblPos = new JLabel[13];
	private static JTextField[] txtPos;
	private static JTextField[] txtAnz;
	private static JTextField[] txtEP;
	private static JTextField[] txtGP;

	private static JTextField txtNetto = new JTextField();
	private static JTextField txtUSt = new JTextField();
	private static JTextField txtBrutto = new JTextField();

	private static JButton btnCalc;
	private static JButton btnUpdate;

	private static int iAnzPos = 0;

	private static String sDatum;
	private static BigDecimal[] bdAnzahl = new BigDecimal[13];
	private static BigDecimal[] bdEinzel = new BigDecimal[13];
	private static BigDecimal[] bdSumme = new BigDecimal[13];
	private static String[] sPosText = new String[13];
	private static String[] sArray = null;
	private static BigDecimal bdNetto;
	private static BigDecimal bdUSt;
	private static BigDecimal bdBrutto;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void showDialog(String[] sTmp, String sReason) {
		try {
			JFeditAnRe frame = new JFeditAnRe(sTmp, sReason);
			frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("showDialog fehlgeschlagen - " + e);
		}
	}

	public JFeditAnRe(String[] sTmp, String sReason) {

		try (InputStream is = JFeditAnRe.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					SQLmasterData.loadBaseData();
					SQLmasterData.loadNummernkreis();
				} catch (SQLException | ParseException | IOException e1) {
					logger.error("JFedit(String[] sTmp, String sReason) - " + e);
				} catch (ClassNotFoundException e1) {
					logger.error("JFedit(String[] sTmp, String sReason) - " + e1);
				}
				JFoverview.loadAngebot(false);
				JFoverview.loadAusgangsRechnung(false);
			}
		});
		setTitle(sReason + " " + sTmp[0] + " bearbeiten");
		setType(Type.POPUP);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 567);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(null);

		// ###################################################################################################################################################
		// ###################################################################################################################################################

		int num = 0;
		switch (sReason) {
		case "Angebot":
			num = 47;
			sArray = new String[47];
			break;
		case "Rechnung":
			num = 51;
			sArray = new String[51];
			break;
		}
		Arrays.fill(sArray, "-");
		for (int zz = 0; zz < num; zz++) {
			if (sTmp[zz].contains("null") /* || sTmp[zz].contains("-") */) {
				sArray[zz] = "-";
			} else {
				sArray[zz] = sTmp[zz];
			}
		}
		txtPos = new JTextField[13];
		txtAnz = new JTextField[13];
		txtEP = new JTextField[13];
		txtGP = new JTextField[13];

		// ###################################################################################################################################################
		// ###################################################################################################################################################

		// ------------------------------------------------------------------------------
		// Felder für Datum, LZ und Referenz anlegen
		// ------------------------------------------------------------------------------
		JLabel lblDate = new JLabel("Datum:");
		lblDate.setHorizontalAlignment(SwingConstants.LEFT);
		lblDate.setBounds(10, 10, 60, 25);
		contentPanel.add(lblDate);

		lblLZ.setHorizontalAlignment(SwingConstants.LEFT);
		lblLZ.setBounds(10, 35, 60, 25);
		contentPanel.add(lblLZ);

		JLabel lblRef = new JLabel("Referenz:");
		lblRef.setHorizontalAlignment(SwingConstants.LEFT);
		lblRef.setBounds(10, 60, 60, 25);
		contentPanel.add(lblRef);

		panelDate = new DemoPanel();
		panelDate.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettings = new DatePickerSettings();
		dateSettings.setWeekNumbersDisplayed(true, true);
		dateSettings.setFormatForDatesCommonEra("dd.MM.yyyy");
		datePicker = new DatePicker(dateSettings);
		datePicker.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePicker.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = datePicker.getDate();
				if (selectedDate != null) {
					sDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sDatum = null;
				}
			}
		});
		datePicker.setBounds(72, 10, 160, 25);
		contentPanel.add(datePicker);

		txtLZ.setHorizontalAlignment(SwingConstants.LEFT);
		txtLZ.setBounds(70, 35, 160, 25);
		contentPanel.add(txtLZ);

		txtRef.setHorizontalAlignment(SwingConstants.LEFT);
		txtRef.setBounds(70, 60, 605, 25);
		contentPanel.add(txtRef);

		// ------------------------------------------------------------------------------
		// Spaltenbeschriftung einfügen
		// ------------------------------------------------------------------------------
		JLabel lblNewLabel_12 = new JLabel("Nr.");
		lblNewLabel_12.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12.setBounds(10, 90, 25, 20);
		contentPanel.add(lblNewLabel_12);

		JLabel lblNewLabel_12_2 = new JLabel("Position");
		lblNewLabel_12_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12_2.setBounds(40, 90, 340, 20);
		contentPanel.add(lblNewLabel_12_2);

		JLabel lblNewLabel_12_3 = new JLabel("Anz.");
		lblNewLabel_12_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12_3.setBounds(480, 90, 40, 20);
		contentPanel.add(lblNewLabel_12_3);

		JLabel lblNewLabel_12_4 = new JLabel("Einzel");
		lblNewLabel_12_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12_4.setBounds(525, 90, 70, 20);
		contentPanel.add(lblNewLabel_12_4);

		JLabel lblNewLabel_12_5 = new JLabel("Summe");
		lblNewLabel_12_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_12_5.setBounds(600, 90, 70, 20);
		contentPanel.add(lblNewLabel_12_5);

		// ------------------------------------------------------------------------------
		// Positionsfelder aufbauen
		// ------------------------------------------------------------------------------
		for (int x = 1; x < 13; x++) {
			final int index = x; // Speichert den aktuellen Wert von x

			lblPos[x] = new JLabel(String.valueOf(x));
			lblPos[x].setHorizontalAlignment(SwingConstants.CENTER);
			lblPos[x].setBounds(10, 115 + ((x - 1) * 25), 20, 25);
			lblPos[x].setVisible(false);
			contentPanel.add(lblPos[x]);

			txtPos[x] = new JTextField();
			txtPos[x].setHorizontalAlignment(SwingConstants.LEFT);
			txtPos[x].setBounds(40, 115 + ((x - 1) * 25), 440, 25);
			txtPos[x].setVisible(false);
			contentPanel.add(txtPos[x]);

			txtAnz[x] = new JTextField();
			txtAnz[x].setHorizontalAlignment(SwingConstants.CENTER);
			txtAnz[x].setBounds(485, 115 + ((x - 1) * 25), 40, 25);
			txtAnz[x].setVisible(false);
			txtAnz[x].addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					SwingUtilities.invokeLater(() -> txtAnz[index].setText(changeKomma(txtAnz[index])));
				}
			});
			contentPanel.add(txtAnz[x]);

			txtEP[x] = new JTextField();
			txtEP[x].setHorizontalAlignment(SwingConstants.RIGHT);
			txtEP[x].setBounds(530, 115 + ((x - 1) * 25), 70, 25);
			txtEP[x].setVisible(false);
			txtEP[x].addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					SwingUtilities.invokeLater(() -> txtEP[index].setText(changeKomma(txtEP[index])));
				}
			});
			contentPanel.add(txtEP[x]);

			txtGP[x] = new JTextField();
			txtGP[x].setHorizontalAlignment(SwingConstants.RIGHT);
			txtGP[x].setBounds(605, 115 + ((x - 1) * 25), 70, 25);
			txtGP[x].setVisible(false);
			txtGP[x].addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					SwingUtilities.invokeLater(() -> txtGP[index].setText(changeKomma(txtGP[index])));
				}
			});
			contentPanel.add(txtGP[x]);
		}

		// ------------------------------------------------------------------------------
		// Summenfelder aufbauen
		// ------------------------------------------------------------------------------
		JLabel lblNewLabel_20 = new JLabel("netto: ");
		lblNewLabel_20.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_20.setBounds(530, 440, 70, 25);
		lblNewLabel_20.setVisible(false);
		contentPanel.add(lblNewLabel_20);

		JLabel lblNewLabel_21 = new JLabel("USt.: ");
		lblNewLabel_21.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_21.setBounds(530, 465, 70, 25);
		lblNewLabel_21.setVisible(false);
		contentPanel.add(lblNewLabel_21);

		JLabel lblNewLabel_22 = new JLabel("brutto: ");
		lblNewLabel_22.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_22.setBounds(530, 490, 70, 25);
		lblNewLabel_22.setVisible(false);
		contentPanel.add(lblNewLabel_22);

		txtNetto.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNetto.setBounds(605, 440, 70, 25);
		txtNetto.setVisible(false);
		contentPanel.add(txtNetto);

		txtUSt.setHorizontalAlignment(SwingConstants.RIGHT);
		txtUSt.setBounds(605, 465, 70, 25);
		txtUSt.setVisible(false);
		contentPanel.add(txtUSt);

		txtBrutto.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBrutto.setBounds(605, 490, 70, 25);
		txtBrutto.setVisible(false);
		contentPanel.add(txtBrutto);

		// ------------------------------------------------------------------------------
		// Buttons
		// ------------------------------------------------------------------------------
		try {
			btnCalc = createButton("<html>aktualisieren</html>", "aktualisieren.png");
			btnUpdate = createButton("<html>update</html>", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnCalc.setBounds(40, 440, 140, 55);
		btnCalc.setEnabled(true);
		contentPanel.add(btnCalc);

		btnUpdate.setBounds(201, 440, 140, 55);
		btnUpdate.setEnabled(false);
		contentPanel.add(btnUpdate);

		switch (sReason) {
		case "Angebot":
			loadEditOffer(sArray);
			btnUpdate.setText("<html>Angebot<br>updaten</html>");
			break;
		case "Rechnung":
			loadEditBill(sArray);
			btnUpdate.setText("<html>Rechnung<br>updaten</html>");
			break;
		}
		contentPanel.revalidate();
		contentPanel.repaint();

		// ------------------------------------------------------------------------------
		// Actionlistener für Buttons einbauen
		// ------------------------------------------------------------------------------
		btnCalc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initializeVar();
				int n = 1;
				for (n = 1; n < 13; n++) {
					if ((txtPos[n].getText().isEmpty()) || (txtPos[n].getText() == null)) {
						for (int m = n; m < 13; m++) {
							Arrays.fill(sPosText, "");
							Arrays.fill(bdAnzahl, new BigDecimal("0.00"));
							Arrays.fill(bdEinzel, new BigDecimal("0.00"));
							Arrays.fill(bdSumme, new BigDecimal("0.00"));
							txtPos[m].setText("");
							txtAnz[m].setText("");
							txtEP[m].setText("");
							txtGP[m].setText("");
						}
						break;
					} else {
						try {
							bdAnzahl[n] = new BigDecimal(txtAnz[n].getText().replace(",", ".")).setScale(2,
									RoundingMode.HALF_UP);
							bdEinzel[n] = new BigDecimal(txtEP[n].getText().replace(",", ".")).setScale(2,
									RoundingMode.HALF_UP);
							bdSumme[n] = bdAnzahl[n].multiply(bdEinzel[n]).setScale(2, RoundingMode.HALF_UP);
							txtAnz[n].setText(bdAnzahl[n].toPlainString());
							txtEP[n].setText(bdEinzel[n].toPlainString());
							txtGP[n].setText(bdSumme[n].toPlainString());
							bdNetto = bdNetto.add(bdSumme[n]);
						} catch (NumberFormatException ex) {
							logger.error("JFedit(String[] sTmp, String sReason) - " + ex);
						}
					}
				}
				switch (sReason) {
				case "Angebot":
					for (int i = 0; i < SQLmasterData.getAnzKunde(); i++) {
						ArrayList<String> Kunde = SQLmasterData.getArrListKunde().get(i);
						if (Kunde.get(0).toString().equals(sArray[7])) {
							try {
								// BigDecimal tmp = new BigDecimal(SQLsource.arrKunde[i][10].replace(",",
								// ".")).setScale(2, RoundingMode.HALF_UP);
								BigDecimal tmp = new BigDecimal(Kunde.get(9).toString().replace(",", ".")).setScale(2,
										RoundingMode.HALF_UP);
								BigDecimal tmp2 = new BigDecimal("100.00");
								bdUSt = tmp.divide(tmp2).setScale(2, RoundingMode.HALF_UP);
								bdBrutto = bdNetto.add(bdNetto.multiply(bdUSt)).setScale(2, RoundingMode.HALF_UP);
								txtNetto.setText(bdNetto.setScale(2, RoundingMode.HALF_UP).toPlainString());
								lblNewLabel_20.setVisible(true);
								txtNetto.setVisible(true);
							} catch (NumberFormatException ex) {
								logger.error("JFedit(String[] sTmp, String sReason) - " + ex);
							}
						} else {
							bdUSt = new BigDecimal("0.00");
						}
					}
					lblNewLabel_21.setVisible(false);
					lblNewLabel_22.setVisible(false);
					txtUSt.setVisible(false);
					txtBrutto.setVisible(false);
					btnUpdate.setEnabled(true);
					break;
				case "Rechnung":
					for (int i = 0; i < SQLmasterData.getAnzKunde(); i++) {
						ArrayList<String> Kunde = SQLmasterData.getArrListKunde().get(i);
						if (Kunde.get(0).toString().equals(sArray[8])) {
							try {
								// BigDecimal tmp = new BigDecimal(SQLsource.arrKunde[i][10].replace(",",
								// ".")).setScale(2, RoundingMode.HALF_UP);
								BigDecimal tmp = new BigDecimal(Kunde.get(9).toString().replace(",", ".")).setScale(2,
										RoundingMode.HALF_UP);
								BigDecimal tmp2 = new BigDecimal("100.00");
								bdUSt = tmp.divide(tmp2).setScale(2, RoundingMode.HALF_UP);
								bdBrutto = bdNetto.add(bdNetto.multiply(bdUSt)).setScale(2, RoundingMode.HALF_UP);
								txtNetto.setText(bdNetto.setScale(2, RoundingMode.HALF_UP).toPlainString());
								txtUSt.setText(
										(bdNetto.multiply(bdUSt).setScale(2, RoundingMode.HALF_UP)).toPlainString());
								txtBrutto.setText(bdBrutto.setScale(2, RoundingMode.HALF_UP).toPlainString());
								lblNewLabel_20.setVisible(true);
								txtNetto.setVisible(true);
							} catch (NumberFormatException ex) {
								logger.error("JFedit(String[] sTmp, String sReason) - " + ex);
							}
						} else {
							bdUSt = new BigDecimal("0.00");
						}
					}
					lblNewLabel_21.setVisible(true);
					lblNewLabel_22.setVisible(true);
					txtUSt.setVisible(true);
					txtBrutto.setVisible(true);
					btnUpdate.setEnabled(true);
					break;
				}
			}
		});
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int a = 1; a < 13; a++) {
					if (txtPos[a].getText().length() > 5) {
						sPosText[a] = txtPos[a].getText();
						try {
							bdAnzahl[a] = new BigDecimal(txtAnz[a].getText().replace(",", ".")).setScale(2,
									RoundingMode.HALF_UP);
							bdEinzel[a] = new BigDecimal(txtEP[a].getText().replace(",", ".")).setScale(2,
									RoundingMode.HALF_UP);
						} catch (NumberFormatException ex) {
							logger.error("JFedit(String[] sTmp, String sReason) - " + ex);
						}
						bdSumme[a] = bdAnzahl[a].multiply(bdEinzel[a]).setScale(2, RoundingMode.HALF_UP);
						iAnzPos = a;
					}
				}
				switch (sReason) {
				case "Angebot":
					try {
						SQLproductiveData.updateAnToDB(sTmp[0], sDatum, txtRef.getText(), iAnzPos, sPosText, bdAnzahl,
								bdEinzel, txtNetto.getText());
					} catch (ClassNotFoundException | SQLException e1) {
						logger.error("error updating offer to database - " + e1);
					}
					break;
				case "Rechnung":
					try {
						SQLproductiveData.updateReToDB(sTmp[0], sDatum, txtLZ.getText(), txtRef.getText(), iAnzPos, sPosText,
								bdAnzahl, bdEinzel, txtNetto.getText(), txtUSt.getText(), txtBrutto.getText());
					} catch (ClassNotFoundException | SQLException e1) {
						logger.error("error updating bill to database - " + e1);
					}
					break;
				}
				dispose();
			}
		});

	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	private static void loadEditOffer(String[] stmp) {
		String[] sArray = new String[47];
		Arrays.fill(sArray, "-");
		for (int zz = 0; zz < 47; zz++) {
			if (stmp[zz].contains("null")) {
				sArray[zz] = "-";
			} else {
				sArray[zz] = stmp[zz];
			}
		}

		LocalDate datum = LocalDate.parse(stmp[5], StartUp.getDfdate());
		datePicker.setDate(datum);

		// txtDate.setText(stmp[5]);
		txtRef.setText(stmp[6]);
		int n = 1;
		int m = 11;
		int numArt = 0;
		try {
			numArt = Integer.valueOf(sArray[10]);
			while (n < (numArt + 1)) {
				sPosText[n] = sArray[m];
				bdAnzahl[n] = new BigDecimal(sArray[m + 1]).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[n] = new BigDecimal(sArray[m + 2]).setScale(2, RoundingMode.HALF_UP);
				txtPos[n].setText(sArray[m]);
				txtAnz[n].setText(sArray[m + 1]);
				txtEP[n].setText(sArray[m + 2]);
				n++;
				m = m + 3;
			}
			for (int x = n; x < 13; x++) {
				bdAnzahl[x] = new BigDecimal("0.00");
				bdEinzel[x] = new BigDecimal("0.00");
				bdSumme[x] = new BigDecimal("0.00");
				txtPos[x].setText("");
				txtAnz[x].setText("");
				txtEP[x].setText("");
				txtGP[x].setText("");
			}
		} catch (NumberFormatException ex) {
			logger.error("loadEditOffer(String[] stmp) - " + ex);
		}
		lblLZ.setVisible(false);
		txtLZ.setVisible(false);
		viewFields(numArt);
	}

	private static void loadEditBill(String[] stmp) {
		String[] sArray = new String[51];
		Arrays.fill(sArray, "-");
		for (int zz = 0; zz < 51; zz++) {
			if (stmp[zz].contains("null")) {
				sArray[zz] = "-";
			} else {
				sArray[zz] = stmp[zz];
			}
		}

		LocalDate datum = LocalDate.parse(stmp[5], StartUp.getDfdate());
		datePicker.setDate(datum);

		// txtDate.setText(stmp[5]);
		txtLZ.setText(stmp[6]);
		txtRef.setText(stmp[7]);
		int n = 1;
		int m = 15;
		int numArt = 0;
		try {
			numArt = Integer.valueOf(sArray[14]);
			while (n < (numArt + 1)) {
				sPosText[n] = sArray[m];
				bdAnzahl[n] = new BigDecimal(sArray[m + 1]).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[n] = new BigDecimal(sArray[m + 2]).setScale(2, RoundingMode.HALF_UP);
				txtPos[n].setText(sArray[m]);
				txtAnz[n].setText(sArray[m + 1]);
				txtEP[n].setText(sArray[m + 2]);
				n++;
				m = m + 3;
			}
		} catch (NumberFormatException ex) {
			logger.error("loadEditOffer(String[] stmp) - " + ex);
		}
		lblLZ.setVisible(true);
		txtLZ.setVisible(true);
		viewFields(numArt);
	}

	public static void initializeVar() {
		for (int r = 1; r < 13; r++) {
			Arrays.fill(sPosText, "");
			Arrays.fill(bdAnzahl, new BigDecimal("0.00"));
			Arrays.fill(bdEinzel, new BigDecimal("0.00"));
			Arrays.fill(bdSumme, new BigDecimal("0.00"));
		}
		bdNetto = new BigDecimal("0.00");
		bdUSt = new BigDecimal("0.00");
		bdBrutto = new BigDecimal("0.00");
	}

	private static void viewFields(int iNumber) {
		int x = 1;
		int y = 1;
		int iAnz = 0;
		if (iNumber < 11) {
			iAnz = iNumber + 2;
		} else if (iNumber > 10 && iNumber < 13) {
			iAnz = iNumber;
		}
		for (x = 1; x < (iAnz + 1); x++) {
			lblPos[x].setVisible(true);
			txtPos[x].setVisible(true);
			txtAnz[x].setVisible(true);
			txtEP[x].setVisible(true);
			txtGP[x].setVisible(true);
		}
		for (y = x; y < 13; y++) {
			lblPos[y].setVisible(false);
			txtPos[y].setVisible(false);
			txtAnz[y].setVisible(false);
			txtEP[y].setVisible(false);
			txtGP[y].setVisible(false);
		}
	}
}
