package org.andy.code.main.overview.table;

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
import org.andy.code.main.LoadData;

public class LoadExpenses {
	
	private static BigDecimal bdNetto = BigDecimal.ZERO;
	private static BigDecimal bdBrutto = BigDecimal.ZERO;
	
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

		AusgabenRepository ausgabenRepository = new AusgabenRepository();
	    List<Ausgaben> ausgabenListe = new ArrayList<>();
		ausgabenListe.addAll(ausgabenRepository.findAllByJahr(Integer.parseInt(LoadData.getStrAktGJ())));
		
		String[][] sTemp = new String [ausgabenListe.size()][7];
		
		for (int i = 0; i < ausgabenListe.size(); i++){
			Ausgaben ausgaben = ausgabenListe.get(i);

			LocalDate date = LocalDate.parse(ausgaben.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(ausgaben.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(ausgaben.getSteuer()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(ausgaben.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = ausgaben.getId().toString();
			sTemp[i][1] = datum;
			sTemp[i][2] = ausgaben.getArt();
			sTemp[i][3] = netto;
			sTemp[i][4] = ust;
			sTemp[i][5] = brutto;
			sTemp[i][6] = ausgaben.getDateiname();
			
			bdNetto = bdNetto.add(ausgaben.getNetto());
			bdBrutto = bdBrutto.add(ausgaben.getBrutto());
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
	
}
