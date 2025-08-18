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

public class ExcelMahnung{

	private static final Logger logger = LogManager.getLogger(ExcelMahnung.class);

	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_E = 4;
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
		Bank bank = ExcelHelper.bankData(rechnung.getIdBank());
		ExcelHelper.textData();

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
			Cell remHeader = ws.getRow(15).getCell(COLUMN_B); //Überschrift
			Cell remAnrede = ws.getRow(17).getCell(COLUMN_B); //Anrede
			Cell remText1 = ws.getRow(19).getCell(COLUMN_B); //Textzeile 1
			Cell remText2 = ws.getRow(20).getCell(COLUMN_B); //Textzeile 2
			Cell remText3 = ws.getRow(21).getCell(COLUMN_B); //Textzeile 3
			Cell remText4 = ws.getRow(22).getCell(COLUMN_B); //Textzeile 4
			Cell remGruss = ws.getRow(25).getCell(COLUMN_B); //Grußformel
			Cell remName = ws.getRow(26).getCell(COLUMN_B); //Name
			Cell remBank = ws.getRow(46).getCell(COLUMN_E); //Bankverbindung E47 - E49: Bankname
			Cell remIBAN = ws.getRow(47).getCell(COLUMN_E); //IBAN
			Cell remBIC = ws.getRow(48).getCell(COLUMN_E); //BIC
			
			//#######################################################################
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			remAdress.setCellValue(kunde.getName() + "\n" + kunde.getStrasse() + "\n" + kunde.getPlz() + " " +
					kunde.getOrt() + ", " + kunde.getLand().toUpperCase());
			remDate.setCellValue(StartUp.getDtNow());
			remDuty.setCellValue(kunde.getPerson());
			
			remHeader.setCellValue(ExcelHelper.getTextMahnung().get(0)
					.replace("{NrMahn}", String.valueOf(iStufe))
					.replace("{RE}", rechnung.getIdNummer())
					.replace("{Datum}", rechnung.getDatum().toString()));

			if(kunde.getPronomen().equals("Herr")) {
				remAnrede.setCellValue(ExcelHelper.getTextMahnung().get(1).replace("{Name}", kunde.getPerson()));
			}else if(kunde.getPronomen().equals("Frau")) {
				remAnrede.setCellValue(ExcelHelper.getTextMahnung().get(2).replace("{Name}", kunde.getPerson()));
			}else {
				remAnrede.setCellValue(ExcelHelper.getTextMahnung().get(3));
			}
			
			switch(iStufe) {
			case 1:
				remText1.setCellValue(ExcelHelper.getTextMahnung().get(4)
						.replace("{Datum}", rechnung.getDatum().toString())
						.replace("{Wert}", rechnung.getBrutto().toString()));
				remText2.setCellValue(ExcelHelper.getTextMahnung().get(6));
				remText3.setCellValue(ExcelHelper.getTextMahnung().get(8));
				remText4.setCellValue(ExcelHelper.getTextMahnung().get(10));
				break;
			case 2:
				remText1.setCellValue(ExcelHelper.getTextMahnung().get(5)
						.replace("{Datum}", rechnung.getDatum().toString())
						.replace("{Wert}", rechnung.getBrutto().toString()));
				remText2.setCellValue(ExcelHelper.getTextMahnung().get(7));
				remText3.setCellValue(ExcelHelper.getTextMahnung().get(9)
						.replace("{Spesen}", "40,00"));
				remText4.setCellValue(ExcelHelper.getTextMahnung().get(11));
				break;
			}

			remGruss.setCellValue(ExcelHelper.getTextZahlErin().get(8));
			remName.setCellValue(ExcelHelper.getTextZahlErin().get(9).replace("{OwnerName}", ExcelHelper.getKontaktName()));

			remBank.setCellValue(bank.getBankName());
			remIBAN.setCellValue(FormatIBAN(bank.getIban()));
			remBIC.setCellValue(bank.getBic());

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

