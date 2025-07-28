package org.andy.code.main.overview.table;

import static org.andy.toolbox.sql.Read.sqlReadArray;
import static org.andy.toolbox.sql.TableHandling.sqlCreateTable;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.andy.code.main.LoadData;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadOffer {
	
	private static final Logger logger = LogManager.getLogger(LoadOffer.class);
	
	private static final String dataTbl = "tbl_an";
	private static String sConn;
	
	private static String[][] tmpArray = new String[100][60];
	private static String[][] sTemp = new String [100][6];
	
	private static int tmpAnz;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadAngebot(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		boolean bResult = false;

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###.00");

		Arrays.stream(sTemp).forEach(a -> Arrays.fill(a, null));

		try {

			Arrays.stream(tmpArray).forEach(a -> Arrays.fill(a, null));
			String sTblName = dataTbl.replace("_", LoadData.getStrAktGJ());
			String sSQLStatement = "SELECT * FROM " + sTblName + " ORDER BY [IdNummer]";

			tmpArray = sqlReadArray(sConn, sSQLStatement);
			JFoverview.setArrYearAN(tmpArray);

			if(tmpArray[0][0] != null) {
				tmpAnz = Integer.parseInt(tmpArray[0][0]);
				JFoverview.setAnzYearAN(tmpAnz);
			}else {
				tmpAnz = 0;
				JFoverview.setAnzYearAN(tmpAnz);
			}

			if(tmpAnz == 0) {
				return sTemp;
			}

			for(int i = 0; i < 100; i++) {
				JFoverview.setbActiveAN(i, true);
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {
				switch(tmpArray[x][2]) {
				case "0":
					JFoverview.setbActiveAN(x-1, false);
					break;
				case "1":
					JFoverview.setbActiveAN(x-1, true);
					break;
				}
				switch(tmpArray[x][3]) {
				case "0":
					JFoverview.setbPrintAN(x-1, false);
					break;
				case "1":
					JFoverview.setbPrintAN(x-1, true);
					break;
				}
				switch(tmpArray[x][4]) {
				case "0":
					JFoverview.setbOrderAN(x-1, false);
					break;
				case "1":
					JFoverview.setbOrderAN(x-1, true);
					break;
				}
				sTemp[x-1][0] = tmpArray[x][1]; // Spalte 0 - AN-Nummer
				sTemp[x-1][1] = tmpArray[x][5]; // Spalte 1 - Status
				sTemp[x-1][2] = tmpArray[x][6]; // Spalte 2 - Datum
				sTemp[x-1][3] = tmpArray[x][7]; // Spalte 3 - Referenz
				sTemp[x-1][4] = searchKunde(tmpArray[x][8]); // Spalte 4 - Kunde
				double tmpN = Double.parseDouble(tmpArray[x][10]);
				sTemp[x-1][5] = df.format(tmpN) + "  EUR"; // Spalte 5 - Netto

			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			bResult = questionCreate();
			if(bResult == false) {
				return sTemp;
			}
			loadAngebot(bResult);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error cause class for database connection is not found - " + e);
		} finally {
			if(!reRun) {
				//setSumAN();
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
	
	static boolean questionCreate() {

		logger.error("no data table for financial year " + LoadData.getStrAktGJ() + " available - asking to create it");

		int auswahl = JOptionPane.showConfirmDialog(null, "<html>keine Tabelle für <b>" + LoadData.getStrAktGJ()
		+ "</b> vorhanden ...<br>soll diese angelegt werden ?</html>", "erzeugen ?", JOptionPane.YES_NO_OPTION);

		if (auswahl == JOptionPane.NO_OPTION) {
			logger.error("user cancelled creating new data table");
			return false;
		}
		if (auswahl == JOptionPane.YES_OPTION) {
			try {
				sqlCreateTable(sConn, Integer.parseInt(LoadData.getStrAktGJ()), LoadData.getStrDBNameSource());
				logger.info("data table for financial year " + LoadData.getStrAktGJ() + " available - successfully created");
				return true;
			} catch (NumberFormatException | ClassNotFoundException | SQLException e1) {
				logger.error("error creating new data table - " + e1);
				return false;
			}
		}
		return false;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		LoadOffer.sConn = sConn;
	}
	
	public static String getDatatbl() {
		return dataTbl;
	}

}
