package org.andy.code.main;

import static org.andy.toolbox.crypto.License.getLicense;
import static org.andy.toolbox.misc.Tools.CheckService;
import static org.andy.toolbox.misc.Tools.getServiceQuery;
import static org.andy.toolbox.misc.Tools.getServiceStart;
import static org.andy.toolbox.misc.Tools.getServiceStop;
import static org.andy.toolbox.misc.Tools.isServiceAutomatic;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.gui.main.JFmainLogIn;
import org.andy.gui.main.JFoverview;

public class StartUp {

	public static final String APP_NAME = "facturaX ";
	public static String APP_VERSION = null;

	static final Logger logger = LogManager.getLogger(StartUp.class);

	private static String APP_LICENSE = null;
	private static int APP_MODE = 0;

	private static String SERVICE_NAME = "";

	private static LocalDate dateNow;
	private static String dtNow;
	private static final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new ShutdownThread()); // shutdown-hook bekannt machen als thread

		try {
			APP_MODE = getLicense(LoadData.getFileLicense());
		} catch (NoSuchAlgorithmException | IOException e) {
			logger.error("error reading license" + e);
		}
		switch (APP_MODE) {
		case 0:
			APP_LICENSE = "unlizensiertes Produkt";
			break;
		case 1:
			APP_LICENSE = "Lizenz DEMO";
			break;
		case 2:
			APP_LICENSE = "Lizenz OK";
			break;
		}

		System.setProperty("log4j.configurationFile", "log4j2.xml");

		dateNow = LocalDate.now();
		dtNow = dateNow.format(dfDate);

		APP_VERSION = getVersion();
		logger.info("facturaX startet - Version: " + APP_VERSION);

		LoadData.LoadProgSettings();

		StartServiceEx(); // ggf. SQL-Server Dienst starten

		JFmainLogIn.loadLogIn(); // Anmeldefenster einblenden

		Runtime.getRuntime().gc();
	}

	// ###################################################################################################################################################
	// ###################################################################################################################################################

	public static void StartServiceEx() {
		if (LoadData.getStrDBComputer().equals("127.0.0.1") || LoadData.getStrDBComputer().equals("localhost")) { // lokale Datenbank
			int iRunning;

			try {
				iRunning = CheckService(SERVICE_NAME, getServiceQuery());
				if (iRunning == 1) {
					int iStarting = CheckService(SERVICE_NAME, getServiceStart());
					if (iStarting == 1 || iStarting == 2) { // Dienst konnte nicht gestartet werden
						JOptionPane.showMessageDialog(null, "SQL-Server Dienst konnte nicht gestartet werden ...",
								StartUp.APP_NAME, JOptionPane.ERROR_MESSAGE);
						logger.error("StartServiceEx() - SQL-Server Dienst konnte nicht gestartet werden ...");
					}
				}
			} catch (IOException e) {
				logger.error("StartServiceEx() - IOException: " + e.getMessage(), e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.error("StartServiceEx() - InterruptedException: " + e.getMessage(), e);
			} catch (Exception e) {
				logger.error("StartServiceEx() - Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage(), e);
			}
		}
	}

	public static void StopServiceEx() {
		if (LoadData.getStrDBComputer().equals("127.0.0.1") || LoadData.getStrDBComputer().equals("localhost")) { // lokale Datenbank
			int iStopped;

			try {
				iStopped = CheckService(SERVICE_NAME, getServiceStop());
				if (iStopped != 0) {
					logger.error("StopServiceEx() - SQL-Server Dienst konnte nicht gestoppt werden. Rückgabewert: "
							+ iStopped);
				}
			} catch (IOException e) {
				logger.error("StopServiceEx() - IOException: " + e.getMessage(), e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.error("StopServiceEx() - InterruptedException: " + e.getMessage(), e);
			} catch (Exception e) {
				logger.error("StopServiceEx() - Unerwarteter Fehler: " + e.getMessage(), e);
			}
		}
	}

	public static String getVersion() {

		InputStream input = StartUp.class.getClassLoader().getResourceAsStream("version.properties");
		Properties properties = new Properties();
		try (input) {
			if (input == null) {
				return "unknown version";
			}
			properties.load(input);
			input.close();
			return properties.getProperty("version");
		} catch (IOException e) {
			return "Fehler beim Laden der Version";
		}

	}

	public static String getAPP_LICENSE() {
		return APP_LICENSE;
	}

	public static int getAPP_MODE() {
		return APP_MODE;
	}

	public static String getSERVICE_NAME() {
		return SERVICE_NAME;
	}

	public static void setSERVICE_NAME(String sSERVICE_NAME) {
		SERVICE_NAME = sSERVICE_NAME;
	}

	public static String getDtNow() {
		return dtNow;
	}

	public static final LocalDate getDateNow() {
		return dateNow;
	}

	public static final DateTimeFormatter getDfdate() {
		return dfDate;
	}

}

// ###################################################################################################################################################
// ###################################################################################################################################################

class ShutdownThread extends Thread {

	@Override
	public void run() {
		JFoverview.getLock().delete();
		boolean bResult = false;
		try {
			String sFullServiceName = "MSSQL$" + LoadData.getStrDBservice();
			bResult = isServiceAutomatic(sFullServiceName);
		} catch (IOException | InterruptedException e) {
			Thread.currentThread().interrupt();
			StartUp.logger.error("error in checking state of service - " + e);
		}
		if (!bResult) {
			StartUp.StopServiceEx();
		}
		Runtime.getRuntime().gc();
	}

}
