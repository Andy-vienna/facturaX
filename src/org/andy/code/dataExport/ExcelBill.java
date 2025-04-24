package org.andy.code.dataExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
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

import org.andy.code.eRechnung.CreateXRechnungXML;
import org.andy.code.eRechnung.CreateZUGFeRDpdf;
import org.andy.code.main.LoadData;
import org.andy.code.qr.ZxingQR;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.main.JFoverview;

public class ExcelBill{

	private static final Logger logger = LogManager.getLogger(ExcelBill.class);

	private static String[][] arrYearBillOut;
	private static int iNumData;
	public static int iNumKunde;
	private static String[][] arrReContent = new String[30][16];

	private static final String TBL_FILE = "tbl_files";
	private static String sConn;

	private static final int START_ROW_OFFSET = 15;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_E = 4;
	private static final int COLUMN_F = 5;

	private static final String ZUGFeRD = "ZUGFeRD";
	private static final String XRECHNUNG = "XRechnung";

	/* Kommentarblock
	 *
	 *###################################################################################################################################################
	 *Daten zusammensammeln und in ein globales Array ablegen:
	 *arrReContent[0][0]				- Anzahl der in der Rechnung enthaltenen Positionszeilen
	 *	 	    [1][1] - [1][14]	- Kundendaten (Kunde, Straße, PLZ, Ort, Land, Pronom, Ansprechpartner, UID, USt.-Satz, Rabatschlüssel, Zahlungsziel, eBillLeitwegId, eBillTyp, eBillMail)
	 *			[2][1]				- Rechnungsdatum
	 *			[2][2]				- Rechnungsnummer
	 *			[2][3]				- Leistungszeitraum
	 *			[2][4]				- Kundenreferenz
	 *			[3][1] - [3][2]		- Rechnungstexte (Steuerhinweis, Zahlungsziel)
	 *			[4][1] - [4][4]		- Bankverbindung (Bankname, IBAN, BIC, Kontoinhaber)
	 *			[10][1] - [10][4]	- 1. Angebotszeile (Text, Anzahl, E-Preis, G-Preis)
	 *			....... - .......	- 2. - 11. Angebotszeile
	 *			[21][1] - [21][4]	- 12. Angebotszeile
	 */
	/* Excel-Vorlage 'template-bill.xlsx' - v2.05 - Struktur:
	 *
	 *Anschriftsfeld: 			B5
	 *Rechnungsdatum: 			F5
	 *Rechnungsnummer: 		F6
	 *Leistungszeitraum: 		F7
	 *Kunden-UID:				F8
	 *Ansprechpartner: 		F9
	 *Kundenreferenz: 			F10
	 *Rechnungspositionen: 	B/C/D/F17 - B/C/D/F28 (Bezeichnung, Menge, E-Preis, G-Preis)
	 *Nettosumme: 				F29
	 *Steuersatz: 				F30
	 *Umsatzsteuer: 			F31
	 *Rechnungsbetrag: 		F32
	 *Steuerhinweis: 			B36
	 *Zahlungsziel: 			B38
	 *QR-Code: 				F37
	 *Bankverbindung: 			E47, E48, E49 (Bank Name, IBAN, BIC)
	 *###################################################################################################################################################
	 */

	private static void reCollectData(String sNr) throws Exception {

		int x = 0;
		int y = 0;
		int n = 0;
		int m = 0;

		arrYearBillOut = JFoverview.getArrYearBillOut();

		n = 1;
		for(n = 1; (n-1) < Integer.valueOf(arrYearBillOut[0][0]); n++) {
			if(arrYearBillOut[n][1].equals(sNr)) {
				iNumData = n; // Datensatznummer der angeforderten Rechnung
			}
		}
		arrReContent[0][0] = arrYearBillOut[iNumData][15]; //Anzahl Zeilen für Rechnung
		String tmp = arrYearBillOut[iNumData][9];

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
			arrReContent[1][x] = kunde.get(x) != null ? kunde.get(x) : ""; // Falls null, ersetze mit leerem String
		}

