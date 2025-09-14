package org.andy.code.dataExport;

import java.util.ArrayList;
import java.util.List;

import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyMaster.Lieferant;
import org.andy.code.dataStructure.entitiyMaster.Owner;
import org.andy.code.dataStructure.entitiyMaster.Text;
import org.andy.code.dataStructure.entitiyProductive.Angebot;
import org.andy.code.dataStructure.entitiyProductive.Bestellung;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.andy.code.dataStructure.repositoryMaster.BankRepository;
import org.andy.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.code.dataStructure.repositoryMaster.OwnerRepository;
import org.andy.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.code.main.Einstellungen;
import org.andy.code.misc.CodeListen;

public class ExcelHelper {
	
	private static String senderOwner;
	private static String footerLeft;
	private static String footerCenter;
	private static String kontaktName;
	private static String steuerNummer;
	
	private static ArrayList<String> TextUSt = new ArrayList<>();
	private static ArrayList<String> TextZahlZiel = new ArrayList<>();
	private static ArrayList<String> TextAngebot = new ArrayList<>();
	private static ArrayList<String> TextZahlErin = new ArrayList<>();
	private static ArrayList<String> TextOrderConfirm = new ArrayList<>();
	private static ArrayList<String> TextMahnung = new ArrayList<>();
	private static ArrayList<String> TextBestellung = new ArrayList<>();
	private static ArrayList<String> TextLieferschein = new ArrayList<>();
	
	private static Owner owner = new Owner();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static Angebot loadAngebot(String AnNr) {
		return readAngebot(AnNr);
	}
	
	public static Rechnung loadRechnung(String ReNr) {
		return readRechnung(ReNr);
	}
	
	public static Bestellung loadBestellung(String BeNr) {
		return readBestellung(BeNr);
	}
	
	public static Kunde kundeData(String KdNr) {
		return readKunde(KdNr);
	}
	
	public static String kundeAnschrift(String KdNr) {
		return formatKunde(KdNr);
	}
	
	public static Lieferant lieferantData(String LiNr) {
		return readLieferant(LiNr);
	}
	
	public static String lieferantAnschrift(String LiNr) {
		return formatLieferant(LiNr);
	}
	
	public static Bank bankData(int id) {
		return readBank(id);
	}
	
	public static ArrayList<String> ownerData(){
		return readOwner();
	}
	
