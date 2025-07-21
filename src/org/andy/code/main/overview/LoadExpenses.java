package org.andy.code.main.overview;

import static org.andy.toolbox.sql.Read.sqlReadArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.andy.code.main.LoadData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadExpenses {
	
	private static final Logger logger = LogManager.getLogger(LoadExpenses.class);
	
	private static final String dataTbl = "tbl_expenses";
	private static String sConn;
	
	private static String[][] tmpArray = new String[100][9];
	private static String[][] sTemp = new String [100][8];
	
	private static int tmpAnz;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadAusgaben(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

		Arrays.stream(sTemp).forEach(a -> Arrays.fill(a, null));

		try {

			Arrays.stream(tmpArray).forEach(a -> Arrays.fill(a, null));
			String sTblName = dataTbl.replace("_", LoadData.getStrAktGJ());
			String sSQLStatement = "SELECT * FROM " + sTblName + " ORDER BY [Datum]";

			tmpArray = sqlReadArray(sConn, sSQLStatement);
			JFoverview.setArrExpenses(tmpArray);

			if(tmpArray[0][0] != null) {
				tmpAnz = Integer.parseInt(tmpArray[0][0]);
				JFoverview.setAnzExpenses(tmpAnz);
			}else {
				tmpAnz = 0;
				JFoverview.setAnzExpenses(tmpAnz);
			}

			if(tmpAnz == 0) {
				return sTemp;
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {

				sTemp[x-1][0] = tmpArray[x][9]; // Spalte 0 - Id
				LocalDate datum = LocalDate.parse(tmpArray[x][1], inputFormat);
				String stmpD = datum.format(formatter);
				sTemp[x-1][1] = stmpD; // Spalte 1 - Datum
				sTemp[x-1][2] = tmpArray[x][2]; // Spalte 2 - Art
				BigDecimal bdtmpN = new BigDecimal(tmpArray[x][3]).setScale(2, RoundingMode.HALF_UP);
				String stmpN = decimalFormat.format(bdtmpN);
				sTemp[x-1][3] = stmpN + "  EUR"; // Spalte 3 - Netto
				sTemp[x-1][4] = tmpArray[x][4]; // Spalte 4 - Steuersatz
				BigDecimal bdtmpU = new BigDecimal(tmpArray[x][5]).setScale(2, RoundingMode.HALF_UP);
				String stmpU = decimalFormat.format(bdtmpU);
				sTemp[x-1][5] = stmpU + "  EUR"; // Spalte 5 - USt.
				BigDecimal bdtmpB = new BigDecimal(tmpArray[x][6]).setScale(2, RoundingMode.HALF_UP);
				String stmpB = decimalFormat.format(bdtmpB);
				sTemp[x-1][6] = stmpB + "  EUR"; // Spalte 6 - Brutto
				sTemp[x-1][7] = tmpArray[x][7]; // Spalte 6 - Dateianlage
			}
		} catch (SQLException e) {
			logger.error("error loading data from database - " + e);
		} catch (ClassNotFoundException e) {
			logger.error("error cause class for database connection is not found - " + e);
		} finally {
			if(!reRun) {
				//setSumEX();
			}
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		LoadExpenses.sConn = sConn;
	}
	
	public static String getDatatbl() {
		return dataTbl;
	}

}
