package org.andy.code.dataExport;

import static org.andy.toolbox.misc.Tools.FormatIBAN;
import static org.andy.toolbox.misc.Tools.isLocked;
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

import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyProductive.FileStore;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.andy.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.code.main.Einstellungen;
import org.andy.code.main.StartUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelMahnstufe2{

	private static final Logger logger = LogManager.getLogger(ExcelMahnstufe2.class);

	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_F = 5;

	//###################################################################################################################################################
	// Mahnung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void mahnungExport(String sNr, int iStufe) throws Exception {

		if(iStufe < 1 || iStufe > 2) {
			return;
		}

		String sExcelIn = Einstellungen.getTplMahnung();
		String sExcelOut = Einstellungen.getWorkPath() + "Mahnung_" + String.valueOf(iStufe) + "_" + sNr + ".xlsx";
		String sPdfOut = Einstellungen.getWorkPath() + "Mahnung_" + String.valueOf(iStufe) + "_" + sNr + ".pdf";

		Rechnung rechnung = ExcelHelper.loadRechnung(sNr);
		Kunde kunde = ExcelHelper.kundeData(rechnung.getIdKunde());
		String adressat = ExcelHelper.kundeAnschrift(rechnung.getIdKunde());
		Bank bank = ExcelHelper.bankData(rechnung.getIdBank());
		
		String[][] txtBaustein = ExcelHelper.findText("Mahnstufe2");

		//#######################################################################
		// Zahlungserinnerung-Excel erzeugen
		//#######################################################################
		FileInputStream inputStream = null;
		OutputStream fileOut = null;
		try {
			inputStream = new FileInputStream(sExcelIn);
			fileOut = new FileOutputStream(sExcelOut);

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Mahnung");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################

			ArrayList<String> editOwner = new ArrayList<>();
		    Footer footer = ws.getFooter();

			editOwner = ExcelHelper.ownerData();
			String senderOwner = ExcelHelper.getSenderOwner();

			// Schrift: Arial 9, Farbe: Grau 50% (#7F7F7F)
			String style = "&\"Arial,Regular\"&9&K7F7F7F";

			footer.setLeft(style + ExcelHelper.getFooterLeft());
			footer.setCenter(style + ExcelHelper.getFooterCenter());

			Cell anOwner = ws.getRow(0).getCell(COLUMN_A); //Angebotsinhaber
			Cell anOwnerSender = ws.getRow(3).getCell(COLUMN_B); //Absender über Adressfeld

			XSSFRichTextString OwnerText = new XSSFRichTextString();
			XSSFRichTextString OwnerSender = new XSSFRichTextString();

			for (int i = 0; i < 6; i++) {
				String part = editOwner.get(i);
				XSSFFont font = wb.createFont();

				if (i == 0) {
					font.setFontName("Arial");
					font.setFontHeightInPoints((short) 24);
					font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
				} else {
					font.setFontName("Arial");
					font.setFontHeightInPoints((short) 12);
					font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
				}

				OwnerText.append(part, font);
			}

			XSSFFont font = wb.createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 7);
			font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			CellStyle rightAlignStyle = wb.createCellStyle();
			rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);
			anOwnerSender.setCellStyle(rightAlignStyle);
			OwnerSender.append(senderOwner, font);

			anOwner.setCellValue(OwnerText);
			anOwnerSender.setCellValue(OwnerSender);

			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			Cell remAdress = ws.getRow(4).getCell(COLUMN_B); //Name und Anschrift
			Cell remDate = ws.getRow(4).getCell(COLUMN_F); //Datum der Zahlungserinnerung
			Cell remDuty = ws.getRow(8).getCell(COLUMN_F); //Ansprechpartner
			
			//#######################################################################
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			remAdress.setCellValue(adressat); // Kundenanschrift
			remDate.setCellValue(StartUp.getDtNow());
			remDuty.setCellValue(kunde.getPerson());
			
			ExcelHelper.replaceCellValue(wb, ws, "{Bank}", bank.getBankName());
			ExcelHelper.replaceCellValue(wb, ws, "{IBAN}", FormatIBAN(bank.getIban()));
			ExcelHelper.replaceCellValue(wb, ws, "{BIC}", bank.getBic());
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    if (val != null) {
			        val = val.replace("{NrMahn}", String.valueOf(iStufe));
			        val = val.replace("{RE}", rechnung.getIdNummer());
			        val = val.replace("{Datum}", rechnung.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			        val = val.replace("{Wert}", rechnung.getBrutto().toString());
			        val = val.replace("{Spesen}", "40,00");
			        val = val.replace("{OwnerName}", ExcelHelper.getKontaktName());
			    }
			    txtBaustein[x][1] = val;
			    
			    ExcelHelper.replaceCellValue(wb, ws, key, val); // Texte in Zellen schreiben

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
		
		Path path = Paths.get(FileNamePath);
		
		switch(iStufe) {
		case 1:
			fileStore.setM1FileName(FileName);
			fileStore.setM1PdfFile(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
			break;
		case 2:
			fileStore.setM2FileName(FileName);
			fileStore.setM2PdfFile(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
			break;
		}
		
		fileStoreRepository.update(fileStore); // Datei in DB speichern
		
		//#######################################################################
		// Status der Rechnung ändern
		//#######################################################################
		
		rechnung.setState(rechnung.getState() + 100); // Zustand Mahnstufe x setzen
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

