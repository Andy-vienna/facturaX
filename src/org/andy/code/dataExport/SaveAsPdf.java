package org.andy.code.dataExport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentInformation;
import com.spire.pdf.conversion.PdfStandardsConverter;

import org.andy.code.main.StartUp;

public class SaveAsPdf {

	private static final Logger logger = LogManager.getLogger(SaveAsPdf.class);

	private static final String OFFER = "Angebot";
	private static final String CONFIRMATION = "Auftragsbestätigung";
	private static final String BILL = "Rechnung";
	private static final String REMINDER = "Zahlungserinnerung";

	/** Excel als pdf exportieren
	 * @param sFileExcel
	 * @param sFilePDF
	 * @throws OwnException
	 */
	public static void toPDF(String sFileExcel, String sFilePDF) {
		ActiveXComponent excel = new ActiveXComponent("Excel.Application");
		Dispatch workbook = null;
		try {
			excel.setProperty("Visible", false); // Excel im Hintergrund starten
			Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();
			workbook = Dispatch.call(workbooks, "Open", sFileExcel).toDispatch();

			Dispatch.call(workbook, "ExportAsFixedFormat", 0, sFilePDF); // Exportieren als PDF

		} catch (Exception e) {
			logger.error("toPDF(String sFileExcel, String sFilePDF) - " + e);
		} finally {
			Dispatch.call(workbook, "Close", false); // Arbeitsmappe schließen
			excel.invoke("Quit"); // Excel beenden
			excel = null;
			System.gc();
			ComThread.Release();
		}
		PdfStandardsConverter converter = new PdfStandardsConverter(sFilePDF); // pdf zu pdf-A wandeln
		converter.toPdfA1A(sFilePDF);
	}

	public static void setPdfMetadata(String sNr, String sTyp, String sPdf) throws Exception {
		//String[] tmp = SQLmasterData.getsArrOwner();

		String sTitel = decodeTyp(sTyp); // Titel festlegen

		PdfDocument document = new PdfDocument(); // PDF-Dokument laden
		document.loadFromFile(sPdf);

		PdfDocumentInformation info = document.getDocumentInformation(); // Zugriff auf die Dokumentinformationen

		// Metadaten festlegen
		info.setAuthor(DataExportHelper.getKontaktName());
		info.setTitle(sTitel + " " + sNr);
		info.setSubject(sTitel);
		info.setKeywords(sTitel + "," + sNr + "," + DataExportHelper.getKontaktName());
		info.setCreator(StartUp.APP_NAME + StartUp.APP_VERSION);

		// PDF speichern
		document.saveToFile(sPdf);
		document.close();

	}

	private static String decodeTyp(String sTyp) throws Exception {
		switch(sTyp) {
		case "AN":
			return OFFER;
		case "AB":
			return CONFIRMATION;
		case "RE":
			return BILL;
		case "ZE":
			return REMINDER;
		default:
			return "unknown";
		}
	}

}
