package org.andy.fx.code.dataExport;

import static org.andy.fx.code.misc.TextFormatter.FormatIBAN;
import static org.andy.fx.code.misc.FileTools.isLocked;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.code.misc.Identified;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelZahlungserinnerung implements Identified {

	public static final String CLASS_ID = ExcelZahlungserinnerung.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(ExcelZahlungserinnerung.class);

	//###################################################################################################################################################
	// Zahlungserinnerung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void reminderExport(String sNr) throws Exception {

		new ArrayList<>();
		String sExcelIn = Einstellungen.getTplReminder();
		String sExcelOut = Einstellungen.getWorkPath() + "Zahlungserinnerung_" + sNr + ".xlsx";
		String sPdfOut = Einstellungen.getWorkPath() + "Zahlungserinnerung_" + sNr + ".pdf";

		Rechnung rechnung = ExportHelper.loadRechnung(sNr);
		Kunde kunde = ExportHelper.kundeData(rechnung.getIdKunde());
		String adressat = ExportHelper.kundeAnschrift(rechnung.getIdKunde());
		Bank bank = ExportHelper.bankData(rechnung.getIdBank());
		
		String[][] txtBaustein = ExportHelper.findText(CLASS_ID);

		//#######################################################################
		// Zahlungserinnerung-Excel erzeugen
		//#######################################################################
		FileInputStream inputStream = null;
		OutputStream fileOut = null;
		try {
			inputStream = new FileInputStream(sExcelIn);
			fileOut = new FileOutputStream(sExcelOut);

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Zahlungserinnerung");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			ExportHelper.applyOwnerAndFooter(wb, ws);

			//#######################################################################
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			ExportHelper.replaceCellValue(wb, ws, "{zeAdresse}", adressat);
			ExportHelper.replaceCellValue(wb, ws, "{zeDatum}", StartUp.getDtNow());
			ExportHelper.replaceCellValue(wb, ws, "{zeDuty}", kunde.getPerson());

			ExportHelper.replaceCellValue(wb, ws, "{Bank}", bank.getBankName());
			ExportHelper.replaceCellValue(wb, ws, "{IBAN}", FormatIBAN(bank.getIban()));
			ExportHelper.replaceCellValue(wb, ws, "{BIC}", bank.getBic());
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    if (val != null) {
			    	val = val.replace("{RE}", rechnung.getIdNummer());
			    	val = val.replace("{Datum}", rechnung.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			    	val = val.replace("{Wert}", rechnung.getBrutto().toString());
			        val = val.replace("{OwnerName}", ExportHelper.getKontaktName());
			    }
			    txtBaustein[x][1] = val;
			    
			    ExportHelper.replaceCellValue(wb, ws, key, val); // Texte in Zellen schreiben

			}
			
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("reminderExport(String sNr) - " + e);
		}
		inputStream.close();
		fileOut.close();
		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		ErzeugePDF.toPDF(sExcelOut, sPdfOut);
		ErzeugePDF.setPdfMetadata(sNr, "ZE", sPdfOut);

		boolean bLockedPDF = isLocked(sPdfOut);
		while(bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}

		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		
		String FileNamePath = sPdfOut;
		File fn = new File(FileNamePath);
		String FileName = fn.getName();
		
		FileStoreRepository fileStoreRepository = new FileStoreRepository();
		FileStore fileStore = fileStoreRepository.findById(rechnung.getIdNummer()); // Tabelleneintrag mit Hibernate lesen
		
		fileStore.setZeFileName(FileName);
		
		Path path = Paths.get(FileNamePath);
		fileStore.setZePdfFile(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.update(fileStore); // Datei in DB speichern
		
		//#######################################################################
		// Status der Rechnung ändern
		//#######################################################################
		
		rechnung.setState(rechnung.getState() + 200); // Zustand Zahlungserinnerung setzen
		RechnungRepository rechnungRepository = new RechnungRepository();
		rechnungRepository.update(rechnung);
		
		//#######################################################################
		// Ursprungs-Excel und -pdf löschen
		//#######################################################################
		boolean bLockedpdf = isLocked(sPdfOut);
		boolean bLockedxlsx = isLocked(sExcelOut);
		while(bLockedpdf || bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		File pdFile = new File(sPdfOut);
		if(xlFile.delete() && pdFile.delete()) {

		}else {
			logger.error("reminderExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}

}

