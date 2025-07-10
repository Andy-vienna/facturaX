package org.andy.code.main.overview;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Read.sqlReadArray;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.andy.code.main.LoadData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnnualResult {
	
	private static final Logger logger = LogManager.getLogger(AnnualResult.class);
	
	private static final int iLeft = 10, iTop = 50, iHeight = 25, iWidth = 400;
	
	private static String[][] arrTaxValues = new String[2][25];
	private static String[][] arrGwbValues = new String[2][10];
	private static String sConn = null;
	
	private static BigDecimal bdExpNetto = BigDecimal.ZERO;
	private static BigDecimal bdSVQ1 = BigDecimal.ZERO, bdSVQ2 = BigDecimal.ZERO, bdSVQ3 = BigDecimal.ZERO, bdSVQ4 = BigDecimal.ZERO;
	private static BigDecimal bdSVYear = BigDecimal.ZERO;

	private static JSeparator sep1 = new JSeparator(), sep2 = new JSeparator(), sep3 = new JSeparator(), sep4 = new JSeparator(),
		sep5 = new JSeparator(), sep6 = new JSeparator(), sep7 = new JSeparator(), sep8 = new JSeparator(), sep9 = new JSeparator(), sep10 = new JSeparator(),
		sep11 = new JSeparator(), sep12 = new JSeparator(), sep13 = new JSeparator();
	
	private static JLabel lblQ1, lblQ2, lblQ3, lblQ4, lblYear;
	private static JLabel lblOvText0, lblOvText1, lblOvText2, lblOvText3, lblOvText4, lblOvText5, lblOvText6, lblOvText7;
	
	private static JLabel lblP109aText0, lblP109aText1, lblP109aText2, lblP109aText3, lblP109aText4, lblP109aText5, lblP109aText6, lblP109aText7,
		lblP109aText8, lblP109aText9, lblP109aText10;
	
	private static JLabel lblGwbHinweis, lblVorGWB, lblGwbStufe1, lblGwbStufe2, lblGwbStufe3, lblGwbStufe4, lblGwbTotal;
	
	private static JLabel lblE1Hinweis, lblE1VorSt, lblE1Text1, lblE1Text2, lblE1Stufe1, lblE1Stufe2, lblE1Stufe3, lblE1Stufe4, lblE1Stufe5,
		lblE1Stufe6, lblE1Stufe7, lblE1Summe;
	
	private static JFormattedTextField txt000Q1, txt000Q2, txt000Q3, txt000Q4, txt000Year;
	private static JFormattedTextField txt010Q1, txt010Q2, txt010Q3, txt010Q4, txt010Year;
	private static JFormattedTextField txt021Q1, txt021Q2, txt021Q3, txt021Q4, txt021Year;
	private static JFormattedTextField txt066Q1, txt066Q2, txt066Q3, txt066Q4, txt066Year;
	private static JFormattedTextField txt067Q1, txt067Q2, txt067Q3, txt067Q4, txt067Year;
	private static JFormattedTextField txt068Q1, txt068Q2, txt068Q3, txt068Q4, txt068Year;
	private static JFormattedTextField txtZahlLastQ1, txtZahlLastQ2, txtZahlLastQ3, txtZahlLastQ4, txtZahlLastYear;
	
	private static JFormattedTextField txtP109aEin, txtP109aSVS1, txtP109aSVS2, txtP109aSVS3, txtP109aSVS4, txtP109aSVSall,
		txtP109aOeffiP, txtP109aAPausch, txtP109aExpenses, txtP109aGrundfrei, txtP109aErgebnis;
	
	private static JFormattedTextField txtVorGWB, txtGwbStufe1, txtGwbStufe2, txtGwbStufe3, txtGwbStufe4, txtGwbTotal;
	
	private static JFormattedTextField txtE1Stufe1, txtE1Stufe2, txtE1Stufe3, txtE1Stufe4, txtE1Stufe5, txtE1Stufe6, txtE1Stufe7;
	private static JFormattedTextField txtE1VorSt, txtE1Tax1, txtE1Tax2, txtE1Tax3, txtE1Tax4, txtE1Tax5, txtE1Tax6, txtE1Tax7,	txtE1Summe;
	
	public static JButton btnExportP109a;
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	public static void setValues(int AnzYearBillIn, int AnzYearBillOut, int AnzExpenses, String[][] arrYearBillIn, String[][] arrYearBillOut, String[][] arrExpenses) {
		getDBData(); // Steuergrenzen und Gewinnfreibetragsgrenzen aus DB lesen
		setValuesUVA(AnzYearBillIn, AnzYearBillOut, AnzExpenses, arrYearBillIn, arrYearBillOut, arrExpenses);
		setValuesTax(AnzYearBillOut, arrYearBillOut);
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	public static JPanel createUStPanel() {
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		
		JPanel content = new JPanel(null);
		
		lblOvText0 = new JLabel("Umsatzsteuer-Voranmeldung (UVA)");
		lblOvText1 = new JLabel("000 - Umsätze zum Normalsteuersatz (20%)");
		lblOvText2 = new JLabel("010 - Steuer davon (20%)");
		lblOvText3 = new JLabel("021 - Innergemeinschaftliche sonstige Leistungen (steuerfrei, z.B. DE B2B)");
		lblOvText4 = new JLabel("066 - Vorsteuer aus Rechnungen mit 20%");
		lblOvText5 = new JLabel("067 - Vorsteuer aus Rechnungen mit 10%");
		lblOvText6 = new JLabel("068 - Vorsteuer aus Rechnungen mit 13%");
		lblOvText7 = new JLabel("Zahllast - USt. (010) minus Summe Vorsteuern (066, 067, 068)");
		
		lblQ1 = new JLabel("Q1 - " + LoadData.getStrAktGJ());
		lblQ1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblQ1.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ2 = new JLabel("Q2 - " + LoadData.getStrAktGJ());
		lblQ2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblQ2.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ3 = new JLabel("Q3 - " + LoadData.getStrAktGJ());
		lblQ3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblQ3.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ4 = new JLabel("Q4 - " + LoadData.getStrAktGJ());
		lblQ4.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblQ4.setHorizontalAlignment(SwingConstants.CENTER);
		lblYear = new JLabel("U1 - " + LoadData.getStrAktGJ());
		lblYear.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblYear.setHorizontalAlignment(SwingConstants.CENTER);
		
		lblOvText0.setBounds( iLeft, 20, iWidth, iHeight);
		lblOvText1.setBounds( iLeft, iTop + 0 * iHeight, iWidth, iHeight);
		lblOvText2.setBounds( iLeft, iTop + 1 * iHeight, iWidth, iHeight);
		lblOvText3.setBounds( iLeft, iTop + 2 * iHeight, iWidth, iHeight);
		lblOvText4.setBounds( iLeft, iTop + 3 * iHeight, iWidth, iHeight);
		lblOvText5.setBounds( iLeft, iTop + 4 * iHeight, iWidth, iHeight);
		lblOvText6.setBounds( iLeft, iTop + 5 * iHeight, iWidth, iHeight);
		
		lblOvText0.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		lblOvText7.setBounds( iLeft, 215, iWidth, iHeight);
		lblOvText7.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOvText7.setForeground(Color.BLUE);
		
		lblQ1.setBounds( iLeft + 400, 20, 150, iHeight);
		lblQ2.setBounds( iLeft + 560, 20, 150, iHeight);
		lblQ3.setBounds( iLeft + 720, 20, 150, iHeight);
		lblQ4.setBounds( iLeft + 880, 20, 150, iHeight);
		lblYear.setBounds( iLeft + 1040, 20, 150, iHeight);
		
		lblQ1.setText("Q1 - " + LoadData.getStrAktGJ());
		lblQ2.setText("Q2 - " + LoadData.getStrAktGJ());
		lblQ3.setText("Q3 - " + LoadData.getStrAktGJ());
		lblQ4.setText("Q4 - " + LoadData.getStrAktGJ());
		
		txt000Q1 = new JFormattedTextField(currencyFormat);
		txt000Q1.setBounds( iLeft + 400, iTop + 0 * iHeight, 150, iHeight);
		txt000Q1.setHorizontalAlignment(SwingConstants.RIGHT);
		txt000Q1.setFocusable(false);
		
		txt000Q2 = new JFormattedTextField(currencyFormat);
		txt000Q2.setBounds( iLeft + 560, iTop + 0 * iHeight, 150, iHeight);
		txt000Q2.setHorizontalAlignment(SwingConstants.RIGHT);
		txt000Q2.setFocusable(false);
		
		txt000Q3 = new JFormattedTextField(currencyFormat);
		txt000Q3.setBounds( iLeft + 720, iTop + 0 * iHeight, 150, iHeight);
		txt000Q3.setHorizontalAlignment(SwingConstants.RIGHT);
		txt000Q3.setFocusable(false);
		
		txt000Q4 = new JFormattedTextField(currencyFormat);
		txt000Q4.setBounds( iLeft + 880, iTop + 0 * iHeight, 150, iHeight);
		txt000Q4.setHorizontalAlignment(SwingConstants.RIGHT);
		txt000Q4.setFocusable(false);
		
		txt010Q1 = new JFormattedTextField(currencyFormat);
		txt010Q1.setBounds( iLeft + 400, iTop + 1 * iHeight, 150, iHeight);
		txt010Q1.setHorizontalAlignment(SwingConstants.RIGHT);
		txt010Q1.setFocusable(false);
		
		txt010Q2 = new JFormattedTextField(currencyFormat);
		txt010Q2.setBounds( iLeft + 560, iTop + 1 * iHeight, 150, iHeight);
		txt010Q2.setHorizontalAlignment(SwingConstants.RIGHT);
		txt010Q2.setFocusable(false);
		
		txt010Q3 = new JFormattedTextField(currencyFormat);
		txt010Q3.setBounds( iLeft + 720, iTop + 1 * iHeight, 150, iHeight);
		txt010Q3.setHorizontalAlignment(SwingConstants.RIGHT);
		txt010Q3.setFocusable(false);
		
		txt010Q4 = new JFormattedTextField(currencyFormat);
		txt010Q4.setBounds( iLeft + 880, iTop + 1 * iHeight, 150, iHeight);
		txt010Q4.setHorizontalAlignment(SwingConstants.RIGHT);
		txt010Q4.setFocusable(false);
		
		txt021Q1 = new JFormattedTextField(currencyFormat);
		txt021Q1.setBounds( iLeft + 400, iTop + 2 * iHeight, 150, iHeight);
		txt021Q1.setHorizontalAlignment(SwingConstants.RIGHT);
		txt021Q1.setFocusable(false);
		
		txt021Q2 = new JFormattedTextField(currencyFormat);
		txt021Q2.setBounds( iLeft + 560, iTop + 2 * iHeight, 150, iHeight);
		txt021Q2.setHorizontalAlignment(SwingConstants.RIGHT);
		txt021Q2.setFocusable(false);
		
		txt021Q3 = new JFormattedTextField(currencyFormat);
		txt021Q3.setBounds( iLeft + 720, iTop + 2 * iHeight, 150, iHeight);
		txt021Q3.setHorizontalAlignment(SwingConstants.RIGHT);
		txt021Q3.setFocusable(false);
		
		txt021Q4 = new JFormattedTextField(currencyFormat);
		txt021Q4.setBounds( iLeft + 880, iTop + 2 * iHeight, 150, iHeight);
		txt021Q4.setHorizontalAlignment(SwingConstants.RIGHT);
		txt021Q4.setFocusable(false);
		
		txt066Q1 = new JFormattedTextField(currencyFormat);
		txt066Q1.setBounds( iLeft + 400, iTop + 3 * iHeight, 150, iHeight);
		txt066Q1.setHorizontalAlignment(SwingConstants.RIGHT);
		txt066Q1.setFocusable(false);
		
		txt066Q2 = new JFormattedTextField(currencyFormat);
		txt066Q2.setBounds( iLeft + 560, iTop + 3 * iHeight, 150, iHeight);
		txt066Q2.setHorizontalAlignment(SwingConstants.RIGHT);
		txt066Q2.setFocusable(false);
		
		txt066Q3 = new JFormattedTextField(currencyFormat);
		txt066Q3.setBounds( iLeft + 720, iTop + 3 * iHeight, 150, iHeight);
		txt066Q3.setHorizontalAlignment(SwingConstants.RIGHT);
		txt066Q3.setFocusable(false);
		
		txt066Q4 = new JFormattedTextField(currencyFormat);
		txt066Q4.setBounds( iLeft + 880, iTop + 3 * iHeight, 150, iHeight);
		txt066Q4.setHorizontalAlignment(SwingConstants.RIGHT);
		txt066Q4.setFocusable(false);
		
		txt067Q1 = new JFormattedTextField(currencyFormat);
		txt067Q1.setBounds( iLeft + 400, iTop + 4 * iHeight, 150, iHeight);
		txt067Q1.setHorizontalAlignment(SwingConstants.RIGHT);
		txt067Q1.setFocusable(false);
		
		txt067Q2 = new JFormattedTextField(currencyFormat);
		txt067Q2.setBounds( iLeft + 560, iTop + 4 * iHeight, 150, iHeight);
		txt067Q2.setHorizontalAlignment(SwingConstants.RIGHT);
		txt067Q2.setFocusable(false);
		
		txt067Q3 = new JFormattedTextField(currencyFormat);
		txt067Q3.setBounds( iLeft + 720, iTop + 4 * iHeight, 150, iHeight);
		txt067Q3.setHorizontalAlignment(SwingConstants.RIGHT);
		txt067Q3.setFocusable(false);
		
		txt067Q4 = new JFormattedTextField(currencyFormat);
		txt067Q4.setBounds( iLeft + 880, iTop + 4 * iHeight, 150, iHeight);
		txt067Q4.setHorizontalAlignment(SwingConstants.RIGHT);
		txt067Q4.setFocusable(false);
		
		txt068Q1 = new JFormattedTextField(currencyFormat);
		txt068Q1.setBounds( iLeft + 400, iTop + 5 * iHeight, 150, iHeight);
		txt068Q1.setHorizontalAlignment(SwingConstants.RIGHT);
		txt068Q1.setFocusable(false);
		
		txt068Q2 = new JFormattedTextField(currencyFormat);
		txt068Q2.setBounds( iLeft + 560, iTop + 5 * iHeight, 150, iHeight);
		txt068Q2.setHorizontalAlignment(SwingConstants.RIGHT);
		txt068Q2.setFocusable(false);
		
		txt068Q3 = new JFormattedTextField(currencyFormat);
		txt068Q3.setBounds( iLeft + 720, iTop + 5 * iHeight, 150, iHeight);
		txt068Q3.setHorizontalAlignment(SwingConstants.RIGHT);
		txt068Q3.setFocusable(false);
		
		txt068Q4 = new JFormattedTextField(currencyFormat);
		txt068Q4.setBounds( iLeft + 880, iTop + 5 * iHeight, 150, iHeight);
		txt068Q4.setHorizontalAlignment(SwingConstants.RIGHT);
		txt068Q4.setFocusable(false);
		
		txt000Year = new JFormattedTextField(currencyFormat);
		txt000Year.setBounds( iLeft + 1040, iTop + 0 * iHeight, 150, iHeight);
		txt000Year.setFont(new Font("Tahoma", Font.BOLD, 11));
		txt000Year.setHorizontalAlignment(SwingConstants.RIGHT);
		txt000Year.setFocusable(false);
		
		txt010Year = new JFormattedTextField(currencyFormat);
		txt010Year.setBounds( iLeft + 1040, iTop + 1 * iHeight, 150, iHeight);
		txt010Year.setFont(new Font("Tahoma", Font.BOLD, 11));
		txt010Year.setHorizontalAlignment(SwingConstants.RIGHT);
		txt010Year.setFocusable(false);
		
		txt021Year = new JFormattedTextField(currencyFormat);
		txt021Year.setBounds( iLeft + 1040, iTop + 2 * iHeight, 150, iHeight);
		txt021Year.setFont(new Font("Tahoma", Font.BOLD, 11));
		txt021Year.setHorizontalAlignment(SwingConstants.RIGHT);
		txt021Year.setFocusable(false);
		
		txt066Year = new JFormattedTextField(currencyFormat);
		txt066Year.setBounds( iLeft + 1040, iTop + 3 * iHeight, 150, iHeight);
		txt066Year.setFont(new Font("Tahoma", Font.BOLD, 11));
		txt066Year.setHorizontalAlignment(SwingConstants.RIGHT);
		txt066Year.setFocusable(false);
		
		txt067Year = new JFormattedTextField(currencyFormat);
		txt067Year.setBounds( iLeft + 1040, iTop + 4 * iHeight, 150, iHeight);
		txt067Year.setFont(new Font("Tahoma", Font.BOLD, 11));
		txt067Year.setHorizontalAlignment(SwingConstants.RIGHT);
		txt067Year.setFocusable(false);
		
		txt068Year = new JFormattedTextField(currencyFormat);
		txt068Year.setBounds( iLeft + 1040, iTop + 5 * iHeight, 150, iHeight);
		txt068Year.setFont(new Font("Tahoma", Font.BOLD, 11));
		txt068Year.setHorizontalAlignment(SwingConstants.RIGHT);
		txt068Year.setFocusable(false);
		
		txtZahlLastQ1 = new JFormattedTextField(currencyFormat);
		txtZahlLastQ1.setBounds( iLeft + 400, 215, 150, iHeight);
		txtZahlLastQ1.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtZahlLastQ1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtZahlLastQ1.setForeground(Color.BLUE);
		txtZahlLastQ1.setFocusable(false);
		
		txtZahlLastQ2 = new JFormattedTextField(currencyFormat);
		txtZahlLastQ2.setBounds( iLeft + 560, 215, 150, iHeight);
		txtZahlLastQ2.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtZahlLastQ2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtZahlLastQ2.setForeground(Color.BLUE);
		txtZahlLastQ2.setFocusable(false);
		
		txtZahlLastQ3 = new JFormattedTextField(currencyFormat);
		txtZahlLastQ3.setBounds( iLeft + 720, 215, 150, iHeight);
		txtZahlLastQ3.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtZahlLastQ3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtZahlLastQ3.setForeground(Color.BLUE);
		txtZahlLastQ3.setFocusable(false);
		
		txtZahlLastQ4 = new JFormattedTextField(currencyFormat);
		txtZahlLastQ4.setBounds( iLeft + 880, 215, 150, iHeight);
		txtZahlLastQ4.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtZahlLastQ4.setHorizontalAlignment(SwingConstants.RIGHT);
		txtZahlLastQ4.setForeground(Color.BLUE);
		txtZahlLastQ4.setFocusable(false);
		
		txtZahlLastYear = new JFormattedTextField(currencyFormat);
		txtZahlLastYear.setBounds( iLeft + 1040, 215, 150, iHeight);
		txtZahlLastYear.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtZahlLastYear.setHorizontalAlignment(SwingConstants.RIGHT);
		txtZahlLastYear.setForeground(Color.BLUE);
		txtZahlLastYear.setFocusable(false);
		
		sep1.setBounds(405, 20, 5, 230);
		sep1.setOrientation(SwingConstants.VERTICAL);
		sep2.setBounds(iLeft, 45, iLeft + 1040 + 150 - 5, 5);
		sep2.setOrientation(SwingConstants.HORIZONTAL);
		sep3.setBounds(iLeft, 205, iLeft + 1040 + 150 - 5, 5);
		sep3.setOrientation(SwingConstants.HORIZONTAL);
		sep4.setBounds(iLeft + 400 + 150 + 5, 20, 5, 230);
		sep4.setOrientation(SwingConstants.VERTICAL);
		sep5.setBounds(iLeft + 560 + 150 + 5, 20, 5, 230);
		sep5.setOrientation(SwingConstants.VERTICAL);
		sep6.setBounds(iLeft + 720 + 150 + 5, 20, 5, 230);
		sep6.setOrientation(SwingConstants.VERTICAL);
		sep7.setBounds(iLeft + 880 + 150 + 5, 20, 5, 230);
		sep7.setOrientation(SwingConstants.VERTICAL);
		
		content.setPreferredSize(new Dimension(iLeft + 1040 + 150 + 10, 250)); // "Größe des Inhalts"
		
		content.add(lblOvText0);
		content.add(lblOvText1);
		content.add(lblOvText2);
		content.add(lblOvText3);
		content.add(lblOvText4);
		content.add(lblOvText5);
		content.add(lblOvText6);
		content.add(lblOvText7);
		
		content.add(lblQ1);
		content.add(lblQ2);
		content.add(lblQ3);
		content.add(lblQ4);
		content.add(lblYear);
		
		content.add(txt000Q1);
		content.add(txt000Q2);
		content.add(txt000Q3);
		content.add(txt000Q4);
		content.add(txt010Q1);
		content.add(txt010Q2);
		content.add(txt010Q3);
		content.add(txt010Q4);
		content.add(txt021Q1);
		content.add(txt021Q2);
		content.add(txt021Q3);
		content.add(txt021Q4);
		content.add(txt066Q1);
		content.add(txt066Q2);
		content.add(txt066Q3);
		content.add(txt066Q4);
		content.add(txt067Q1);
		content.add(txt067Q2);
		content.add(txt067Q3);
		content.add(txt067Q4);
		content.add(txt068Q1);
		content.add(txt068Q2);
		content.add(txt068Q3);
		content.add(txt068Q4);
		content.add(txt000Year);
		content.add(txt010Year);
		content.add(txt021Year);
		content.add(txt066Year);
		content.add(txt067Year);
		content.add(txt068Year);
		
		content.add(txtZahlLastQ1);
		content.add(txtZahlLastQ2);
		content.add(txtZahlLastQ3);
		content.add(txtZahlLastQ4);
		content.add(txtZahlLastYear);
		
		content.add(sep1);
		content.add(sep2);
		content.add(sep3);
		content.add(sep4);
		content.add(sep5);
		content.add(sep6);
		content.add(sep7);
		
		return content;
		
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	public static JPanel createP109aPanel() {
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		
		JPanel content = new JPanel(null);
		
		lblP109aText0 = new JLabel("E/A-Rechnung ($109a Mitteilung) - " + LoadData.getStrAktGJ());
		lblP109aText1 = new JLabel("Einkünfte aus selbstständiger Tätigkeit");
		lblP109aText2 = new JLabel("SVS Vorschreibung Q1/" + LoadData.getStrAktGJ());
		lblP109aText3 = new JLabel("SVS Vorschreibung Q2/" + LoadData.getStrAktGJ());
		lblP109aText4 = new JLabel("SVS Vorschreibung Q3/" + LoadData.getStrAktGJ());
		lblP109aText5 = new JLabel("SVS Vorschreibung Q4/" + LoadData.getStrAktGJ());
		lblP109aText6 = new JLabel("50% Öffi-Pauschale");
		lblP109aText7 = new JLabel("großes Arbeitsplatzpauschale");
		lblP109aText8 = new JLabel("Betriebsausgaben netto");
		lblP109aText9 = new JLabel("Grundfreibetrag");
		lblP109aText10 = new JLabel("Einnahmenüberschuss");
		lblGwbHinweis = new JLabel("Berechnung Grundfreibetrag");
		lblVorGWB = new JLabel("Gewinn vor GWB");
		lblGwbStufe1 = new JLabel("bis § [&%]");
		lblGwbStufe2 = new JLabel("weitere § [&%]");
		lblGwbStufe3 = new JLabel("weitere § [&%]");
		lblGwbStufe4 = new JLabel("weitere § [&%]");
		
		ImageIcon leftArrow = new ImageIcon(AnnualResult.class.getResource("/icons/linkspfeil.png"));
		lblGwbTotal = new JLabel("Summe GWB", leftArrow, JLabel.LEFT);
		
		lblE1Hinweis = new JLabel("Berechnung Einkommensteuer");
		
		ImageIcon rightArrow = new ImageIcon(AnnualResult.class.getResource("/icons/rechtspfeil.png"));
		lblE1VorSt = new JLabel("Gewinn vor Steuer", rightArrow, JLabel.LEFT);
		lblE1Text1 = new JLabel("Steuerstufen");
		lblE1Text2 = new JLabel("Steuer aus Stufe");
		lblE1Stufe1 = new JLabel("von $ bis § [&%]");
		lblE1Stufe2 = new JLabel("von $ bis § [&%]");
		lblE1Stufe3 = new JLabel("von $ bis § [&%]");
		lblE1Stufe4 = new JLabel("von $ bis § [&%]");
		lblE1Stufe5 = new JLabel("von $ bis § [&%]");
		lblE1Stufe6 = new JLabel("von $ bis § [&%]");
		lblE1Stufe7 = new JLabel("von $ bis § [&%]");
		
		ImageIcon star = new ImageIcon(AnnualResult.class.getResource("/icons/stern.png"));
		lblE1Summe = new JLabel("vorauss. Einkommensteuer für das Jahr " + LoadData.getStrAktGJ(), star, JLabel.LEFT);
		
		lblP109aText0.setBounds( iLeft, 20, iWidth, iHeight);
		lblP109aText1.setBounds( iLeft, iTop + 0 * iHeight, iWidth, iHeight);
		lblP109aText2.setBounds( iLeft, iTop + 1 * iHeight, iWidth, iHeight);
		lblP109aText3.setBounds( iLeft, iTop + 2 * iHeight, iWidth, iHeight);
		lblP109aText4.setBounds( iLeft, iTop + 3 * iHeight, iWidth, iHeight);
		lblP109aText5.setBounds( iLeft, iTop + 4 * iHeight, iWidth, iHeight);
		lblP109aText6.setBounds( iLeft, iTop + 5 * iHeight, iWidth, iHeight);
		lblP109aText7.setBounds( iLeft, iTop + 6 * iHeight, iWidth, iHeight);
		lblP109aText8.setBounds( iLeft, iTop + 7 * iHeight, iWidth, iHeight);
		lblP109aText9.setBounds( iLeft, iTop + 8 * iHeight, iWidth, iHeight);
		lblP109aText10.setBounds(iLeft, 290, iWidth, iHeight);
		
		lblGwbHinweis.setBounds( iLeft + 720, 20, iWidth, iHeight);
		lblVorGWB.setBounds(     iLeft + 720, iTop + 0 * iHeight, iWidth, iHeight);
		lblGwbStufe1.setBounds(  iLeft + 720, iTop + 1 * iHeight, iWidth, iHeight);
		lblGwbStufe2.setBounds(  iLeft + 720, iTop + 2 * iHeight, iWidth, iHeight);
		lblGwbStufe3.setBounds(  iLeft + 720, iTop + 3 * iHeight, iWidth, iHeight);
		lblGwbStufe4.setBounds(  iLeft + 720, iTop + 4 * iHeight, iWidth, iHeight);
		lblGwbTotal.setBounds(   iLeft + 720, iTop + 8 * iHeight, iWidth, iHeight);
		
		lblE1Hinweis.setBounds(  iLeft + 1040, 20, iWidth, iHeight);
		lblE1VorSt.setBounds(    iLeft + 1040, iTop + 0 * iHeight, iWidth, iHeight);
		lblE1Text1.setBounds(   iLeft + 1300, iTop + 1 * iHeight, 150, iHeight);
		lblE1Text2.setBounds(   iLeft + 1460, iTop + 1 * iHeight, 150, iHeight);
		lblE1Stufe1.setBounds(   iLeft + 1040, iTop + 2 * iHeight, iWidth, iHeight);
		lblE1Stufe2.setBounds(   iLeft + 1040, iTop + 3 * iHeight, iWidth, iHeight);
		lblE1Stufe3.setBounds(   iLeft + 1040, iTop + 4 * iHeight, iWidth, iHeight);
		lblE1Stufe4.setBounds(   iLeft + 1040, iTop + 5 * iHeight, iWidth, iHeight);
		lblE1Stufe5.setBounds(   iLeft + 1040, iTop + 6 * iHeight, iWidth, iHeight);
		lblE1Stufe6.setBounds(   iLeft + 1040, iTop + 7 * iHeight, iWidth, iHeight);
		lblE1Stufe7.setBounds(   iLeft + 1040, iTop + 8 * iHeight, iWidth, iHeight);
		lblE1Summe.setBounds(    iLeft + 1040, 290, iWidth, iHeight);
		
		lblP109aText0.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblP109aText10.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblP109aText10.setForeground(Color.BLUE);
		
		lblGwbHinweis.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGwbTotal.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGwbTotal.setForeground(Color.BLUE);
		
		lblE1Hinweis.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblE1Text1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblE1Text1.setHorizontalAlignment(SwingConstants.CENTER);
		lblE1Text2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblE1Text2.setHorizontalAlignment(SwingConstants.CENTER);
		lblE1Summe.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblE1Summe.setForeground(Color.BLUE);
		
		txtP109aEin = new JFormattedTextField(currencyFormat);
		txtP109aEin.setBounds( iLeft + 560, iTop + 0 * iHeight, 150, iHeight);
		txtP109aEin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aEin.setFocusable(false);
		
		txtP109aSVS1 = new JFormattedTextField(currencyFormat);
		txtP109aSVS1.setBounds( iLeft + 400, iTop + 1 * iHeight, 150, iHeight);
		txtP109aSVS1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aSVS1.setBackground(new Color(192, 255, 192)); // light green background
		txtP109aSVS1.setFocusable(false);
		
		txtP109aSVS2 = new JFormattedTextField(currencyFormat);
		txtP109aSVS2.setBounds( iLeft + 400, iTop + 2 * iHeight, 150, iHeight);
		txtP109aSVS2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aSVS2.setBackground(new Color(192, 255, 192)); // light green background
		txtP109aSVS2.setFocusable(false);
		
		txtP109aSVS3 = new JFormattedTextField(currencyFormat);
		txtP109aSVS3.setBounds( iLeft + 400, iTop + 3 * iHeight, 150, iHeight);
		txtP109aSVS3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aSVS3.setBackground(new Color(192, 255, 192)); // light green background
		txtP109aSVS3.setFocusable(false);
		
		txtP109aSVS4 = new JFormattedTextField(currencyFormat);
		txtP109aSVS4.setBounds( iLeft + 400, iTop + 4 * iHeight, 150, iHeight);
		txtP109aSVS4.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aSVS4.setBackground(new Color(192, 255, 192)); // light green background
		txtP109aSVS4.setFocusable(false);
		
		txtP109aSVSall = new JFormattedTextField(currencyFormat);
		txtP109aSVSall.setBounds( iLeft + 560, iTop + 4 * iHeight, 150, iHeight);
		txtP109aSVSall.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aSVSall.setBackground(new Color(192, 255, 192)); // light green background for total SVS
		txtP109aSVSall.setFocusable(false);
		
		txtP109aOeffiP = new JFormattedTextField(currencyFormat);
		txtP109aOeffiP.setBounds( iLeft + 560, iTop + 5 * iHeight, 150, iHeight);
		txtP109aOeffiP.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aOeffiP.setFocusable(false);
		
		txtP109aAPausch = new JFormattedTextField(currencyFormat);
		txtP109aAPausch.setBounds( iLeft + 560, iTop + 6 * iHeight, 150, iHeight);
		txtP109aAPausch.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aAPausch.setFocusable(false);
		
		txtP109aExpenses = new JFormattedTextField(currencyFormat);
		txtP109aExpenses.setBounds( iLeft + 560, iTop + 7 * iHeight, 150, iHeight);
		txtP109aExpenses.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aExpenses.setFocusable(false);
		
		txtP109aGrundfrei = new JFormattedTextField(currencyFormat);
		txtP109aGrundfrei.setBounds( iLeft + 560, iTop + 8 * iHeight, 150, iHeight);
		txtP109aGrundfrei.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aGrundfrei.setBackground(new Color(173, 216, 230)); // light blue background for Grundfreibetrag
		txtP109aGrundfrei.setFocusable(false);
		
		txtP109aErgebnis = new JFormattedTextField(currencyFormat);
		txtP109aErgebnis.setBounds( iLeft + 560, 290, 150, iHeight);
		txtP109aErgebnis.setHorizontalAlignment(SwingConstants.RIGHT);
		txtP109aErgebnis.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtP109aErgebnis.setBackground(new Color(255, 255, 204)); // light yellow background for pre-tax profit
		txtP109aErgebnis.setFocusable(false);
		
		txtVorGWB = new JFormattedTextField(currencyFormat);
		txtVorGWB.setBounds( iLeft + 880, iTop + 0 * iHeight, 150, iHeight);
		txtVorGWB.setHorizontalAlignment(SwingConstants.RIGHT);
		txtVorGWB.setFocusable(false);
		
		txtGwbStufe1 = new JFormattedTextField(currencyFormat);
		txtGwbStufe1.setBounds( iLeft + 880, iTop + 1 * iHeight, 150, iHeight);
		txtGwbStufe1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGwbStufe1.setFocusable(false);
		
		txtGwbStufe2 = new JFormattedTextField(currencyFormat);
		txtGwbStufe2.setBounds( iLeft + 880, iTop + 2 * iHeight, 150, iHeight);
		txtGwbStufe2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGwbStufe2.setFocusable(false);
		
		txtGwbStufe3 = new JFormattedTextField(currencyFormat);
		txtGwbStufe3.setBounds( iLeft + 880, iTop + 3 * iHeight, 150, iHeight);
		txtGwbStufe3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGwbStufe3.setFocusable(false);
		
		txtGwbStufe4 = new JFormattedTextField(currencyFormat);
		txtGwbStufe4.setBounds( iLeft + 880, iTop + 4 * iHeight, 150, iHeight);
		txtGwbStufe4.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGwbStufe4.setFocusable(false);
		
		txtGwbTotal = new JFormattedTextField(currencyFormat);
		txtGwbTotal.setBounds( iLeft + 880, iTop + 8 * iHeight, 150, iHeight);
		txtGwbTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGwbTotal.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtGwbTotal.setBackground(new Color(173, 216, 230)); // light blue background for Grundfreibetrag
		txtGwbTotal.setFocusable(false);
		
		txtE1VorSt = new JFormattedTextField(currencyFormat);
		txtE1VorSt.setBounds( iLeft + 1300, iTop + 0 * iHeight, 150, iHeight);
		txtE1VorSt.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1VorSt.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtE1VorSt.setBackground(new Color(255, 255, 204)); // light yellow background for pre-tax profit
		txtE1VorSt.setFocusable(false);
		
		txtE1Stufe1 = new JFormattedTextField(currencyFormat);
		txtE1Stufe1.setBounds( iLeft + 1300, iTop + 2 * iHeight, 150, iHeight);
		txtE1Stufe1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe1.setFocusable(false);
		
		txtE1Stufe2 = new JFormattedTextField(currencyFormat);
		txtE1Stufe2.setBounds( iLeft + 1300, iTop + 3 * iHeight, 150, iHeight);
		txtE1Stufe2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe2.setFocusable(false);
		
		txtE1Stufe3 = new JFormattedTextField(currencyFormat);
		txtE1Stufe3.setBounds( iLeft + 1300, iTop + 4 * iHeight, 150, iHeight);
		txtE1Stufe3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe3.setFocusable(false);
		
		txtE1Stufe4 = new JFormattedTextField(currencyFormat);
		txtE1Stufe4.setBounds( iLeft + 1300, iTop + 5 * iHeight, 150, iHeight);
		txtE1Stufe4.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe4.setFocusable(false);
		
		txtE1Stufe5 = new JFormattedTextField(currencyFormat);
		txtE1Stufe5.setBounds( iLeft + 1300, iTop + 6 * iHeight, 150, iHeight);
		txtE1Stufe5.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe5.setFocusable(false);
		
		txtE1Stufe6 = new JFormattedTextField(currencyFormat);
		txtE1Stufe6.setBounds( iLeft + 1300, iTop + 7 * iHeight, 150, iHeight);
		txtE1Stufe6.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe6.setFocusable(false);
		
		txtE1Stufe7 = new JFormattedTextField(currencyFormat);
		txtE1Stufe7.setBounds( iLeft + 1300, iTop + 8 * iHeight, 150, iHeight);
		txtE1Stufe7.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Stufe7.setFocusable(false);
		
		txtE1Tax1 = new JFormattedTextField(currencyFormat);
		txtE1Tax1.setBounds( iLeft + 1460, iTop + 2 * iHeight, 150, iHeight);
		txtE1Tax1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax1.setFocusable(false);
		
		txtE1Tax2 = new JFormattedTextField(currencyFormat);
		txtE1Tax2.setBounds( iLeft + 1460, iTop + 3 * iHeight, 150, iHeight);
		txtE1Tax2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax2.setFocusable(false);
		
		txtE1Tax3 = new JFormattedTextField(currencyFormat);
		txtE1Tax3.setBounds( iLeft + 1460, iTop + 4 * iHeight, 150, iHeight);
		txtE1Tax3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax3.setFocusable(false);
		
		txtE1Tax4 = new JFormattedTextField(currencyFormat);
		txtE1Tax4.setBounds( iLeft + 1460, iTop + 5 * iHeight, 150, iHeight);
		txtE1Tax4.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax4.setFocusable(false);
		
		txtE1Tax5 = new JFormattedTextField(currencyFormat);
		txtE1Tax5.setBounds( iLeft + 1460, iTop + 6 * iHeight, 150, iHeight);
		txtE1Tax5.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax5.setFocusable(false);
		
		txtE1Tax6 = new JFormattedTextField(currencyFormat);
		txtE1Tax6.setBounds( iLeft + 1460, iTop + 7 * iHeight, 150, iHeight);
		txtE1Tax6.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax6.setFocusable(false);
		
		txtE1Tax7 = new JFormattedTextField(currencyFormat);
		txtE1Tax7.setBounds( iLeft + 1460, iTop + 8 * iHeight, 150, iHeight);
		txtE1Tax7.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Tax7.setFocusable(false);
		
		txtE1Summe = new JFormattedTextField(currencyFormat);
		txtE1Summe.setBounds( iLeft + 1460, 290, 150, iHeight);
		txtE1Summe.setHorizontalAlignment(SwingConstants.RIGHT);
		txtE1Summe.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtE1Summe.setBackground(new Color(255, 182, 193)); // light pink background for total tax
		txtE1Summe.setFocusable(false);
		
		sep8.setBounds(405, 20, 5, 350);
		sep8.setOrientation(SwingConstants.VERTICAL);
		sep9.setBounds(iLeft, 45, iLeft + 1460 + 150 - 5, 5);
		sep9.setOrientation(SwingConstants.HORIZONTAL);
		sep10.setBounds(iLeft, 280, iLeft + 1460 + 150 - 5, 5);
		sep10.setOrientation(SwingConstants.HORIZONTAL);
		sep11.setBounds(iLeft + 560 + 150 + 5, 20, 5, 350);
		sep11.setOrientation(SwingConstants.VERTICAL);
		sep12.setBounds(iLeft + 880 + 150 + 5, 20, 5, 350);
		sep12.setOrientation(SwingConstants.VERTICAL);
		sep13.setBounds(iLeft + 1460 + 150 + 5, 20, 5, 350);
		sep13.setOrientation(SwingConstants.VERTICAL);
		
		try {
			btnExportP109a = createButton("<html>Export<br>§109a</html>", "export.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnExportP109a.setEnabled(false); // initially disabled
		btnExportP109a.setBounds( 4 * 130 + 60, 320, 130, 50);
		
		content.setPreferredSize(new Dimension(iLeft + 1040 + 150 + 10, 385)); // "Größe des Inhalts"
		
		content.add(lblP109aText0);
		content.add(lblP109aText1);
		content.add(lblP109aText2);
		content.add(lblP109aText3);
		content.add(lblP109aText4);
		content.add(lblP109aText5);
		content.add(lblP109aText6);
		content.add(lblP109aText7);
		content.add(lblP109aText8);
		content.add(lblP109aText9);
		content.add(lblP109aText10);
		content.add(lblGwbHinweis);
		content.add(lblVorGWB);
		content.add(lblGwbStufe1);
		content.add(lblGwbStufe2);
		content.add(lblGwbStufe3);
		content.add(lblGwbStufe4);
		content.add(lblGwbTotal);
		content.add(lblE1Hinweis);
		content.add(lblE1VorSt);
		content.add(lblE1Text1);
		content.add(lblE1Text2);
		content.add(lblE1Stufe1);
		content.add(lblE1Stufe2);
		content.add(lblE1Stufe3);
		content.add(lblE1Stufe4);
		content.add(lblE1Stufe5);
		content.add(lblE1Stufe6);
		content.add(lblE1Stufe7);
		content.add(lblE1Summe);
		
		content.add(txtP109aEin);
		content.add(txtP109aSVS1);
		content.add(txtP109aSVS2);
		content.add(txtP109aSVS3);
		content.add(txtP109aSVS4);
		content.add(txtP109aSVSall);
		content.add(txtP109aOeffiP);
		content.add(txtP109aAPausch);
		content.add(txtP109aExpenses);
		content.add(txtP109aGrundfrei);
		content.add(txtP109aErgebnis);
		content.add(txtVorGWB);
		content.add(txtGwbStufe1);
		content.add(txtGwbStufe2);
		content.add(txtGwbStufe3);
		content.add(txtGwbStufe4);
		content.add(txtGwbTotal);
		content.add(txtE1Stufe1);
		content.add(txtE1Stufe2);
		content.add(txtE1Stufe3);
		content.add(txtE1Stufe4);
		content.add(txtE1Stufe5);
		content.add(txtE1Stufe6);
		content.add(txtE1Stufe7);
		content.add(txtE1VorSt);
		content.add(txtE1Tax1);
		content.add(txtE1Tax2);
		content.add(txtE1Tax3);
		content.add(txtE1Tax4);
		content.add(txtE1Tax5);
		content.add(txtE1Tax6);
		content.add(txtE1Tax7);
		content.add(txtE1Summe);
		
		content.add(sep8);
		content.add(sep9);
		content.add(sep10);
		content.add(sep11);
		content.add(sep12);
		content.add(sep13);
		
		content.add(btnExportP109a); // Füge den Button zum Panel hinzu
		
		return content;
	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	private static void setValuesUVA(int AnzYearBillIn, int AnzYearBillOut, int AnzExpenses, String[][] arrYearBillIn, String[][] arrYearBillOut, String[][] arrExpenses) {

		BigDecimal bdRaATQ1 = new BigDecimal("0.00"), bdUStATQ1 = new BigDecimal("0.00"), bdRaEUQ1 = new BigDecimal("0.00");
		BigDecimal bdRaATQ2 = new BigDecimal("0.00"), bdUStATQ2 = new BigDecimal("0.00"), bdRaEUQ2 = new BigDecimal("0.00");
		BigDecimal bdRaATQ3 = new BigDecimal("0.00"), bdUStATQ3 = new BigDecimal("0.00"), bdRaEUQ3 = new BigDecimal("0.00");
		BigDecimal bdRaATQ4 = new BigDecimal("0.00"), bdUStATQ4 = new BigDecimal("0.00"), bdRaEUQ4 = new BigDecimal("0.00");
		BigDecimal bdRaATYear = new BigDecimal("0.00"), bdUStATYear = new BigDecimal("0.00"), bdRaEUYear = new BigDecimal("0.00");
		BigDecimal bd066Q1 = new BigDecimal("0.00"), bd066Q2 = new BigDecimal("0.00"), bd066Q3 = new BigDecimal("0.00"), bd066Q4 = new BigDecimal("0.00");
		BigDecimal bd067Q1 = new BigDecimal("0.00"), bd067Q2 = new BigDecimal("0.00"), bd067Q3 = new BigDecimal("0.00"), bd067Q4 = new BigDecimal("0.00");
		BigDecimal bd068Q1 = new BigDecimal("0.00"), bd068Q2 = new BigDecimal("0.00"), bd068Q3 = new BigDecimal("0.00"), bd068Q4 = new BigDecimal("0.00");
		BigDecimal bd066Year = new BigDecimal("0.00"), bd067Year = new BigDecimal("0.00"), bd068Year = new BigDecimal("0.00");
		
		BigDecimal bdZahllastQ1 = new BigDecimal("0.00"), bdZahllastQ2 = new BigDecimal("0.00"), bdZahllastQ3 = new BigDecimal("0.00"), bdZahllastQ4 = new BigDecimal("0.00");
		BigDecimal bdZahllastYear = new BigDecimal("0.00");
		
		try {
			if(AnzYearBillOut > 0) {
				for(int x = 1; (x - 1) < AnzYearBillOut; x++) {
					
					int iQuartal = getQuartalFromString(arrYearBillOut[x][6].trim(), "dd.MM.yyyy");
					String sValue = arrYearBillOut[x][13].trim();
		
					if(iQuartal == 1) {
						if(sValue.equals("0.00")) { // USt. = 0.00, also innergemeinschaftliche Lieferung/Leistung
							bdRaEUQ1 = bdRaEUQ1.add(new BigDecimal(arrYearBillOut[x][12].trim()));
						}
						if(!sValue.equals("0.00")) { // USt. = 20.00, also innländische Lieferung/Leistung
							bdRaATQ1 = bdRaATQ1.add(new BigDecimal(arrYearBillOut[x][12].trim()));
							bdUStATQ1 = bdUStATQ1.add(new BigDecimal(arrYearBillOut[x][13].trim()));
						}
					}
					
					if(iQuartal == 2) {
						if(sValue.equals("0.00")) { // USt. = 0.00, also innergemeinschaftliche Lieferung/Leistung
							bdRaEUQ2 = bdRaEUQ2.add(new BigDecimal(arrYearBillOut[x][12].trim()));
						}
						if(!sValue.equals("0.00")) { // USt. = 20.00, also innländische Lieferung/Leistung
							bdRaATQ2 = bdRaATQ2.add(new BigDecimal(arrYearBillOut[x][12].trim()));
							bdUStATQ2 = bdUStATQ2.add(new BigDecimal(arrYearBillOut[x][13].trim()));
						}
					}
					
					if(iQuartal == 3) {
						if(sValue.equals("0.00")) { // USt. = 0.00, also innergemeinschaftliche Lieferung/Leistung
							bdRaEUQ3 = bdRaEUQ3.add(new BigDecimal(arrYearBillOut[x][12].trim()));
						}
						if(!sValue.equals("0.00")) { // USt. = 20.00, also innländische Lieferung/Leistung
							bdRaATQ3 = bdRaATQ3.add(new BigDecimal(arrYearBillOut[x][12].trim()));
							bdUStATQ3 = bdUStATQ3.add(new BigDecimal(arrYearBillOut[x][13].trim()));
						}
					}
					
					if(iQuartal == 4) {
						if(sValue.equals("0.00")) { // USt. = 0.00, also innergemeinschaftliche Lieferung/Leistung
							bdRaEUQ4 = bdRaEUQ4.add(new BigDecimal(arrYearBillOut[x][12].trim()));
						}
						if(!sValue.equals("0.00")) { // USt. = 20.00, also innländische Lieferung/Leistung
							bdRaATQ4 = bdRaATQ4.add(new BigDecimal(arrYearBillOut[x][12].trim()));
							bdUStATQ4 = bdUStATQ4.add(new BigDecimal(arrYearBillOut[x][13].trim()));
						}
					}
				}
			}
		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}
		
		try {
			if(AnzYearBillIn >0) {
				for(int x = 1; (x - 1) < AnzYearBillIn; x++) {
					
					int iQuartal = getQuartalFromString(arrYearBillIn[x][2].trim(), "yyyy-MM-dd");
					String sValue = arrYearBillIn[x][10].trim();
					
					if(iQuartal == 1) {
						if(sValue.equals("20")) {
							bd066Q1 = bd066Q1.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q1 = bd067Q1.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q1 = bd068Q1.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
					}
					
					if(iQuartal == 2) {
						if(sValue.equals("20")) {
							bd066Q2 = bd066Q2.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q2 = bd067Q2.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q2 = bd068Q2.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
					}
					
					if(iQuartal == 3) {
						if(sValue.equals("20")) {
							bd066Q3 = bd066Q3.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q3 = bd067Q3.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q3 = bd068Q3.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
					}
					
					if(iQuartal == 4) {
						if(sValue.equals("20")) {
							bd066Q4 = bd066Q4.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q4 = bd067Q4.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q4 = bd068Q4.add(new BigDecimal(arrYearBillIn[x][13].trim()));
						}
					}
					
				}
			}
		} catch (NullPointerException e1){
			logger.error("error in calculatin inbound billing sum - " + e1);
		}
			
		try {
			if(AnzExpenses > 0) {
				for(int x = 1; (x - 1) < AnzExpenses; x++) {
					
					int iQuartal = getQuartalFromString(arrExpenses[x][1].trim(), "yyyy-MM-dd");
					String sValue = arrExpenses[x][4].trim();
					
					if(iQuartal == 1) {
						if(sValue.equals("20")) {
							bd066Q1 = bd066Q1.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q1 = bd067Q1.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q1 = bd068Q1.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
					}
					
					if(iQuartal == 2) {
						if(sValue.equals("20")) {
							bd066Q2 = bd066Q2.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q2 = bd067Q2.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q2 = bd068Q2.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
					}
					
					if(iQuartal == 3) {
						if(sValue.equals("20")) {
							bd066Q3 = bd066Q3.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q3 = bd067Q3.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q3 = bd068Q3.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
					}
					
					if(iQuartal == 4) {
						if(sValue.equals("20")) {
							bd066Q4 = bd066Q4.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("10")) {
							bd067Q4 = bd067Q4.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
						if(sValue.equals("13")) {
							bd068Q4 = bd068Q4.add(new BigDecimal(arrExpenses[x][5].trim()));
						}
					}
					
				}
			}
		} catch (NullPointerException e1){
			logger.error("error in calculatin expenses sum - " + e1);
		}
		
		bdZahllastQ1 = bdUStATQ1.subtract(bd066Q1).subtract(bd067Q1).subtract(bd068Q1);
		bdZahllastQ2 = bdUStATQ2.subtract(bd066Q2).subtract(bd067Q2).subtract(bd068Q2);
		bdZahllastQ3 = bdUStATQ3.subtract(bd066Q3).subtract(bd067Q3).subtract(bd068Q3);
		bdZahllastQ4 = bdUStATQ4.subtract(bd066Q4).subtract(bd067Q4).subtract(bd068Q4);
		
		bdRaATYear = bdRaATQ1.add(bdRaATQ2).add(bdRaATQ3).add(bdRaATQ4);
		bdUStATYear = bdUStATQ1.add(bdUStATQ2).add(bdUStATQ3).add(bdUStATQ4);
		bdRaEUYear = bdRaEUQ1.add(bdRaEUQ2).add(bdRaEUQ3).add(bdRaEUQ4);
		bd066Year = bd066Q1.add(bd066Q2).add(bd066Q3).add(bd066Q4);
		bd067Year = bd067Q1.add(bd067Q2).add(bd067Q3).add(bd067Q4);
		bd068Year = bd068Q1.add(bd068Q2).add(bd068Q3).add(bd068Q4);
		bdZahllastYear = bdUStATYear.subtract(bd066Year).subtract(bd067Year).subtract(bd068Year);
		
		txt000Q1.setValue(Double.valueOf(bdRaATQ1.toString().replace(",", ".")));
		txt010Q1.setValue(Double.valueOf(bdUStATQ1.toString().replace(",", ".")));
		txt021Q1.setValue(Double.valueOf(bdRaEUQ1.toString().replace(",", ".")));
		
		txt066Q1.setValue(Double.valueOf(bd066Q1.toString().replace(",", ".")));
		txt067Q1.setValue(Double.valueOf(bd067Q1.toString().replace(",", ".")));
		txt068Q1.setValue(Double.valueOf(bd068Q1.toString().replace(",", ".")));
		
		txt000Q2.setValue(Double.valueOf(bdRaATQ2.toString().replace(",", ".")));
		txt010Q2.setValue(Double.valueOf(bdUStATQ2.toString().replace(",", ".")));
		txt021Q2.setValue(Double.valueOf(bdRaEUQ2.toString().replace(",", ".")));
		
		txt066Q2.setValue(Double.valueOf(bd066Q2.toString().replace(",", ".")));
		txt067Q2.setValue(Double.valueOf(bd067Q2.toString().replace(",", ".")));
		txt068Q2.setValue(Double.valueOf(bd068Q2.toString().replace(",", ".")));
		
		txt000Q3.setValue(Double.valueOf(bdRaATQ3.toString().replace(",", ".")));
		txt010Q3.setValue(Double.valueOf(bdUStATQ3.toString().replace(",", ".")));
		txt021Q3.setValue(Double.valueOf(bdRaEUQ3.toString().replace(",", ".")));
		
		txt066Q3.setValue(Double.valueOf(bd066Q3.toString().replace(",", ".")));
		txt067Q3.setValue(Double.valueOf(bd067Q3.toString().replace(",", ".")));
		txt068Q3.setValue(Double.valueOf(bd068Q3.toString().replace(",", ".")));
		
		txt000Q4.setValue(Double.valueOf(bdRaATQ4.toString().replace(",", ".")));
		txt010Q4.setValue(Double.valueOf(bdUStATQ4.toString().replace(",", ".")));
		txt021Q4.setValue(Double.valueOf(bdRaEUQ4.toString().replace(",", ".")));
		
		txt066Q4.setValue(Double.valueOf(bd066Q4.toString().replace(",", ".")));
		txt067Q4.setValue(Double.valueOf(bd067Q4.toString().replace(",", ".")));
		txt068Q4.setValue(Double.valueOf(bd068Q4.toString().replace(",", ".")));
		
		txtZahlLastQ1.setValue(Double.valueOf(bdZahllastQ1.toString().replace(",", ".")));
		txtZahlLastQ2.setValue(Double.valueOf(bdZahllastQ2.toString().replace(",", ".")));
		txtZahlLastQ3.setValue(Double.valueOf(bdZahllastQ3.toString().replace(",", ".")));
		txtZahlLastQ4.setValue(Double.valueOf(bdZahllastQ4.toString().replace(",", ".")));
		
		txt000Year.setValue(Double.valueOf(bdRaATYear.toString().replace(",", ".")));
		txt010Year.setValue(Double.valueOf(bdUStATYear.toString().replace(",", ".")));
		txt021Year.setValue(Double.valueOf(bdRaEUYear.toString().replace(",", ".")));
		txt066Year.setValue(Double.valueOf(bd066Year.toString().replace(",", ".")));
		txt067Year.setValue(Double.valueOf(bd067Year.toString().replace(",", ".")));
		txt068Year.setValue(Double.valueOf(bd068Year.toString().replace(",", ".")));
		txtZahlLastYear.setValue(Double.valueOf(bdZahllastYear.toString().replace(",", ".")));

	}
	
	//###################################################################################################################################################
	//###################################################################################################################################################
	
	private static void setValuesTax(int AnzYearBillOut, String[][] arrYearBillOut) {
		
		BigDecimal bdTmp1 = BigDecimal.ZERO;
		BigDecimal bdTmp2 = BigDecimal.ZERO;
		BigDecimal bdVorGwb = BigDecimal.ZERO;
		BigDecimal bdErgYear = BigDecimal.ZERO;
		BigDecimal bdOeffiP = new BigDecimal(arrTaxValues[1][23].toString().replace(",", ".")).multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP);
		BigDecimal bdAPausch = new BigDecimal(arrTaxValues[1][24].toString().replace(",", ".")).multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP);
		BigDecimal bdExpenses = BigDecimal.ZERO;
		BigDecimal bdGwbTotal = BigDecimal.ZERO;
		BigDecimal bdGwbTotalNeg = BigDecimal.ZERO;
		BigDecimal bdTaxTotal = BigDecimal.ZERO;
		List<BigDecimal> GwbStufe = new ArrayList<>();
		List<BigDecimal> TaxStufe = new ArrayList<>();
		
		try {
			
			if(AnzYearBillOut > 0) {
				for(int x = 1; (x - 1) < AnzYearBillOut; x++) {
					String sTmp = arrYearBillOut[x][3].trim();
					String sValue = arrYearBillOut[x][12].trim();
					if(sTmp.equals("1")) { // Rechnung wurde ausgestellt
						bdTmp1 = new BigDecimal(sValue);
						bdTmp2 = bdTmp2.add(bdTmp1);
					}
				}
			}
			
			bdExpenses = new BigDecimal(bdExpNetto.toString().replace(",", ".")).multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP); // Ausgaben netto negativ
			
			bdVorGwb = bdTmp2.add(bdSVYear).add(bdOeffiP).add(bdAPausch).add(bdExpenses); // VorGWB wird aus der Summe der Einnahmen, SV, öffentlicher Pauschale, APauschale und Ausgaben netto berechnet
			
			GwbStufe = calcGWB(bdVorGwb); // Berechnung der GWB-Stufen
			
			bdGwbTotal = GwbStufe.stream().reduce(BigDecimal.ZERO, BigDecimal::add); // Summe der GWB-Stufen
			bdGwbTotalNeg = bdGwbTotal.multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP); // GWB negativ
			
			bdErgYear = bdTmp2.add(bdSVYear).add(bdOeffiP).add(bdAPausch).add(bdExpenses).add(bdGwbTotalNeg); // Ergebnis wird aus der Summe der Einnahmen, SV, öffentlicher Pauschale, APauschale, Ausgaben netto und GWB negativ berechnet

			//bdErgYear = new BigDecimal("98456.23");
			
			TaxStufe = calcTAX(bdErgYear); // Berechnung der Steuerstufen
			
			bdTaxTotal = TaxStufe.get(7).add(TaxStufe.get(8)).add(TaxStufe.get(9)).add(TaxStufe.get(10)).add(TaxStufe.get(11))
				.add(TaxStufe.get(12)).add(TaxStufe.get(13)); // Summe der errechneten Steuern bilden
			
			
		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}
		
		txtP109aEin.setValue(Double.valueOf(bdTmp2.toString().replace(",", ".")));
		txtP109aSVSall.setValue(Double.valueOf(bdSVYear.toString().replace(",", ".")));
		txtP109aOeffiP.setValue(Double.valueOf(bdOeffiP.toString().replace(",", ".")));
		txtP109aAPausch.setValue(Double.valueOf(bdAPausch.toString().replace(",", ".")));
		txtP109aExpenses.setValue(Double.valueOf(bdExpenses.toString().replace(",", ".")));
		
		txtVorGWB.setValue(Double.valueOf(bdVorGwb.toString().replace(",", ".")));
		txtGwbStufe1.setValue(Double.valueOf(GwbStufe.get(0).toString().replace(",", ".")));
		txtGwbStufe2.setValue(Double.valueOf(GwbStufe.get(1).toString().replace(",", ".")));
		txtGwbStufe3.setValue(Double.valueOf(GwbStufe.get(2).toString().replace(",", ".")));
		txtGwbStufe4.setValue(Double.valueOf(GwbStufe.get(3).toString().replace(",", ".")));
		txtGwbTotal.setValue(Double.valueOf(bdGwbTotal.toString().replace(",", ".")));
		
		txtP109aGrundfrei.setValue(Double.valueOf(bdGwbTotalNeg.toString().replace(",", ".")));
		txtP109aErgebnis.setValue(Double.valueOf(bdErgYear.toString().replace(",", ".")));
		
		txtE1VorSt.setValue(Double.valueOf(bdErgYear.toString().replace(",", "."))); // Gewinn vor Steuer für E1 wird aus dem Ergebnis der P109a übernommen
		txtE1Stufe1.setValue(Double.valueOf(TaxStufe.get(0).toString().replace(",", "."))); // Summe Stufe 1
		txtE1Stufe2.setValue(Double.valueOf(TaxStufe.get(1).toString().replace(",", "."))); // Summe Stufe 2
		txtE1Stufe3.setValue(Double.valueOf(TaxStufe.get(2).toString().replace(",", "."))); // Summe Stufe 3
		txtE1Stufe4.setValue(Double.valueOf(TaxStufe.get(3).toString().replace(",", "."))); // Summe Stufe 4
		txtE1Stufe5.setValue(Double.valueOf(TaxStufe.get(4).toString().replace(",", "."))); // Summe Stufe 5
		txtE1Stufe6.setValue(Double.valueOf(TaxStufe.get(5).toString().replace(",", "."))); // Summe Stufe 6
		txtE1Stufe7.setValue(Double.valueOf(TaxStufe.get(6).toString().replace(",", "."))); // Summe Stufe 7
		
		txtE1Tax1.setValue(Double.valueOf(TaxStufe.get(7).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 1
		txtE1Tax2.setValue(Double.valueOf(TaxStufe.get(8).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 2
		txtE1Tax3.setValue(Double.valueOf(TaxStufe.get(9).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 3
		txtE1Tax4.setValue(Double.valueOf(TaxStufe.get(10).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 4
		txtE1Tax5.setValue(Double.valueOf(TaxStufe.get(11).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 5
		txtE1Tax6.setValue(Double.valueOf(TaxStufe.get(12).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 6
		txtE1Tax7.setValue(Double.valueOf(TaxStufe.get(13).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 7
		txtE1Summe.setValue(Double.valueOf(bdTaxTotal.toString().replace(",", "."))); // voraussichtliche Einkommensteuer gesamt
		
	}
	
	private static List<BigDecimal> calcGWB(BigDecimal bdVorGwb) {
		
		NumberFormat waehrungsFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		
		BigDecimal rest1 = BigDecimal.ZERO;
		BigDecimal tmp1 = BigDecimal.ZERO;
		BigDecimal rest2 = BigDecimal.ZERO;
		BigDecimal tmp2 = BigDecimal.ZERO;
		BigDecimal rest3 = BigDecimal.ZERO;
		BigDecimal tmp3 = BigDecimal.ZERO;
		BigDecimal tmp4 = BigDecimal.ZERO;
		
		BigDecimal bdGwbTmp1 = new BigDecimal(arrGwbValues[1][2].toString().replace(",", "."));
		BigDecimal bdGwbVal1 = new BigDecimal(arrGwbValues[1][3].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdGwbTmp2 = new BigDecimal(arrGwbValues[1][4].toString().replace(",", "."));
		BigDecimal bdGwbVal2 = new BigDecimal(arrGwbValues[1][5].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdGwbTmp3 = new BigDecimal(arrGwbValues[1][6].toString().replace(",", "."));
		BigDecimal bdGwbVal3 = new BigDecimal(arrGwbValues[1][7].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdGwbTmp4 = new BigDecimal(arrGwbValues[1][8].toString().replace(",", "."));
		BigDecimal bdGwbVal4 = new BigDecimal(arrGwbValues[1][9].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		
		Double dTmp1 = Double.valueOf(bdGwbTmp1.toString().replace(",", "."));
		Double dTmp2 = Double.valueOf(bdGwbTmp2.toString().replace(",", "."));
		Double dTmp3 = Double.valueOf(bdGwbTmp3.toString().replace(",", "."));
		Double dTmp4 = Double.valueOf(bdGwbTmp4.toString().replace(",", "."));
		
		String sTmpd1 = waehrungsFormat.format(dTmp1);
		String sTmpd2 = waehrungsFormat.format(dTmp2);
		String sTmpd3 = waehrungsFormat.format(dTmp3);
		String sTmpd4 = waehrungsFormat.format(dTmp4);
		
		String sTmp1 = lblGwbStufe1.getText().replace("§", sTmpd1).replace("&", bdGwbVal1.toString());
		String sTmp2 = lblGwbStufe2.getText().replace("§", sTmpd2).replace("&", bdGwbVal2.toString());
		String sTmp3 = lblGwbStufe3.getText().replace("§", sTmpd3).replace("&", bdGwbVal3.toString());
		String sTmp4 = lblGwbStufe4.getText().replace("§", sTmpd4).replace("&", bdGwbVal4.toString());
		
		lblGwbStufe1.setText(sTmp1); // Texte für GWB Stufen anpassen
		lblGwbStufe2.setText(sTmp2);
		lblGwbStufe3.setText(sTmp3);
		lblGwbStufe4.setText(sTmp4);
		
		List<BigDecimal> liste = new ArrayList<>();
		
		if(bdVorGwb.compareTo(bdGwbTmp1) >= 0) { // wenn VorGWB größer oder gleich GWB Stufe 1
			rest1 = bdVorGwb.subtract(bdGwbTmp1);
			tmp1 = bdGwbTmp1.multiply(new BigDecimal(arrGwbValues[1][3].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		} else {
			rest1 = BigDecimal.ZERO;
			tmp1 = bdVorGwb.multiply(new BigDecimal(arrGwbValues[1][3].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		}
		
		if(rest1.compareTo(bdGwbTmp2) >= 0) { // wenn Rest größer oder gleich GWB Stufe 2
			rest2 = rest1.subtract(bdGwbTmp2);
			tmp2 = bdGwbTmp2.multiply(new BigDecimal(arrGwbValues[1][5].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		} else {
			rest2 = BigDecimal.ZERO;
			tmp2 = rest1.multiply(new BigDecimal(arrGwbValues[1][5].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		}
		
		if(rest2.compareTo(bdGwbTmp3) >= 0) { // wenn Rest größer oder gleich GWB Stufe 3
			rest3 = rest2.subtract(bdGwbTmp3);
			tmp3 = bdGwbTmp3.multiply(new BigDecimal(arrGwbValues[1][7].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		} else {
			rest3 = BigDecimal.ZERO;
			tmp3 = rest2.multiply(new BigDecimal(arrGwbValues[1][7].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		}
		
		if(rest3.compareTo(bdGwbTmp4) >= 0) { // wenn Rest größer oder gleich GWB Stufe 4
			tmp4 = bdGwbTmp4.multiply(new BigDecimal(arrGwbValues[1][9].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		} else {
			tmp4 = rest3.multiply(new BigDecimal(arrGwbValues[1][9].toString().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
		}
		
		liste.add(tmp1); // GWB Stufe 1
		liste.add(tmp2); // GWB Stufe 2
		liste.add(tmp3); // GWB Stufe 3
		liste.add(tmp4); // GWB Stufe 4
		
		return liste;
		
	}
	
	private static List<BigDecimal> calcTAX(BigDecimal bdVorTax){
		
		NumberFormat waehrungsFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

		BigDecimal rest1 = BigDecimal.ZERO;
		BigDecimal rest2 = BigDecimal.ZERO;
		BigDecimal rest3 = BigDecimal.ZERO;
		BigDecimal rest4 = BigDecimal.ZERO;
		BigDecimal rest5 = BigDecimal.ZERO;
		BigDecimal rest6 = BigDecimal.ZERO;
		BigDecimal rest7 = BigDecimal.ZERO;
		
		BigDecimal bdTaxVon1 = new BigDecimal(arrTaxValues[1][2].toString().replace(",", "."));
		BigDecimal bdTaxBis1 = new BigDecimal(arrTaxValues[1][3].toString().replace(",", "."));
		BigDecimal bdTaxVal1 = new BigDecimal(arrTaxValues[1][4].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon2 = new BigDecimal(arrTaxValues[1][5].toString().replace(",", "."));
		BigDecimal bdTaxBis2 = new BigDecimal(arrTaxValues[1][6].toString().replace(",", "."));
		BigDecimal bdTaxVal2 = new BigDecimal(arrTaxValues[1][7].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon3 = new BigDecimal(arrTaxValues[1][8].toString().replace(",", "."));
		BigDecimal bdTaxBis3 = new BigDecimal(arrTaxValues[1][9].toString().replace(",", "."));
		BigDecimal bdTaxVal3 = new BigDecimal(arrTaxValues[1][10].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon4 = new BigDecimal(arrTaxValues[1][11].toString().replace(",", "."));
		BigDecimal bdTaxBis4 = new BigDecimal(arrTaxValues[1][12].toString().replace(",", "."));
		BigDecimal bdTaxVal4 = new BigDecimal(arrTaxValues[1][13].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon5 = new BigDecimal(arrTaxValues[1][14].toString().replace(",", "."));
		BigDecimal bdTaxBis5 = new BigDecimal(arrTaxValues[1][15].toString().replace(",", "."));
		BigDecimal bdTaxVal5 = new BigDecimal(arrTaxValues[1][16].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon6 = new BigDecimal(arrTaxValues[1][17].toString().replace(",", "."));
		BigDecimal bdTaxBis6 = new BigDecimal(arrTaxValues[1][18].toString().replace(",", "."));
		BigDecimal bdTaxVal6 = new BigDecimal(arrTaxValues[1][19].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon7 = new BigDecimal(arrTaxValues[1][20].toString().replace(",", "."));
		BigDecimal bdTaxBis7 = new BigDecimal(arrTaxValues[1][21].toString().replace(",", "."));
		BigDecimal bdTaxVal7 = new BigDecimal(arrTaxValues[1][22].toString().replace(",", ".")).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);

		Double dTmp1 = Double.valueOf(bdTaxVon1.toString().replace(",", "."));
		Double dTmp2 = Double.valueOf(bdTaxBis1.toString().replace(",", "."));
		Double dTmp3 = Double.valueOf(bdTaxVon2.toString().replace(",", "."));
		Double dTmp4 = Double.valueOf(bdTaxBis2.toString().replace(",", "."));
		Double dTmp5 = Double.valueOf(bdTaxVon3.toString().replace(",", "."));
		Double dTmp6 = Double.valueOf(bdTaxBis3.toString().replace(",", "."));
		Double dTmp7 = Double.valueOf(bdTaxVon4.toString().replace(",", "."));
		Double dTmp8 = Double.valueOf(bdTaxBis4.toString().replace(",", "."));
		Double dTmp9 = Double.valueOf(bdTaxVon5.toString().replace(",", "."));
		Double dTmp10 = Double.valueOf(bdTaxBis5.toString().replace(",", "."));
		Double dTmp11 = Double.valueOf(bdTaxVon6.toString().replace(",", "."));
		Double dTmp12 = Double.valueOf(bdTaxBis6.toString().replace(",", "."));
		Double dTmp13 = Double.valueOf(bdTaxVon7.toString().replace(",", "."));
		Double dTmp14 = Double.valueOf(bdTaxBis7.toString().replace(",", "."));
		
		String sTmpd1 = waehrungsFormat.format(dTmp1), sTmpd2 = waehrungsFormat.format(dTmp2);
		String sTmpd3 = waehrungsFormat.format(dTmp3), sTmpd4 = waehrungsFormat.format(dTmp4);
		String sTmpd5 = waehrungsFormat.format(dTmp5), sTmpd6 = waehrungsFormat.format(dTmp6);
		String sTmpd7 = waehrungsFormat.format(dTmp7), sTmpd8 = waehrungsFormat.format(dTmp8);
		String sTmpd9 = waehrungsFormat.format(dTmp9), sTmpd10 = waehrungsFormat.format(dTmp10);
		String sTmpd11 = waehrungsFormat.format(dTmp11), sTmpd12 = waehrungsFormat.format(dTmp12);
		String sTmpd13 = waehrungsFormat.format(dTmp13), sTmpd14 = waehrungsFormat.format(dTmp14);
		
		String sTmp1 = lblE1Stufe1.getText().replace("$", sTmpd1).replace("§", sTmpd2).replace("&", bdTaxVal1.toString());
		String sTmp2 = lblE1Stufe2.getText().replace("$", sTmpd3).replace("§", sTmpd4).replace("&", bdTaxVal2.toString());
		String sTmp3 = lblE1Stufe3.getText().replace("$", sTmpd5).replace("§", sTmpd6).replace("&", bdTaxVal3.toString());
		String sTmp4 = lblE1Stufe4.getText().replace("$", sTmpd7).replace("§", sTmpd8).replace("&", bdTaxVal4.toString());
		String sTmp5 = lblE1Stufe5.getText().replace("$", sTmpd9).replace("§", sTmpd10).replace("&", bdTaxVal5.toString());
		String sTmp6 = lblE1Stufe4.getText().replace("$", sTmpd11).replace("§", sTmpd12).replace("&", bdTaxVal6.toString());
		String sTmp7 = lblE1Stufe5.getText().replace("$", sTmpd13).replace("§", sTmpd14).replace("&", bdTaxVal7.toString());
		
		lblE1Stufe1.setText(sTmp1); // Texte für GWB Stufen anpassen
		lblE1Stufe2.setText(sTmp2);
		lblE1Stufe3.setText(sTmp3);
		lblE1Stufe4.setText(sTmp4);
		lblE1Stufe5.setText(sTmp5);
		lblE1Stufe6.setText(sTmp6);
		lblE1Stufe7.setText(sTmp7);
		
		List<BigDecimal> liste = new ArrayList<>();
		
		// Steuerstufen berechnen

		if(bdVorTax.compareTo(bdTaxVon7) >= 1) { // wenn VorTax größer oder gleich Stufe 7 von
			rest7 = bdVorTax.subtract(bdTaxVon7);
			rest6 = bdTaxBis6.subtract(bdTaxVon6);
			rest5 = bdTaxBis5.subtract(bdTaxVon5);
			rest4 = bdTaxBis4.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon6) >= 1) { // wenn VorTax größer oder gleich Stufe 6 von
			rest7 = new BigDecimal("0.00");
			rest6 = bdVorTax.subtract(bdTaxVon6);
			rest5 = bdTaxBis5.subtract(bdTaxVon5);
			rest4 = bdTaxBis4.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon5) >= 1) { // wenn VorTax größer oder gleich Stufe 5 von
			rest7 = new BigDecimal("0.00");
			rest6 = new BigDecimal("0.00");
			rest5 = bdVorTax.subtract(bdTaxVon5);
			rest4 = bdTaxBis4.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon4) >= 1) { // wenn VorTax größer oder gleich Stufe 4 von
			rest7 = new BigDecimal("0.00");
			rest6 = new BigDecimal("0.00");
			rest5 = new BigDecimal("0.00");
			rest4 = bdVorTax.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon3) >= 1) { // wenn VorTax größer oder gleich Stufe 3 von
			rest7 = new BigDecimal("0.00");
			rest6 = new BigDecimal("0.00");
			rest5 = new BigDecimal("0.00");
			rest4 = new BigDecimal("0.00");
			rest3 = bdVorTax.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon2) >= 1) { // wenn VorTax größer oder gleich Stufe 2 von
			rest7 = new BigDecimal("0.00");
			rest6 = new BigDecimal("0.00");
			rest5 = new BigDecimal("0.00");
			rest4 = new BigDecimal("0.00");
			rest3 = new BigDecimal("0.00");
			rest2 = bdVorTax.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon1) >= 1) { // wenn VorTax größer oder gleich Stufe 1 von
			rest7 = new BigDecimal("0.00");
			rest6 = new BigDecimal("0.00");
			rest5 = new BigDecimal("0.00");
			rest4 = new BigDecimal("0.00");
			rest3 = new BigDecimal("0.00");
			rest2 = new BigDecimal("0.00");
			rest1 = bdVorTax.subtract(bdTaxVon1);
		}
		
		liste.add(rest1); // Wert Stufe 1
		liste.add(rest2); // Wert Stufe 2
		liste.add(rest3); // Wert Stufe 3
		liste.add(rest4); // Wert Stufe 4
		liste.add(rest5); // Wert Stufe 5
		liste.add(rest6); // Wert Stufe 6
		liste.add(rest7); // Wert Stufe 7
		liste.add(rest1.multiply(new BigDecimal(arrTaxValues[1][4].toString().replace(",", ".")))); // Steuer Stufe 1
		liste.add(rest2.multiply(new BigDecimal(arrTaxValues[1][7].toString().replace(",", ".")))); // Steuer Stufe 2
		liste.add(rest3.multiply(new BigDecimal(arrTaxValues[1][10].toString().replace(",", ".")))); // Steuer Stufe 3
		liste.add(rest4.multiply(new BigDecimal(arrTaxValues[1][13].toString().replace(",", ".")))); // Steuer Stufe 4
		liste.add(rest5.multiply(new BigDecimal(arrTaxValues[1][16].toString().replace(",", ".")))); // Steuer Stufe 5
		liste.add(rest6.multiply(new BigDecimal(arrTaxValues[1][19].toString().replace(",", ".")))); // Steuer Stufe 6
		liste.add(rest7.multiply(new BigDecimal(arrTaxValues[1][22].toString().replace(",", ".")))); // Steuer Stufe 7
		
		return liste;
		
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }
	
	private static void getDBData() {
		
		String sSQLStatement = null;
		
		try {

			Arrays.stream(arrTaxValues).forEach(a -> Arrays.fill(a, null));
			Arrays.stream(arrGwbValues).forEach(a -> Arrays.fill(a, null));
			
			sSQLStatement = "SELECT * FROM [tblTaxValue] WHERE [id_year]=" + LoadData.getStrAktGJ();
			arrTaxValues = sqlReadArray(sConn, sSQLStatement);
			
			sSQLStatement = "SELECT * FROM [tblGwbValue] WHERE [id_year]=" + LoadData.getStrAktGJ();
			arrGwbValues = sqlReadArray(sConn, sSQLStatement);
		
		} catch (SQLException e) {
			logger.error("error in getting DB data - " + e);
		} catch (NullPointerException e) {
			logger.error("error in getting DB data - " + e);
		} catch (Exception e) {
			logger.error("error in getting DB data - " + e);
		}
		
		
		
	}
	
	public static void getSVData(BigDecimal bdSVQx1, BigDecimal bdSVQx2, BigDecimal bdSVQx3, BigDecimal bdSVQx4) {
		
		bdSVQ1 = bdSVQx1;
		bdSVQ2 = bdSVQx2;
		bdSVQ3 = bdSVQx3;
		bdSVQ4 = bdSVQx4;
		
		txtP109aSVS1.setValue(Double.valueOf(bdSVQ1.toString().replace(",", ".")));
		txtP109aSVS2.setValue(Double.valueOf(bdSVQ2.toString().replace(",", ".")));
		txtP109aSVS3.setValue(Double.valueOf(bdSVQ3.toString().replace(",", ".")));
		txtP109aSVS4.setValue(Double.valueOf(bdSVQ4.toString().replace(",", ".")));
		
		bdSVYear = bdSVQ1.add(bdSVQ2).add(bdSVQ3).add(bdSVQ4);
		bdSVYear = bdSVYear.multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP);
		
	}
	
	public static void setsConn(String sConn) {
		AnnualResult.sConn = sConn;
	}

	public static void setBdExpNetto(BigDecimal bdExpNetto) {
		AnnualResult.bdExpNetto = bdExpNetto;
	}

}
