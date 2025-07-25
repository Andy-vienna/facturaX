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
import org.andy.gui.main.overview_panels.TaxPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadSvTax {
	
	private static final Logger logger = LogManager.getLogger(LoadOffer.class);
	
	private static final String dataTbl = "tbl_svtax";
	private static String sConn;
	
	private static String[][] tmpArray = new String[30][9];
	private static String[][] sTemp = new String [30][6];
	
	private static int tmpAnz;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadSvTax(boolean reRun, TaxPanel panelP109a) {
		return loadData(reRun, panelP109a);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun, TaxPanel panelP109a) {

		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###.00");
		
		BigDecimal bdSvQ1 = BigDecimal.ZERO, 
				bdSvQ2 = BigDecimal.ZERO, 
				bdSvQ3 = BigDecimal.ZERO, 
				bdSvQ4 = BigDecimal.ZERO;

		Arrays.stream(sTemp).forEach(a -> Arrays.fill(a, null));

		try {

			Arrays.stream(tmpArray).forEach(a -> Arrays.fill(a, null));
			String sTblName = dataTbl.replace("_", LoadData.getStrAktGJ());
			String sSQLStatement = "SELECT * FROM " + sTblName + " ORDER BY [datum]";

			tmpArray = sqlReadArray(sConn, sSQLStatement);
			JFoverview.setArrSvTax(tmpArray);

			if(tmpArray[0][0] != null) {
				tmpAnz = Integer.parseInt(tmpArray[0][0]);
				JFoverview.setAnzSvTax(tmpAnz);
			}else {
				tmpAnz = 0;
				JFoverview.setAnzSvTax(tmpAnz);
			}

			if(tmpAnz == 0) {
				return sTemp;
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {
				switch(Integer.parseInt(tmpArray[x][9])) {
				case 0:
					JFoverview.setbPayedSvTax(x-1, false);
					break;
				case 1:
					JFoverview.setbPayedSvTax(x-1, true);
					break;
				}
			}

			for(int x = 1; (x - 1) < tmpAnz; x++) {

				LocalDate datum1 = LocalDate.parse(tmpArray[x][2], inputFormat);
				String stmpA = datum1.format(formatter);
				sTemp[x-1][0] = stmpA; // Spalte 1 - Datum

				sTemp[x-1][1] = tmpArray[x][3]; // Spalte 2 - Empfänger
				sTemp[x-1][2] = tmpArray[x][4]; // Spalte 3 - Bezeichnung

				BigDecimal bdtmpN1 = new BigDecimal(tmpArray[x][5]).setScale(2, RoundingMode.HALF_UP);
				String stmpN1 = df.format(bdtmpN1);
				sTemp[x-1][3] = stmpN1 + "  EUR"; // Spalte 4 - Zahllast

				LocalDate datum2 = LocalDate.parse(tmpArray[x][6], inputFormat);
				String stmpB = datum2.format(formatter);
				sTemp[x-1][4] = stmpB; // Spalte 5 - Fälligkeit

				sTemp[x-1][5] = tmpArray[x][7]; // Spalte 6 - Dateiname
				
				if(tmpArray[x][3].contains("Sozialversicherung")) {
					if(tmpArray[x][4].contains("Q1")){
						BigDecimal tmpQ1 = new BigDecimal(tmpArray[x][5]).setScale(2, RoundingMode.HALF_UP);
						bdSvQ1 = bdSvQ1.add(tmpQ1);
					}
					if(tmpArray[x][4].contains("Q2")){
						BigDecimal tmpQ2 = new BigDecimal(tmpArray[x][5]).setScale(2, RoundingMode.HALF_UP);
						bdSvQ2 = bdSvQ2.add(tmpQ2);
					}
					if(tmpArray[x][4].contains("Q3")){
						BigDecimal tmpQ3 = new BigDecimal(tmpArray[x][5]).setScale(2, RoundingMode.HALF_UP);
						bdSvQ3 = bdSvQ3.add(tmpQ3);
					}
					if(tmpArray[x][4].contains("Q4")){
						BigDecimal tmpQ4 = new BigDecimal(tmpArray[x][5]).setScale(2, RoundingMode.HALF_UP);
						bdSvQ4 = bdSvQ4.add(tmpQ4);
					}
					if(panelP109a != null) {
						CalcTaxData.getSVData(panelP109a, bdSvQ1, bdSvQ2, bdSvQ3, bdSvQ4);
					}
				}
				

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
		LoadSvTax.sConn = sConn;
	}
	
	public static String getDatatbl() {
		return dataTbl;
	}

}
