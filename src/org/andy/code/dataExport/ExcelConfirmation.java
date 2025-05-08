package org.andy.code.dataExport;

import static org.andy.toolbox.misc.Tools.FormatIBAN;
import static org.andy.toolbox.misc.Tools.isLocked;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.andy.code.main.LoadData;
import org.andy.code.qr.ZxingQR;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.main.JFoverview;
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

	private static String[][] arrYearOffer;
	public static int iNumData;
	public static int iNumKunde;
	public static String[][] arrAbContent = new String[30][15];

	private static final String TBL_FILE = "tbl_files";
	private static String sConn;

	private static final int START_ROW_OFFSET = 15;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_E = 4;
	private static final int COLUMN_F = 5;

	/* Kommentarblock
	 *
	 *###################################################################################################################################################
	 *Daten zusammensammeln und in ein globales Array ablegen:
	 *arrAnContent[0][0]				- Anzahl der enthaltenen Positionszeilen
	 *		   	  [1][1] - [1][7]		- Kundendaten (Kunde, Straße, PLZ, Ort, Land, Pronom, Ansprechpartner)
	 *			  [2][1]				- Datum
	 *			  [2][2]				- Nummer
	 *			  [2][3]				- Referenz
	 *			  [2][4]				- Angebotsnummer
	 *			  [3][1] - [3][7]		- Texte
	 *			  [4][1] - [4][3]		- Bankverbindung (Bankname, IBAN, BIC)
	 *			  [10][1] - [10][4]		- 1. Zeile (Text, Anzahl, E-Preis, G-Preis)
	 *			  .......	- .......	- 2. - 11. Zeile
	 *			  [21][1] - [21][4]		- 12. Zeile
	 *
	 */
	/* Excel-Vorlage 'template-order-confirm.xlsx' - v2.05 - Struktur:
	 *
	 *Anschriftsfeld: 		B5
	 *Datum: 				F5
	 *Nummer: 				F6
	 *Referenz: 			F7
	 *Ansprechpartner: 		F9
	 *Positionen:			B/C/D/F17 - B/C/D/F28 (Bezeichnung, Menge, E-Preis, G-Preis)
	 *Summe: 				F29
	 *Texte: 				B35, B38
	 *AGB Hinweis:			B44
	 *Zahlungsbedingungen:	B47
	 *Bankverbindung: 		E47, E48, E49 (Bank Name, IBAN, BIC)
	 *###################################################################################################################################################
	 */

	public static void abCollectData(String sNr) throws Exception {

		int x = 0;
		int y = 0;
		int n = 0;
		int m = 0;

		arrYearOffer = JFoverview.getArrYearOffer();

		n = 1;
		for(n = 1; (n-1) < Integer.valueOf(arrYearOffer[0][0]); n++) {
			if(arrYearOffer[n][1].equals(sNr)) {
				iNumData = n; // Datensatznummer des angeforderten Angebots
			}
		}
		arrAbContent[0][0] = arrYearOffer[iNumData][11]; // Anzahl Zeilen für Angebot
		String tmp = arrYearOffer[iNumData][8];

		for (m = 0; m < SQLmasterData.getAnzKunde(); m++) {
			List<String> kunde = SQLmasterData.getArrListKunde().get(m);

			if (kunde != null && !kunde.isEmpty() && kunde.get(0) != null && kunde.get(0).equals(tmp)) {
				iNumKunde = m;
				break;
			}
		}
		if (m >= SQLmasterData.getAnzKunde()) {
			System.out.println("Fehler: Kunde nicht gefunden.");
			return; // Verhindert, dass ein ungültiger Index verwendet wird
		}

		List<String> kunde = SQLmasterData.getArrListKunde().get(m);

		// Sicherheitsprüfung, ob Kunde null oder zu klein ist
		if (kunde == null || kunde.size() < 15) {
			System.out.println("Fehler: Kundendaten unvollständig.");
			return;
		}

		// Kopiere die Kundendaten sicher in arrReContent
		for (x = 0; x < 15; x++) {
			arrAbContent[1][x] = kunde.get(x) != null ? kunde.get(x) : ""; // Falls null, ersetze mit leerem String
		}

		arrAbContent[2][1] = arrYearOffer[iNumData][6]; // Datum
		arrAbContent[2][2] = sNr.replace("AN", "AB"); // Nummer
		arrAbContent[2][3] = arrYearOffer[iNumData][7]; // Referenz
		arrAbContent[2][4] = sNr; // Angebotsnummer
		x = 1;
		y = 12;
		while(x < (Integer.valueOf(arrAbContent[0][0]) + 1)) {
			arrAbContent[x + 9][1] = arrYearOffer[iNumData][y];
			arrAbContent[x + 9][2] = arrYearOffer[iNumData][y + 1];
			arrAbContent[x + 9][3] = arrYearOffer[iNumData][y + 2];
			double anz = Double.parseDouble(arrAbContent[x + 9][2]);
			double ep = Double.parseDouble(arrAbContent[x + 9][3]);
			double gp = anz * ep;
			arrAbContent[x + 9][4] = String.valueOf(gp);
			x = x + 1;
			y = y + 3;
		}

		ArrayList<ArrayList<String>> textList = SQLmasterData.getArrListText();
		if (textList != null && textList.size() >= 5) {
			for (x = 0; x < 5; x++) { // Texte
				ArrayList<String> text = textList.get(x);
				if (text != null && text.size() > 5) {
					arrAbContent[3][x + 1] = text.get(5);
				} else {
					arrAbContent[3][x + 1] = "Fehlender Text"; // Fallback
				}
			}
		} else {
			System.out.println("Fehler: Textliste ist null oder hat zu wenige Einträge.");
		}

		// Sicherstellen, dass arrYearOffer gültige Daten enthält
		if (arrYearOffer != null && arrYearOffer.length > iNumData && arrYearOffer[iNumData] != null &&
				arrYearOffer[iNumData].length > 9 && arrYearOffer[iNumData][9] != null) {

			String tmpBank = arrYearOffer[iNumData][9];

			ArrayList<ArrayList<String>> bankList = SQLmasterData.getArrListBank();
			if (bankList != null && bankList.size() >= SQLmasterData.getAnzBank()) {

				for (m = 0; m < SQLmasterData.getAnzBank(); m++) {
					ArrayList<String> bank = bankList.get(m);

					if (bank != null && bank.size() > 4 && bank.get(0) != null && bank.get(0).equals(tmpBank)) {
						arrAbContent[4][1] = bank.get(1) != null ? bank.get(1) : "";
						arrAbContent[4][2] = bank.get(2) != null ? bank.get(2) : "";
						arrAbContent[4][3] = bank.get(3) != null ? bank.get(3) : "";
						arrAbContent[4][4] = bank.get(4) != null ? bank.get(4) : "";
						break; // Beende die Schleife, wenn die Bank gefunden wurde
					}
				}
			} else {
				System.out.println("Fehler: Bankliste ist null oder hat weniger Einträge als erwartet.");
			}
		} else {
			System.out.println("Fehler: arrYearOffer ist ungültig oder enthält keine Bankdaten.");
		}

	}

	//###################################################################################################################################################
	// Angebot erzeugen und auf Wunsch als pdf exportieren
	//###################################################################################################################################################

	public static void abExport(String sNr) throws Exception {

		String sExcelIn = LoadData.getTplConfirmation();
		String sExcelOut = LoadData.getWorkPath() + "\\Auftragsbestätigung_" + sNr.replace("AN", "AB") + ".xlsx";
		String sPdfOut = LoadData.getWorkPath() + "\\Auftragsbestätigung_" + sNr.replace("AN", "AB") + ".pdf";

		final Cell abPos[] = new Cell[13];
		final Cell abText[] = new Cell[13];
		final Cell abAnz[] = new Cell[13];
		final Cell abEPreis[] = new Cell[13];
		final Cell abGPreis[] = new Cell[13];

		double dSumme = 0;

		abCollectData(sNr); //Daten aufbereiten

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

			Footer footer = ws.getFooter();
			ArrayList<String> Owner = SQLmasterData.getOwner();
			ArrayList<String> editOwner = new ArrayList<>();

			editOwner.add(Owner.get(0) + "\n");
			editOwner.add(Owner.get(1) + " | ");
			editOwner.add(Owner.get(2) + " ");
			editOwner.add(Owner.get(3) + " | ");
			editOwner.add(Owner.get(4).toUpperCase() + "\n");
			editOwner.add(Owner.get(5));
			String senderOwner = Owner.get(0) + ", " + Owner.get(1) + ", " + Owner.get(2) + " " + Owner.get(3);

			// Schrift: Arial 9, Farbe: Grau 50% (#7F7F7F)
			String style = "&\"Arial,Regular\"&9&K7F7F7F";

			footer.setLeft(style + Owner.get(0) + " | Bearbeiter: " + LoadData.getStrAktUser());
			footer.setCenter(style + Owner.get(7) + " | " + Owner.get(8));

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
			for(int i = 1; i < (Integer.parseInt(arrAbContent[0][0]) + 1); i++ ) { //Positionen B, C, D, F Zeile 17-28
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
			abAdress.setCellValue(arrAbContent[1][1] + "\n" + arrAbContent[1][2] + "\n" + arrAbContent[1][3] + " " +
					arrAbContent[1][4] + ", " + arrAbContent[1][5]);
			abDate.setCellValue(arrAbContent[2][1]);
			abNr.setCellValue(arrAbContent[2][2]);
			abRef.setCellValue(arrAbContent[2][3]);
			abDuty.setCellValue(arrAbContent[1][6] + " " + arrAbContent[1][7]);
			for(int i = 1; i < (Integer.parseInt(arrAbContent[0][0]) + 1); i++ ) {
				abPos[i].setCellValue(String.valueOf(i));
				abText[i].setCellValue(arrAbContent[(i + 9)][1]);
				abAnz[i].setCellValue(Double.parseDouble(arrAbContent[(i + 9)][2]));
				abEPreis[i].setCellValue(Double.parseDouble(arrAbContent[(i + 9)][3]));
				abGPreis[i].setCellValue(Double.parseDouble(arrAbContent[(i + 9)][4]));
				dSumme = dSumme + Double.parseDouble(arrAbContent[(i + 9)][4]);
			}
			abSumme.setCellValue(dSumme);
			String fillText = ReplaceText.doReplace(arrAbContent[3][1], "none", arrAbContent[2][4], JFconfirmA.getsConfNr(), JFconfirmA.getsConfDatum(), "none", "none", "none", "none", "none", "none");
			abText1.setCellValue(fillText);
			abText2.setCellValue(ReplaceText.doReplace(arrAbContent[3][2], "none", "none", "none", JFconfirmA.getsConfStart(), "none", "none", "none", "none", "none", "none"));
			abText3.setCellValue(ReplaceText.doReplace(arrAbContent[3][3], "none", "none", "none", "none", arrAbContent[1][11], "none", "none", "none", "none", "none"));
			abText4.setCellValue(arrAbContent[3][4]);
			abBank.setCellValue(arrAbContent[4][1]);
			abIBAN.setCellValue(FormatIBAN(arrAbContent[4][2]));
			abBIC.setCellValue(arrAbContent[4][3]);
			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			try {
				ZxingQR.makeLinkQR(arrAbContent[3][5]);
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

		try {

			String FileNamePath = sPdfOut;
			File fn = new File(FileNamePath);
			String FileName = fn.getName();

			String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
			String sSQLStatementB = "UPDATE " + tblName + " SET [ABFIleName] = '" + FileName + "',[ABpdfFIle] = (SELECT * FROM OPENROWSET(BULK '"
					+ FileNamePath + "', SINGLE_BLOB) AS DATA) WHERE [IdNummer] = '" + sNr + "'";

			sqlUpdate(sConn, sSQLStatementB);

		} catch (SQLException | ClassNotFoundException e) {
			logger.error("error inserting offer confirmation files into database - " + e);
		}

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

	public static void setsConn(String sConn) {
		ExcelConfirmation.sConn = sConn;
	}
}

