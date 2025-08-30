package org.andy.code.main.overview.table;

import static org.andy.code.misc.ArithmeticHelper.parseStringToIntSafe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.andy.code.dataStructure.entitiyProductive.Ausgaben;
import org.andy.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.code.main.Einstellungen;
import org.andy.code.misc.BD;

public class LadeAusgaben {
	
	private static BigDecimal bdNetto = BD.ZERO; private static BigDecimal bdBrutto = BD.ZERO;
	private static BigDecimal bd10Proz = BD.ZERO; private static BigDecimal bd20Proz = BD.ZERO;
	private static BigDecimal bdUstSonst = BD.ZERO;
	
	private static int[] belegID = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadAusgaben(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		bdNetto = BD.ZERO; bdBrutto = BD.ZERO; bd10Proz = BD.ZERO; bd20Proz = BD.ZERO; bdUstSonst = BD.ZERO;

		AusgabenRepository ausgabenRepository = new AusgabenRepository();
	    List<Ausgaben> ausgabenListe = new ArrayList<>();
		ausgabenListe.addAll(ausgabenRepository.findAllByJahr(parseStringToIntSafe(Einstellungen.getStrAktGJ())));
		
		String[][] sTemp = new String [ausgabenListe.size() + 1][8];
		belegID = new int[ausgabenListe.size()];
		
		for (int i = 0; i < ausgabenListe.size(); i++){
			Ausgaben ausgaben = ausgabenListe.get(i);

			LocalDate date = LocalDate.parse(ausgaben.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(ausgaben.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(ausgaben.getSteuer()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(ausgaben.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = datum;
			sTemp[i][1] = ausgaben.getArt().trim();
			sTemp[i][2] = ausgaben.getLand().trim();
			sTemp[i][3] = ausgaben.getSteuersatz().trim();
			sTemp[i][4] = netto;
			sTemp[i][5] = ust;
			sTemp[i][6] = brutto;
			sTemp[i][7] = ausgaben.getDateiname().trim();
			
			belegID[i] = ausgaben.getId();
			
			bdNetto = bdNetto.add(ausgaben.getNetto());
			bdBrutto = bdBrutto.add(ausgaben.getBrutto());
			if (ausgaben.getLand().equals("AT")) {
				if (ausgaben.getSteuersatz().equals("10")) bd10Proz = bd10Proz.add(ausgaben.getSteuer());
				if (ausgaben.getSteuersatz().equals("20")) bd20Proz = bd20Proz.add(ausgaben.getSteuer());
			} else {
				bdUstSonst = bdUstSonst.add(ausgaben.getSteuer());
			}
			
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static BigDecimal getBdNetto() {
		return bdNetto;
	}

	public static BigDecimal getBdBrutto() {
		return bdBrutto;
	}
	
	public static BigDecimal getBd10Proz() {
		return bd10Proz;
	}

	public static BigDecimal getBd20Proz() {
		return bd20Proz;
	}
	
	public static BigDecimal getUstSonst() {
		return bdUstSonst;
	}

	public static int[] getBelegID() {
		return belegID;
	}
	
}
