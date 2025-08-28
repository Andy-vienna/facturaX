package org.andy.code.dataExport;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.andy.code.dataStructure.entitiyProductive.Angebot;
import org.andy.code.main.Einstellungen;

public class ErzeugeLeistungsbeschreibung {
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static void doLeistungsbeschreibung(Angebot a, String pdfDescription) throws Exception {

		// 1) HTML in temp-Datei
		Path tmpHtml = Files.createTempFile("seite2-", ".html");
		Files.writeString(tmpHtml, a.getBeschreibungHtml(), StandardCharsets.UTF_8);

		// 2) Content-PDF via Runner erzeugen
		Path contentPdf = tmpHtml.resolveSibling("leistungsbeschreibung-content.pdf");

		Path runnerJar = Path.of("lib/html2pdfRunner.jar"); // relativ zum working dir

		ProcessBuilder pb = new ProcessBuilder("java", "-jar", runnerJar.toString(), tmpHtml.toString(),
				contentPdf.toString());
		pb.inheritIO();
		int code = pb.start().waitFor();
		if (code != 0)
			throw new IllegalStateException("Runner exit=" + code);

		// 3) Overlay: Content in Vorlage platzieren
		File vorlage = new File(Einstellungen.getTplDescription());
		File out = new File(pdfDescription);

		overlay(vorlage, contentPdf.toFile(), out); // Overlay erzeugen
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private static void overlay(File templatePdf, File contentPdf, File outPdf) throws Exception {
		PdfReader tmplR = new PdfReader(templatePdf.getAbsolutePath());
		PdfReader contR = new PdfReader(contentPdf.getAbsolutePath());

		Rectangle pageSize = tmplR.getPageSize(1);
		Document doc = new Document(pageSize);
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(outPdf));
		doc.open();

		int contentPages = contR.getNumberOfPages();
		int templatePages = tmplR.getNumberOfPages();

		for (int i = 1; i <= contentPages; i++) {
			if (i > 1)
				doc.newPage();

			PdfContentByte cb = writer.getDirectContent();

			// Vorlage
			PdfImportedPage tmplPage = writer.getImportedPage(tmplR, Math.min(i, templatePages));
			cb.addTemplate(tmplPage, 0, 0);

			// Content skalieren
			PdfImportedPage contPage = writer.getImportedPage(contR, i);
			cb.addTemplate(contPage, 1, 0, 0, 1, 0, -90); // keine Skalierung, volle Seite, nur Abstand oben
		}

		doc.close();
		tmplR.close();
		contR.close();
	}
}
