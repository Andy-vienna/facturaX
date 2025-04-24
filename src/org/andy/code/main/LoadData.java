package org.andy.code.main;

import static main.java.toolbox.misc.Tools.loadSettingsEx;
import static main.java.toolbox.misc.Tools.saveSettingsApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Year;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.dataExport.ExcelBill;
import org.andy.code.dataExport.ExcelConfirmation;
import org.andy.code.dataExport.ExcelMahnung;
import org.andy.code.dataExport.ExcelOffer;
import org.andy.code.dataExport.ExcelReminder;
import org.andy.code.sql.SQLmasterData;
import org.andy.code.sql.SQLproductiveData;
import org.andy.gui.bill.in.JFeditRe;
import org.andy.gui.bill.in.JFnewRe;
import org.andy.gui.bill.out.JFnewRa;
import org.andy.gui.bill.out.JFstatusRa;
import org.andy.gui.expenses.JFeditEx;
import org.andy.gui.expenses.JFnewEx;
import org.andy.gui.file.JFfileView;
import org.andy.gui.main.JFmainLogIn;
import org.andy.gui.main.JFoverview;
import org.andy.gui.offer.JFconfirmA;
import org.andy.gui.offer.JFnewA;
import org.andy.gui.offer.JFstatusA;
import org.andy.gui.reminder.JFnewReminder;
import org.andy.gui.settings.JFartikel;
import org.andy.gui.settings.JFbank;
import org.andy.gui.settings.JFdbSettings;
import org.andy.gui.settings.JFkunde;
import org.andy.gui.settings.JFowner;
import org.andy.gui.settings.JFpathMgmt;
import org.andy.gui.settings.JFsepaQR;
import org.andy.gui.settings.JFuserMgmt;
import org.andy.gui.svtax.JFeditSvTax;
import org.andy.gui.svtax.JFnewSvTax;

public class LoadData {

	private static final Logger logger = LogManager.getLogger(LoadData.class);

	private static final String FILE_LICENSE = System.getProperty("user.dir") + "\\license.lic";
	private static Properties prpDBSettings = new Properties();
	private static Properties prpAppSettings = new Properties();

	private static String strAktUser;
	private static String strAktGJ;

	private static String sSizeX = "1200";
	private static String sSizeY = "800";
	private static String tplOffer;
	private static String tplConfirmation;
	private static String tplBill;
	private static String tplReminder;
	private static String tplMahnung;
	private static String workPath;
	private static String backupPath;

	private static String strDBservice = "";
	private static String strDBComputer = "";
	private static String strDBPort = "";
	private static String strDBUser = "";
	private static String strDBNameSource = "";
	private static String strDBNameDest = "";
	private static String strDBPass = "";
	private static String strDBencrypted = "";
	private static String strDBServerCert = "";

	private static String strQRschema = "";

