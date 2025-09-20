package org.andy.code.main;

import static org.andy.toolbox.misc.Tools.loadSettingsEx;
import static org.andy.toolbox.misc.Tools.saveSettingsApp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Einstellungen {

	private static final Logger logger = LogManager.getLogger(Einstellungen.class);

	private static final String FILE_LICENSE = System.getProperty("user.dir") + "\\license.lic";
	private static Properties prpDBSettings = new Properties();
	private static Properties prpAppSettings = new Properties();

	private static String strAktUser;
	private static String strAktGJ;
	private static String htmlBaseText;
	private static String htmlBaseStyle;

	private static String tplOffer;
	private static String tplDescription;
	private static String tplDescriptionBase;
	private static String tplDescriptionStyle;
	private static String tplConfirmation;
	private static String tplBill;
	private static String tplReminder;
	private static String tplMahnung;
	private static String tplBestellung;
	private static String tplLieferschein;
	private static String tplP109a;
	private static String workPath;
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
			tplDescription = prpAppSettings.getProperty("templatedescription");
			tplDescriptionBase = prpAppSettings.getProperty("templatedescriptionbase");
			tplDescriptionStyle = prpAppSettings.getProperty("templatedescriptionstyle");
			tplConfirmation = prpAppSettings.getProperty("templateconfirmation");
			tplBill = prpAppSettings.getProperty("templatebill");
			tplReminder = prpAppSettings.getProperty("templatereminder");
			tplMahnung = prpAppSettings.getProperty("templatemahnung");
			tplBestellung = prpAppSettings.getProperty("templatebestellung");
			tplLieferschein = prpAppSettings.getProperty("templatelieferschein");
			tplP109a = prpAppSettings.getProperty("templatep109a");
			workPath = prpAppSettings.getProperty("work");
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
		sMasterData = "jdbc:sqlserver://" + Einstellungen.strDBComputer + ":" + Einstellungen.strDBPort + ";databaseName="
				+ Einstellungen.strDBNameSource + ";encrypt=" + Einstellungen.strDBencrypted + ";trustServerCertificate=" + Einstellungen.strDBServerCert;
		sProductiveData = "jdbc:sqlserver://" + Einstellungen.strDBComputer + ":" + Einstellungen.strDBPort + ";databaseName="
				+ Einstellungen.strDBNameDest + ";encrypt=" + Einstellungen.strDBencrypted + ";trustServerCertificate=" + Einstellungen.strDBServerCert;
		
		htmlBaseText = htmlBaseText();
		htmlBaseStyle = htmlBaseStyle();
	}

	// ###################################################################################################################################################
	
	private static String htmlBaseText() {
		Path path = Path.of(tplDescriptionBase);
        String content = null;
		try {
			content = Files.readString(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return content;
	}
	
	private static String htmlBaseStyle() {
		Path path = Path.of(tplDescriptionStyle);
        String content = null;
		try {
			content = Files.readString(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return content;
	}
	
	// ###################################################################################################################################################

	private static String askFinacialYear() {
		JOptionPane.showMessageDialog(null, "Kein Geschäftsjahr festgelegt, es wird das aktuelle Jahr verwendet.", "Hinweis", JOptionPane.WARNING_MESSAGE);
		int aktuellesJahr = Year.now().getValue();
		String year = String.valueOf(aktuellesJahr);
		try {
			strAktGJ = year;
			prpAppSettings.setProperty("year", strAktGJ);
			saveSettingsApp(Einstellungen.getPrpAppSettings());
			return year;
		} catch (IOException e1) {
			logger.error("error writing financial year " + e1);
		}
		return null;
	}

	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

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
	
	public static String getTplDescription() {
		return tplDescription;
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

	public static final void setPrpAppSettings(String sKey, String sEntry) {
		Einstellungen.prpAppSettings.setProperty(sKey, sEntry);
		try {
			saveSettingsApp(Einstellungen.getPrpAppSettings());
		} catch (IOException e) {
			logger.error("error writing settings to file " + e);
		}
	}

	public static void setPrpDBSettings(String sKey, String sEntry) {
		Einstellungen.prpDBSettings.setProperty(sKey, sEntry);
	}

	public static void setStrAktGJ(String strAktGJ) {
		Einstellungen.strAktGJ = strAktGJ;
	}

	public static void setStrAktUser(String strAktUser) {
		Einstellungen.strAktUser = strAktUser;
	}

	public static void setStrDBComputer(String strDBComputer) {
		Einstellungen.strDBComputer = strDBComputer;
	}

	public static void setStrDBencrypted(String strDBencrypted) {
		Einstellungen.strDBencrypted = strDBencrypted;
	}

	public static void setStrDBNameDest(String strDBNameDest) {
		Einstellungen.strDBNameDest = strDBNameDest;
	}

	public static void setStrDBNameSource(String strDBNameSource) {
		Einstellungen.strDBNameSource = strDBNameSource;
	}

	public static void setStrDBPort(String strDBPort) {
		Einstellungen.strDBPort = strDBPort;
	}

	public static void setStrDBServerCert(String strDBServerCert) {
		Einstellungen.strDBServerCert = strDBServerCert;
	}

	public static void setStrQRschema(String strQRschema) {
		Einstellungen.strQRschema = strQRschema;
	}

	public static void setTplBill(String tplBill) {
		Einstellungen.tplBill = tplBill;
	}

	public static void setTplConfirmation(String tplConfirmation) {
		Einstellungen.tplConfirmation = tplConfirmation;
	}

	public static void setTplOffer(String tplOffer) {
		Einstellungen.tplOffer = tplOffer;
	}
	
	public static void setTplDescription(String tplDescription) {
		Einstellungen.tplDescription = tplDescription;
	}

	public static void setTplReminder(String tplReminder) {
		Einstellungen.tplReminder = tplReminder;
	}
	
	public static void setTplMahnung(String tplMahnung) {
		Einstellungen.tplMahnung = tplMahnung;
	}
	
	public static void setTplP109a(String tplP109a) {
		Einstellungen.tplP109a = tplP109a;
	}

	public static void setWorkPath(String workPath) {
		Einstellungen.workPath = workPath;
	}

	public static void setbFinished(boolean bFinished) {
		Einstellungen.bFinished = bFinished;
	}

	public static String getsMasterData() {
		return sMasterData;
	}

	public static String getsProductiveData() {
		return sProductiveData;
	}

	public static String getTplBestellung() {
		return tplBestellung;
	}

	public static void setTplBestellung(String tplBestellung) {
		Einstellungen.tplBestellung = tplBestellung;
	}

	public static String getTplLieferschein() {
		return tplLieferschein;
	}

	public static void setTplLieferschein(String tplLieferschein) {
		Einstellungen.tplLieferschein = tplLieferschein;
	}

	public static String getHtmlBaseText() {
		return htmlBaseText;
	}

	public static String getTplDescriptionStyle() {
		return tplDescriptionStyle;
	}

	public static String getHtmlBaseStyle() {
		return htmlBaseStyle;
	}

	public static String getTplDescriptionBase() {
		return tplDescriptionBase;
	}

}
