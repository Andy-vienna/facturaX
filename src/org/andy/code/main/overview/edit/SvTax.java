package org.andy.code.main.overview.edit;

import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.choosePath;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
import static org.andy.toolbox.sql.Insert.sqlInsert;
import static org.andy.toolbox.sql.Read.sqlExtractFile;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import org.andy.code.main.LoadData;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SvTax {
	
	private static final Logger logger = LogManager.getLogger(SvTax.class);
	
	private static final String dataTbl = "tbl_svtax";
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

	public static String writeData(String[] arrTmp, int id, boolean file) {
		String sSQLStatement;
		
		for(int x = 0; x < 19; x++) {
			if(arrTmp == null) {
				JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Beleg erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
				return NOK;
			}
		}
		
		String tblName = dataTbl.replace("_", LoadData.getStrAktGJ());
		
		try {
			if (id == 0) {
				String sId = String.valueOf(JFoverview.getAnzYearST() + 1);
				
				sSQLStatement = "INSERT INTO " + tblName + " ([Id],[datum],[organisation],[bezeichnung],[zahllast],[zahlungsziel],[dateiname],[datei],[status]) VALUES ('"
						+ sId + "','" + arrTmp[0]	+ "','" + arrTmp[1] + "','" + arrTmp[2] + "'," + arrTmp[3] + ",'" + arrTmp[4] + "','" + arrTmp[5] + "',"
						+ "(SELECT * FROM OPENROWSET(BULK '" + arrTmp[6] + "', SINGLE_BLOB) AS DATA),0)";
				
				sqlInsert(sConn, sSQLStatement);
			
			} else {
				
				if (file) {
					sSQLStatement = "UPDATE " + tblName + " SET [dateiname] = '" + arrTmp[5] + "',[datei] = (SELECT * FROM OPENROWSET(BULK '"
						+ arrTmp[6] + "', SINGLE_BLOB) AS DATA) ,[status] = 1 WHERE [Id] = '" + id + "'";
				
				} else {
					sSQLStatement = "UPDATE " + tblName + " SET [status] = 1 WHERE [Id] = '" + id + "'";
				}
				
				sqlUpdate(sConn, sSQLStatement);
				
			}
					
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error writing new expenses - " + e);
		}
		
		return OK;
	}
	
	//###################################################################################################################################################
	
	public static void actionMouseClick(MouseEvent e, String sId) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern

			String outputPath;
			try {
				outputPath = choosePath(LoadData.getWorkPath());

				if (outputPath.equals(getNotSelected())) {
					return;
				}

				String tblName = dataTbl.replace("_", LoadData.getStrAktGJ());
				String sSQLStatement = "SELECT [dateiname], [datei] FROM " + tblName + " WHERE [Id] = '" + sId + "'";

				sqlExtractFile(sConn, sSQLStatement, outputPath, "dateiname", "datei");
			} catch (InterruptedException | ClassNotFoundException | SQLException | IOException e1) {
				Thread.currentThread().interrupt();
				logger.error("error while extracting file from database - " + e1);
			}

		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		SvTax.sConn = sConn;
	}

	public static String getFilePath() {
		return FilePath;
	}

	

}
