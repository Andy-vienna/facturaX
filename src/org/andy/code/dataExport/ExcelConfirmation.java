package org.andy.code.dataExport;

import static org.andy.toolbox.misc.Tools.FormatIBAN;
import static org.andy.toolbox.misc.Tools.isLocked;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyProductive.Angebot;
import org.andy.code.dataStructure.entitiyProductive.FileStore;
import org.andy.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.code.main.LoadData;
import org.andy.code.qr.ZxingQR;
import org.andy.gui.offer.JFconfirmA;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.zxing.WriterException;

public class ExcelConfirmation{

	private static final Logger logger = LogManager.getLogger(ExcelConfirmation.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_E = 4;
	private static final int COLUMN_F = 5;

	private static String[] sAnTxt = new String[12];
	private static double[] dAnz = new double[12];
	private static double[] dEp = new double[12];

	//###################################################################################################################################################
	// Angebotbestätigung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void abExport(String sNr) throws Exception {

		String sExcelIn = LoadData.getTplConfirmation();
		String sExcelOut = LoadData.getWorkPath() + "Auftragsbestätigung_" + sNr.replace("AN", "AB") + ".xlsx";
		String sPdfOut = LoadData.getWorkPath() + "Auftragsbestätigung_" + sNr.replace("AN", "AB") + ".pdf";

		final Cell abPos[] = new Cell[13];
		final Cell abText[] = new Cell[13];
		final Cell abAnz[] = new Cell[13];
		final Cell abEPreis[] = new Cell[13];
		final Cell abGPreis[] = new Cell[13];

		Angebot angebot = DataExportHelper.loadAngebot(sNr);
		Kunde kunde = DataExportHelper.kundeData(angebot.getIdKunde());
		Bank bank = DataExportHelper.bankData(angebot.getIdBank());
		DataExportHelper.textData();

		//#######################################################################
		// Angebots-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Angebot");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################

			ArrayList<String> editOwner = new ArrayList<>();
		    Footer footer = ws.getFooter();

			editOwner = DataExportHelper.ownerData();
			String senderOwner = DataExportHelper.getSenderOwner();

			// Schrift: Arial 9, Farbe: Grau 50% (#7F7F7F)
			String style = "&\"Arial,Regular\"&9&K7F7F7F";

			footer.setLeft(style + DataExportHelper.getFooterLeft());
			footer.setCenter(style + DataExportHelper.getFooterCenter());

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
			Cell abAdress = ws.getRow(4).getCell(COLUMN_B); //Name und Anschrift
			Cell abDate = ws.getRow(4).getCell(COLUMN_F); //Datum
			Cell abNr = ws.getRow(5).getCell(COLUMN_F); //Nummer
			Cell abRef = ws.getRow(6).getCell(COLUMN_F); //Referenz
			Cell abDuty = ws.getRow(8).getCell(COLUMN_F); //Ansprechpartner
			
			for(int i = 0; i < angebot.getAnzPos().intValue(); i++ ) { //Positionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				abPos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				abText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				abAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				abEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				abGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}
			Cell abSumme = ws.getRow(28).getCell(COLUMN_F); //Gesamtsumme
			Cell abText1 = ws.getRow(34).getCell(COLUMN_A); //Angebotstexte
			Cell abText2 = ws.getRow(37).getCell(COLUMN_A);
			Cell abText4 = ws.getRow(43).getCell(COLUMN_A);
			Cell abText3 = ws.getRow(46).getCell(COLUMN_A);
			Cell abBank = ws.getRow(46).getCell(COLUMN_E); //Bankverbindung E47 - E49: Bankname
			Cell abIBAN = ws.getRow(47).getCell(COLUMN_E); //IBAN
			Cell abBIC = ws.getRow(48).getCell(COLUMN_E); //BIC
			
			//#######################################################################
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			abAdress.setCellValue(kunde.getName() + "\n" + kunde.getStrasse() + "\n" + kunde.getPlz() + " " +
					kunde.getOrt() + ", " + kunde.getLand().toUpperCase());
			abDate.setCellValue(angebot.getDatum().toString());
			abNr.setCellValue(angebot.getIdNummer().replace("AN", "AB"));
			abDuty.setCellValue(kunde.getPronomen() + " " + kunde.getPerson());
			abRef.setCellValue(angebot.getIdNummer());
			
			for(int i = 0; i < angebot.getAnzPos().intValue(); i++ ) {
				abPos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Angebot.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal menge = (BigDecimal) Angebot.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal ep = (BigDecimal) Angebot.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(angebot);
		            sAnTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            abText[i].setCellValue(art);
					abAnz[i].setCellValue(menge.doubleValue());
					abEPreis[i].setCellValue(ep.doubleValue());
					abGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			
			abSumme.setCellValue(angebot.getNetto().doubleValue());
			abText1.setCellValue(DataExportHelper.getTextOrderConfirm().get(0).replace("{AN}", angebot.getIdNummer())
					.replace("{Best-Nr}", JFconfirmA.getsConfNr()).replace("{Datum}", JFconfirmA.getsConfDatum()));
			abText2.setCellValue(DataExportHelper.getTextOrderConfirm().get(1).replace("{Datum}", JFconfirmA.getsConfStart()));
			abText3.setCellValue(DataExportHelper.getTextOrderConfirm().get(2).replace("{Tage}", kunde.getZahlungsziel()));
			abText4.setCellValue(DataExportHelper.getTextOrderConfirm().get(3));
			
			abBank.setCellValue(bank.getBankName());
			abIBAN.setCellValue(FormatIBAN(bank.getIban()));
			abBIC.setCellValue(bank.getBic());
			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			try {
				ZxingQR.makeLinkQR(DataExportHelper.getTextOrderConfirm().get(4));
			} catch (WriterException e) {
				logger.error("makeLinkQR(arrAbContent[3][5]) - " + e);
			}
			//#######################################################################
			// erzeugten QR Code als png-Datei einlesen
			//#######################################################################
			try (FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "\\link.png")) {
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
				is.close();
				XSSFCreationHelper helper = wb.getCreationHelper();
				Drawing<?> drawing = ws.createDrawingPatriarch(); //POI Patriarch erstellen als Container, Bildelement hinzufügen
				XSSFClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(4); //obere linke Ecke festlegen
				anchor.setRow1(36);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.resize(0.9, 0.9); //Bild im Faktor 0,9x0,9 zoomen
			} catch (IOException e) {
				logger.error("abExport(String sNr) - " + e);
			}
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("abExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("abExport(String sNr) - " + e);
		}
		//#######################################################################
		// Datei link.png wieder löschen
		//#######################################################################
		File qrFile = new File(System.getProperty("user.dir") + "\\link.png");
		if(qrFile.delete())
		{

		}else {
			logger.error("reExport(String sNr) - link.png konnte nicht gelöscht werden");
		}
		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		SaveAsPdf.toPDF(sExcelOut, sPdfOut);
		SaveAsPdf.setPdfMetadata(sNr.replace("AN", "AB"), "AB", sPdfOut);

		boolean bLockedPDF = isLocked(sPdfOut);
		while(bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}

		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		
		String PdfNamePath = sPdfOut;
		File PdfFn = new File(PdfNamePath);
		String PdfName = PdfFn.getName();
		
		FileStoreRepository fileStoreRepository = new FileStoreRepository();
		FileStore fileStore = fileStoreRepository.findById(angebot.getIdNummer()); // Tabelleneintrag mit Hibernate lesen
		
		fileStore.setAbFileName(PdfName);
		
		Path PdfPath = Paths.get(PdfNamePath);
		fileStore.setAbPdfFile(Files.readAllBytes(PdfPath)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.update(fileStore); // Datei in DB speichern
		
		//#######################################################################
		// Status des Angebots ändern
		//#######################################################################
		
		angebot.setState(angebot.getState() + 100); // Zustand bestätigt setzen
		AngebotRepository angebotRepository = new AngebotRepository();
		angebotRepository.update(angebot);

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
			logger.error("reExport(String sNr) - xlsx-Datei konnte nicht gelöscht werden");
		}
	}

}

