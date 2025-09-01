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

import org.andy.code.dataStructure.entitiyProductive.Einkauf;
import org.andy.code.dataStructure.repositoryProductive.EinkaufRepository;
import org.andy.code.main.Einstellungen;
import org.andy.code.misc.BD;
import org.andy.code.misc.CodeListen;

public class LadeEinkauf {
	
	private static BigDecimal bdNetto = BD.ZERO; private static BigDecimal bdBrutto = BD.ZERO;
	private static BigDecimal bd10Proz = BD.ZERO; private static BigDecimal bd20Proz = BD.ZERO;
	private static BigDecimal bdUstEU = BD.ZERO; private static BigDecimal bdUstEUnoEURO = BD.ZERO;
	private static BigDecimal bdUstNonEU = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadEinkaufsRechnung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		bdNetto = BD.ZERO; bdBrutto = BD.ZERO; bd10Proz = BD.ZERO; bd20Proz = BD.ZERO;
		bdUstEU = BD.ZERO; bdUstEUnoEURO = BD.ZERO; bdUstNonEU = BD.ZERO;

		EinkaufRepository einkaufRepository = new EinkaufRepository();
	    List<Einkauf> einkaufListe = new ArrayList<>();
	    einkaufListe.addAll(einkaufRepository.findAllByJahr(parseStringToIntSafe(Einstellungen.getStrAktGJ())));
		
		String[][] sTemp = new String [einkaufListe.size() + 1][11]; // 1 Zeile mehr für neuen Beleg
		
		for (int i = 0; i < einkaufListe.size(); i++){
			Einkauf einkauf = einkaufListe.get(i);

			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			LocalDate date = LocalDate.parse(einkauf.getReDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate dateZZ = LocalDate.parse(einkauf.getZahlungsziel().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        String datum = date.format(outputFormatter);
	        String datumZZ = dateZZ.format(outputFormatter);
	        
	        Currency currency = Currency.getInstance(einkauf.getWaehrung());
	        String netto = df.format(einkauf.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(einkauf.getUst()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(einkauf.getBrutto()) + " " + currency.getCurrencyCode();
	        
	        String status = null;
	        switch(einkauf.getStatus()) {
	        case 0 -> status = "nein";
	        case 1 -> status = "angezahlt";
	        case 2 -> status = "ja";
	        case 3 -> status = "ja, Skonto 1";
	        case 4 -> status = "ja, Skonto 2";
	        default -> status = "unbekannt";
	        }

			sTemp[i][0] = datum;
			sTemp[i][1] = einkauf.getId();
			sTemp[i][2] = einkauf.getKredName();
			sTemp[i][3] = einkauf.getKredLand();
			sTemp[i][4] = einkauf.getSteuersatz();
			sTemp[i][5] = netto;
			sTemp[i][6] = ust;
			sTemp[i][7] = brutto;
			sTemp[i][8] = datumZZ;
			sTemp[i][9] = status;
			sTemp[i][10] = einkauf.getDateiname();
			
			bdNetto = bdNetto.add(einkauf.getNetto());
			bdBrutto = bdBrutto.add(einkauf.getBrutto());
			
			CodeListen cl = new CodeListen();
			boolean eu = cl.isEU(einkauf.getKredLand()); boolean euro = cl.isEurozone(einkauf.getKredLand());
			
			if (eu) {
				if (einkauf.getKredLand().equals("AT")) {
					if (einkauf.getSteuersatz().equals("10")) bd10Proz = bd10Proz.add(einkauf.getUst());
					if (einkauf.getSteuersatz().equals("20")) bd20Proz = bd20Proz.add(einkauf.getUst());
				} else if (euro) {
					bdUstEU = bdUstEU.add(einkauf.getUst());
				} else {
					bdUstEUnoEURO = bdUstEUnoEURO.add(einkauf.getUst());
				}
			} else {
				bdUstNonEU = bdUstNonEU.add(einkauf.getUst());
			}
			
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
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

	public static BigDecimal getBdUstEU() {
		return bdUstEU;
	}

	public static BigDecimal getBdUstNonEU() {
		return bdUstNonEU;
	}

	public static BigDecimal getBdUstEUnoEURO() {
		return bdUstEUnoEURO;
	}

}
