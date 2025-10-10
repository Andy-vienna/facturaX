package org.andy.fx.code.main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JOptionPane;

import org.andy.fx.code.dataStructure.jsonSettings.JsonApp;
import org.andy.fx.code.dataStructure.jsonSettings.JsonDb;
import org.andy.fx.code.dataStructure.jsonSettings.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Einstellungen {

	private static final Logger logger = LogManager.getLogger(Einstellungen.class);

	private static final String FILE_LICENSE = System.getProperty("user.dir") + "\\license.lic";
	
	private static JsonApp appSettings = new JsonApp();
	private static JsonDb dbSettings = new JsonDb();
	
	private static Path fileApp;
	private static Path fileDB;
	
	private static String sMasterData;
	private static String sProductiveData;

	private static String htmlBaseText;
	private static String htmlBaseStyle;

	// ###################################################################################################################################################
	// public teil
	// ###################################################################################################################################################

	public static void LoadProgSettings() {
		LoadSettings();
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static void LoadSettings() {
		
		boolean app = fileExist("settingsApp.json");
		boolean db = fileExist("settingsDb.json");
		if (!app || !db) {
			JOptionPane.showMessageDialog(null, "<html>Anwendungs- und/oder DB-Einstellungen nicht vorhanden<br>Anwendung wird beendet ...",
					"FacturaX v2", JOptionPane.ERROR_MESSAGE);
            System.exit(90);
		}
		
		// ------------------------------------------------------------------------------
		// App- und DB-Einstellungen laden
		// ------------------------------------------------------------------------------
		Path dir  = Path.of(System.getProperty("user.dir"));
		fileApp = dir.resolve("settingsApp.json");   // Dateiname anhängen
		fileDB = dir.resolve("settingsDb.json");   // Dateiname anhängen
		try {
			appSettings = JsonUtil.loadAPP(fileApp);
			dbSettings = JsonUtil.loadDB(fileDB);
		} catch (IOException e) {
			logger.error("error loading app or db settings: " + e.getMessage());
		}
		
		// ------------------------------------------------------------------------------
		// Datenbank Connection strings für Hibernate
		// ------------------------------------------------------------------------------
		if (dbSettings.dbType == null) {
			JOptionPane.showMessageDialog(null, "<html>settingsDb.json - Inhalt unklar oder nicht lesbar<br>Anwendung wird beendet ...",
					"FacturaX v2", JOptionPane.ERROR_MESSAGE);
            System.exit(91);
		}
		switch(dbSettings.dbType) {
    	case "mssql":
    		sMasterData = "jdbc:sqlserver://" + dbSettings.dbHost + ":" + dbSettings.dbPort + ";databaseName="
    				+ dbSettings.dbMaster + ";encrypt=" + dbSettings.dbEncrypt + ";trustServerCertificate=" + dbSettings.dbCert;
    		sProductiveData = "jdbc:sqlserver://" + dbSettings.dbHost + ":" + dbSettings.dbPort + ";databaseName="
    				+ dbSettings.dbData + ";encrypt=" + dbSettings.dbEncrypt + ";trustServerCertificate=" + dbSettings.dbCert;
    		break;
    	case "postgre":
    		sMasterData = "jdbc:postgresql://" + dbSettings.dbHost + ":" + dbSettings.dbPort + "/"
    				+ dbSettings.dbMaster + "?currentSchema=public&sslmode=disable";
    		sProductiveData = "jdbc:postgresql://" + dbSettings.dbHost + ":" + dbSettings.dbPort + "/"
    				+ dbSettings.dbData + "?currentSchema=public&sslmode=disable";
    		break;
    	}
		
		htmlBaseText = htmlBaseText();
		htmlBaseStyle = htmlBaseStyle();
		
		// sollte die client_secret.json nicht vorhanden sein, dann auch keine Google Schaltfläche
		boolean ok = fileExist("client_secret.json");
		ok = appSettings.oAuth ? ok : false;
	}

	// ###################################################################################################################################################
	
	private static String htmlBaseText() {
		if (appSettings.tplDescriptionBase.equals("---") || appSettings.tplDescriptionBase.isEmpty()) return null;
		Path path = Path.of(appSettings.tplDescriptionBase);
        String content = null;
		try {
			content = Files.readString(path);
		} catch (IOException e) {
			logger.error("error reading htmlbasetext: " + e.getMessage());
		}
        return content;
	}
	
	private static String htmlBaseStyle() {
		if (appSettings.tplDescriptionStyle.equals("---") || appSettings.tplDescriptionStyle.isEmpty()) return null;
		Path path = Path.of(appSettings.tplDescriptionStyle);
        String content = null;
		try {
			content = Files.readString(path);
		} catch (IOException e) {
			logger.error("error reading htmlbasestyle: " + e.getMessage());
		}
        return content;
	}
	
	public static boolean fileExist(String fileName) {
		File f = new File(fileName);
		return f.isFile() ? true : false;
	}
	
	public static boolean isLocked(String fileName) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
				FileLock lock = randomAccessFile.getChannel().lock()) {
			return lock == null;
		} catch (IOException ex) {
			return true;
		}
	}
	
	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public static String getFileLicense() {
		return FILE_LICENSE;
	}

	public static JsonApp getAppSettings() {
		return appSettings;
	}

	public static JsonDb getDbSettings() {
		return dbSettings;
	}

	public static Path getFileApp() {
		return fileApp;
	}

	public static Path getFileDB() {
		return fileDB;
	}

	public static String getsMasterData() {
		return sMasterData;
	}

	public static String getsProductiveData() {
		return sProductiveData;
	}

	public static String getHtmlBaseText() {
		return htmlBaseText;
	}

	public static String getHtmlBaseStyle() {
		return htmlBaseStyle;
	}

	public static void setAppSettings(JsonApp appSettings) {
		Einstellungen.appSettings = appSettings;
	}

}
