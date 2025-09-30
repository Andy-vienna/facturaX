package org.andy.code.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Properties;

public class FileTools {

	public static boolean saveSettingsApp(Properties settings) throws IOException {
		FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + "\\app.properties");
		settings.storeToXML(out, "settings", "UTF-8");
		return true;
	}

	public static boolean saveSettingsDB(Properties settings) throws IOException {
		FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + "\\db.properties");
		settings.storeToXML(out, "settings", "UTF-8");
		return true;
	}

	public static Properties loadSettingsEx(File fIn) throws IOException {
		FileInputStream in = new FileInputStream(fIn);
		Properties einstellungen = new Properties();
		einstellungen.loadFromXML(in);
		in.close();
		return einstellungen;
	}
	
	public static boolean isLocked(String fileName) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
				FileLock lock = randomAccessFile.getChannel().lock()) {
			return lock == null;
		} catch (IOException ex) {
			return true;
		}
	}

}
