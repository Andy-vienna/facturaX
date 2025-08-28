package org.andy.code.main;

import static org.andy.toolbox.crypto.License.getLicense;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.andy.code.dataStructure.entitiyMaster.User;
import org.andy.code.dataStructure.repositoryMaster.UserRepository;
import org.andy.gui.main.HauptFenster;
import org.andy.gui.main.AnmeldeFenster;
import org.andy.gui.misc.MyFlatTabbedPaneUI;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class StartUp {
	
	private static org.apache.logging.log4j.Logger logger;

	private static java.nio.channels.FileChannel LOCK_CH;
    private static java.nio.channels.FileLock LOCK;
    private static java.nio.file.Path LOCK_PATH;

	public static final String APP_NAME = "FacturaX v2";
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
        // 1) Logging konfigurieren
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        logger = org.apache.logging.log4j.LogManager.getLogger(StartUp.class);

        // 2) Globale Fehlerbehandlung
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Uncaught in " + t.getName(), e);
            System.exit(98);
        });
        
        // 3) Instanzprüfung
        if (!acquireSingleInstanceLock()) {
        	JOptionPane.showMessageDialog(null, "Es läuft bereits eine Instanz von FacturaX v2", "FacturaX v2", JOptionPane.ERROR_MESSAGE);
            System.exit(99);
        }

        // 4) Lizenz einlesen
        try {
            APP_MODE = getLicense(Einstellungen.getFileLicense());
        } catch (java.security.NoSuchAlgorithmException | java.io.IOException e) {
            logger.error("error reading license", e);
            APP_MODE = 0;
        }
        APP_LICENSE = switch (APP_MODE) {
            case 1 -> "Lizenz DEMO";
            case 2 -> "Lizenz OK";
            default -> "unlizensiertes Produkt";
        };

        // 5) Version lesen
        APP_VERSION = getVersion();

        // 6) Einstellungen laden
        Einstellungen.LoadProgSettings();

        // 7) UI auf EDT starten
        SwingUtilities.invokeLater(() -> {
            try {
                FlatIntelliJLaf.setup();
                UIManager.setLookAndFeel(new FlatIntelliJLaf());

                UIManager.put("Button.arc", 10);
                UIManager.put("Component.arc", 10);
                UIManager.put("TextComponent.arc", 10);
                UIManager.put("ProgressBar.arc", 10);
                UIManager.put("TabbedPaneUI", MyFlatTabbedPaneUI.class.getName());
                UIManager.put("TabbedPane.tabType", "card");
                UIManager.put("TabbedPane.cardTabSelectionHeight", 0);
                UIManager.put("MenuBar.selectionBackground", Color.LIGHT_GRAY);
                UIManager.put("MenuBar.hoverBackground", Color.LIGHT_GRAY);
                UIManager.put("MenuBar.underlineSelectionColor", Color.LIGHT_GRAY);
                UIManager.put("MenuBar.underlineSelectionBackground", Color.LIGHT_GRAY);
                UIManager.put("MenuItem.selectionBackground", Color.LIGHT_GRAY);
                UIManager.put("MenuItem.hoverBackground", Color.LIGHT_GRAY);
                UIManager.put("MenuItem.underlineSelectionColor", Color.LIGHT_GRAY);
                UIManager.put("MenuItem.underlineSelectionBackground", Color.LIGHT_GRAY);
                UIManager.put("TableHeader.background", new Color(255,248,220));
                UIManager.put("TableHeader.foreground", Color.BLACK);

            } catch (Exception ex) {
                logger.error("cannot load FlatIntelliJLaf theme", ex);
            }

            // hier erst Fenster erzeugen
            new AnmeldeFenster(new UserRepository(), new AnmeldeFenster.AuthCallback() {
            	@Override
                public void onSuccess(User u) { HauptFenster.loadGUI(u.getId(), u.getRoles()); }
                public void onCancel() { System.exit(0); }
            }).show();
        });

        // 8) Shutdown-Hook für Aufräumen
        Runtime.getRuntime().addShutdownHook(new Thread(StartUp::releaseSingleInstanceLock));
    }
	
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	private static boolean acquireSingleInstanceLock() {
        try {
            LOCK_PATH = java.nio.file.Paths.get(System.getProperty("user.home"), ".facturax", "app.lock");
            java.nio.file.Files.createDirectories(LOCK_PATH.getParent());
            LOCK_CH = java.nio.channels.FileChannel.open(
                    LOCK_PATH,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.WRITE);
            LOCK = LOCK_CH.tryLock();               // entscheidend: OS-Lock
            return LOCK != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static void releaseSingleInstanceLock() {
        try { if (LOCK != null && LOCK.isValid()) LOCK.release(); } catch (Exception ignore) {}
        try { if (LOCK_CH != null && LOCK_CH.isOpen()) LOCK_CH.close(); } catch (Exception ignore) {}
        try { if (LOCK_PATH != null) java.nio.file.Files.deleteIfExists(LOCK_PATH); } catch (Exception ignore) {}
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
			return "0.0.0";
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

