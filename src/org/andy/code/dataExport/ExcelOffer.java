package org.andy.code.dataExport;

import static org.andy.toolbox.misc.Tools.FormatIBAN;
import static org.andy.toolbox.misc.Tools.isLocked;
import static org.andy.toolbox.sql.Insert.sqlInsert;
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

import org.andy.code.entity.SQLmasterData;
import org.andy.code.main.LoadData;
import org.andy.code.qr.ZxingQR;
import org.andy.gui.main.JFoverview;

public class ExcelOffer{

	private static final Logger logger = LogManager.getLogger(ExcelOffer.class);

	private static String[][] arrYearOffer;
	private static int iNumData;
	public static int iNumKunde;
	private static String[][] arrAnContent = new String[30][20];

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
	 *arrAnContent[0][0]				- Anzahl der im Angebot enthaltenen Positionszeilen
	 *		   	  [1][1] - [1][7]		- Kundendaten (Kunde, Straße, PLZ, Ort, Land, Pronom, Ansprechpartner)
	 *			  [2][1]				- Angebotsdatum
	 *			  [2][2]				- Angebotsnummer
	 *			  [2][3]				- Angebotsreferenz
	 *			  [3][1] - [3][7]		- Angebotstexte (Angebotsgültigkeit, etc.)
	 *			  [4][1] - [4][3]		- Bankverbindung (Bankname, IBAN, BIC)
	 *			  [10][1] - [10][4]		- 1. Angebotszeile (Text, Anzahl, E-Preis, G-Preis)
	 *			  .......	- .......	- 2. - 11. Angebotszeile
	 *			  [21][1] - [21][4]		- 12. Angebotszeile
	 *
	 */
	/* Excel-Vorlage 'template-offer.xlsx' - v2.05 - Struktur:
	 *
	 *Anschriftsfeld: 		B5
	 *Angebotsdatum: 		F5
	 *Angebotsnummer: 		F6
	 *Angebotsfreferenz: 	F7
	 *Ansprechpartner: 	F9
	 *Angebotspositionen:	B/C/D/F17 - B/C/D/F28 (Bezeichnung, Menge, E-Preis, G-Preis)
	 *Angebotssumme: 		F29
	 *Angebotstexte: 		B33, B36, B37, B39, B40
	 *AGB Hinweis:			B44
	 *Zahlungsbedingungen:	B47
	 *Bankverbindung: 		E47, E48, E49 (Bank Name, IBAN, BIC)
	 *###################################################################################################################################################
	 */

	public static void anCollectData(String sNr) throws Exception {
		new ArrayList<>();
		new ArrayList<>();
		new ArrayList<>();
		int x = 0;
		int y = 0;
		int n = 0;
		int m = 0;

		arrYearOffer = JFoverview.getArrYearAN();

		n = 1;
		for(n = 1; (n-1) < Integer.valueOf(arrYearOffer[0][0]); n++) {
			if(arrYearOffer[n][1].equals(sNr)) {
				iNumData = n; // Datensatznummer des angeforderten Angebots
			}
		}
		arrAnContent[0][0] = arrYearOffer[iNumData][11]; // Anzahl Zeilen für Angebot
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
			arrAnContent[1][x] = kunde.get(x) != null ? kunde.get(x) : ""; // Falls null, ersetze mit leerem String
		}

		arrAnContent[2][1] = arrYearOffer[iNumData][6]; // Angebotssdatum
		arrAnContent[2][2] = sNr; // Angebotsnummer
		arrAnContent[2][3] = arrYearOffer[iNumData][7]; // Kundenreferenz
		x = 1;
		y = 12;
		while(x < (Integer.valueOf(arrAnContent[0][0]) + 1)) {
			arrAnContent[x + 9][1] = arrYearOffer[iNumData][y];
			arrAnContent[x + 9][2] = arrYearOffer[iNumData][y + 1];
			arrAnContent[x + 9][3] = arrYearOffer[iNumData][y + 2];
			double anz = Double.parseDouble(arrAnContent[x + 9][2]);
			double ep = Double.parseDouble(arrAnContent[x + 9][3]);
			double gp = anz * ep;
			arrAnContent[x + 9][4] = String.valueOf(gp);
			x = x + 1;
			y = y + 3;
		}

