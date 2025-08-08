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

import org.andy.code.dataStructure.entitiyProductive.Einkauf;
import org.andy.code.dataStructure.repositoryProductive.EinkaufRepository;
import org.andy.code.main.LoadData;

public class LoadPurchase {
	
	private static BigDecimal bdNetto = BigDecimal.ZERO;
	private static BigDecimal bdBrutto = BigDecimal.ZERO;
	
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

		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

		EinkaufRepository einkaufRepository = new EinkaufRepository();
	    List<Einkauf> einkaufListe = new ArrayList<>();
	    einkaufListe.addAll(einkaufRepository.findAllByJahr(Integer.parseInt(LoadData.getStrAktGJ())));
		
		String[][] sTemp = new String [einkaufListe.size()][9];
		
		for (int i = 0; i < einkaufListe.size(); i++){
			Einkauf einkauf = einkaufListe.get(i);

			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			LocalDate date = LocalDate.parse(einkauf.getReDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate dateZZ = LocalDate.parse(einkauf.getZahlungsziel().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        String datum = date.format(outputFormatter);
	        String datumZZ = dateZZ.format(outputFormatter);
	        
	        String netto = df.format(einkauf.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(einkauf.getUst()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(einkauf.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = datum;
			sTemp[i][1] = einkauf.getId();
			sTemp[i][2] = einkauf.getKredName();
			sTemp[i][3] = einkauf.getKredLand();
			sTemp[i][4] = netto;
			sTemp[i][5] = ust;
			sTemp[i][6] = brutto;
			sTemp[i][7] = datumZZ;
			sTemp[i][8] = einkauf.getDateiname();
			
			bdNetto = bdNetto.add(einkauf.getNetto());
			bdBrutto = bdBrutto.add(einkauf.getBrutto());
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

}
