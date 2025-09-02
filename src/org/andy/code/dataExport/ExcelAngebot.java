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
import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyProductive.Angebot;
import org.andy.code.dataStructure.entitiyProductive.FileStore;
import org.andy.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.code.main.Einstellungen;
import org.andy.code.qr.ZxingQR;

public class ExcelAngebot{

	private static final Logger logger = LogManager.getLogger(ExcelAngebot.class);

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
	// Angebot erzeugen und pdf exportieren
	//###################################################################################################################################################

	public static void anExport(String sNr) throws Exception {
		String revNr = null;
		String sExcelIn = Einstellungen.getTplOffer();
		if(sNr.contains("/")) {
			revNr = sNr.replace("/", "rev");
		} else {
			revNr = sNr;
		}
		String sExcelOut = Einstellungen.getWorkPath() + "Angebot_" + revNr + ".xlsx";
		String sPdfOut = Einstellungen.getWorkPath() + "Angebot_" + revNr + ".pdf";
		String sPdfDesc = Einstellungen.getWorkPath() + "Leistungsbeschreibung_Angebot_" + revNr + ".pdf";

		final Cell anPos[] = new Cell[13];
		final Cell anText[] = new Cell[13];
		final Cell anAnz[] = new Cell[13];
		final Cell anEPreis[] = new Cell[13];
		final Cell anGPreis[] = new Cell[13];

		Angebot angebot = ExcelHelper.loadAngebot(sNr);
		Kunde kunde = ExcelHelper.kundeData(angebot.getIdKunde());
		String adressat = ExcelHelper.kundeAnschrift(angebot.getIdKunde());
		Bank bank = ExcelHelper.bankData(angebot.getIdBank());
		ExcelHelper.textData();

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
			Cell anAdress = ws.getRow(4).getCell(COLUMN_B); //Name und Anschrift
			Cell anDate = ws.getRow(4).getCell(COLUMN_F); //Angebotsdatum
			Cell anNr = ws.getRow(5).getCell(COLUMN_F); //Angebotsnummer
			Cell anRef = ws.getRow(6).getCell(COLUMN_F); //Angebotsreferenz
			Cell anDuty = ws.getRow(8).getCell(COLUMN_F); //Ansprechpartner
			Cell anTextPre1 = ws.getRow(11).getCell(COLUMN_A); //Einleitungstext
			Cell anTextPre2 = ws.getRow(12).getCell(COLUMN_A); //Einleitungstext
			Cell anTextPre3 = ws.getRow(13).getCell(COLUMN_A); //Einleitungstext
			Cell anTextPre4 = ws.getRow(14).getCell(COLUMN_A); //Einleitungstext
			ws.getRow(13).getCell(COLUMN_A);
			
			for(int i = 0; i < angebot.getAnzPos().intValue(); i++ ) { //Angebotspositionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				anPos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				anText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				anAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				anEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				anGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}
			Cell anSumme = ws.getRow(28).getCell(COLUMN_F); //Gesamtsumme
			Cell anText1 = ws.getRow(32).getCell(COLUMN_A); //Angebotstexte
			Cell anText2 = ws.getRow(35).getCell(COLUMN_A);
			Cell anText3 = ws.getRow(36).getCell(COLUMN_A);
			Cell anText4 = ws.getRow(38).getCell(COLUMN_A);
			Cell anText5 = ws.getRow(39).getCell(COLUMN_A);
			Cell anText6 = ws.getRow(43).getCell(COLUMN_A);
			Cell anText7 = ws.getRow(46).getCell(COLUMN_A);
			Cell anBank = ws.getRow(46).getCell(COLUMN_E); //Bankverbindung E47 - E49: Bankname
			Cell anIBAN = ws.getRow(47).getCell(COLUMN_E); //IBAN
			Cell anBIC = ws.getRow(48).getCell(COLUMN_E); //BIC
			
			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			anAdress.setCellValue(adressat); // Kundenanschrift
			anDate.setCellValue(angebot.getDatum().toString());
			anNr.setCellValue(angebot.getIdNummer());
			anDuty.setCellValue(kunde.getPronomen() + " " + kunde.getPerson());
			anRef.setCellValue(angebot.getRef());
			
			if(kunde.getPronomen().equals("Herr")) {
				anTextPre1.setCellValue(ExcelHelper.getTextAngebot().get(8).replace("{Anrede}", "r " + kunde.getPronomen() + " " + kunde.getPerson()));
			}else if(kunde.getPronomen().equals("Frau")) {
				anTextPre1.setCellValue(ExcelHelper.getTextAngebot().get(8).replace("{Anrede}", " " + kunde.getPronomen() + " " + kunde.getPerson()));
			}else {
				anTextPre1.setCellValue(ExcelHelper.getTextAngebot().get(9));
			}
			
			int idx = angebot.getIdNummer().indexOf('/');
			String basis = idx < 0 ? angebot.getIdNummer() : angebot.getIdNummer().substring(0, idx); // bei Revision: urspr. Angebotsnummer
			Integer rev = idx < 0 ? 0 : Integer.parseInt(angebot.getIdNummer().substring(idx + 1)); // bei Revision: Revisionsnummer
			
			if(angebot.getIdNummer().contains("/")) {
				anTextPre2.setCellValue(ExcelHelper.getTextAngebot().get(12).replace("{Revision}", "Revision " + rev));
				anTextPre3.setCellValue(ExcelHelper.getTextAngebot().get(13).replace("{Angebot-Nr}", basis));
				if(angebot.getPage2() == 1) {
					anTextPre4.setCellValue(ExcelHelper.getTextAngebot().get(11));
				} else {
					anTextPre4.setCellValue(" ");
				}
			} else {
				anTextPre2.setCellValue(ExcelHelper.getTextAngebot().get(10));
				if(angebot.getPage2() == 1) {
					anTextPre3.setCellValue(ExcelHelper.getTextAngebot().get(11));
					anTextPre4.setCellValue(" ");
				} else {
					anTextPre3.setCellValue(" ");
					anTextPre4.setCellValue(" ");
				}
			}
			
			for(int i = 0; i < angebot.getAnzPos().intValue(); i++ ) {
				anPos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Angebot.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal menge = (BigDecimal) Angebot.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal ep = (BigDecimal) Angebot.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(angebot);
		            sAnTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            anText[i].setCellValue(art);
					anAnz[i].setCellValue(menge.doubleValue());
					anEPreis[i].setCellValue(ep.doubleValue());
					anGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			
			anSumme.setCellValue(angebot.getNetto().doubleValue());
			anText1.setCellValue(ExcelHelper.getTextAngebot().get(0));
			anText2.setCellValue(ExcelHelper.getTextAngebot().get(1));
			anText3.setCellValue(ExcelHelper.getTextAngebot().get(2));
			anText4.setCellValue(ExcelHelper.getTextAngebot().get(3));
			anText5.setCellValue(ExcelHelper.getTextAngebot().get(4).replace("{OwnerName}", ExcelHelper.getKontaktName()));
			anText6.setCellValue(ExcelHelper.getTextAngebot().get(6));
			anText7.setCellValue(ExcelHelper.getTextAngebot().get(5).replace("{Tage}", kunde.getZahlungsziel()));
			
			anBank.setCellValue(bank.getBankName());
			anIBAN.setCellValue(FormatIBAN(bank.getIban()));
			anBIC.setCellValue(bank.getBic());
			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			try {
				ZxingQR.makeLinkQR(ExcelHelper.getTextAngebot().get(7));
			} catch (WriterException e) {
				logger.error("makeLinkQR(...); - " + e);
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
				logger.error("anExport(String sNr) - " + e);
			}
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("anExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("anExport(String sNr) - " + e);
		}
		//#######################################################################
		// Datei link.png wieder löschen
		//#######################################################################
		File qrFile = new File(System.getProperty("user.dir") + "\\link.png");
		if(qrFile.delete())
		{

		}else {
			logger.error("anExport(String sNr) - link.png konnte nicht gelöscht werden");
		}
		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		ErzeugePDF.toPDF(sExcelOut, sPdfOut);
		ErzeugePDF.setPdfMetadata(sNr, "AN", sPdfOut);

		boolean bLockedXLSX = isLocked(sExcelOut);
		boolean bLockedPDF = isLocked(sPdfOut);
		while(bLockedXLSX || bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}
		
		//#######################################################################
		// FileStore Entität instanzieren
		//#######################################################################
		FileStoreRepository fileStoreRepository = new FileStoreRepository();
		FileStore fileStore = new FileStore();
		
		fileStore.setIdNummer(angebot.getIdNummer()); // Angebotsnummer als Index für fileStore schreiben
		fileStore.setJahr(angebot.getJahr()); // Jahr in fileStore schreiben
		
		//#######################################################################
		// wenn erforderlich Leistungsbeschreibung.pdf erzeugen
		//#######################################################################
		if (angebot.getPage2() == 1) {
			ErzeugeLeistungsbeschreibung.doLeistungsbeschreibung(angebot, sPdfDesc);
			String DescNamePath = sPdfDesc;
			File DescFn = new File(DescNamePath);
			
			String DescName = DescFn.getName();
			fileStore.setAddFileName01(DescName); // Dateiname übergeben
			
			Path DescPath = Paths.get(DescNamePath);
			fileStore.setAddFile01(Files.readAllBytes(DescPath)); // ByteArray für Dateiinhalt
		}
		
		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		String PdfNamePath = sPdfOut;
		File PdfFn = new File(PdfNamePath);
		
		String PdfName = PdfFn.getName();
		fileStore.setAnFileName(PdfName);
		
		Path PdfPath = Paths.get(PdfNamePath);
		fileStore.setAnPdfFile(Files.readAllBytes(PdfPath)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.save(fileStore); // Datei(en) in DB speichern
		
		//#######################################################################
		// Status des Angebots ändern
		//#######################################################################
		
		angebot.setState(angebot.getState() + 10); // Zustand gedruckt setzen
		AngebotRepository angebotRepository = new AngebotRepository();
		angebotRepository.update(angebot);

		//#######################################################################
		// Ursprungs-Excel und -pdf löschen
		//#######################################################################
		boolean bLockedpdf = isLocked(sPdfOut);
		boolean bLockedDesc = isLocked(sPdfDesc);
		boolean bLockedxlsx = isLocked(sExcelOut);
		while(bLockedpdf || bLockedDesc || bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		File pdFile = new File(sPdfOut);
		File descFile = new File(sPdfDesc);
		if(xlFile.delete() && pdFile.delete() && descFile.delete()) {

		}else {
			logger.error("anExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}

}

