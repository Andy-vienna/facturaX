package org.andy.code.main.overview;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JFormattedTextField;

import org.andy.code.misc.parseBigDecimal;
import org.andy.gui.main.overview_panels.UStPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CalcUStData {

	private static final Logger logger = LogManager.getLogger(CalcUStData.class);
	
	public static JFormattedTextField[][] txtFields;     // [zeile][spalte] → z.B. [0][0] = txt000Q1
	public static JFormattedTextField[] txtZahllast;     // [spalte]        → z.B. [0] = Q1, [4] = Jahr
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static void setValuesUVA(UStPanel panel, int AnzYearBillIn, int AnzYearBillOut, int AnzExpenses, String[][] arrYearBillIn,
			String[][] arrYearBillOut, String[][] arrExpenses) {
		setValues(panel, AnzYearBillIn, AnzYearBillOut, AnzExpenses, arrYearBillIn, arrYearBillOut, arrExpenses);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setValues(UStPanel panel, int AnzYearBillIn, int AnzYearBillOut, int AnzExpenses, String[][] arrYearBillIn,
			String[][] arrYearBillOut, String[][] arrExpenses) {

		// Pro Quartal: 0=Q1, 1=Q2, 2=Q3, 3=Q4
		BigDecimal[] bdKz000 = new BigDecimal[4], bdKz021 = new BigDecimal[4], bdKz022 = new BigDecimal[4], bdKz060 = new BigDecimal[4];
		BigDecimal[] zahlLast = new BigDecimal[4], tmp20 = new BigDecimal[4], tmp10 = new BigDecimal[4], tmp13 = new BigDecimal[4];
		BigDecimal[] tmpKz021 = new BigDecimal[4], tmpKz022 = new BigDecimal[4];
		BigDecimal[] tmpUst20 = new BigDecimal[5];
		

		// Initialisieren
		for (int i = 0; i < 4; i++) {
			bdKz000[i] = bdKz021[i] = bdKz022[i] = bdKz060[i] = BigDecimal.ZERO;
			tmpKz021[i] = tmpKz022[i] = BigDecimal.ZERO;
			tmp20[i] = tmp10[i] = tmp13[i] = zahlLast[i] = BigDecimal.ZERO;
		}
		for (int i = 0; i < 5; i++) {
			tmpUst20[i] = BigDecimal.ZERO;
		}

		// Berechnung Bemessungsgrundlage (Ausgangsrechnungen Inland | Ausgangsrechnungen Ausland)
		try {
			for (int x = 1; (x - 1) < AnzYearBillOut; x++) {
				int quartal = getQuartalFromString(arrYearBillOut[x][6].trim(), "dd.MM.yyyy") - 1;
				String sValue = arrYearBillOut[x][13].trim();
				BigDecimal bdVal = parseBigDecimal.fromArray(arrYearBillOut, x, 12);
				if (quartal >= 0 && quartal < 4) {
					if (sValue.equals("0.00")) {
						tmpKz021[quartal] = tmpKz021[quartal].add(bdVal);
					} else {
						tmpKz022[quartal] = tmpKz022[quartal].add(bdVal);
					}
				}
			}
			for (int i = 0; i < 4; i++) {
				bdKz000[i] = tmpKz021[i].add(tmpKz022[i]); // Kz.000 = Summe der Bemessungsgrundlage
				bdKz021[i] = tmpKz021[i]; // Kz.021 = Innergemeinschaftliche sonstige Leistungen
				bdKz022[i] = tmpKz022[i]; // Kz.022 = zu versteuern mit Normalsteuersatz 20%
				tmpUst20[i] = tmpKz022[i].multiply(new BigDecimal("0.2")); // USt 20% auf Kz.022
			}
			
		} catch (NullPointerException e) {
			logger.error("error in calculating revenue sum - " + e);
		}
		
		// Berechnung der abziehbaren Vorsteuer (Eingangsrechnungen Inland mit Steuersatz 20%, 10% und 13%)
		try {
			for (int x = 1; (x - 1) < AnzYearBillIn; x++) {
				int quartal = getQuartalFromString(arrYearBillIn[x][2].trim(), "yyyy-MM-dd") - 1;
				String sValue = arrYearBillIn[x][10].trim();
				BigDecimal bdVal = parseBigDecimal.fromArray(arrYearBillIn, x, 11);
				if (quartal >= 0 && quartal < 4) {
					switch (sValue) {
					case "20":
						tmp20[quartal] = tmp20[quartal].add(bdVal);
						break;
					case "10":
						tmp10[quartal] = tmp10[quartal].add(bdVal);
						break;
					case "13":
						tmp13[quartal] = tmp13[quartal].add(bdVal);
						break;
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating inbound billing sum - " + e);
		}

		// Berechnung der abziehbaren Vorsteuer (Betriebsausgaben Inland mit Steuersatz 20%, 10% und 13%)
		try {
			for (int x = 1; (x - 1) < AnzExpenses; x++) {
				int quartal = getQuartalFromString(arrExpenses[x][1].trim(), "yyyy-MM-dd") - 1;
				String sValue = arrExpenses[x][4].trim();
				BigDecimal bdVal = parseBigDecimal.fromArray(arrExpenses, x, 5);
				if (quartal >= 0 && quartal < 4) {
					switch (sValue) {
					case "20":
						tmp20[quartal] = tmp20[quartal].add(bdVal);
						break;
					case "10":
						tmp10[quartal] = tmp10[quartal].add(bdVal);
						break;
					case "13":
						tmp13[quartal] = tmp13[quartal].add(bdVal);
						break;
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating expenses sum - " + e);
		}
		
		// Gesamtbetrag der Vorsteuer (Kz.060) pro Quartal
		for (int i = 0; i < 4; i++) {
			bdKz060[i] = tmp20[i].add(tmp10[i]).add(tmp13[i]);
		}

		// Zahllast & Jahreswerte berechnen
		BigDecimal bdKz000year = BigDecimal.ZERO, bdKz021year = BigDecimal.ZERO, bdKz022year = BigDecimal.ZERO;
		BigDecimal bdKz060year = BigDecimal.ZERO;
		BigDecimal zahlLastYear = BigDecimal.ZERO;

		for (int i = 0; i < 4; i++) {
			zahlLast[i] = tmpUst20[i].subtract(bdKz060[i]);
			bdKz000year = bdKz000year.add(bdKz000[i]);
			bdKz021year = bdKz021year.add(bdKz021[i]);
			bdKz022year = bdKz022year.add(bdKz022[i]);
			bdKz060year = bdKz060year.add(bdKz060[i]);
		}
		tmpUst20[4] = bdKz022year.multiply(new BigDecimal("0.2")); // USt 20% auf Kz.022 für das Jahr
		zahlLastYear = tmpUst20[4].subtract(bdKz060year);

		// Ausgabe (je nach UI einfaches Array)
		setTxtFieldsQ(panel, bdKz000, bdKz021, bdKz022, bdKz060, zahlLast);
		setTxtFieldsYear(panel, bdKz000year, bdKz021year, bdKz022year, bdKz060year, zahlLastYear);
	}

	//###################################################################################################################################################
	
	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }
 
	private static void setTxtFieldsQ(UStPanel panel, BigDecimal[] Kz000, BigDecimal[] Kz021, BigDecimal[] Kz022,
			BigDecimal[] Kz060, BigDecimal[] zahlLast) {
		for (int i = 0; i < 4; i++) { // Q1-Q4
			panel.setFieldValue(0, i, Double.valueOf(parseBigDecimal.fromBD(Kz000[i])));
			panel.setFieldValue(1, i, Double.valueOf(parseBigDecimal.fromBD(Kz021[i])));
			panel.setFieldValue(2, i, Double.valueOf(parseBigDecimal.fromBD(Kz022[i])));
			panel.setFieldValue(3, i, Double.valueOf(parseBigDecimal.fromBD(Kz060[i])));
			panel.setZahllast(i, Double.valueOf(parseBigDecimal.fromBD(zahlLast[i])));
		}
	}

	private static void setTxtFieldsYear(UStPanel panel, BigDecimal Kz000year, BigDecimal Kz021year, BigDecimal Kz022year, BigDecimal Kz060year,
			BigDecimal zahlLastYear) {
		panel.setFieldValue(0, 4, Double.valueOf(parseBigDecimal.fromBD(Kz000year)));
		panel.setFieldValue(1, 4, Double.valueOf(parseBigDecimal.fromBD(Kz021year)));
		panel.setFieldValue(2, 4, Double.valueOf(parseBigDecimal.fromBD(Kz022year)));
		panel.setFieldValue(3, 4, Double.valueOf(parseBigDecimal.fromBD(Kz060year)));
		panel.setZahllast(4, Double.valueOf(parseBigDecimal.fromBD(zahlLastYear)));
	}

}
