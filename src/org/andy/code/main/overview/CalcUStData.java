package org.andy.code.main.overview;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JFormattedTextField;

import org.andy.code.main.overview.panels.UStPanel;
import org.andy.code.misc.parseBigDecimal;
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
		BigDecimal[] raAT = new BigDecimal[4], ustAT = new BigDecimal[4], raEU = new BigDecimal[4];
		BigDecimal[] bd066 = new BigDecimal[4], bd067 = new BigDecimal[4], bd068 = new BigDecimal[4];
		BigDecimal[] zahlLast = new BigDecimal[4];

		// Initialisieren
		for (int i = 0; i < 4; i++) {
			raAT[i] = ustAT[i] = raEU[i] = bd066[i] = bd067[i] = bd068[i] = BigDecimal.ZERO;
		}

		try {
			for (int x = 1; (x - 1) < AnzYearBillOut; x++) {
				int quartal = getQuartalFromString(arrYearBillOut[x][6].trim(), "dd.MM.yyyy") - 1;
				String sValue = arrYearBillOut[x][13].trim();
				if (quartal >= 0 && quartal < 4) {
					if (sValue.equals("0.00")) {
						raEU[quartal] = raEU[quartal].add(parseBigDecimal.fromArray(arrYearBillOut, x, 12));
					} else {
						raAT[quartal] = raAT[quartal].add(parseBigDecimal.fromArray(arrYearBillOut, x, 12));
						ustAT[quartal] = ustAT[quartal].add(parseBigDecimal.fromArray(arrYearBillOut, x, 13));
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating revenue sum - " + e);
		}

		try {
			for (int x = 1; (x - 1) < AnzYearBillIn; x++) {
				int quartal = getQuartalFromString(arrYearBillIn[x][2].trim(), "yyyy-MM-dd") - 1;
				String sValue = arrYearBillIn[x][10].trim();
				if (quartal >= 0 && quartal < 4) {
					addValueToVatArray(sValue, bd066, bd067, bd068, quartal,
							parseBigDecimal.fromArray(arrYearBillIn, x, 13));
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating inbound billing sum - " + e);
		}

		try {
			for (int x = 1; (x - 1) < AnzExpenses; x++) {
				int quartal = getQuartalFromString(arrExpenses[x][1].trim(), "yyyy-MM-dd") - 1;
				String sValue = arrExpenses[x][4].trim();
				if (quartal >= 0 && quartal < 4) {
					addValueToVatArray(sValue, bd066, bd067, bd068, quartal,
							parseBigDecimal.fromArray(arrExpenses, x, 5));
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating expenses sum - " + e);
		}

		// Zahllast & Jahreswerte berechnen
		BigDecimal raATYear = BigDecimal.ZERO, ustATYear = BigDecimal.ZERO, raEUYear = BigDecimal.ZERO;
		BigDecimal bd066Year = BigDecimal.ZERO, bd067Year = BigDecimal.ZERO, bd068Year = BigDecimal.ZERO;
		BigDecimal zahlLastYear = BigDecimal.ZERO;

		for (int i = 0; i < 4; i++) {
			zahlLast[i] = ustAT[i].subtract(bd066[i]).subtract(bd067[i]).subtract(bd068[i]);
			raATYear = raATYear.add(raAT[i]);
			ustATYear = ustATYear.add(ustAT[i]);
			raEUYear = raEUYear.add(raEU[i]);
			bd066Year = bd066Year.add(bd066[i]);
			bd067Year = bd067Year.add(bd067[i]);
			bd068Year = bd068Year.add(bd068[i]);
		}
		zahlLastYear = ustATYear.subtract(bd066Year).subtract(bd067Year).subtract(bd068Year);

		// Ausgabe (je nach UI einfaches Array)
		setTxtFieldsQ(panel, raAT, ustAT, raEU, bd066, bd067, bd068, zahlLast);
		setTxtFieldsYear(panel, raATYear, ustATYear, raEUYear, bd066Year, bd067Year, bd068Year, zahlLastYear);
	}

	//###################################################################################################################################################
	
	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }
 
	private static void addValueToVatArray(String sValue, BigDecimal[] bd066, BigDecimal[] bd067, BigDecimal[] bd068,
			int idx, BigDecimal val) {
		switch (sValue) {
		case "20":
			bd066[idx] = bd066[idx].add(val);
			break;
		case "10":
			bd067[idx] = bd067[idx].add(val);
			break;
		case "13":
			bd068[idx] = bd068[idx].add(val);
			break;
		}
	}

	private static void setTxtFieldsQ(UStPanel panel, BigDecimal[] raAT, BigDecimal[] ustAT, BigDecimal[] raEU,
			BigDecimal[] bd066, BigDecimal[] bd067, BigDecimal[] bd068, BigDecimal[] zahlLast) {
		for (int i = 0; i < 4; i++) { // Q1-Q4
			panel.setFieldValue(0, i, Double.valueOf(parseBigDecimal.fromBD(raAT[i])));
			panel.setFieldValue(1, i, Double.valueOf(parseBigDecimal.fromBD(ustAT[i])));
			panel.setFieldValue(2, i, Double.valueOf(parseBigDecimal.fromBD(raEU[i])));
			panel.setFieldValue(3, i, Double.valueOf(parseBigDecimal.fromBD(bd066[i])));
			panel.setFieldValue(4, i, Double.valueOf(parseBigDecimal.fromBD(bd067[i])));
			panel.setFieldValue(5, i, Double.valueOf(parseBigDecimal.fromBD(bd068[i])));
			panel.setZahllast(i, Double.valueOf(parseBigDecimal.fromBD(zahlLast[i])));
		}
	}

	private static void setTxtFieldsYear(UStPanel panel, BigDecimal raATYear, BigDecimal ustATYear, BigDecimal raEUYear, BigDecimal bd066Year,
			BigDecimal bd067Year, BigDecimal bd068Year, BigDecimal zahlLastYear) {
		panel.setFieldValue(0, 4, Double.valueOf(parseBigDecimal.fromBD(raATYear)));
		panel.setFieldValue(1, 4, Double.valueOf(parseBigDecimal.fromBD(ustATYear)));
		panel.setFieldValue(2, 4, Double.valueOf(parseBigDecimal.fromBD(raEUYear)));
		panel.setFieldValue(3, 4, Double.valueOf(parseBigDecimal.fromBD(bd066Year)));
		panel.setFieldValue(4, 4, Double.valueOf(parseBigDecimal.fromBD(bd067Year)));
		panel.setFieldValue(5, 4, Double.valueOf(parseBigDecimal.fromBD(bd068Year)));
		panel.setZahllast(4, Double.valueOf(parseBigDecimal.fromBD(zahlLastYear)));
	}

}
