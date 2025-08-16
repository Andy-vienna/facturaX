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

import org.andy.code.dataStructure.entitiyProductive.SVSteuer;
import org.andy.code.dataStructure.repositoryProductive.SVSteuerRepository;
import org.andy.code.main.LoadData;
import org.andy.code.misc.BD;

public class LoadSvTax {
	
	private static BigDecimal bdSV = BD.ZERO;
	private static BigDecimal bdSteuer = BD.ZERO;
	
	private static BigDecimal[] bdSVQ = new BigDecimal[4];
	
	private static int[] belegID = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadSvTax(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		bdSV = BD.ZERO; bdSteuer = BD.ZERO;
		for (int a = 0; a < bdSVQ.length; a++) {
			 bdSVQ[a] = BD.ZERO;
		}
		
		SVSteuerRepository svsteuerRepository = new SVSteuerRepository();
	    List<SVSteuer> svsteuerListe = new ArrayList<>();
	    svsteuerListe.addAll(svsteuerRepository.findAllByJahr(parseStringToIntSafe(LoadData.getStrAktGJ())));
		
		String[][] sTemp = new String [svsteuerListe.size() + 1][7];
		belegID = new int[svsteuerListe.size()];
		
		for (int i = 0; i < svsteuerListe.size(); i++){
			SVSteuer svsteuer = svsteuerListe.get(i);
			
			LocalDate date = LocalDate.parse(svsteuer.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate dateF = LocalDate.parse(svsteuer.getZahlungsziel().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        String datumF = dateF.format(outputFormatter);

	        String zahllast = df.format(svsteuer.getZahllast()) + " " + currency.getCurrencyCode();
	        
	        String status = null;
	        if (svsteuer.getStatus() == 1) {
	        	status = "ja";
	        } else {
	        	status = "nein";
	        }
		
	        sTemp[i][0] = datum;
	        sTemp[i][1] = svsteuer.getOrganisation();
	        sTemp[i][2] = svsteuer.getBezeichnung();
	        sTemp[i][3] = zahllast;
	        sTemp[i][4] = datumF;
	        sTemp[i][5] = status;
	        sTemp[i][6] = svsteuer.getDateiname();
	        
	        belegID[i] = svsteuer.getId();
			
	        if (svsteuer.getOrganisation().contains("Sozialversicherung")) {
	        	bdSV = bdSV.add(svsteuer.getZahllast());
	        	for (int x = 0; x < 4; x++) {
	        	    String token = "Q" + (x + 1);
	        	    if (svsteuer.getBezeichnung().contains(token)) {
	        	        bdSVQ[x] = bdSVQ[x].add(svsteuer.getZahllast());
	        	    }
	        	}
	        }
	        if (svsteuer.getOrganisation().contains("Finanzamt")) {
	        	bdSteuer = bdSteuer.add(svsteuer.getZahllast());
	        }
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static BigDecimal getBdSV() {
		return bdSV;
	}

	public static BigDecimal getBdSteuer() {
		return bdSteuer;
	}

	public static int[] getBelegID() {
		return belegID;
	}

	public static BigDecimal[] getBdSVQ() {
		return bdSVQ;
	}

}
