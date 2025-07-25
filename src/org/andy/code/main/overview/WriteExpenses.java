package org.andy.code.main.overview;

import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
import static org.andy.toolbox.sql.Insert.sqlInsert;

import java.io.File;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import org.andy.code.main.LoadData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WriteExpenses {
	
	private static final Logger logger = LogManager.getLogger(WriteExpenses.class);
	
	private static final String TBL_EXPENSES = "tbl_expenses";
	private static final String OK = "OK";
	private static final String NOK = "NOK";
	
	private static String sConn;
	private static String FilePath;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String selectFile() {
		String FileNamePath = chooseFile(LoadData.getWorkPath());
		if(FileNamePath == getNotSelected()) {
			return getNotSelected();
		}
		File fn = new File(FileNamePath);
		FilePath = fn.getPath();
		String FileName = fn.getName();
		return FileName;
	}
	
	//###################################################################################################################################################

	public static String writeExpense(String[] arrTmp) {

		String sId = String.valueOf(JFoverview.getAnzExpenses() + 1);

		for(int x = 0; x < 8; x++) {
			if(arrTmp == null) {
				JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Beleg erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
				return NOK;
			}
		}

		String tblName = TBL_EXPENSES.replace("_", LoadData.getStrAktGJ());

		String sSQLStatement = "INSERT INTO " + tblName + " ([Datum],[Art],[netto],[Steuersatz],[steuer],[brutto],[dateiname],[datei],[Id]) VALUES ('"
				+ arrTmp[0] + "','" + arrTmp[1]	+ "','" + arrTmp[2] + "','" + arrTmp[3] + "','" + arrTmp[4] + "','" + arrTmp[5] + "','" + arrTmp[6]
				+ "',(SELECT * FROM OPENROWSET(BULK '" + arrTmp[7] + "', SINGLE_BLOB) AS DATA),'" + sId  + "')";

		try {
			sqlInsert(sConn, sSQLStatement);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error writing new expenses - " + e);
		}
		return OK;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		WriteExpenses.sConn = sConn;
	}

	public static String getFilePath() {
		return FilePath;
	}

	

}
