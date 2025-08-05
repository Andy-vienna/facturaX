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

import org.andy.code.entityProductive.Rechnung;
import org.andy.code.entityProductive.RechnungRepository;
import org.andy.code.main.LoadData;

public class LoadBill {
	
	private static RechnungRepository rechnungRepository = new RechnungRepository();
    private static List<Rechnung> rechnungListe = new ArrayList<>();
    
    private static BigDecimal sumOpen = BigDecimal.ZERO;
    private static BigDecimal sumPayed = BigDecimal.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadRechnung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {
		
		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		sumOpen = BigDecimal.ZERO; sumPayed = BigDecimal.ZERO;
		
		rechnungListe.clear();
		rechnungListe.addAll(rechnungRepository.findAllByJahr(Integer.parseInt(LoadData.getStrAktGJ())));
		
		String[][] sTemp = new String [rechnungListe.size()][9];

		for (int i = 0; i < rechnungListe.size(); i++){
			Rechnung rechnung = rechnungListe.get(i);
			
			String status = switch(rechnung.getState()) {
				case 0 -> "storniert";
				case 1 -> "erstellt";
				case 11 -> "gedruckt";
				case 111 -> "bezahlt";
				case 211 -> "Zahlungserinnerung";
				case 311 -> "Mahnstufe 1";
				case 411 -> "Mahnstufe 2";
				default -> "-----";
			};
			
			LocalDate date = LocalDate.parse(rechnung.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(rechnung.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(rechnung.getUst()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(rechnung.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = rechnung.getIdNummer();
			sTemp[i][1] = status;
			sTemp[i][2] = datum;
			sTemp[i][3] = rechnung.getlZeitr();
			sTemp[i][4] = rechnung.getRef();
			sTemp[i][5] = LoadDataHelper.searchKunde(rechnung.getIdKunde());
			sTemp[i][6] = netto;
			sTemp[i][7] = ust;
			sTemp[i][8] = brutto;
			
			if (rechnung.getState() > 0) { // nicht storniert
				switch (rechnung.getState()) {
				case 11 -> sumOpen = sumOpen.add(rechnung.getBrutto());
				case 111 -> sumPayed = sumPayed.add(rechnung.getBrutto());
				}
			}
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static BigDecimal getSumOpen() {
		return sumOpen;
	}

	public static BigDecimal getSumPayed() {
		return sumPayed;
	}
	
}
