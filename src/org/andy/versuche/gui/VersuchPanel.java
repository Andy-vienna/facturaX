package org.andy.versuche.gui;

import static org.andy.fx.gui.misc.CreateButton.createButton;
import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityJSON.JsonAI;
import org.andy.fx.code.googleServices.CheckEnvAI;
import org.andy.fx.code.googleServices.CloudInvoiceExtractor;
import org.andy.fx.code.googleServices.DateParser;
import org.andy.fx.code.googleServices.InterfaceBuilder.DocAiConfig;
import org.andy.fx.code.googleServices.InterfaceBuilder.InvoiceExtractionResult;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.FileSelect;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VersuchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(VersuchPanel.class);
	private final Font font = new Font("Tahoma", Font.BOLD, 11);
	private final Color titleColor = Color.BLUE;
	
	private JButton[] btn = new JButton[2];
	private JLabel[] label = new JLabel[8];
	private JTextField[] txt = new JTextField[8];
	private JTextField selectedFile = new JTextField();

	private String fileNamePath = null;

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public VersuchPanel() {
		setLayout(null);
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
				"Versuche für kommende Funktionen");
		border.setTitleFont(font);
		border.setTitleColor(titleColor);
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		setBorder(border);

		buildPanel();
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private void buildPanel() {
		int x = 30, y = 20; // Variablen für automatische Positionierung
		int btnWidth = HauptFenster.getButtonx();
		int btnHeight = HauptFenster.getButtony();
		
		String[] lbl = { "Datei", "Parse AI" };
		String[] txtlbl = { "Lieferant", "Beschreibung", "Rechnungsdatum", "Währung", "Steuersatz", "Netto", "Steuer", "Brutto" };
		
		for (int i = 0; i < btn.length; i++) {
			btn[i] = createButton(lbl[i], null, null);
			btn[i].setBounds(x, y + (i * 70), btnWidth, btnHeight);
			btn[i].setEnabled(true);
			add(btn[i]);
		}
		btn[0].addActionListener(_ -> {
			fileNamePath = doSelectFile();
		});
		
		btn[1].addActionListener(_ -> {
			try {
				doParseAI(fileNamePath);
			} catch (Exception e1) {
				logger.error("error parsing document: " + e1.getMessage());
				StartUp.gracefulQuit(66);
			}
		});
		
		selectedFile.setBounds(x, y + 225, 870, 25);
		add(selectedFile);
		
		for (int i = 0; i < label.length; i++) {
			label[i] = new JLabel(txtlbl[i]);
			label[i].setBounds(200, y + (i * 25), 200, 25);
			add(label[i]);
		}
		
		for (int i = 0; i < txt.length; i++) {
			txt[i] = new JTextField();
			txt[i].setBounds(400, y + (i * 25), 500, 25);
			add(txt[i]);
		}
		
		

	}
	
	private String doSelectFile() {
		String sel = FileSelect.chooseFile("c:/users/andre/documents");
		selectedFile.setText(sel);
		return sel;
	}
	
	private void doParseAI(String fileName) throws Exception {
		
		Path fileIn = Paths.get(fileName);
		
		JsonAI settingsAI = CheckEnvAI.getSettingsAI();
		DocAiConfig cfg = new DocAiConfig(settingsAI.documentAIprojectID, settingsAI.documentAIlocation, settingsAI.documentAIprocessorId);
				
		CloudInvoiceExtractor cloud = new CloudInvoiceExtractor(cfg);
		InvoiceExtractionResult result = cloud.extract(fileIn);
		
		String readDate = result.header().get("invoiceDate");
		
		LocalDate date = DateParser.parseOrDefault(readDate, LocalDate.of(1900,1,1));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String datum = date.format(outputFormatter);
		
		txt[0].setText(result.header().get("supplierName"));
		txt[1].setText(result.lineItems().get(0).get("description"));
		txt[2].setText(datum);
		txt[3].setText(result.currency());
		txt[4].setText(result.header().get("taxRate"));
		txt[5].setText(result.header().get("netAmount"));
		txt[6].setText(result.header().get("taxAmount"));
		txt[7].setText(result.header().get("totalAmount"));
		
		String json = new com.fasterxml.jackson.databind.ObjectMapper()
			    .writerWithDefaultPrettyPrinter()
			    .writeValueAsString(result);
			System.out.println(json);
		
	}
	
	

}