		ArrayList<ArrayList<String>> textList = SQLmasterData.getArrListText();
		if (textList != null && textList.size() >= 15) {
			for (x = 0; x < 15; x++) { // Angebotstexte
				ArrayList<String> text = textList.get(x);
				if (text != null && text.size() > 3) {
					arrAnContent[3][x + 1] = text.get(3);
				} else {
					arrAnContent[3][x + 1] = "Fehlender Text"; // Fallback für fehlende Daten
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
						arrAnContent[4][1] = bank.get(1) != null ? bank.get(1) : "";
						arrAnContent[4][2] = bank.get(2) != null ? bank.get(2) : "";
						arrAnContent[4][3] = bank.get(3) != null ? bank.get(3) : "";
						arrAnContent[4][4] = bank.get(4) != null ? bank.get(4) : "";
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

	public static void anExport(String sNr) throws Exception {

		String sExcelIn = LoadData.getTplOffer();
		String sExcelOut = LoadData.getWorkPath() + "\\Angebot_" + sNr + ".xlsx";
		String sPdfOut = LoadData.getWorkPath() + "\\Angebot_" + sNr + ".pdf";

		final Cell anPos[] = new Cell[13];
		final Cell anText[] = new Cell[13];
		final Cell anAnz[] = new Cell[13];
		final Cell anEPreis[] = new Cell[13];
		final Cell anGPreis[] = new Cell[13];

		double dSumme = 0;

		anCollectData(sNr); //Daten aufbereiten

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
			Cell anAdress = ws.getRow(4).getCell(COLUMN_B); //Name und Anschrift
			Cell anDate = ws.getRow(4).getCell(COLUMN_F); //Angebotsdatum
			Cell anNr = ws.getRow(5).getCell(COLUMN_F); //Angebotsnummer
			Cell anRef = ws.getRow(6).getCell(COLUMN_F); //Angebotsreferenz
			Cell anDuty = ws.getRow(8).getCell(COLUMN_F); //Ansprechpartner
			Cell anTextPre1 = ws.getRow(11).getCell(COLUMN_A); //Einleitungstext
			Cell anTextPre2 = ws.getRow(12).getCell(COLUMN_A); //Einleitungstext
			Cell anTextPre3 = ws.getRow(13).getCell(COLUMN_A); //Einleitungstext
			ws.getRow(13).getCell(COLUMN_A);
			for(int i = 1; i < (Integer.parseInt(arrAnContent[0][0]) + 1); i++ ) { //Angebotspositionen B, C, D, F Zeile 17-28
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
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			anAdress.setCellValue(arrAnContent[1][1] + "\n" + arrAnContent[1][2] + "\n" + arrAnContent[1][3] + " " +
					arrAnContent[1][4] + ", " + arrAnContent[1][5]);
			anDate.setCellValue(arrAnContent[2][1]);
			anNr.setCellValue(arrAnContent[2][2]);
			anRef.setCellValue(arrAnContent[2][3]);
			anDuty.setCellValue(arrAnContent[1][6] + " " + arrAnContent[1][7]);

			if(arrAnContent[1][6].equals("Herr")) {
				anTextPre1.setCellValue(arrAnContent[3][9].replace("{Anrede}", "r " + arrAnContent[1][6] + " " + arrAnContent[1][7]));
			}else if(arrAnContent[1][6].equals("Frau")) {
				anTextPre1.setCellValue(arrAnContent[3][9].replace("{Anrede}", " " + arrAnContent[1][6] + " " + arrAnContent[1][7]));
			}else {
				anTextPre1.setCellValue(arrAnContent[3][10]);
			}
			anTextPre2.setCellValue(arrAnContent[3][11]);
			if(arrYearOffer[iNumData][48].equals("true")) {
				anTextPre3.setCellValue(arrAnContent[3][12]);
			}else {
				anTextPre3.setCellValue("");
			}

			for(int i = 1; i < (Integer.parseInt(arrAnContent[0][0]) + 1); i++ ) {
				anPos[i].setCellValue(String.valueOf(i));
				anText[i].setCellValue(arrAnContent[(i + 9)][1]);
				anAnz[i].setCellValue(Double.parseDouble(arrAnContent[(i + 9)][2]));
				anEPreis[i].setCellValue(Double.parseDouble(arrAnContent[(i + 9)][3]));
				anGPreis[i].setCellValue(Double.parseDouble(arrAnContent[(i + 9)][4]));
				dSumme = dSumme + Double.parseDouble(arrAnContent[(i + 9)][4]);
			}
			anSumme.setCellValue(dSumme);
			anText1.setCellValue(arrAnContent[3][1]);
			anText2.setCellValue(arrAnContent[3][2]);
			anText3.setCellValue(arrAnContent[3][3]);
			anText4.setCellValue(arrAnContent[3][4]);
			anText5.setCellValue(ReplaceText.doReplace(arrAnContent[3][5], "none", "none", "none", "none", "none", "none", "none", "none", "none", SQLmasterData.getsArrOwner()[7]));
			anText6.setCellValue(arrAnContent[3][7]);
			anText7.setCellValue(ReplaceText.doReplace(arrAnContent[3][6], "none", "none", "none", "none", arrAnContent[1][11], "none", "none", "none", "none", "none"));
			anBank.setCellValue(arrAnContent[4][1]);
			anIBAN.setCellValue(FormatIBAN(arrAnContent[4][2]));
			anBIC.setCellValue(arrAnContent[4][3]);
			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			try {
				ZxingQR.makeLinkQR(arrAnContent[3][8]);
			} catch (WriterException e) {
				logger.error("makeLinkQR(arrAnContent[3][8]); - " + e);
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
		// Datei als pdf speichern und beide Dateien in die DB speichern
		//#######################################################################
		SaveAsPdf.toPDF(sExcelOut, sPdfOut);
		SaveAsPdf.setPdfMetadata(sNr, "AN", sPdfOut);

		boolean bLockedXLSX = isLocked(sExcelOut);
		boolean bLockedPDF = isLocked(sPdfOut);
		while(bLockedXLSX || bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}

		try {

			String FileNamePath = sPdfOut;
			File fn = new File(FileNamePath);
			String FileName = fn.getName();

			String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
			String sSQLstatementA = "INSERT INTO " + tblName + " ([IdNummer],[ANFileName],[ANpdfFile]) VALUES ('" + sNr + "','" + FileName
					+ "',(SELECT * FROM OPENROWSET(BULK '" + FileNamePath + "', SINGLE_BLOB) AS DATA))";

			sqlInsert(sConn, sSQLstatementA);

			String FileNamePathU = sExcelOut;
			File fnU = new File(FileNamePathU);
			String FileNameU = fnU.getName();

			String sSQLStatementB = "UPDATE " + tblName + " SET [AddFileName01] = '" + FileNameU + "',[AddFile01] = (SELECT * FROM OPENROWSET(BULK '"
					+ FileNamePathU + "', SINGLE_BLOB) AS DATA) WHERE [IdNummer] = '" + sNr + "'";

			sqlUpdate(sConn, sSQLStatementB);

		} catch (SQLException | ClassNotFoundException e) {
			logger.error("error inserting offer files into database - " + e);
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
			logger.error("anExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}

	public static void setsConn(String sConn) {
		ExcelOffer.sConn = sConn;
	}
}

