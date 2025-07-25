package org.andy.code.sql;

import static org.andy.toolbox.sql.Read.sqlReadArrayList;
import static org.andy.toolbox.misc.Tools.cutBack;
import static org.andy.toolbox.misc.Tools.cutFront;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import org.andy.code.main.LoadData;
import org.andy.code.main.overview.WriteRecState;

public class SQLmasterData {

	private static final String TBL_OWN = "tblOwner";
	private static final String TBL_AN = "tblAN";
	private static final String TBL_RE = "tblRE";
	private static final String TBL_ART = "tblArtikel";
	private static final String TBL_BANK = "tblBank";
	private static final String TBL_CUST = "tblKunde";
	private static final String TBL_TEXT = "tblText";

	public static final String SORT_BY_ID = "Id";

	private static String sConn;
	private static String strAktAnNr;
	private static String strAktReNr;

	private static String[] sArrOwner = new String[11];
	private static ArrayList<ArrayList<String>> arrListArtikel = new ArrayList<>();
	private static ArrayList<ArrayList<String>> arrListBank = new ArrayList<>();
	private static ArrayList<ArrayList<String>> arrListKunde = new ArrayList<>();
	private static ArrayList<ArrayList<String>> arrListText = new ArrayList<>();
	private static ArrayList<ArrayList<String>> arrListAnNr = new ArrayList<>();
	private static ArrayList<ArrayList<String>> arrListReNr = new ArrayList<>();
	private static ArrayList<String> Owner = new ArrayList<>();

	private static String[][] sArrArtikel;
	private static String[][] sArrBank;
	private static String[][] sArrKunde;
	private static String[][] sArrText;

	private static int AnzArtikel;
	private static int AnzBank;
	private static int AnzKunde;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadBaseData() throws ClassNotFoundException, SQLException, ParseException {
		loadDBData();
	}

	public static void loadNummernkreis() throws ClassNotFoundException, SQLException, IOException {
		loadDBNummern();
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static void loadDBData() throws SQLException, ParseException, ClassNotFoundException {
		ArrayList<ArrayList<String>> tmpData = new ArrayList<>();
		tmpData = sqlReadArrayList(sConn, TBL_OWN, "Name", "*");

		if (tmpData.size() < 1) {
			throw new SQLException("No data found in " + TBL_OWN);
		}

		Owner = tmpData.get(0);
		for(int n = 1; n < 11; n++) {
			sArrOwner[n] = Owner.get(n-1).toString();
		}
		arrListArtikel = sqlReadArrayList(sConn, TBL_ART, "*", "*");
		arrListBank = sqlReadArrayList(sConn, TBL_BANK, "*", "*");
		arrListKunde = sqlReadArrayList(sConn, TBL_CUST, "*", "*");
		arrListText = sqlReadArrayList(sConn, TBL_TEXT, "*", "*");

		AnzArtikel = arrListArtikel.size();
		AnzBank = arrListBank.size();
		AnzKunde = arrListKunde.size();

		if(AnzArtikel >0) {
			sArrArtikel = copyList(arrListArtikel);
		}
		if(AnzBank >0) {
			sArrBank = copyList(arrListBank);
		}
		if(AnzKunde >0) {
			sArrKunde = copyList(arrListKunde);
		}
		if(arrListText.size() > 0) {
			sArrText = copyList(arrListText);
		}
		
		WriteRecState.setKunde(arrListKunde); // Kundendaten übergeben für Zusammenfassende Meldung

	}

	private static void loadDBNummern() throws SQLException, IOException, ClassNotFoundException {

		arrListAnNr = sqlReadArrayList(sConn, TBL_AN, SORT_BY_ID, LoadData.getStrAktGJ());
		arrListReNr = sqlReadArrayList(sConn, TBL_RE, SORT_BY_ID, LoadData.getStrAktGJ());

		if(arrListAnNr.size() >= 1) {
			String sCutTextAn = "";
			int iIncAn = 0;
			sCutTextAn = cutFront(arrListAnNr.get(arrListAnNr.size()-1).toString(), "-", 2);
			String sAnNr = cutBack(sCutTextAn, "]", 1);
			iIncAn = Integer.parseInt(sAnNr) + 1;
			String sAn = cutFront(arrListAnNr.get(arrListAnNr.size()-1).toString(), "[", 1);
			strAktAnNr = cutBack(sAn, "-", 1) + "-" + String.format("%04d", iIncAn);
		}else {
			strAktAnNr = "AN-" + LoadData.getStrAktGJ() + "-0001";
		}

		if(arrListReNr.size() >= 1) {
			String sCutTextRe = "";
			int iIncRe = 0;
			sCutTextRe = cutFront(arrListReNr.get(arrListReNr.size()-1).toString(), "-", 2);
			String sReNr = cutBack(sCutTextRe, "]", 1);
			iIncRe = Integer.parseInt(sReNr) + 1;
			String sRe = cutFront(arrListReNr.get(arrListReNr.size()-1).toString(), "[", 1);
			strAktReNr = cutBack(sRe, "-", 1) + "-" + String.format("%04d", iIncRe);
		}else {
			strAktReNr = "RE-" + LoadData.getStrAktGJ() + "-0001";
		}

	}

	private static String[][] copyList(ArrayList<ArrayList<String>> list){

		int rows = list.size() + 1;
		int columns = list.get(0).size() + 1;
		String[][] array = new String[rows][columns];

		array[0][0] = String.valueOf(list.size());
		array[0][1] = String.valueOf(columns - 1);

		for (int i = 0; i < list.size(); i++) {
			ArrayList<String> innerList = list.get(i);
			for(int n = 0; n < innerList.size(); n++) {
				array[i + 1][n + 1] = innerList.get(n);
			}
		}

		return array;

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		SQLmasterData.sConn = sConn;
	}

	public static String getStrAktAnNr() {
		return strAktAnNr;
	}

	public static String getStrAktReNr() {
		return strAktReNr;
	}

	public static int getAnzArtikel() {
		return AnzArtikel;
	}

	public static int getAnzBank() {
		return AnzBank;
	}

	public static int getAnzKunde() {
		return AnzKunde;
	}

	public static String[] getsArrOwner() {
		return sArrOwner;
	}

	public static String[][] getsArrArtikel() {
		return sArrArtikel;
	}

	public static String[][] getsArrBank() {
		return sArrBank;
	}

	public static String[][] getsArrKunde() {
		return sArrKunde;
	}

	public static String[][] getsArrText() {
		return sArrText;
	}

	public static ArrayList<ArrayList<String>> getArrListArtikel() {
		return arrListArtikel;
	}

	public static ArrayList<ArrayList<String>> getArrListBank() {
		return arrListBank;
	}

	public static ArrayList<ArrayList<String>> getArrListKunde() {
		return arrListKunde;
	}

	public static ArrayList<ArrayList<String>> getArrListText() {
		return arrListText;
	}

	public static ArrayList<String> getOwner() {
		return Owner;
	}

}
