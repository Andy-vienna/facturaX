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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyProductive.FileStore;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.andy.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.code.eRechnung.CreateXRechnungXML;
import org.andy.code.eRechnung.CreateZUGFeRDpdf;
import org.andy.code.main.LoadData;
import org.andy.code.qr.ZxingQR;
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

public class ExcelBill{

	private static final Logger logger = LogManager.getLogger(ExcelBill.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_E = 4;
	private static final int COLUMN_F = 5;

	private static final String ZUGFeRD = "ZUGFeRD";
	private static final String XRECHNUNG = "XRechnung";
	
	private static String[] sReTxt = new String[12];
	private static double[] dAnz = new double[12];
	private static double[] dEp = new double[12];
	
	private static String taxNote;

	//###################################################################################################################################################
	// Rechnung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void reExport(String sNr) throws Exception {
		
		String sExcelIn = LoadData.getTplBill();
		String sExcelOut = LoadData.getWorkPath() + "Rechnung_" + sNr + ".xlsx";
		String sPdfOut = LoadData.getWorkPath() + "Rechnung_" + sNr + ".pdf";

		DecimalFormat formatter = new DecimalFormat("#.##");

		final Cell rePos[] = new Cell[12];
		final Cell reText[] = new Cell[12];
		final Cell reAnz[] = new Cell[12];
		final Cell reEPreis[] = new Cell[12];
		final Cell reGPreis[] = new Cell[12];

		Rechnung rechnung = DataExportHelper.loadRechnung(sNr);
		Kunde kunde = DataExportHelper.kundeData(rechnung.getIdKunde());
		Bank bank = DataExportHelper.bankData(rechnung.getIdBank());
		DataExportHelper.textData();

		//#######################################################################
		// Rechnungs-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Rechnung");

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
			Cell reAdress = ws.getRow(4).getCell(COLUMN_B); //Name und Anschrift
			Cell reDate = ws.getRow(4).getCell(COLUMN_F); //Rechnungsdatum
			Cell reNr = ws.getRow(5).getCell(COLUMN_F); //Rechnungsnummer
			Cell reLZ = ws.getRow(6).getCell(COLUMN_F); //Leistungszeitraum
			Cell reUID = ws.getRow(7).getCell(COLUMN_F); //UID
			Cell reDuty = ws.getRow(8).getCell(COLUMN_F); //Ansprechpartner
			Cell reRef = ws.getRow(9).getCell(COLUMN_F); //Kundenreferenz
			
			for(int i = 0; i < rechnung.getAnzPos().intValue(); i++ ) { //Rechnungspositionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				rePos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				reText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				reAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				reEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				reGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}
			
			Cell reNetto = ws.getRow(28).getCell(COLUMN_F); //Nettosumme, Steuersatz, USt., Gesamtsumme
			Cell reUstSatz = ws.getRow(29).getCell(COLUMN_F);
			Cell reUSt = ws.getRow(30).getCell(COLUMN_F);
			Cell reBrutto = ws.getRow(31).getCell(COLUMN_F);
			Cell reText1 = ws.getRow(35).getCell(COLUMN_A); //Rechnungstexte (Steuerhinweis, Zahlungsziel)
			Cell reText2 = ws.getRow(37).getCell(COLUMN_A);
			Cell reBank = ws.getRow(46).getCell(COLUMN_E); //Bankverbindung E47 - E49: Bankname
			Cell reIBAN = ws.getRow(47).getCell(COLUMN_E); //IBAN
			Cell reBIC = ws.getRow(48).getCell(COLUMN_E); //BIC
			
			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			reAdress.setCellValue(kunde.getName() + "\n" + kunde.getStrasse() + "\n" + kunde.getPlz() + " " +
					kunde.getOrt() + ", " + kunde.getLand().toUpperCase());
			reDate.setCellValue(rechnung.getDatum().toString());
			reNr.setCellValue(rechnung.getIdNummer());
			reLZ.setCellValue(rechnung.getlZeitr());
			reUID.setCellValue(kunde.getUstid());
			reDuty.setCellValue(kunde.getPronomen() + " " + kunde.getPerson());
			reRef.setCellValue(rechnung.getRef());
			
			for(int i = 0; i < rechnung.getAnzPos().intValue(); i++ ) {
				rePos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Rechnung.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(rechnung);
		            BigDecimal menge = (BigDecimal) Rechnung.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(rechnung);
		            BigDecimal ep = (BigDecimal) Rechnung.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(rechnung);
		            sReTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            reText[i].setCellValue(art);
					reAnz[i].setCellValue(menge.doubleValue());
					reEPreis[i].setCellValue(ep.doubleValue());
					reGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}

			reNetto.setCellValue(rechnung.getNetto().doubleValue());
			reUstSatz.setCellValue(kunde.getTaxvalue() + "%");
			reUSt.setCellValue(rechnung.getUst().doubleValue());
			reBrutto.setCellValue(rechnung.getBrutto().doubleValue());
			
			if(rechnung.getRevCharge() == 0) {
				taxNote = DataExportHelper.getTextUSt().get(0); // Steuerhinweis
			} else {
				taxNote = DataExportHelper.getTextUSt().get(1); // Steuerhinweis Reverse Charge
			}
			reText1.setCellValue(taxNote); // Steuerhinweis
			
			if(kunde.getZahlungsziel().equals("0")) {
				reText2.setCellValue(DataExportHelper.getTextZahlZiel().get(0)); // Zahlungsziel
			} else {
				reText2.setCellValue(DataExportHelper.getTextZahlZiel().get(1).replace("{Tage}", kunde.getZahlungsziel())); // Zahlungsziel
			}
			
			reBank.setCellValue(bank.getBankName());
			reIBAN.setCellValue(FormatIBAN(bank.getIban()));
			reBIC.setCellValue(bank.getBic());
			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			String sBrutto = formatter.format(rechnung.getBrutto());
			try {
				ZxingQR.makeQR(LoadData.getStrQRschema(), bank.getKtoName(), bank.getIban(), bank.getBic(), sBrutto.replace(",", "."), sNr);
			} catch (WriterException e) {
				logger.error("makeQR(...) - " + e);
			} catch (IOException e) {
				logger.error("makeQR(...) - " + e);
			}
			//#######################################################################
			// erzeugten QR Code als png-Datei einlesen
			//#######################################################################
			try (FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "\\qr.png")) {
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
				logger.error("reExport(...) - " + e);
			}
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("reExport(...) - " + e);
		} catch (IOException e) {
			logger.error("reExport(...) - " + e);
		}
		//#######################################################################
		// Datei qr.png wieder löschen
		//#######################################################################
		File qrFile = new File(System.getProperty("user.dir") + "\\qr.png");
		if(qrFile.delete())
		{

		}else {
			logger.error("reExport(String sNr) - qr.png konnte nicht gelöscht werden");
		}
		//#######################################################################
		// PDF-A1 Datei erzeugen
		//#######################################################################
		SaveAsPdf.toPDF(sExcelOut, sPdfOut);