		arrReContent[2][1] = arrYearBillOut[iNumData][6]; //Rechnungsdatum
		arrReContent[2][2] = sNr; //Rechnungsnummer
		arrReContent[2][3] = arrYearBillOut[iNumData][7]; //Leistungszeitraum
		arrReContent[2][4] = arrYearBillOut[iNumData][8]; //Kundenreferenz
		x = 1;
		y = 16;
		while(x < (Integer.valueOf(arrReContent[0][0]) + 1)) {
			arrReContent[x + 9][1] = arrYearBillOut[iNumData][y];
			arrReContent[x + 9][2] = arrYearBillOut[iNumData][y + 1];
			arrReContent[x + 9][3] = arrYearBillOut[iNumData][y + 2];
			double anz = Double.parseDouble(arrReContent[x + 9][2]);
			double ep = Double.parseDouble(arrReContent[x + 9][3]);
			double gp = anz * ep;
			arrReContent[x + 9][4] = String.valueOf(gp);
			x = x + 1;
			y = y + 3;
		}

		if (kunde != null && kunde.size() > 9 && "0".equals(kunde.get(9))) { // Steuerhinweis prüfen
			if (arrYearBillOut != null && arrYearBillOut[iNumData] != null &&
					arrYearBillOut[iNumData].length > 10 && "true".equals(arrYearBillOut[iNumData][10])) {

				ArrayList<ArrayList<String>> textList = SQLmasterData.getArrListText();

				if (textList != null && textList.size() > 1) {
					ArrayList<String> text = textList.get(1);
					if (text != null && text.size() > 1) {
						arrReContent[3][1] = text.get(1);
					} else {
						arrReContent[3][1] = "Fehlender Text"; // Fallback
					}
				}
			} else {
				ArrayList<ArrayList<String>> textList = SQLmasterData.getArrListText();

				if (textList != null && textList.size() > 0) {
					ArrayList<String> text = textList.get(0);
					if (text != null && text.size() > 1) {
						arrReContent[3][1] = text.get(1);
					} else {
						arrReContent[3][1] = "Fehlender Text"; // Fallback
					}
				}
			}
		} else {
			arrReContent[3][1] = " ";
		}

		if (kunde != null && kunde.size() > 11 && kunde.get(11) != null) { // Stelle sicher, dass Kunde[11] existiert
			String zahlungsziel = kunde.get(11).toString();

			ArrayList<ArrayList<String>> textList = SQLmasterData.getArrListText();
			if (textList != null && textList.size() > 1) { // Sicherstellen, dass genug Texte vorhanden sind
				if ("0".equals(zahlungsziel)) {
					ArrayList<String> text = textList.get(0);
					if (text != null && text.size() > 2) {
						arrReContent[3][2] = text.get(2);
					} else {
						arrReContent[3][2] = "Fehlender Text"; // Fallback für fehlende Einträge
					}
				} else {
					ArrayList<String> text = textList.get(1);
					if (text != null && text.size() > 2) {
						arrReContent[3][2] = ReplaceText.doReplace(text.get(2), "none", "none", "none", "none", zahlungsziel, "none", "none", "none", "none", "none");
					} else {
						arrReContent[3][2] = "Fehlender Text"; // Fallback für fehlende Einträge
					}
				}
			} else {
				arrReContent[3][2] = "Fehlende Textdaten"; // Fallback für fehlende Text-Liste
			}
		} else {
			arrReContent[3][2] = " ";
		}

