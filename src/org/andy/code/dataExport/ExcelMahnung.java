package org.andy.code.dataExport;

import static org.andy.toolbox.misc.Tools.FormatIBAN;
import static org.andy.toolbox.misc.Tools.isLocked;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.code.sql.SQLmasterData;
import org.andy.gui.main.JFoverview;
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

	private static String[][] arrYearBillOut;
	private static int iNumData;
	private static String[][] arrReminderContent = new String[30][15];

	private static final String TBL_FILE = "tbl_files";
	private static String sConn;

	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_E = 4;
	private static final int COLUMN_F = 5;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void mahnungExport(String sNr, int iStufe) throws Exception {

		if(iStufe < 1 || iStufe > 2) {
			return;
		}

		new ArrayList<>();
		String sExcelIn = LoadData.getTplMahnung();
		String sExcelOut = LoadData.getWorkPath() + "\\Mahnung_" + String.valueOf(iStufe) + "_" + sNr + ".xlsx";
		String sPdfOut = LoadData.getWorkPath() + "\\Mahnung_" + String.valueOf(iStufe) + "_" + sNr + ".pdf";

		mahnungCollectData(sNr); //Daten aufbereiten

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
			remAdress.setCellValue(arrReminderContent[1][1] + "\n" + arrReminderContent[1][2] + "\n" + arrReminderContent[1][3] + " " +
					arrReminderContent[1][4] + ", " + arrReminderContent[1][5]);
			remDate.setCellValue(StartUp.getDtNow());
			remDuty.setCellValue(arrReminderContent[1][7]);

			ArrayList<ArrayList<String>> textList = SQLmasterData.getArrListText();
			if (textList != null && textList.size() >= 10) {

				// Erste Textzeile setzen
				ArrayList<String> text = textList.get(0);
				if (text != null && text.size() > 4) {
					remHeader.setCellValue(ReplaceText.doReplace(text.get(6), arrReminderContent[2][2], "none", "none", arrReminderContent[2][1], "none", "none", "none", String.valueOf(iStufe), "none", "none"));
				}

				// Anrede setzen
				if (arrReminderContent != null && arrReminderContent.length > 1 && arrReminderContent[1] != null &&
						arrReminderContent[1].length > 7 && arrReminderContent[1][6] != null) {

					if (arrReminderContent[1][6].equals("Herr") && textList.size() > 1) {
						text = textList.get(1);
					} else if (arrReminderContent[1][6].equals("Frau") && textList.size() > 2) {
						text = textList.get(2);
					} else {
						text = textList.get(3);
					}

					if (text != null && text.size() > 4) {
						remAnrede.setCellValue(ReplaceText.doReplace(text.get(6), String.valueOf(iStufe), "none", "none", "none", "none", arrReminderContent[1][7], "none", "none", "none", "none"));
					}
				}

				// Weitere Texte setzen
				for (int i = 4; i <= 13; i++) {
					if (textList.size() > i) {
						text = textList.get(i);
						if (text != null && text.size() > 4) {
							switch (i) {
							case 4:
								if(iStufe == 1) {
									remText1.setCellValue(ReplaceText.doReplace(text.get(6), "none", "none", "none", arrReminderContent[2][1], "none", "none", arrReminderContent[2][3], "none", "none", "none"));
								}
								break;
							case 5:
								if(iStufe == 2) {
									remText1.setCellValue(ReplaceText.doReplace(text.get(6), "none", "none", "none", arrReminderContent[2][1], "none", "none", arrReminderContent[2][3], "none", "none", "none"));
								}
								break;
							case 6:
								if(iStufe == 1) {
									remText2.setCellValue(text.get(6));
								}
								break;
							case 7:
								if(iStufe == 2) {
									remText2.setCellValue(text.get(6));
								}
								break;
							case 8:
								if(iStufe == 1) {
									remText3.setCellValue(text.get(6));
								}
								break;
							case 9:
								if(iStufe == 2) {
									remText3.setCellValue(ReplaceText.doReplace(text.get(6), "none", "none", "none", "none", "none", "none", "none", "none", "40,00", "none"));
								}
								break;
							case 10:
								if(iStufe == 1) {
									remText4.setCellValue(text.get(6));
								}
								break;
							case 11:
								if(iStufe == 2) {
									remText4.setCellValue(text.get(6));
								}
								break;
							case 12:
								remGruss.setCellValue(text.get(6));
								break;
							case 13:
								remName.setCellValue(ReplaceText.doReplace(text.get(6), "none", "none", "none", "none", "none", "none", "none", "none", "none", SQLmasterData.getsArrOwner()[7]));
								break;
							}
						}
					}
				}
			} else {
				System.out.println("Fehler: Textliste ist null oder hat zu wenige Einträge.");
			}

			// Bankdaten prüfen und setzen
			if (arrReminderContent != null && arrReminderContent.length > 4 && arrReminderContent[4] != null &&
					arrReminderContent[4].length > 3) {

				remBank.setCellValue(arrReminderContent[4][1] != null ? arrReminderContent[4][1] : "");
				remIBAN.setCellValue(arrReminderContent[4][2] != null ? FormatIBAN(arrReminderContent[4][2]) : "");
				remBIC.setCellValue(arrReminderContent[4][3] != null ? arrReminderContent[4][3] : "");
			} else {
				System.out.println("Fehler: arrReminderContent ist null oder hat keine Bankdaten.");
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
		SaveAsPdf.toPDF(sExcelOut, sPdfOut);
		SaveAsPdf.setPdfMetadata(sNr, "ZE", sPdfOut);

		boolean bLockedPDF = isLocked(sPdfOut);
		while(bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}

		try {
			String sSQLStatementB = null;

			String FileNamePath = sPdfOut;
			File fn = new File(FileNamePath);
			String FileName = fn.getName();

			String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
			if(iStufe == 1) {
				sSQLStatementB = "UPDATE " + tblName + " SET [AddFileName02] = '" + FileName + "',[AddFile02] = (SELECT * FROM OPENROWSET(BULK '"
						+ FileNamePath + "', SINGLE_BLOB) AS DATA) WHERE [IdNummer] = '" + sNr + "'";
			}
			if(iStufe == 2) {
				sSQLStatementB = "UPDATE " + tblName + " SET [AddFileName03] = '" + FileName + "',[AddFile03] = (SELECT * FROM OPENROWSET(BULK '"
						+ FileNamePath + "', SINGLE_BLOB) AS DATA) WHERE [IdNummer] = '" + sNr + "'";
			}

			sqlUpdate(sConn, sSQLStatementB);

		} catch (SQLException | ClassNotFoundException e) {
			logger.error("error inserting payment reminder files into database - " + e);
		}

		File reminder = new File(sPdfOut);
		if(reminder.delete()) {

		}else {
			logger.error("reminderExport(String sNr) - pdf-Datei konnte nicht gelöscht werden");
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
			logger.error("reminderExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}

	//###################################################################################################################################################
	//###################################################################################################################################################
	/* Kommentarblock
	 *###################################################################################################################################################
	 *Daten zusammensammeln und in ein globales Array ablegen:
	 *arrReminderContent[0][0]				- Anzahl der in der Rechnung enthaltenen Positionszeilen
	 *	 	    [1][1] - [1][12]	- Kundendaten (Kunde, Straße, PLZ, Ort, Land, Pronom, Ansprechpartner, UID, USt.-Satz, Rabatschlüssel, Zahlungsziel)
	 *			[2][1]				- Rechnungsdatum
	 *			[2][2]				- Rechnungsnummer
	 *			[2][3]				- Rechnungsbetrag
	 *			[4][1] - [4][3]		- Bankverbindung (Bankname, IBAN, BIC)
	 */
	/* Excel-Vorlage 'template-mahnung.xlsx' - v2.10 - Struktur:
	 *Anschriftsfeld: 	B5
	 *Datum: 			F5
	 *Ansprechpartner: 	F9
	 *Text 1:			B16
	 *Anrede:			B18
	 *Zeile 1:			B20
	 *Zeile 2:			B21
	 *Zeile 3:			B22
	 *Zeile 4:			B23
	 *Gruß:				B26
	 *Name:				B27
	 *Bankverbindung: 	E47, E48, E49 (Bank Name, IBAN, BIC)
	 *###################################################################################################################################################
	 */

	private static void mahnungCollectData(String sNr) throws Exception {

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###.00");
		int x = 0;
		int n = 0;
		int m = 0;

		arrYearBillOut = JFoverview.getArrYearRE();

		n = 1;
		for(n = 1; (n-1) < Integer.valueOf(arrYearBillOut[0][0]); n++) {
			if(arrYearBillOut[n][1].equals(sNr)) {
				iNumData = n; // Datensatznummer der angeforderten Rechnung
				break;
			}
		}
		arrReminderContent[0][0] = arrYearBillOut[iNumData][15]; //Anzahl Zeilen für Rechnung
		String tmp = arrYearBillOut[iNumData][9];

		for (m = 0; m < SQLmasterData.getAnzKunde(); m++) {
			List<String> kunde = SQLmasterData.getArrListKunde().get(m);

			if (kunde != null && !kunde.isEmpty() && kunde.get(0) != null && kunde.get(0).equals(tmp)) {
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
			arrReminderContent[1][x] = kunde.get(x) != null ? kunde.get(x) : ""; // Falls null, ersetze mit leerem String
		}

		arrReminderContent[2][1] = arrYearBillOut[iNumData][6]; //Rechnungsdatum
		arrReminderContent[2][2] = sNr; //Rechnungsnummer

		double tmpBr = Double.parseDouble(arrYearBillOut[iNumData][14]);
		String sBrutto = df.format(tmpBr); // Bruttosumme

		arrReminderContent[2][3] = sBrutto; //Rechnungsbetrag brutto formattiert

		if (arrYearBillOut != null && arrYearBillOut.length > iNumData &&
				arrYearBillOut[iNumData] != null && arrYearBillOut[iNumData].length > 11 &&
				arrYearBillOut[iNumData][11] != null) {

			String tmpBank = arrYearBillOut[iNumData][11];

			ArrayList<ArrayList<String>> bankList = SQLmasterData.getArrListBank();
			if (bankList != null && bankList.size() >= SQLmasterData.getAnzBank()) {

				for (m = 0; m < SQLmasterData.getAnzBank(); m++) {
					ArrayList<String> bank = bankList.get(m);

					if (bank != null && bank.size() > 4 && bank.get(0) != null && bank.get(0).equals(tmpBank)) {
						arrReminderContent[4][1] = bank.get(1) != null ? bank.get(1) : "";
						arrReminderContent[4][2] = bank.get(2) != null ? bank.get(2) : "";
						arrReminderContent[4][3] = bank.get(3) != null ? bank.get(3) : "";
						arrReminderContent[4][4] = bank.get(4) != null ? bank.get(4) : "";
						break; // Beende die Schleife, wenn die Bank gefunden wurde
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
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		ExcelMahnung.sConn = sConn;
	}
}

