package org.andy.code.eRechnung;

import static org.andy.toolbox.misc.Tools.cutBack;
import static org.andy.toolbox.misc.Tools.cutFront;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.andy.code.dataExport.DataExportHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mustangproject.BankDetails;
import org.mustangproject.Contact;
import org.mustangproject.Invoice;
import org.mustangproject.Item;
import org.mustangproject.Product;
import org.mustangproject.TradeParty;

public class SetInvoiceEx {

	private static final Logger logger = LogManager.getLogger(SetInvoiceEx.class);

	private static String[] SENDER = new String[11];

	private static Invoice setInvoice(String[][] sArray) throws ParseException, IOException {

		ArrayList<String> owner = DataExportHelper.ownerData();
		for (int i = 0; i < owner.size(); i++) {
			SENDER[i] = owner.get(i);
		}

		long issue = 0;
		long due = 0;
		long start = 0;
		long end = 0;
		Item[] position = new Item[12];
		String RECV_COUNTRY = null;
		String RECV_NAME = sArray[1][1];
		String RECV_ADRESS = sArray[1][2];
		String RECV_ZIP = sArray[1][3];
		String RECV_TOWN = sArray[1][4];
		switch(sArray[1][5]) {
		case "DEUTSCHLAND":
			RECV_COUNTRY = "DE";
			break;
		case "ÖSTERREICH":
			RECV_COUNTRY = "AT";
			break;
		}
		String RECV_DUTY = sArray[1][7];
		String RECV_VAT = sArray[1][8];
		String RECV_TAX = sArray[1][9];
		String LEITWEG_ID = sArray[1][12];
		String RECV_MAIL = sArray[1][14];
		String RECV_PHONE = sArray[1][15];
		String RE_NR = sArray[2][2];
		String TAX_NOTE = sArray[3][1];
		String BANK_IBAN = sArray[4][2];
		String BANK_BIC = sArray[4][3];
		String BANK_HOLDER = sArray[4][4];

		BankDetails bDetail = new BankDetails(BANK_IBAN, BANK_BIC).setAccountName(BANK_HOLDER);
		Contact cSend = new Contact(SENDER[7], SENDER[8], SENDER[9]);
		Contact cRecv = new Contact(RECV_DUTY, RECV_PHONE, RECV_MAIL);
		TradeParty sender = new TradeParty(SENDER[1], SENDER[2], SENDER[3], SENDER[4], SENDER[5]).setVATID(SENDER[6])
				.setContact(cSend).addBankDetails(bDetail).setEmail(SENDER[9]);
		TradeParty recipient = new TradeParty(RECV_NAME, RECV_ADRESS, RECV_ZIP, RECV_TOWN, RECV_COUNTRY).setVATID(RECV_VAT)
				.setContact(cRecv).setEmail(RECV_MAIL);

		int ziel = Integer.valueOf(sArray[1][11]);
		issue = dateInMilis(sArray[2][1]); // Rechnungsdatum
		due = addDaysInMilis(sArray[2][1], ziel); // Fälligkeit

		// Leisutngszeitraum zerlegen
		String LZvon = cutBack(sArray[2][3], "-", 1);
		String LZbis = cutFront(sArray[2][3], "-", 1);

		start = dateInMilis(LZvon); // Lieferdatum (aus Leistungszeitraum von)
		end = dateInMilis(LZbis); // Lieferdatum (aus Leistungszeitraum von)

		int iAnz = Integer.valueOf(sArray[0][0]);
		for(int x = 0; x < iAnz; x++) {
			//new Item(new Product("Artikeltext", "Artikelbeschreibung", "C62", new BigDecimal(Steuersatz), new BigDecimal(E-Preis),  new BigDecimal(Menge))
			position[x] = new Item(new Product(sArray[x + 10][1], "", "C62", new BigDecimal(RECV_TAX)), new BigDecimal(sArray[x + 10][3]), new BigDecimal(sArray[x + 10][2]));
		}
		Invoice iInv = new Invoice()
				.setDueDate(new Date(due)) // Fälligkeit
				.setIssueDate(new Date(issue)) // Rechnungsdatum
				.setBuyerOrderReferencedDocumentID(sArray[2][4]) // Kundenreferenz
				.setDetailedDeliveryPeriod(new Date(start), new Date(end)) // Leistungszeitraum
				.setDeliveryDate(new Date(start)) // Liefertermin od. Leistungszeitraum
				.setCurrency(SENDER[10])
				.setSender(sender)
				.setRecipient(recipient)
				.setReferenceNumber(LEITWEG_ID)
				.setNumber(RE_NR);

		if(RECV_COUNTRY != "AT") {
			iInv.addTaxNote(TAX_NOTE);
		}
		for(int i = 0; i < iAnz; i++) {
			iInv.addItem(position[i]);
		}
		return iInv;
	}

	private static long dateInMilis(String date) throws ParseException {
		Date d = new SimpleDateFormat("dd.MM.yyyy").parse(date);
		long timestamp = d.getTime();
		return timestamp;
	}

	private static long addDaysInMilis(String date, int add) throws ParseException {
		Date d = new SimpleDateFormat("dd.MM.yyyy").parse(date);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		calendar.add(Calendar.DAY_OF_MONTH, add); // add n days to calendar instance
		Date future = calendar.getTime(); // get the date instance
		long timestamp = future.getTime();
		return timestamp;
	}

	public static Invoice doInvoice(String[][] sArray) {
		try {
			return setInvoice(sArray);
		} catch (ParseException | IOException e) {
			logger.error("doInvoice(String[][] sArray) - " + e);
		}
		return null;
	}

	public static String[] getSENDER() {
		return SENDER;
	}

}