		//#######################################################################
		// eRechnung erstellen nach hinterlegtem Format (ZUGFeRD oder XRechnung)
		//#######################################################################
		String sFile = null; boolean bResult = false;
		
		switch(kunde.geteBillTyp()) {
		case ZUGFeRD:
			sFile = LoadData.getWorkPath() + "Rechnung_" + sNr + "_ZUGFeRD.pdf";

			try {
				CreateZUGFeRDpdf.generateZUGFeRDpdf(rechnung, bank, kunde, DataExportHelper.getOwner(), sPdfOut, sFile);
			} catch (ParseException | IOException e) {
				logger.error("error generating zugferd - " + e);
			}
			bResult = true;
			break;

		case XRECHNUNG:
			sFile = LoadData.getWorkPath() + "Rechnung_" + sNr + "_XRechnung.xml";

			try {
				CreateXRechnungXML.generateXRechnungXML(rechnung, bank, kunde, DataExportHelper.getOwner(), sFile);
			} catch (ParseException | IOException e) {
				logger.error("error generating xrechnung - " + e);
			}
			bResult = true;
			break;

		default:
			return;
		}
		
		if (bResult) {
			boolean bLockedoutput = isLocked(sFile);
			while(bLockedoutput) {
				System.out.println("warte auf Datei ...");
			}
			
			//#######################################################################
			// Datei in DB speichern
			//#######################################################################
			
			String FileNamePath = sFile;
			File fn = new File(FileNamePath);
			String FileName = fn.getName();
			
			FileStoreRepository fileStoreRepository = new FileStoreRepository();
			FileStore fileStore = new FileStore();
			
			fileStore.setIdNummer(rechnung.getIdNummer());
			fileStore.setYear(rechnung.getJahr());
			fileStore.setReFileName(FileName);
			
			Path path = Paths.get(FileNamePath);
			fileStore.setRePdfFile(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
			
			fileStoreRepository.save(fileStore); // Datei in DB speichern
			
			//#######################################################################
			// Status der Rechnung ändern
			//#######################################################################
			
			rechnung.setState(rechnung.getState() + 10); // Zustand gedruckt setzen
			RechnungRepository rechnungRepository = new RechnungRepository();
			rechnungRepository.update(rechnung);
			
			//#######################################################################
			// Ursprungs-Excel und -pdf löschen
			//#######################################################################
			boolean bLockedpdf = isLocked(sPdfOut);
			boolean bLockedxlsx = isLocked(sExcelOut);
			boolean bLockedout = isLocked(sFile);
			while(bLockedpdf || bLockedxlsx || bLockedout) {
				System.out.println("warte auf Dateien ...");
			}
			File xlFile = new File(sExcelOut);
			File pdFile = new File(sPdfOut);
			File outFile = new File(sFile);
			if(xlFile.delete() && pdFile.delete() && outFile.delete()) {

			}else {
				logger.error("reExport(...) - Dateien konnten nicht gelöscht werden");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Problem beim Drucken der Rechnung", "Rechnung drucken", JOptionPane.INFORMATION_MESSAGE);
			logger.error("Rechnung nicht gedruckt !");
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static String[] getsReTxt() {
		return sReTxt;
	}

	public static double[] getdAnz() {
		return dAnz;
	}

	public static double[] getdEp() {
		return dEp;
	}

	public static String getTaxNote() {
		return taxNote;
	}

}

