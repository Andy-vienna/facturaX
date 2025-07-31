package org.andy.code.main.overview.table;

import static org.andy.toolbox.sql.Read.sqlReadArray;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.andy.code.entity.SQLmasterData;
import org.andy.code.main.LoadData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadBillOut {
	
	private static final Logger logger = LogManager.getLogger(LoadBillOut.class);
	
	private static final String dataTbl = "tbl_reOUT";
	private static String sConn;
	
	private static String[][] tmpArray = new String[100][60];
	private static String[][] sTemp = new String [100][9];
	
	private static int tmpAnz;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadAusgangsRechnung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###.00");

		Arrays.stream(sTemp).forEach(a -> Arrays.fill(a, null));

		try {

			Arrays.stream(tmpArray).forEach(a -> Arrays.fill(a, null));
			String sTblName = dataTbl.replace("_", LoadData.getStrAktGJ());
			String sSQLStatement = "SELECT * FROM " + sTblName + " ORDER BY [IdNummer]";

			tmpArray = sqlReadArray(sConn, sSQLStatement);
			JFoverview.setArrYearRE(tmpArray);

			if(tmpArray[0][0] != null) {
				tmpAnz = Integer.parseInt(tmpArray[0][0]);
				JFoverview.setAnzYearRE(tmpAnz);
			}else {
				tmpAnz = 0;
				JFoverview.setAnzYearRE(tmpAnz);
			}

			if(tmpAnz == 0) {
				return sTemp;
			}

			for(int i = 0; i < 100; i++) {
				JFoverview.setbActiveRE(i, true);
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {
				switch(tmpArray[x][2]) {
				case "0":
					JFoverview.setbActiveRE(x-1, false);
					break;
				case "1":
					JFoverview.setbActiveRE(x-1, true);
					break;
				}
				switch(tmpArray[x][3]) {
				case "0":
					JFoverview.setbPrintRE(x-1, false);
					break;
				case "1":
					JFoverview.setbPrintRE(x-1, true);
					break;
				}
				switch(tmpArray[x][4]) {
				case "0":
					JFoverview.setbMoneyRE(x-1, false);
					break;
				case "1":
					JFoverview.setbMoneyRE(x-1, true);
					break;
				}
				sTemp[x-1][0] = tmpArray[x][1]; // Spalte 0 - RE-Nummer
				sTemp[x-1][1] = tmpArray[x][5]; // Spalte 1 - Status
				sTemp[x-1][2] = tmpArray[x][6]; // Spalte 2 - Datum
				sTemp[x-1][3] = tmpArray[x][7]; // Spalte 3 - L-Zeitr.
				sTemp[x-1][4] = tmpArray[x][8]; // Spalte 4 - Referenz
				sTemp[x-1][5] = searchKunde(tmpArray[x][9]); // Spalte 5 - Kunde
				double tmpN = Double.parseDouble(tmpArray[x][12]);
				sTemp[x-1][6] = df.format(tmpN) + "  EUR"; // Spalte 6 - Netto
				double tmpU = Double.parseDouble(tmpArray[x][13]);
				sTemp[x-1][7] = df.format(tmpU) + "  EUR"; // Spalte 7 - USt.
				double tmpB = Double.parseDouble(tmpArray[x][14]);
				sTemp[x-1][8] = df.format(tmpB) + "  EUR"; // Spalte 8 - Brutto
			}
		} catch (SQLException e) {
			logger.error("error loading data from database - " + e);
		} catch (ClassNotFoundException e) {
			logger.error("error cause class for database connection is not found - " + e);
		} finally {
			if(!reRun) {
				//setSumREa();
			}
		}
		
		return sTemp;
	}
	
	//###################################################################################################################################################
	
	static String searchKunde(String sKdNr) {
		List<ArrayList<String>> kundenListe = SQLmasterData.getArrListKunde();

		// Prüfen, ob die Kundenliste null ist
		if (kundenListe == null || kundenListe.isEmpty()) {
			return sKdNr; // Falls die Liste leer oder null ist, gib die ursprüngliche Kundennummer zurück.
		}

		for (int kd = 0; kd < kundenListe.size(); kd++) {
			ArrayList<String> kunde = kundenListe.get(kd);

			// Prüfen, ob die Kunde-Liste null oder zu kurz ist
			if (kunde == null || kunde.size() < 2 || kunde.get(0) == null) {
				continue; // Überspringe ungültige Einträge
			}

			if (kunde.get(0).equals(sKdNr)) {
				return kunde.get(1); // Gib den Kundennamen zurück
			}
		}
		return sKdNr; // Falls keine Übereinstimmung gefunden wurde, gib die Nummer zurück
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		LoadBillOut.sConn = sConn;
	}

	public static String getDatatbl() {
		return dataTbl;
	}

}
