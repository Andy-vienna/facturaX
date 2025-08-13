package org.andy.code.main;

import static org.andy.toolbox.crypto.License.getLicense;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.andy.gui.main.JFmainLogIn;

public class StartUp {
	
	static final Logger logger = LogManager.getLogger(StartUp.class);
	@SuppressWarnings("unused")
	private static ServerSocket lockSocket;

	public static final String APP_NAME = "FacturaX v2 ";
	public static String APP_VERSION = null;
	private static String APP_LICENSE = null;
	private static int APP_MODE = 0;

	private static LocalDate dateNow;
	private static String dtNow;
	private static final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	// ###################################################################################################################################################
	// Starten der Applikation
	// ###################################################################################################################################################

	public static void main(String[] args) {
		
		if (isAlreadyRunning()) {
			System.err.println("Beende: eine Instanz läuft bereits");
			System.exit(99); // 99 = eine Instanz läuft bereits
		}

		try {
			APP_MODE = getLicense(LoadData.getFileLicense());
		} catch (NoSuchAlgorithmException | IOException e) {
			logger.error("error reading license" + e);
		}
		switch (APP_MODE) {
		case 0 -> APP_LICENSE = "unlizensiertes Produkt";
		case 1 -> APP_LICENSE = "Lizenz DEMO";
		case 2 -> APP_LICENSE = "Lizenz OK";
		}

		System.setProperty("log4j.configurationFile", "log4j2.xml");

		dateNow = LocalDate.now();
		dtNow = dateNow.format(dfDate);
		APP_VERSION = getVersion();

		LoadData.LoadProgSettings(); // Einstellungen laden
		JFmainLogIn.loadLogIn(); // Anmeldefenster einblenden
	}
	
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	private static boolean isAlreadyRunning() {
		try {
			lockSocket = new ServerSocket(54556);
			return false;
		} catch (IOException e) {
			logger.info("OrderManager ist bereits gestartet.");
			return true;
		}
	}

	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

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

