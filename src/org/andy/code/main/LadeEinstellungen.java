package org.andy.code.main;

import static org.andy.toolbox.misc.Tools.loadSettingsEx;
import static org.andy.toolbox.misc.Tools.saveSettingsApp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Year;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LadeEinstellungen {

	private static final Logger logger = LogManager.getLogger(LadeEinstellungen.class);

	private static final String FILE_LICENSE = System.getProperty("user.dir") + "\\license.lic";
	private static Properties prpDBSettings = new Properties();
	private static Properties prpAppSettings = new Properties();

	private static String strAktUser;
	private static String strAktGJ;

	private static String tplOffer;
	private static String tplConfirmation;
	private static String tplBill;
	private static String tplReminder;
	private static String tplMahnung;
	private static String tplP109a;
	private static String workPath;
	private static String backupPath;
	private static String sMasterData;
	private static String sProductiveData;

	private static String strDBComputer = "";
	private static String strDBPort = "";
	private static String strDBNameSource = "";
	private static String strDBNameDest = "";
	private static String strDBencrypted = "";
	private static String strDBServerCert = "";

	private static String strQRschema = "";

	private static boolean bFinished = false;

	// ###################################################################################################################################################
	// public teil
	// ###################################################################################################################################################

	public static void LoadProgSettings() {
		try {
			LoadSettings();
		} catch (IOException e) {
			logger.error("Error reading program settings", e);
		}
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static void LoadSettings() throws FileNotFoundException, IOException {

		// ------------------------------------------------------------------------------
		// App-Einstellungen laden
		// ------------------------------------------------------------------------------
		try {
			prpAppSettings = loadSettingsEx(new File(System.getProperty("user.dir") + "\\app.properties")); // App-Einstellungen laden
			strAktGJ = prpAppSettings.getProperty("year");
			strQRschema = prpAppSettings.getProperty("qrschema");
			tplOffer = prpAppSettings.getProperty("templateoffer");
			tplConfirmation = prpAppSettings.getProperty("templateconfirmation");
			tplBill = prpAppSettings.getProperty("templatebill");
			tplReminder = prpAppSettings.getProperty("templatereminder");
			tplMahnung = prpAppSettings.getProperty("templatemahnung");
			tplP109a = prpAppSettings.getProperty("templatep109a");
			workPath = prpAppSettings.getProperty("work");
			backupPath = prpAppSettings.getProperty("backup");
		} catch (FileNotFoundException e) {
			logger.error("Error loading app.properties", e);
			JOptionPane.showMessageDialog(null, "Die Datei app.properties wurde nicht gefunden, starte mit Standardwerten.", "Fehler", JOptionPane.INFORMATION_MESSAGE);
			strAktGJ = askFinacialYear();
			while (!bFinished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					logger.error("Error in sleep thread", e1);
				}
			}
			bFinished = false;
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
			strDBComputer = prpDBSettings.getProperty("computer");
			strDBPort = prpDBSettings.getProperty("port");
			strDBNameSource = prpDBSettings.getProperty("names");
			strDBNameDest = prpDBSettings.getProperty("named");
			strDBencrypted = prpDBSettings.getProperty("encrypt");
			strDBServerCert = prpDBSettings.getProperty("cert");
		} catch (FileNotFoundException e) {
			logger.error("Error loading db.properties", e);
		} catch (IOException e) {
			logger.error("Error reading db.properties", e);
			JOptionPane.showMessageDialog(null, "Die Datei db.properties konnte nicht gelesen werden, Anwendung wird beendet!", "Fehler", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		// ------------------------------------------------------------------------------
		// Datenbank Connection strings für Hibernate
		// ------------------------------------------------------------------------------
		sMasterData = "jdbc:sqlserver://" + LadeEinstellungen.strDBComputer + ":" + LadeEinstellungen.strDBPort + ";databaseName="
				+ LadeEinstellungen.strDBNameSource + ";encrypt=" + LadeEinstellungen.strDBencrypted + ";trustServerCertificate=" + LadeEinstellungen.strDBServerCert;
		sProductiveData = "jdbc:sqlserver://" + LadeEinstellungen.strDBComputer + ":" + LadeEinstellungen.strDBPort + ";databaseName="
				+ LadeEinstellungen.strDBNameDest + ";encrypt=" + LadeEinstellungen.strDBencrypted + ";trustServerCertificate=" + LadeEinstellungen.strDBServerCert;
	}

	// ###################################################################################################################################################

	private static String askFinacialYear() {
		JOptionPane.showMessageDialog(null, "Kein Geschäftsjahr festgelegt, es wird das aktuelle Jahr verwendet.", "Hinweis", JOptionPane.WARNING_MESSAGE);
		int aktuellesJahr = Year.now().getValue();
		String year = String.valueOf(aktuellesJahr);
		try {
			strAktGJ = year;
			prpAppSettings.setProperty("year", strAktGJ);
			saveSettingsApp(LadeEinstellungen.getPrpAppSettings());
			return year;
		} catch (IOException e1) {
			logger.error("error writing financial year " + e1);
		}
		return null;
	}

	// ###################################################################################################################################################
	// Getter und Setter
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

	public static String getStrDBPort() {
		return strDBPort;
	}

	public static String getStrDBServerCert() {
		return strDBServerCert;
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
	
	public static String getTplMahnung() {
		return tplMahnung;
	}
	
	public static String getTplP109a() {
		return tplP109a;
	}

	public static String getWorkPath() {
		return workPath;
	}

	public static void setBackupPath(String backupPath) {
		LadeEinstellungen.backupPath = backupPath;
	}

	public static final void setPrpAppSettings(String sKey, String sEntry) {
		LadeEinstellungen.prpAppSettings.setProperty(sKey, sEntry);
		try {
			saveSettingsApp(LadeEinstellungen.getPrpAppSettings());
		} catch (IOException e) {
			logger.error("error writing settings to file " + e);
		}
	}

	public static void setPrpDBSettings(String sKey, String sEntry) {
		LadeEinstellungen.prpDBSettings.setProperty(sKey, sEntry);
	}

	public static void setStrAktGJ(String strAktGJ) {
		LadeEinstellungen.strAktGJ = strAktGJ;
	}

	public static void setStrAktUser(String strAktUser) {
		LadeEinstellungen.strAktUser = strAktUser;
	}

	public static void setStrDBComputer(String strDBComputer) {
		LadeEinstellungen.strDBComputer = strDBComputer;
	}

	public static void setStrDBencrypted(String strDBencrypted) {
		LadeEinstellungen.strDBencrypted = strDBencrypted;
	}

	public static void setStrDBNameDest(String strDBNameDest) {
		LadeEinstellungen.strDBNameDest = strDBNameDest;
	}

	public static void setStrDBNameSource(String strDBNameSource) {
		LadeEinstellungen.strDBNameSource = strDBNameSource;
	}

	public static void setStrDBPort(String strDBPort) {
		LadeEinstellungen.strDBPort = strDBPort;
	}

	public static void setStrDBServerCert(String strDBServerCert) {
		LadeEinstellungen.strDBServerCert = strDBServerCert;
	}

	public static void setStrQRschema(String strQRschema) {
		LadeEinstellungen.strQRschema = strQRschema;
	}

	public static void setTplBill(String tplBill) {
		LadeEinstellungen.tplBill = tplBill;
	}

	public static void setTplConfirmation(String tplConfirmation) {
		LadeEinstellungen.tplConfirmation = tplConfirmation;
	}

	public static void setTplOffer(String tplOffer) {
		LadeEinstellungen.tplOffer = tplOffer;
	}

	public static void setTplReminder(String tplReminder) {
		LadeEinstellungen.tplReminder = tplReminder;
	}
	
	public static void setTplMahnung(String tplMahnung) {
		LadeEinstellungen.tplMahnung = tplMahnung;
	}
	
	public static void setTplP109a(String tplP109a) {
		LadeEinstellungen.tplP109a = tplP109a;
	}

	public static void setWorkPath(String workPath) {
		LadeEinstellungen.workPath = workPath;
	}

	public static void setbFinished(boolean bFinished) {
		LadeEinstellungen.bFinished = bFinished;
	}

	public static String getsMasterData() {
		return sMasterData;
	}

	public static String getsProductiveData() {
		return sProductiveData;
	}

}
