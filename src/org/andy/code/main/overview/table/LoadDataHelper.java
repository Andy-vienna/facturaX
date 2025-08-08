package org.andy.code.main.overview.table;

import java.util.ArrayList;
import java.util.List;

import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.repositoryMaster.KundeRepository;

public class LoadDataHelper {
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String searchKunde(String sKdNr) {
		return kundeName(sKdNr);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String kundeName(String sKdNr) {
		KundeRepository kundeRepository = new KundeRepository();
	    List<Kunde> kundeListe = new ArrayList<>();
	    kundeListe.addAll(kundeRepository.findAll());

		// Prüfen, ob die Kundenliste null ist
		if (kundeListe.size() == 0) {
			return sKdNr; // Falls die Liste leer oder null ist, gib die ursprüngliche Kundennummer zurück.
		}

		for (int kd = 0; kd < kundeListe.size(); kd++) {
			Kunde kunde = kundeListe.get(kd);
			String id = kunde.getId().trim();
			// Prüfen, ob die Kunde-Liste null oder zu kurz ist
			if (id == null) {
				continue; // Überspringe ungültige Einträge
			}

			if (id.equals(sKdNr)) {
				return kunde.getName(); // Gib den Kundennamen zurück
			}
		}
		return sKdNr; // Falls keine Übereinstimmung gefunden wurde, gib die Nummer zurück
	}
	
	//###################################################################################################################################################
	
	

}