		if (arrYearBillOut != null && arrYearBillOut.length > iNumData && arrYearBillOut[iNumData] != null &&
				arrYearBillOut[iNumData].length > 11 && arrYearBillOut[iNumData][11] != null) {

			String tmpBank = arrYearBillOut[iNumData][11];

			ArrayList<ArrayList<String>> bankList = SQLmasterData.getArrListBank();
			if (bankList != null && bankList.size() >= SQLmasterData.getAnzBank()) {

				for (m = 0; m < SQLmasterData.getAnzBank(); m++) {
					ArrayList<String> bank = bankList.get(m);

					if (bank != null && bank.size() > 4 && bank.get(0) != null && bank.get(0).equals(tmpBank)) {
						arrReContent[4][1] = bank.get(1) != null ? bank.get(1) : "";
						arrReContent[4][2] = bank.get(2) != null ? bank.get(2) : "";
						arrReContent[4][3] = bank.get(3) != null ? bank.get(3) : "";
						arrReContent[4][4] = bank.get(4) != null ? bank.get(4) : "";
						break; // Sobald die passende Bank gefunden ist, die Schleife beenden
					}
				}
			} else {
				System.out.println("Fehler: Bankliste ist null oder hat weniger Einträge als erwartet.");
			}
		} else {
			System.out.println("Fehler: arrYearBillOut ist ungültig oder enthält keine Bankdaten.");
		}

	}

	//###################################################################################################################################################
	// Rechnung erzeugen und auf Wunsch als pdf exportieren
	//###################################################################################################################################################

	public static void reExport(String sNr) throws Exception {

		String sExcelIn = LoadData.getTplBill();
		String sExcelOut = LoadData.getWorkPath() + "\\Rechnung_" + sNr + ".xlsx";
		String sPdfOut = LoadData.getWorkPath() + "\\Rechnung_" + sNr + ".pdf";

		DecimalFormat formatter = new DecimalFormat("#.##");

		final Cell rePos[] = new Cell[13];
		final Cell reText[] = new Cell[13];
		final Cell reAnz[] = new Cell[13];
		final Cell reEPreis[] = new Cell[13];
		final Cell reGPreis[] = new Cell[13];

		double dNetto = 0;
		double dUstSatz = 0;
		double dUSt = 0;
		double dBrutto = 0;

		reCollectData(sNr); //Daten aufbereiten

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
			Cell reAdress = ws.getRow(4).getCell(COLUMN_B); //Name und Anschrift
			Cell reDate = ws.getRow(4).getCell(COLUMN_F); //Rechnungsdatum
			Cell reNr = ws.getRow(5).getCell(COLUMN_F); //Rechnungsnummer
			Cell reLZ = ws.getRow(6).getCell(COLUMN_F); //Leistungszeitraum
			Cell reUID = ws.getRow(7).getCell(COLUMN_F); //UID
			Cell reDuty = ws.getRow(8).getCell(COLUMN_F); //Ansprechpartner
			Cell reRef = ws.getRow(9).getCell(COLUMN_F); //Kundenreferenz
			for(int i = 1; i < (Integer.parseInt(arrReContent[0][0]) + 1); i++ ) { //Rechnungspositionen B, C, D, F Zeile 17-28
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
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			reAdress.setCellValue(arrReContent[1][1] + "\n" + arrReContent[1][2] + "\n" + arrReContent[1][3] + " " +
					arrReContent[1][4] + ", " + arrReContent[1][5]);
			reDate.setCellValue(arrReContent[2][1]);
			reNr.setCellValue(arrReContent[2][2]);
			reLZ.setCellValue(arrReContent[2][3]);
			reUID.setCellValue(arrReContent[1][8]);
			reDuty.setCellValue(arrReContent[1][6] + " " + arrReContent[1][7]);
			reRef.setCellValue(arrReContent[2][4]);
			for(int i = 1; i < (Integer.parseInt(arrReContent[0][0]) + 1); i++ ) {
				rePos[i].setCellValue(String.valueOf(i));
				reText[i].setCellValue(arrReContent[(i + 9)][1]);
				reAnz[i].setCellValue(Double.parseDouble(arrReContent[(i + 9)][2]));
				reEPreis[i].setCellValue(Double.parseDouble(arrReContent[(i + 9)][3]));
				reGPreis[i].setCellValue(Double.parseDouble(arrReContent[(i + 9)][4]));
				dNetto = dNetto + Double.parseDouble(arrReContent[(i + 9)][4]);
			}
			dUstSatz = Double.parseDouble(arrReContent[1][9]) / 100;
			dUSt = dNetto * dUstSatz;
			dBrutto = dNetto + dUSt;
			reNetto.setCellValue(dNetto);
			reUstSatz.setCellValue(arrReContent[1][9] + "%");
			reUSt.setCellValue(dUSt);
			reBrutto.setCellValue(dBrutto);
			reText1.setCellValue(arrReContent[3][1]);
			reText2.setCellValue(arrReContent[3][2]);
			reBank.setCellValue(arrReContent[4][1]);
			reIBAN.setCellValue(main.java.toolbox.misc.Tools.FormatIBAN(arrReContent[4][2]));
			reBIC.setCellValue(arrReContent[4][3]);
			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			String sBrutto = formatter.format(dBrutto);
			try {
				ZxingQR.makeQR(LoadData.getStrQRschema(), arrReContent[4][4], arrReContent[4][2], arrReContent[4][3], sBrutto.replace(",", "."), sNr);
			} catch (WriterException e) {
				logger.error("makeQR(LoadData.strQRschema, arrReContent[4][4], arrReContent[4][2], arrReContent[4][3], sBrutto.replace(\",\", \".\"), sNr) - " + e);
			} catch (IOException e) {
				logger.error("makeQR(LoadData.strQRschema, arrReContent[4][4], arrReContent[4][2], arrReContent[4][3], sBrutto.replace(\",\", \".\"), sNr) - " + e);
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
				logger.error("reExport(String sNr) - " + e);
			}
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
		switch(arrReContent[1][13]) {
		case ZUGFeRD:
			String sFeRDpdf = LoadData.getWorkPath() + "\\Rechnung_" + sNr + "_ZUGFeRD.pdf";

			try {
				CreateZUGFeRDpdf.generateZUGFeRDpdf(arrReContent, sPdfOut, sFeRDpdf);
			} catch (ParseException | IOException e) {
				logger.error("error generating zugferd - " + e);
			}

			boolean bLockedpdf = main.java.toolbox.misc.Tools.isLocked(sFeRDpdf);
			while(bLockedpdf) {
				System.out.println("warte auf Datei ...");
			}

			try {

				String FileNamePath = sFeRDpdf;
				File fn = new File(FileNamePath);
				String FileName = fn.getName();

				String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
				String sSQLstatement = "INSERT INTO " + tblName + " ([IdNummer],[REFileName],[REpdfFile]) VALUES ('" + sNr + "','" + FileName
						+ "',(SELECT * FROM OPENROWSET(BULK '" + FileNamePath + "', SINGLE_BLOB) AS DATA))";

				main.java.toolbox.sql.Insert.sqlInsert(sConn, sSQLstatement);
			} catch (SQLException | ClassNotFoundException e) {
				logger.error("error writing zugferd to database - " + e);
			}

			File FeRD = new File(sFeRDpdf);
			if(FeRD.delete()) {

			}else {
				logger.error("reExport(String sNr) - pdf-Datei konnte nicht gelöscht werden");
			}
			break;

		case XRECHNUNG:
			String sXRxml = LoadData.getWorkPath() + "\\Rechnung_" + sNr + "_XRechnung.xml";

			try {
				CreateXRechnungXML.generateXRechnungXML(arrReContent, sXRxml);
			} catch (ParseException | IOException e) {
				logger.error("error generating xrechnung - " + e);
			}

			boolean bLockedXML = main.java.toolbox.misc.Tools.isLocked(sXRxml);
			while(bLockedXML) {
				System.out.println("warte auf Datei ...");
			}

			try {

				String FileNamePath = sXRxml;
				File fn = new File(FileNamePath);
				String FileName = fn.getName();

				String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
				String sSQLstatement = "INSERT INTO " + tblName + " ([IdNummer],[REFileName],[REpdfFile]) VALUES ('" + sNr + "','" + FileName
						+ "',(SELECT * FROM OPENROWSET(BULK '" + FileNamePath + "', SINGLE_BLOB) AS DATA))";

				main.java.toolbox.sql.Insert.sqlInsert(sConn, sSQLstatement);
			} catch (SQLException | ClassNotFoundException e) {
				logger.error("error writing xrechnung to database - " + e);
			}

			File XR = new File(sXRxml);
			if(XR.delete()) {

			}else {
				logger.error("reExport(String sNr) - xml-Datei konnte nicht gelöscht werden");
			}
		default:
			break;
		}
		//#######################################################################
		// Ursprungs-Excel und -pdf löschen
		//#######################################################################
		boolean bLockedpdf = main.java.toolbox.misc.Tools.isLocked(sPdfOut);
		boolean bLockedxlsx = main.java.toolbox.misc.Tools.isLocked(sExcelOut);
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
		ExcelBill.sConn = sConn;
	}
}

