package org.andy.code.dataExport;

import java.util.ArrayList;
import java.util.List;

import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyMaster.Owner;
import org.andy.code.dataStructure.entitiyMaster.Text;
import org.andy.code.dataStructure.entitiyProductive.Angebot;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.andy.code.dataStructure.repositoryMaster.BankRepository;
import org.andy.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.code.dataStructure.repositoryMaster.OwnerRepository;
import org.andy.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.code.main.LadeEinstellungen;

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
	
	public static Kunde kundeData(String KdNr) {
		return readKunde(KdNr);
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
	    
	    ArrayList<String> owTmp = new ArrayList<>();

	    owTmp.add(owner.getName() + "\n");
	    owTmp.add(owner.getAdresse() + " | ");
	    owTmp.add(owner.getPlz() + " ");
	    owTmp.add(owner.getOrt() + " | ");
	    owTmp.add(owner.getLand().toUpperCase() + "\n");
	    owTmp.add(owner.getUstid());
	    
	    senderOwner = owner.getName() + ", " + owner.getAdresse() + ", " + owner.getPlz() + " " + owner.getOrt();
	    footerLeft = owner.getName() + " | Bearbeiter: " + LadeEinstellungen.getStrAktUser();
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
	
}