	private static boolean bFinished = false;

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static void LoadProgSettings() {
		try {
			LoadSettings();
		} catch (IOException e) {
			logger.error("Error reading program settings", e);
		}
	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	private static void LoadSettings() throws FileNotFoundException, IOException {

		// ------------------------------------------------------------------------------
		// App-Einstellungen laden
		// ------------------------------------------------------------------------------
		try {
			prpAppSettings = loadSettingsEx(new File(System.getProperty("user.dir") + "\\app.properties")); // App-Einstellungen laden
			sSizeX = prpAppSettings.getProperty("screenx");
			sSizeY = prpAppSettings.getProperty("screeny");
			strAktGJ = prpAppSettings.getProperty("year");
			strQRschema = prpAppSettings.getProperty("qrschema");
			tplOffer = prpAppSettings.getProperty("templateoffer");
			tplConfirmation = prpAppSettings.getProperty("templateconfirmation");
			tplBill = prpAppSettings.getProperty("templatebill");
			tplReminder = prpAppSettings.getProperty("templatereminder");
			tplMahnung = prpAppSettings.getProperty("templatemahnung");
			workPath = prpAppSettings.getProperty("work");
			backupPath = prpAppSettings.getProperty("backup");
		} catch (FileNotFoundException e) {
			logger.error("Error loading app.properties", e);
			JOptionPane.showMessageDialog(null, "Die Datei app.properties wurde nicht gefunden, starte mit Standardwerten.", "Fehler", JOptionPane.INFORMATION_MESSAGE);
			strAktGJ = askFinacialYear();
			JFsepaQR.loadGUI(true);
			while (!bFinished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					logger.error("Error in sleep thread", e1);
				}
			}
			bFinished = false;
			JFpathMgmt.loadGUI();
			while (!bFinished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					logger.error("Error in sleep thread", e1);
				}
			}
			bFinished = false;
		} catch (IOException e) {
			logger.error("Error reading app.properties", e);
			JOptionPane.showMessageDialog(null, "Die Datei app.properties konnte nicht gelesen werden, Anwendung wird beendet!", "Fehler", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		// ------------------------------------------------------------------------------
		// DB-Einstellungen laden
		// ------------------------------------------------------------------------------
		try {
			prpDBSettings = loadSettingsEx(new File(System.getProperty("user.dir") + "\\db.properties")); // DB-Einstellungen laden
			strDBservice = prpDBSettings.getProperty("service");
			strDBComputer = prpDBSettings.getProperty("computer");
			strDBPort = prpDBSettings.getProperty("port");
			strDBNameSource = prpDBSettings.getProperty("names");
			strDBNameDest = prpDBSettings.getProperty("named");
			strDBUser = prpDBSettings.getProperty("user");
			String tmpPass = prpDBSettings.getProperty("pass");
			strDBPass = main.java.toolbox.crypto.Caesar.decryptString(tmpPass, 13);
			strDBencrypted = prpDBSettings.getProperty("encrypt");
			strDBServerCert = prpDBSettings.getProperty("cert");
		} catch (FileNotFoundException e) {
			logger.error("Error loading db.properties", e);
			JOptionPane.showMessageDialog(null, "Die Datei db.properties wurde nicht gefunden, bitte Verbindung festlegen.", "Fehler", JOptionPane.INFORMATION_MESSAGE);
			JFdbSettings.loadGUI(true);
			while (!bFinished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					logger.error("Error in sleep thread", e1);
				}
			}
			bFinished = false;
		} catch (IOException e) {
			logger.error("Error reading db.properties", e);
			JOptionPane.showMessageDialog(null, "Die Datei db.properties konnte nicht gelesen werden, Anwendung wird beendet!", "Fehler", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		// ------------------------------------------------------------------------------
		// Datenbank-String zusammenbauen
		// ------------------------------------------------------------------------------

		String tmpConnA = "jdbc:sqlserver://" + LoadData.strDBComputer + ":" + LoadData.strDBPort + ";database="
				+ LoadData.strDBNameSource + ";user=" + LoadData.strDBUser + ";password=" + LoadData.strDBPass
				+ ";encrypt=" + LoadData.strDBencrypted + ";trustServerCertificate=" + LoadData.strDBServerCert
				+ ";loginTimeout=30;";

		SQLmasterData.setsConn(tmpConnA);
		JFmainLogIn.setsConn(tmpConnA);
		JFoverview.setsConnMaster(tmpConnA);
		JFnewA.setsConnSource(tmpConnA);
		JFnewRa.setsConnSource(tmpConnA);
		JFuserMgmt.setsConn(tmpConnA);
		JFartikel.setsConn(tmpConnA);
		JFbank.setsConn(tmpConnA);
		JFkunde.setsConn(tmpConnA);
		JFowner.setsConn(tmpConnA);

		String tmpConnB = "jdbc:sqlserver://" + LoadData.strDBComputer + ":" + LoadData.strDBPort + ";database="
				+ LoadData.strDBNameDest + ";user=" + LoadData.strDBUser + ";password=" + LoadData.strDBPass
				+ ";encrypt=" + LoadData.strDBencrypted + ";trustServerCertificate=" + LoadData.strDBServerCert
				+ ";loginTimeout=30;";

		SQLproductiveData.setsConn(tmpConnB);
		JFoverview.setsConn(tmpConnB);
		JFfileView.setsConn(tmpConnB);
		JFnewA.setsConnDest(tmpConnB);
		JFstatusA.setsConn(tmpConnB);
		JFconfirmA.setsConn(tmpConnB);
		JFnewRa.setsConnDest(tmpConnB);
		JFstatusRa.setsConn(tmpConnB);
		JFnewReminder.setsConn(tmpConnB);
		ExcelBill.setsConn(tmpConnB);
		ExcelConfirmation.setsConn(tmpConnB);
		ExcelOffer.setsConn(tmpConnB);
		ExcelReminder.setsConn(tmpConnB);
		ExcelMahnung.setsConn(tmpConnB);
		JFnewRe.setsConn(tmpConnB);
		JFeditRe.setsConn(tmpConnB);
		JFnewEx.setsConn(tmpConnB);
		JFeditEx.setsConn(tmpConnB);
		JFnewSvTax.setsConn(tmpConnB);
		JFeditSvTax.setsConn(tmpConnB);

		StartUp.setSERVICE_NAME("\"SQL Server (" + strDBservice + ")\"");

	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	private static String askFinacialYear() {
		JOptionPane.showMessageDialog(null, "Kein Geschäftsjahr festgelegt, es wird das aktuelle Jahr verwendet.", "Hinweis", JOptionPane.WARNING_MESSAGE);
		int aktuellesJahr = Year.now().getValue();
		String year = String.valueOf(aktuellesJahr);
		try {
			strAktGJ = year;
			prpAppSettings.setProperty("year", strAktGJ);
			prpAppSettings.setProperty("screenx", sSizeX);
			prpAppSettings.setProperty("screeny", sSizeY);
			saveSettingsApp(LoadData.getPrpAppSettings());
			return year;
		} catch (IOException e1) {
			logger.error("error writing financial year " + e1);
		}
		return null;
	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static final String getBackupPath() {
		return backupPath;
	}

	public static String getFileLicense() {
		return FILE_LICENSE;
	}

	public static final Properties getPrpAppSettings() {
		return prpAppSettings;
	}

	public static Properties getPrpDBSettings() {
		return prpDBSettings;
	}

	public static String getsSizeX() {
		return sSizeX;
	}

	public static String getsSizeY() {
		return sSizeY;
	}

	public static String getStrAktGJ() {
		return strAktGJ;
	}

	public static String getStrAktUser() {
		return strAktUser;
	}

	public static final String getStrDBComputer() {
		return strDBComputer;
	}

	public static String getStrDBencrypted() {
		return strDBencrypted;
	}

	public static final String getStrDBNameDest() {
		return strDBNameDest;
	}

	public static final String getStrDBNameSource() {
		return strDBNameSource;
	}

	public static String getStrDBPass() {
		return strDBPass;
	}

	public static String getStrDBPort() {
		return strDBPort;
	}

	public static String getStrDBServerCert() {
		return strDBServerCert;
	}

	public static final String getStrDBservice() {
		return strDBservice;
	}

	public static String getStrDBUser() {
		return strDBUser;
	}

	public static final String getStrQRschema() {
		return strQRschema;
	}

	public static String getTplBill() {
		return tplBill;
	}

	public static String getTplConfirmation() {
		return tplConfirmation;
	}

	public static String getTplOffer() {
		return tplOffer;
	}

	public static String getTplReminder() {
		return tplReminder;
	}

	public static String getWorkPath() {
		return workPath;
	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static void setBackupPath(String backupPath) {
		LoadData.backupPath = backupPath;
	}

	public static final void setPrpAppSettings(String sKey, String sEntry) {
		LoadData.prpAppSettings.setProperty(sKey, sEntry);
	}

	public static void setPrpDBSettings(String sKey, String sEntry) {
		LoadData.prpDBSettings.setProperty(sKey, sEntry);
	}

	public static void setsSizeX(String sSizeX) {
		LoadData.sSizeX = sSizeX;
	}

	public static void setsSizeY(String sSizeY) {
		LoadData.sSizeY = sSizeY;
	}

	public static void setStrAktGJ(String strAktGJ) {
		LoadData.strAktGJ = strAktGJ;
	}

	public static void setStrAktUser(String strAktUser) {
		LoadData.strAktUser = strAktUser;
	}

	public static void setStrDBComputer(String strDBComputer) {
		LoadData.strDBComputer = strDBComputer;
	}

	public static void setStrDBencrypted(String strDBencrypted) {
		LoadData.strDBencrypted = strDBencrypted;
	}

	public static void setStrDBNameDest(String strDBNameDest) {
		LoadData.strDBNameDest = strDBNameDest;
	}

	public static void setStrDBNameSource(String strDBNameSource) {
		LoadData.strDBNameSource = strDBNameSource;
	}

	public static void setStrDBPass(String strDBPass) {
		LoadData.strDBPass = strDBPass;
	}

	public static void setStrDBPort(String strDBPort) {
		LoadData.strDBPort = strDBPort;
	}

	public static void setStrDBServerCert(String strDBServerCert) {
		LoadData.strDBServerCert = strDBServerCert;
	}

	public static void setStrDBservice(String strDBservice) {
		LoadData.strDBservice = strDBservice;
	}

	public static void setStrDBUser(String strDBUser) {
		LoadData.strDBUser = strDBUser;
	}

	public static void setStrQRschema(String strQRschema) {
		LoadData.strQRschema = strQRschema;
	}

	public static void setTplBill(String tplBill) {
		LoadData.tplBill = tplBill;
	}

	public static void setTplConfirmation(String tplConfirmation) {
		LoadData.tplConfirmation = tplConfirmation;
	}

	public static void setTplOffer(String tplOffer) {
		LoadData.tplOffer = tplOffer;
	}

	public static void setTplReminder(String tplReminder) {
		LoadData.tplReminder = tplReminder;
	}

	public static void setWorkPath(String workPath) {
		LoadData.workPath = workPath;
	}

	public static String getTplMahnung() {
		return tplMahnung;
	}

	public static void setTplMahnung(String tplMahnung) {
		LoadData.tplMahnung = tplMahnung;
	}

	public static void setbFinished(boolean bFinished) {
		LoadData.bFinished = bFinished;
	}

}
