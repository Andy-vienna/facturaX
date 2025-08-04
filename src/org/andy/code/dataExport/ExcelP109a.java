package org.andy.code.dataExport;

import static org.andy.toolbox.misc.Tools.isLocked;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.andy.code.main.LoadData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelP109a {
	
	private static final Logger logger = LogManager.getLogger(ExcelP109a.class);
	
	static String sExcelIn = null, sExcelOut = null, sPdfOut = null;
	
	private static final int COLUMN_A = 0;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public static void ExportP109a(ArrayList<BigDecimal> listContent) {
        setData(listContent);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setData(ArrayList<BigDecimal> listContent) {
		
		sExcelIn = LoadData.getTplP109a();
		sExcelOut = LoadData.getWorkPath() + "\\Mitteilung_nach_P109a_" + LoadData.getStrAktGJ() + ".xlsx";
		sPdfOut = LoadData.getWorkPath() + "\\Mitteilung_nach_P109a_" + LoadData.getStrAktGJ() + ".pdf";
		
		//#######################################################################
		// Rechnungs-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("EA-Rechnung");
		
			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			Cell owner       = ws.getRow( 0).getCell(COLUMN_A); //Name
			Cell taxID       = ws.getRow( 0).getCell(COLUMN_C); //Steuernummer
			Cell year        = ws.getRow( 5).getCell(COLUMN_D); //Wirtschaftsjahr
			
			Cell P109In      = ws.getRow( 9).getCell(COLUMN_D); //Einkünfte aus selbstständiger Arbeit
			Cell P109SVSQ1   = ws.getRow(11).getCell(COLUMN_C); //SV-Beiträge 1. Quartal
			Cell P109SVSQ2   = ws.getRow(12).getCell(COLUMN_C); //SV-Beiträge 2. Quartal
			Cell P109SVSQ3   = ws.getRow(13).getCell(COLUMN_C); //SV-Beiträge 3. Quartal
			Cell P109SVSQ4   = ws.getRow(14).getCell(COLUMN_C); //SV-Beiträge 4. Quartal
			Cell P109SVTotal = ws.getRow(14).getCell(COLUMN_D); //SV-Beiträge Summe
			
			Cell P109OeffiP  = ws.getRow(15).getCell(COLUMN_D); //50% Öffi-Pauschale
			Cell P109APau    = ws.getRow(16).getCell(COLUMN_D); //großes Arbeitsplatzpauschale
			
			Cell P109Exp     = ws.getRow(17).getCell(COLUMN_D); //Betriebsausgaben
			Cell P109ZSum    = ws.getRow(18).getCell(COLUMN_D); //Zwischensumme
			
			Cell P109GWB     = ws.getRow(19).getCell(COLUMN_D); //Gewinnfreibetrag
			Cell P109Erg     = ws.getRow(20).getCell(COLUMN_D); //Ergebnis
			
			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################

			ArrayList<String> editOwner = new ArrayList<>();
		    Footer footer = ws.getFooter();

			editOwner = DataExportHelper.ownerData();

			// Schrift: Arial 9, Farbe: Grau 50% (#7F7F7F)
			String style = "&\"Arial,Regular\"&9&K7F7F7F";

			footer.setLeft(style + DataExportHelper.getFooterLeft());
			footer.setCenter(style + DataExportHelper.getFooterCenter());

			XSSFRichTextString OwnerText = new XSSFRichTextString();

			for (int i = 0; i < 4; i++) {
				String part = editOwner.get(i);
				XSSFFont font = wb.createFont();

				if (i == 0) {
					font.setFontName("Arial");
					font.setFontHeightInPoints((short) 16);
					font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
				} else {
					font.setFontName("Arial");
					font.setFontHeightInPoints((short) 11);
					font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
				}
				OwnerText.append(part, font);
			}
			
			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			owner.setCellValue(OwnerText); //Name
			taxID.setCellValue("Steuernummer: " + DataExportHelper.getSteuerNummer()); //Steuernummer
			year.setCellValue(LoadData.getStrAktGJ()); //Wirtschaftsjahr
			
			P109In.setCellValue(listContent.get(0).doubleValue()); //Einkünfte aus selbstständiger Arbeit
			P109SVSQ1.setCellValue(listContent.get(1).doubleValue()); //SV-Beiträge 1. Quartal
			P109SVSQ2.setCellValue(listContent.get(2).doubleValue()); //SV-Beiträge 2. Quartal
			P109SVSQ3.setCellValue(listContent.get(3).doubleValue()); //SV-Beiträge 3. Quartal
			P109SVSQ4.setCellValue(listContent.get(4).doubleValue()); //SV-Beiträge 4. Quartal
			P109SVTotal.setCellValue(listContent.get(5).doubleValue()); //SV-Beiträge Summe
			
			P109OeffiP.setCellValue(listContent.get(6).doubleValue()); //50% Öffi-Pauschale
			P109APau.setCellValue(listContent.get(7).doubleValue()); //großes Arbeitsplatzpauschale
			P109Exp.setCellValue(listContent.get(8).doubleValue()); //Betriebsausgaben
			P109ZSum.setCellValue(listContent.get(9).doubleValue()); //Zwischensumme
			
			P109GWB.setCellValue(listContent.get(10).doubleValue()); //Gewinnfreibetrag
			P109Erg.setCellValue(listContent.get(11).doubleValue()); //Ergebnis
			
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("reExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("reExport(String sNr) - " + e);
		}
		
		//#######################################################################
		// PDF-A1 Datei erzeugen
		//#######################################################################
		SaveAsPdf.toPDF(sExcelOut, sPdfOut);
		
		//#######################################################################
		// Ursprungs-Excel löschen
		//#######################################################################
		boolean bLockedxlsx = isLocked(sExcelOut);
		while(bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		if(xlFile.delete()) {

		}else {
			logger.error("§109a Mitteilung - xlsx-Datei konnte nicht gelöscht werden");
		}
	}
}
