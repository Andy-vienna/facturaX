package org.andy.code.dataExport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.andy.code.entityMaster.Bank;
import org.andy.code.entityMaster.BankRepository;
import org.andy.code.entityMaster.Kunde;
import org.andy.code.entityMaster.KundeRepository;
import org.andy.code.entityMaster.Owner;
import org.andy.code.entityMaster.OwnerRepository;
import org.andy.code.entityMaster.Text;
import org.andy.code.entityMaster.TextRepository;
import org.andy.code.main.LoadData;

public class DataExportHelper {
	
	private static String senderOwner;
	private static String footerLeft;
	private static String footerCenter;
	private static String kontaktName;
	private static String steuerNummer;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static String[] kundeData(String tmp) {
		return getKunde(tmp);
	}
	
	public static String[] bankData(int id) {
		return getBank(id);
	}
	
	public static ArrayList<String> ownerData(){
		return getOwner();
	}
	
	public static ArrayList<ArrayList<String>> textData(){
		return getText();
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[] getKunde(String tmp){
		int m = 0; int iNumKunde = 0;
		String[] kdTmp = new String[16];
		KundeRepository kundeRepository = new KundeRepository();
		List<Kunde> kundeListe = new ArrayList<>();
		kundeListe.addAll(kundeRepository.findAll());
		
		for (m = 0; m < kundeListe.size(); m++) {
			
			Kunde kunde = kundeListe.get(m);
			
			if (kunde.getId() != null && !kunde.getId().isEmpty() && kunde.getId().equals(tmp)) {
				iNumKunde = m;
				break;
			}
		}
		
		Kunde kunde = kundeListe.get(iNumKunde);

		// Kopiere die Kundendaten sicher in ein String-Array
		kdTmp[0] = kunde.getId();
		kdTmp[1] = kunde.getName();
		kdTmp[2] = kunde.getStrasse();
		kdTmp[3] = kunde.getPlz();
		kdTmp[4] = kunde.getOrt();
		kdTmp[5] = kunde.getLand();
		kdTmp[6] = kunde.getPronomen();
		kdTmp[7] = kunde.getPerson();
		kdTmp[8] = kunde.getUstid();
		kdTmp[9] = kunde.getTaxvalue();
		kdTmp[10] = kunde.getDeposit();
		kdTmp[11] = kunde.getZahlungsziel();
		kdTmp[12] = kunde.getLeitwegId();
		kdTmp[13] = kunde.geteBillTyp();
		kdTmp[14] = kunde.geteBillMail();
		kdTmp[15] = kunde.geteBillPhone();
		
		return kdTmp;
	}
	
	//###################################################################################################################################################
	
	private static String[] getBank(int id) {
		int m = 0; int iNumBank = 0;
		String[] bkTmp = new String[4];
		BankRepository bankRepository = new BankRepository();
	    List<Bank> bankListe = new ArrayList<>();
	    bankListe.addAll(bankRepository.findAll());

		for (m = 0; m < bankListe.size(); m++) {
			
			Bank bank = bankListe.get(m);

			if (bank.getBankName() != null && bank.getIban() != null && bank.getId() == id) {
				iNumBank = m;
				break; // Sobald die passende Bank gefunden ist, die Schleife beenden
			}
		}
		
		Bank bank = bankListe.get(iNumBank);
		
		bkTmp[0] = bank.getBankName() != null ? bank.getBankName() : "";
		bkTmp[1] = bank.getIban() != null ? bank.getIban() : "";
		bkTmp[2] = bank.getBic() != null ? bank.getBic() : "";
		bkTmp[3] = bank.getKtoName() != null ? bank.getKtoName() : "";
		
		return bkTmp;
	}
	
	//###################################################################################################################################################
	
	private static ArrayList<String> getOwner(){
		OwnerRepository ownerRepository = new OwnerRepository();
	    List<Owner> ownerListe = new ArrayList<>();
	    ownerListe.addAll(ownerRepository.findAll());
	    Owner owner = ownerListe.get(0);
	    
	    ArrayList<String> owTmp = new ArrayList<>();

	    owTmp.add(owner.getName() + "\n");
	    owTmp.add(owner.getAdresse() + " | ");
	    owTmp.add(owner.getPlz() + " ");
	    owTmp.add(owner.getOrt() + " | ");
	    owTmp.add(owner.getLand().toUpperCase() + "\n");
	    owTmp.add(owner.getUstid());
	    
	    senderOwner = owner.getName() + ", " + owner.getAdresse() + ", " + owner.getPlz() + " " + owner.getOrt();
	    footerLeft = owner.getName() + " | Bearbeiter: " + LoadData.getStrAktUser();
		footerCenter = owner.getKontaktTel() + " | " + owner.getKontaktMail();
		kontaktName = owner.getKontaktName();
		steuerNummer = owner.getTaxid();
	    
	    return owTmp;
	}
	
	//###################################################################################################################################################
	
	private static ArrayList<ArrayList<String>> getText(){
		TextRepository textRepository = new TextRepository();
		List<Text> textListe = new ArrayList<>();
	    textListe.addAll(textRepository.findAll());
	    
		ArrayList<ArrayList<String>> result = textListe.stream()
			    .map(t -> {
			        ArrayList<String> row = new ArrayList<>();
			        row.add(String.valueOf(t.getId()));
			        row.add(t.getTextUst());
			        row.add(t.getTextZahlZiel());
			        row.add(t.getTextAngebot());
			        row.add(t.getTextZahlErin());
			        row.add(t.getTextOrderConfirm());
			        row.add(t.getTextMahnung());
			        return row;
			    })
			    .collect(Collectors.toCollection(ArrayList::new));
		
		return result;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static String getSenderOwner() {
		return senderOwner;
	}

	public static String getFooterLeft() {
		return footerLeft;
	}

	public static String getFooterCenter() {
		return footerCenter;
	}

	public static String getKontaktName() {
		return kontaktName;
	}

	public static String getSteuerNummer() {
		return steuerNummer;
	}
	
}
