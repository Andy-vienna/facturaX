package org.andy.code.main.overview;

import static org.andy.toolbox.sql.Read.sqlReadArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.andy.code.main.LoadData;
import org.andy.gui.main.overview_panels.TaxPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalcTaxData {
	
	private static final Logger logger = LogManager.getLogger(CalcTaxData.class);
	
	private static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
	
	private static BigDecimal bdSVQ1 = BigDecimal.ZERO, bdSVQ2 = BigDecimal.ZERO, bdSVQ3 = BigDecimal.ZERO, bdSVQ4 = BigDecimal.ZERO;
	private static BigDecimal bdSVYear = BigDecimal.ZERO;
	
	private static String[][] arrTaxValues = new String[2][25];
	private static String[][] arrGwbValues = new String[2][10];
	private static String sConn = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static void setValuesTax(TaxPanel panel, int AnzYearBillOut, String[][] arrYearBillOut, BigDecimal bdREnetto, BigDecimal bdEnetto) {	
		setValues(panel, AnzYearBillOut, arrYearBillOut, bdREnetto, bdEnetto);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setValues(TaxPanel panel, int AnzYearBillOut, String[][] arrYearBillOut, BigDecimal bdREnetto, BigDecimal bdEnetto) {
		
		getDBData(); // Steuergrenzen und Gewinnfreibetragsgrenzen aus DB lesen
		
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
			
			BigDecimal bdBA = bdREnetto.add(bdEnetto); // Betriebsausgaben netto komplett
			bdExpenses = bdBA.multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP); // Betriebsausgaben netto negativ
			
			bdVorGwb = bdTmp2.add(bdSVYear).add(bdOeffiP).add(bdAPausch).add(bdExpenses); // VorGWB wird aus der Summe der Einnahmen, SV, öffentlicher Pauschale, APauschale und Ausgaben netto berechnet
			
			GwbStufe = calcGWB(panel, bdVorGwb); // Berechnung der GWB-Stufen
			
			bdGwbTotal = GwbStufe.stream().reduce(BigDecimal.ZERO, BigDecimal::add); // Summe der GWB-Stufen
			bdGwbTotalNeg = bdGwbTotal.multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP); // GWB negativ
			
			bdErgYear = bdTmp2.add(bdSVYear).add(bdOeffiP).add(bdAPausch).add(bdExpenses).add(bdGwbTotalNeg); // Ergebnis wird aus der Summe der Einnahmen, SV, öffentlicher Pauschale, APauschale, Ausgaben netto und GWB negativ berechnet

			//bdErgYear = new BigDecimal("98456.23");
			
			TaxStufe = calcTAX(panel, bdErgYear); // Berechnung der Steuerstufen
			
			bdTaxTotal = TaxStufe.get(7).add(TaxStufe.get(8)).add(TaxStufe.get(9)).add(TaxStufe.get(10)).add(TaxStufe.get(11))
				.add(TaxStufe.get(12)).add(TaxStufe.get(13)); // Summe der errechneten Steuern bilden
			
			
		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}
		
		panel.setTxtP109aEin(Double.valueOf(bdTmp2.toString().replace(",", ".")));
		panel.setTxtP109aSVS(0, Double.valueOf(bdSVYear.toString().replace(",", ".")));
		panel.setTxtP109aOeffiP(Double.valueOf(bdOeffiP.toString().replace(",", ".")));
		panel.setTxtP109aAPausch(Double.valueOf(bdAPausch.toString().replace(",", ".")));
		panel.setTxtP109aExpenses(Double.valueOf(bdExpenses.toString().replace(",", ".")));

		panel.setTxtVorGWB(Double.valueOf(bdVorGwb.toString().replace(",", ".")));
		panel.setTxtGwbStufen(0, Double.valueOf(GwbStufe.get(0).toString().replace(",", ".")));
		panel.setTxtGwbStufen(1, Double.valueOf(GwbStufe.get(1).toString().replace(",", ".")));
		panel.setTxtGwbStufen(2, Double.valueOf(GwbStufe.get(2).toString().replace(",", ".")));
		panel.setTxtGwbStufen(3, Double.valueOf(GwbStufe.get(3).toString().replace(",", ".")));
		panel.setTxtGwbTotal(Double.valueOf(bdGwbTotal.toString().replace(",", ".")));
		
		panel.setTxtP109aGrundfrei(Double.valueOf(bdGwbTotalNeg.toString().replace(",", ".")));
		panel.setTxtP109aErgebnis(Double.valueOf(bdErgYear.toString().replace(",", ".")));
		
		panel.setTxtE1VorSt(Double.valueOf(bdErgYear.toString().replace(",", "."))); // Gewinn vor Steuer für E1 wird aus dem Ergebnis der P109a übernommen
		panel.setTxtE1Stufen(0, Double.valueOf(TaxStufe.get(0).toString().replace(",", "."))); // Summe Stufe 1
		panel.setTxtE1Stufen(1, Double.valueOf(TaxStufe.get(1).toString().replace(",", "."))); // Summe Stufe 2
		panel.setTxtE1Stufen(2, Double.valueOf(TaxStufe.get(2).toString().replace(",", "."))); // Summe Stufe 3
		panel.setTxtE1Stufen(3, Double.valueOf(TaxStufe.get(3).toString().replace(",", "."))); // Summe Stufe 4
		panel.setTxtE1Stufen(4, Double.valueOf(TaxStufe.get(4).toString().replace(",", "."))); // Summe Stufe 5
		panel.setTxtE1Stufen(5, Double.valueOf(TaxStufe.get(5).toString().replace(",", "."))); // Summe Stufe 6
		panel.setTxtE1Stufen(6, Double.valueOf(TaxStufe.get(6).toString().replace(",", "."))); // Summe Stufe 7
		
		panel.setTxtE1Tax(0, Double.valueOf(TaxStufe.get(7).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 1
		panel.setTxtE1Tax(1, Double.valueOf(TaxStufe.get(8).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 2
		panel.setTxtE1Tax(2, Double.valueOf(TaxStufe.get(9).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 3
		panel.setTxtE1Tax(3, Double.valueOf(TaxStufe.get(10).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 4
		panel.setTxtE1Tax(4, Double.valueOf(TaxStufe.get(11).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 5
		panel.setTxtE1Tax(5, Double.valueOf(TaxStufe.get(12).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 6
		panel.setTxtE1Tax(6, Double.valueOf(TaxStufe.get(13).toString().replace(",", "."))); // voraussichtliche Steuer in Stufe 7
		panel.setTxtE1Summe(Double.valueOf(bdTaxTotal.toString().replace(",", "."))); // voraussichtliche Einkommensteuer gesamt
		
		ArrayList<BigDecimal> tmpListe = new ArrayList<>(); // Liste für §109a Formular erzeugen und in Setter schreiben
		tmpListe.add(bdTmp2); // Einnahmen
		tmpListe.add(bdSVQ1); // SV Q1
		tmpListe.add(bdSVQ2); // SV Q2
		tmpListe.add(bdSVQ3); // SV Q3
		tmpListe.add(bdSVQ4); // SV Q4
		tmpListe.add(bdSVYear); // SV Jahr
		tmpListe.add(bdOeffiP); // Öffi-Pauschale
		tmpListe.add(bdAPausch); // Arbeitsplatzpauschale
		tmpListe.add(bdExpenses); // Betriebsausgaben netto
		tmpListe.add(bdVorGwb); // Zwischensumme
		tmpListe.add(bdGwbTotalNeg); // Gewinnfreibetrag
		tmpListe.add(bdErgYear); // Ergebnis
		
		panel.setDataExcel(tmpListe); // Liste für P109a in Setter schreiben
		
	}
	
	private static List<BigDecimal> calcGWB(TaxPanel panel, BigDecimal bdVorGwb) {

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
		
		String sTmpd1 = currencyFormat.format(dTmp1);
		String sTmpd2 = currencyFormat.format(dTmp2);
		String sTmpd3 = currencyFormat.format(dTmp3);
		String sTmpd4 = currencyFormat.format(dTmp4);
		
		String sTmp1 = panel.getLblGwbStufen(0).replace("§", sTmpd1).replace("&", bdGwbVal1.toString());
		String sTmp2 = panel.getLblGwbStufen(1).replace("§", sTmpd2).replace("&", bdGwbVal2.toString());
		String sTmp3 = panel.getLblGwbStufen(2).replace("§", sTmpd3).replace("&", bdGwbVal3.toString());
		String sTmp4 = panel.getLblGwbStufen(3).replace("§", sTmpd4).replace("&", bdGwbVal4.toString());
		
		panel.setLblGwbStufen(0, sTmp1); // Texte für GWB Stufen anpassen
		panel.setLblGwbStufen(1, sTmp2);
		panel.setLblGwbStufen(2, sTmp3);
		panel.setLblGwbStufen(3, sTmp4);
		
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
	
	private static List<BigDecimal> calcTAX(TaxPanel panel, BigDecimal bdVorTax){

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
		
		String sTmpd1 = currencyFormat.format(dTmp1), sTmpd2 = currencyFormat.format(dTmp2);
		String sTmpd3 = currencyFormat.format(dTmp3), sTmpd4 = currencyFormat.format(dTmp4);
		String sTmpd5 = currencyFormat.format(dTmp5), sTmpd6 = currencyFormat.format(dTmp6);
		String sTmpd7 = currencyFormat.format(dTmp7), sTmpd8 = currencyFormat.format(dTmp8);
		String sTmpd9 = currencyFormat.format(dTmp9), sTmpd10 = currencyFormat.format(dTmp10);
		String sTmpd11 = currencyFormat.format(dTmp11), sTmpd12 = currencyFormat.format(dTmp12);
		String sTmpd13 = currencyFormat.format(dTmp13), sTmpd14 = currencyFormat.format(dTmp14);
		
		String sTmp1 = panel.getLblE1Stufen(0).replace("$", sTmpd1).replace("§", sTmpd2).replace("&", bdTaxVal1.toString());
		String sTmp2 = panel.getLblE1Stufen(1).replace("$", sTmpd3).replace("§", sTmpd4).replace("&", bdTaxVal2.toString());
		String sTmp3 = panel.getLblE1Stufen(2).replace("$", sTmpd5).replace("§", sTmpd6).replace("&", bdTaxVal3.toString());
		String sTmp4 = panel.getLblE1Stufen(3).replace("$", sTmpd7).replace("§", sTmpd8).replace("&", bdTaxVal4.toString());
		String sTmp5 = panel.getLblE1Stufen(4).replace("$", sTmpd9).replace("§", sTmpd10).replace("&", bdTaxVal5.toString());
		String sTmp6 = panel.getLblE1Stufen(5).replace("$", sTmpd11).replace("§", sTmpd12).replace("&", bdTaxVal6.toString());
		String sTmp7 = panel.getLblE1Stufen(6).replace("$", sTmpd13).replace("§", sTmpd14).replace("&", bdTaxVal7.toString());
		
		panel.setLblE1Stufen(0, sTmp1); // Texte für E1 Stufen anpassen
		panel.setLblE1Stufen(1, sTmp2);
		panel.setLblE1Stufen(2, sTmp3);
		panel.setLblE1Stufen(3, sTmp4);
		panel.setLblE1Stufen(4, sTmp5);
		panel.setLblE1Stufen(5, sTmp6);
		panel.setLblE1Stufen(6, sTmp7);

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
	
	public static void getSVData(TaxPanel panel, BigDecimal bdSVQx1, BigDecimal bdSVQx2, BigDecimal bdSVQx3, BigDecimal bdSVQx4) {
		
		bdSVQ1 = bdSVQx1;
		bdSVQ2 = bdSVQx2;
		bdSVQ3 = bdSVQx3;
		bdSVQ4 = bdSVQx4;
		
		panel.setTxtP109aSVS(1, Double.valueOf(bdSVQ1.toString().replace(",", ".")));
		panel.setTxtP109aSVS(2, Double.valueOf(bdSVQ2.toString().replace(",", ".")));
		panel.setTxtP109aSVS(3, Double.valueOf(bdSVQ3.toString().replace(",", ".")));
		panel.setTxtP109aSVS(4, Double.valueOf(bdSVQ4.toString().replace(",", ".")));
		
		bdSVYear = bdSVQ1.add(bdSVQ2).add(bdSVQ3).add(bdSVQ4);
		bdSVYear = bdSVYear.multiply(new BigDecimal("-1")).setScale(2, RoundingMode.HALF_UP);
		
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
	
	public static void setsConn(String sConn) {
		CalcTaxData.sConn = sConn;
	}

}
