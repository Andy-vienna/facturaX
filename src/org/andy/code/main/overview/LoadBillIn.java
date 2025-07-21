package org.andy.code.main.overview;

import static org.andy.toolbox.sql.Read.sqlReadArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import org.andy.code.main.LoadData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadBillIn {
	
	private static final Logger logger = LogManager.getLogger(LoadBillIn.class);
	
	private static final String dataTbl = "tbl_reIN";
	private static String sConn;
	
	private static String[][] tmpArray = new String[100][20];
	private static String[][] sTemp = new String [100][17];
	
	private static int tmpAnz;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadEingangsRechnung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###.00");

		Arrays.stream(sTemp).forEach(a -> Arrays.fill(a, null));

		try {

			Arrays.stream(tmpArray).forEach(a -> Arrays.fill(a, null));
			String sTblName = dataTbl.replace("_", LoadData.getStrAktGJ());
			String sSQLStatement = "SELECT * FROM " + sTblName + " ORDER BY [re_datum]";

			tmpArray = sqlReadArray(sConn, sSQLStatement);
			JFoverview.setArrYearBillIn(tmpArray);

			if(tmpArray[0][0] != null) {
				tmpAnz = Integer.parseInt(tmpArray[0][0]);
				JFoverview.setAnzYearBillIn(tmpAnz);
			}else {
				tmpAnz = 0;
				JFoverview.setAnzYearBillIn(tmpAnz);
			}

			if(tmpAnz == 0) {
				return sTemp;
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {
				switch(Integer.parseInt(tmpArray[x][19])) {
				case 0:
					JFoverview.setbPayedRe(x-1, false);
					break;
				case 1:
					JFoverview.setbPayedRe(x-1, true);
					break;
				}
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {

				sTemp[x-1][0] = tmpArray[x][1];

				LocalDate datum1 = LocalDate.parse(tmpArray[x][2], inputFormat);
				String stmpA = datum1.format(formatter);
				sTemp[x-1][1] = stmpA;

				sTemp[x-1][2] = tmpArray[x][3];
				sTemp[x-1][3] = tmpArray[x][4];
				sTemp[x-1][4] = tmpArray[x][5];
				sTemp[x-1][5] = tmpArray[x][6];
				sTemp[x-1][6] = tmpArray[x][7];
				sTemp[x-1][7] = tmpArray[x][8];
				sTemp[x-1][8] = tmpArray[x][9];

				BigDecimal bdtmpN1 = new BigDecimal(tmpArray[x][10]).setScale(2, RoundingMode.HALF_UP);
				String stmpN1 = df.format(bdtmpN1);
				sTemp[x-1][9] = stmpN1;

				BigDecimal bdtmpN2 = new BigDecimal(tmpArray[x][11]).setScale(2, RoundingMode.HALF_UP);
				String stmpN2 = df.format(bdtmpN2);
				sTemp[x-1][10] = stmpN2;

				BigDecimal bdtmpN3 = new BigDecimal(tmpArray[x][12]).setScale(2, RoundingMode.HALF_UP);
				String stmpN3 = df.format(bdtmpN3);
				sTemp[x-1][11] = stmpN3;

				BigDecimal bdtmpN4 = new BigDecimal(tmpArray[x][13]).setScale(2, RoundingMode.HALF_UP);
				String stmpN4 = df.format(bdtmpN4);
				sTemp[x-1][12] = stmpN4;

				BigDecimal bdtmpN5 = new BigDecimal(tmpArray[x][14]).setScale(2, RoundingMode.HALF_UP);
				String stmpN5 = df.format(bdtmpN5);
				sTemp[x-1][13] = stmpN5;

				LocalDate datum2 = LocalDate.parse(tmpArray[x][15], inputFormat);
				String stmpG = datum2.format(formatter);
				sTemp[x-1][14] = stmpG;

				sTemp[x-1][15] = tmpArray[x][16];
				sTemp[x-1][16] = tmpArray[x][17];

			}
		} catch (SQLException e) {
			logger.error("error loading data from database - " + e);
		} catch (ClassNotFoundException e) {
			logger.error("error cause class for database connection is not found - " + e);
		} finally {
			if(!reRun) {
				//setSumREe();
			}
		}
		return sTemp;

	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		LoadBillIn.sConn = sConn;
	}

	public static String getDatatbl() {
		return dataTbl;
	}

}
