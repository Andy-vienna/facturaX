package org.andy.code.main.overview.result;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.misc.parseBigDecimal;
import org.andy.gui.main.result_panels.RecStatePanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecStateData {
	
	private static final Logger logger = LogManager.getLogger(UStData.class);
	
	private static List<Kunde> kundeListe = new ArrayList<>(); // Kundenliste
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static void RecState(RecStatePanel panel, int anzahl, String[][] array) {
		setValues(panel, anzahl, array);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setValues(RecStatePanel panel, int anzahl, String[][] array) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		
	    List<Map<String, Statistik>> mapProQuartal = new ArrayList<>();
	    for (int i = 0; i < 4; i++) {
	        mapProQuartal.add(new HashMap<>());
	    }

	    try {
	        for (int x = 1; x < anzahl + 1; x++) {
	            int quartal = getQuartalFromString(array[x][6].trim(), "dd.MM.yyyy") - 1;
	            String sKunde = array[x][9].trim();

	            for (int i = 0; i < kundeListe.size(); i++) {
	                if (kundeListe.get(i).getName().trim().equals(sKunde)) {
	                    if (!kundeListe.get(i).getLand().trim().equals("ÖSTERREICH")) {
	                        String ustId = kundeListe.get(i).getUstid().trim();
	                        BigDecimal betrag = parseBigDecimal.fromArray(array, x, 12);

	                        mapProQuartal.get(quartal)
	                            .computeIfAbsent(ustId, _ -> new Statistik())
	                            .add(betrag);
	                    }
	                }
	            }
	        }

	        // Übertrage in panel-Felder
	        for (int q = 0; q < 4; q++) {
	            int index = 0;
	            for (Map.Entry<String, Statistik> entry : mapProQuartal.get(q).entrySet()) {
	                panel.setTxtFields(index, q * 2, entry.getKey()); // USt-Id
	                String summeFormatted = nf.format(entry.getValue().summe);
	                panel.setTxtFields(index, q * 2 + 1, summeFormatted + " €");
	                index++;
	            }
	        }

	    } catch (NullPointerException e) {
	        logger.error("error in calculating revenue sum - " + e);
	    }
	}
	
	//###################################################################################################################################################
	
	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }

	public static void setKundeListe(List<Kunde> kundeListe2) {
		RecStateData.kundeListe = kundeListe2;
	}
	
	//###################################################################################################################################################
	// interne Klasse
	//###################################################################################################################################################
	
	static class Statistik {
        long anzahl = 0;
        BigDecimal summe = BigDecimal.ZERO;

        void add(BigDecimal betrag) {
            anzahl++;
            summe = summe.add(betrag);
        }

        @Override
        public String toString() {
            return "Anzahl: " + anzahl + ", Summe: " + summe;
        }
    }

}