	public static void textData(){
		readText();
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static Angebot readAngebot(String AnNr) {
		AngebotRepository angebotRepository = new AngebotRepository();
		return angebotRepository.findById(AnNr);
	}
	
	//###################################################################################################################################################
	
	private static Rechnung readRechnung(String ReNr) {
		RechnungRepository rechnungRepository = new RechnungRepository();
		return rechnungRepository.findById(ReNr);
	}
	
	//###################################################################################################################################################
	
	private static Bestellung readBestellung(String BeNr) {
		BestellungRepository bestellungRepository = new BestellungRepository();
		return bestellungRepository.findById(BeNr);
	}
	
	//###################################################################################################################################################
	
	private static Kunde readKunde(String tmp){
		KundeRepository kundeRepository = new KundeRepository();
		List<Kunde> kundeListe = new ArrayList<>();
		kundeListe.addAll(kundeRepository.findAll());
		
		for (int m = 0; m < kundeListe.size(); m++) {
			
			Kunde kunde = kundeListe.get(m);
			
			if (kunde.getId() != null && !kunde.getId().isEmpty() && kunde.getId().equals(tmp)) {
				return kunde;
			}
		}
		return null;
	}
	
	private static String formatKunde(String tmp) {
		KundeRepository kundeRepository = new KundeRepository();
		List<Kunde> kundeListe = new ArrayList<>();
		kundeListe.addAll(kundeRepository.findAll());
		
		for (int m = 0; m < kundeListe.size(); m++) {
			
			Kunde kunde = kundeListe.get(m);
			
			if (kunde.getId() != null && !kunde.getId().isEmpty() && kunde.getId().equals(tmp)) {
				CodeListen cl = new CodeListen();
			    String land = cl.getCountryFromCode(kunde.getLand()).toUpperCase();
				return kunde.getName() + "\n" + kunde.getStrasse() + "\n" + kunde.getPlz() + " " +
						kunde.getOrt() + ", " + land;
			}
		}
		return null;
	}
	
	//###################################################################################################################################################
	
	private static Lieferant readLieferant(String tmp){
		LieferantRepository lieferantRepository = new LieferantRepository();
		List<Lieferant> lieferantListe = new ArrayList<>();
		lieferantListe.addAll(lieferantRepository.findAll());
		
		for (int m = 0; m < lieferantListe.size(); m++) {
			
			Lieferant lieferant = lieferantListe.get(m);
			
			if (lieferant.getId() != null && !lieferant.getId().isEmpty() && lieferant.getId().equals(tmp)) {
				return lieferant;
			}
		}
		return null;
	}
	
	private static String formatLieferant(String tmp) {
		LieferantRepository lieferantRepository = new LieferantRepository();
		List<Lieferant> lieferantListe = new ArrayList<>();
		lieferantListe.addAll(lieferantRepository.findAll());
		
		for (int m = 0; m < lieferantListe.size(); m++) {
			
			Lieferant lieferant = lieferantListe.get(m);
			
			if (lieferant.getId() != null && !lieferant.getId().isEmpty() && lieferant.getId().equals(tmp)) {
				CodeListen cl = new CodeListen();
			    String land = cl.getCountryFromCode(lieferant.getLand()).toUpperCase();
				return lieferant.getName() + "\n" + lieferant.getStrasse() + "\n" + lieferant.getPlz() + " " +
						lieferant.getOrt() + ", " + land;
			}
		}
		return null;
	}
	
	//###################################################################################################################################################
	
	private static Bank readBank(int id) {
		BankRepository bankRepository = new BankRepository();
	    List<Bank> bankListe = new ArrayList<>();
	    bankListe.addAll(bankRepository.findAll());

		for (int m = 0; m < bankListe.size(); m++) {
			
			Bank bank = bankListe.get(m);

			if (bank.getId() != 0 && bank.getId() == id) {
				return bank;
			}
		}
		return null;
	}
	
	//###################################################################################################################################################
	
	private static ArrayList<String> readOwner(){
		OwnerRepository ownerRepository = new OwnerRepository();
	    List<Owner> ownerListe = new ArrayList<>();
	    ownerListe.addAll(ownerRepository.findAll());
	    owner = ownerListe.get(0);
	    
	    CodeListen cl = new CodeListen();
	    String land = cl.getCountryFromCode(owner.getLand()).toUpperCase();
	    
	    ArrayList<String> owTmp = new ArrayList<>();

	    owTmp.add(owner.getName() + "\n");
	    owTmp.add(owner.getAdresse() + " | ");
	    owTmp.add(owner.getPlz() + " ");
	    owTmp.add(owner.getOrt() + " | ");
	    owTmp.add(land + "\n");
	    owTmp.add(owner.getUstid());
	    
	    senderOwner = owner.getName() + ", " + owner.getAdresse() + ", " + owner.getPlz() + " " + owner.getOrt();
	    footerLeft = owner.getName() + " | Bearbeiter: " + Einstellungen.getStrAktUser();
		footerCenter = owner.getKontaktTel() + " | " + owner.getKontaktMail();
		kontaktName = owner.getKontaktName();
		steuerNummer = owner.getTaxid();
	    
	    return owTmp;
	}
	
	//###################################################################################################################################################
	
	private static void readText(){
		TextRepository textRepository = new TextRepository();
		List<Text> textListe = new ArrayList<>();
	    textListe.addAll(textRepository.findAll());
	    
		for(int i = 0; i < textListe.size(); i++) {
			TextUSt.add(textListe.get(i).getTextUst());
			TextZahlZiel.add(textListe.get(i).getTextZahlZiel());
			TextAngebot.add(textListe.get(i).getTextAngebot());
			TextZahlErin.add(textListe.get(i).getTextZahlErin());
			TextOrderConfirm.add(textListe.get(i).getTextOrderConfirm());
			TextMahnung.add(textListe.get(i).getTextMahnung());
			TextBestellung.add(textListe.get(i).getTextBestellung());
			TextLieferschein.add(textListe.get(i).getTextLieferschein());
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static Owner getOwner() {
		return owner;
	}
	
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

	public static ArrayList<String> getTextUSt() {
		return TextUSt;
	}

	public static ArrayList<String> getTextZahlZiel() {
		return TextZahlZiel;
	}

	public static ArrayList<String> getTextAngebot() {
		return TextAngebot;
	}

	public static ArrayList<String> getTextZahlErin() {
		return TextZahlErin;
	}

	public static ArrayList<String> getTextOrderConfirm() {
		return TextOrderConfirm;
	}

	public static ArrayList<String> getTextMahnung() {
		return TextMahnung;
	}

	public static ArrayList<String> getTextBestellung() {
		return TextBestellung;
	}

	public static ArrayList<String> getTextLieferschein() {
		return TextLieferschein;
	}
	
}
