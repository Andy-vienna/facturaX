package org.andy.code.sql;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

import org.andy.code.main.LoadData;
import org.andy.gui.bill.out.JFstatusRa;
import org.andy.gui.offer.JFstatusA;

public class SQLproductiveData {

	private static final String TBL_OFFER = "tbl_an";
	private static final String TBL_BILL_OUT = "tbl_reOUT";

	private static String[] arrWriteR = new String[51];
	private static String[] arrWriteA = new String[47];

	private static String sConn;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void updateAnToDB(String sAn, String sDate, String sRef, int numPos, String[] sPos, BigDecimal[] bdAnz, BigDecimal[] bdEinzel, String sNetto) throws ClassNotFoundException, SQLException{
		updateAn(sAn, sDate, sRef, numPos, sPos, bdAnz, bdEinzel, sNetto);
	}

	public static void updateReToDB(String sRe, String sDate, String sLZ, String sRef, int numPos, String[] sPos, BigDecimal[] bdAnz, BigDecimal[] bdEinzel, String sNetto, String sUst, String sBrutto) throws ClassNotFoundException, SQLException {
		updateRe(sRe, sDate, sLZ, sRef, numPos, sPos, bdAnz, bdEinzel, sNetto, sUst, sBrutto);
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static void updateAn(String sAn, String sDate, String sRef, int numPos, String[] sPos, BigDecimal[] bdAnz, BigDecimal[] bdEinzel, String sNetto) throws ClassNotFoundException, SQLException {
		Arrays.fill(arrWriteA, "");

		arrWriteA[0] = sAn; // Angebotsnummer
		arrWriteA[1] = "1"; // active
		arrWriteA[2] = "0"; // printed
		arrWriteA[3] = "0"; // payed
		arrWriteA[4] = JFstatusA.getWritten();
		arrWriteA[5] = sDate;
		arrWriteA[6] = sRef;

		arrWriteA[8] = sNetto.replace(",", ".");
		arrWriteA[9] = String.valueOf(numPos);

		int m = 10;
		int n = 1;
		while(n < (numPos + 1)) {
			arrWriteA[m] = sPos[n];
			arrWriteA[m + 1] = bdAnz[n].toPlainString();
			arrWriteA[m + 2] = bdEinzel[n].toPlainString();
			n++;
			m = m + 3;
		}

		String tblName = TBL_OFFER.replace("_", LoadData.getStrAktGJ());
		String sStatement = "UPDATE " + tblName +
				" SET [activeState] = '" + arrWriteA[1] +
				"', [printState] = '" + arrWriteA[2] + "', [orderState] = '" + arrWriteA[3] + "', [Status] = '" + arrWriteA[4] +
				"', [Datum] = '" + arrWriteA[5] + "', [Ref] = '" + arrWriteA[6] +
				"', [Netto] = '" + arrWriteA[8] +
				"', [AnzPos] = '" + arrWriteA[9] +
				"', [IdArt01] = '" + arrWriteA[10] + "', [Menge01] = '" + arrWriteA[11] + "', [EPreis01] = '" + arrWriteA[12] +
				"', [IdArt02] = '" + arrWriteA[13] + "', [Menge02] = '" + arrWriteA[14] + "', [EPreis02] = '" + arrWriteA[15] +
				"', [IdArt03] = '" + arrWriteA[16] + "', [Menge03] = '" + arrWriteA[17] + "', [EPreis03] = '" + arrWriteA[18] +
				"', [IdArt04] = '" + arrWriteA[19] + "', [Menge04] = '" + arrWriteA[20] + "', [EPreis04] = '" + arrWriteA[21] +
				"', [IdArt05] = '" + arrWriteA[22] + "', [Menge05] = '" + arrWriteA[23] + "', [EPreis05] = '" + arrWriteA[24] +
				"', [IdArt06] = '" + arrWriteA[25] + "', [Menge06] = '" + arrWriteA[26] + "', [EPreis06] = '" + arrWriteA[37] +
				"', [IdArt07] = '" + arrWriteA[28] + "', [Menge07] = '" + arrWriteA[29] + "', [EPreis07] = '" + arrWriteA[30] +
				"', [IdArt08] = '" + arrWriteA[31] + "', [Menge08] = '" + arrWriteA[32] + "', [EPreis08] = '" + arrWriteA[33] +
				"', [IdArt09] = '" + arrWriteA[34] + "', [Menge09] = '" + arrWriteA[35] + "', [EPreis09] = '" + arrWriteA[36] +
				"', [IdArt10] = '" + arrWriteA[37] + "', [Menge10] = '" + arrWriteA[38] + "', [EPreis10] = '" + arrWriteA[39] +
				"', [IdArt11] = '" + arrWriteA[40] + "', [Menge11] = '" + arrWriteA[41] + "', [EPreis11] = '" + arrWriteA[42] +
				"', [IdArt12] = '" + arrWriteA[43] + "', [Menge12] = '" + arrWriteA[44] + "', [EPreis12] = '" + arrWriteA[45] +
				"' WHERE [IdNummer] = '" + arrWriteA[0] + "'";

		main.java.toolbox.sql.Update.sqlUpdate(sConn, sStatement);

	}

	private static void updateRe(String sRe, String sDate, String sLZ, String sRef, int numPos, String[] sPos, BigDecimal[] bdAnz, BigDecimal[] bdEinzel, String sNetto, String sUst, String sBrutto) throws ClassNotFoundException, SQLException {
		Arrays.fill(arrWriteR, "");

		arrWriteR[0] = sRe; // Rechnungsnummer
		arrWriteR[1] = "1"; // active
		arrWriteR[2] = "0"; // printed
		arrWriteR[3] = "0"; // payed
		arrWriteR[4] = JFstatusRa.getWritten();
		arrWriteR[5] = sDate;
		arrWriteR[6] = sLZ;
		arrWriteR[7] = sRef;

		arrWriteR[11] = sNetto.replace(",", ".");
		arrWriteR[12] = sUst.replace(",", ".");
		arrWriteR[13] = sBrutto.replace(",", ".");
		arrWriteR[14] = String.valueOf(numPos);

		int m = 15;
		int n = 1;
		while(n < (numPos + 1)) {
			arrWriteR[m] = sPos[n];
			arrWriteR[m + 1] = bdAnz[n].toPlainString();
			arrWriteR[m + 2] = bdEinzel[n].toPlainString();
			n++;
			m = m + 3;
		}

		String tblName = TBL_BILL_OUT.replace("_", LoadData.getStrAktGJ());
		String sStatement = "UPDATE " + tblName +
				" SET [activeState] = '" + arrWriteR[1] +
				"', [printState] = '" + arrWriteR[2] + "', [moneyState] = '" + arrWriteR[3] + "', [Status] = '" + arrWriteR[4] +
				"', [Datum] = '" + arrWriteR[5] + "', [LZeitr] = '" + arrWriteR[6] + "', [Ref] = '" + arrWriteR[7] +
				"', [Netto] = '" + arrWriteR[11] + "', [USt] = '" + arrWriteR[12] + "', [Brutto] = '" + arrWriteR[13] +
				"', [AnzPos] = '" + arrWriteR[14] +
				"', [IdArt01] = '" + arrWriteR[15] + "', [Menge01] = '" + arrWriteR[16] + "', [EPreis01] = '" + arrWriteR[17] +
				"', [IdArt02] = '" + arrWriteR[18] + "', [Menge02] = '" + arrWriteR[19] + "', [EPreis02] = '" + arrWriteR[20] +
				"', [IdArt03] = '" + arrWriteR[21] + "', [Menge03] = '" + arrWriteR[22] + "', [EPreis03] = '" + arrWriteR[23] +
				"', [IdArt04] = '" + arrWriteR[24] + "', [Menge04] = '" + arrWriteR[25] + "', [EPreis04] = '" + arrWriteR[26] +
				"', [IdArt05] = '" + arrWriteR[27] + "', [Menge05] = '" + arrWriteR[28] + "', [EPreis05] = '" + arrWriteR[29] +
				"', [IdArt06] = '" + arrWriteR[30] + "', [Menge06] = '" + arrWriteR[31] + "', [EPreis06] = '" + arrWriteR[32] +
				"', [IdArt07] = '" + arrWriteR[33] + "', [Menge07] = '" + arrWriteR[34] + "', [EPreis07] = '" + arrWriteR[35] +
				"', [IdArt08] = '" + arrWriteR[36] + "', [Menge08] = '" + arrWriteR[37] + "', [EPreis08] = '" + arrWriteR[38] +
				"', [IdArt09] = '" + arrWriteR[39] + "', [Menge09] = '" + arrWriteR[40] + "', [EPreis09] = '" + arrWriteR[41] +
				"', [IdArt10] = '" + arrWriteR[42] + "', [Menge10] = '" + arrWriteR[43] + "', [EPreis10] = '" + arrWriteR[44] +
				"', [IdArt11] = '" + arrWriteR[45] + "', [Menge11] = '" + arrWriteR[46] + "', [EPreis11] = '" + arrWriteR[47] +
				"', [IdArt12] = '" + arrWriteR[48] + "', [Menge12] = '" + arrWriteR[49] + "', [EPreis12] = '" + arrWriteR[50] +
				"' WHERE [IdNummer] = '" + arrWriteR[0] + "'";

		main.java.toolbox.sql.Update.sqlUpdate(sConn, sStatement);

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		SQLproductiveData.sConn = sConn;
	}

}
