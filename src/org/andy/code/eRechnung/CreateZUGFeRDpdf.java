package org.andy.code.eRechnung;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.swing.JOptionPane;

import org.mustangproject.FileAttachment;
import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromA3;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;

public class CreateZUGFeRDpdf {

	private static String[] SENDER;

	@SuppressWarnings({ "resource" })
	public static void generateZUGFeRDpdf(String[][] sArray, String sPdfInput, String sFeRDpdf) throws ParseException, IOException {

		String[] sAttachment = new String[10];

		Invoice i = SetInvoiceEx.doInvoice(sArray);
		SENDER = SetInvoiceEx.getSENDER();

		int dialogButton = 0;
		dialogButton = JOptionPane.showConfirmDialog (null, "Soll eine Anlage angefügt werden ?","Attachment", dialogButton);
		if(dialogButton == JOptionPane.YES_OPTION) {
			sAttachment[0] = main.java.toolbox.misc.SelectFile.chooseFile(LoadData.getWorkPath());
			for(int num = 1; num < 10; num++) {
				dialogButton = JOptionPane.showConfirmDialog (null, num + "/10 Anlagen vorhanden, soll eine weitere angefügt werden ?","Attachment", dialogButton);
				if(dialogButton == JOptionPane.YES_OPTION) {
					sAttachment[num] = main.java.toolbox.misc.SelectFile.chooseFile(LoadData.getWorkPath());
				}
				if(dialogButton == JOptionPane.NO_OPTION) {
					break;
				}
			}
			ZUGFeRDExporterFromA3 ze = new ZUGFeRDExporterFromA3().load(sPdfInput)
					.setCreatorTool(StartUp.APP_NAME + StartUp.APP_VERSION)
					.setProducer(StartUp.APP_NAME + StartUp.APP_VERSION)
					.setCreator(StartUp.APP_NAME + StartUp.APP_VERSION)
					.ignorePDFAErrors();
			int anz = 0;
			while(sAttachment[anz] != null) {
				String fileName = Paths.get(sAttachment[anz]).getFileName().toString();
				byte[] attachmentContents = Files.readAllBytes(Paths.get(sAttachment[anz]));
				String attachmentMime = Files.probeContentType(Paths.get(sAttachment[anz]));
				FileAttachment appendFile = new FileAttachment(fileName, attachmentMime, "Unspecified", attachmentContents).setDescription("Anlage: " + fileName);
				ze.attachFile(appendFile);
				i.embedFileInXML(appendFile);
				anz++;
				if(anz > 9) {
					break;
				}
			}
			ze.setCreatorTool(StartUp.APP_NAME);
			ze.setTransaction(i);
			ze.export(sFeRDpdf);
		} else if(dialogButton == JOptionPane.NO_OPTION) {
			ZUGFeRDExporterFromA3 ze = new ZUGFeRDExporterFromA3().load(sPdfInput)
					.setCreatorTool(StartUp.APP_NAME + StartUp.APP_VERSION)
					.setProducer(StartUp.APP_NAME + StartUp.APP_VERSION)
					.setCreator(StartUp.APP_NAME + StartUp.APP_VERSION)
					.ignorePDFAErrors();
			ze.setCreatorTool(StartUp.APP_NAME);
			ze.setTransaction(i);
			ze.export(sFeRDpdf);
		}
	}

	/*
	private static String chooseFileNameAttachment() {

		JFileChooser tmpChooser = new JFileChooser();
		tmpChooser.setDialogTitle("Dateianlage auswählen");
		tmpChooser.setAcceptAllFileFilterUsed(false);
		tmpChooser.setFileFilter(new FileNameExtensionFilter("Dateiauswahl", "pdf", "jpg", "png", "xlsx", "csv"));
		tmpChooser.setCurrentDirectory(new File(LoadData.workPath));
		int choiceOf = tmpChooser.showOpenDialog(null);
		if(choiceOf == JFileChooser.APPROVE_OPTION) {
			File tempLoc = tmpChooser.getSelectedFile();
			String sFileName = tempLoc.getAbsolutePath();
			return sFileName;
		}
		return null;
	}
	 */

	/**
	 * @return the sENDER
	 */
	public static String[] getSENDER() {
		return SENDER;
	}

	/**
	 * @param sENDER the sENDER to set
	 */
	public static void setSENDER(String[] sENDER) {
		SENDER = sENDER;
	}
}
