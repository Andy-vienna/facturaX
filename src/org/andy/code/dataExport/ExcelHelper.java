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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {
	
	private static String senderOwner;
	private static String footerLeft;
	private static String footerCenter;
	private static String kontaktName;
	private static String steuerNummer;
	
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
	
	public static String[][] findText(String forDocument) {
		
		TextRepository textRepository = new TextRepository();
		List<Text> textListe = new ArrayList<>();
	    textListe.addAll(textRepository.findAll());
	    
	    String[][] tmp = new String[textListe.size()][2];
	    
	    switch(forDocument) {
	    case "Angebot":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextAngebot();
	    		tmp[i][1] = textListe.get(i).getTextAngebot();
	    	}
	    	break;
	    case "AngebotRev":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextAngebotRev();
	    		tmp[i][1] = textListe.get(i).getTextAngebotRev();
	    	}
	    	break;
	    case "AuftragsBest":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextOrderConfirm();
	    		tmp[i][1] = textListe.get(i).getTextOrderConfirm();
	    	}
	    	break;
	    case "Bestellung":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextBestellung();
	    		tmp[i][1] = textListe.get(i).getTextBestellung();
	    	}
	    	break;
	    case "Mahnstufe1":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextMahnungStufe1();
	    		tmp[i][1] = textListe.get(i).getTextMahnungStufe1();
	    	}
	    	break;
	    case "Mahnstufe2":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextMahnungStufe2();
	    		tmp[i][1] = textListe.get(i).getTextMahnungStufe2();
	    	}
	    	break;
	    case "Rechnung":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextRechnung();
	    		tmp[i][1] = textListe.get(i).getTextRechnung();
	    	}
	    	break;
	    case "Zahlungserinnerung":
	    	for(int i = 0; i < textListe.size(); i++) {
	    		tmp[i][0] = textListe.get(i).getVarTextZahlErin();
	    		tmp[i][1] = textListe.get(i).getTextZahlErin();
	    	}
	    	break;
	    }
	    return tmp;
	}
	
	//###################################################################################################################################################
	
	public static boolean replaceCellValue(XSSFWorkbook wb, Sheet ws, String placeholder, String target) {
		
		DataFormatter fmt = new DataFormatter();
	    FormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();
	    
	    CellReference start = new CellReference("A1");
	    CellReference end   = new CellReference("G60");
		
		for (int r = start.getRow(); r <= end.getRow(); r++) {
	        Row row = ws.getRow(r); // kann null sein
	        for (int c = start.getCol(); c <= end.getCol(); c++) {
	            Cell cell = (row != null) ? row.getCell(c) : null;

	            String text = (cell != null) ? fmt.formatCellValue(cell, eval) : null;
	            if (text != null && text.equals(placeholder)) {
				    cell.setCellValue(target);
				    System.out.println(text + " - Text: " + target);
	            	return true;
	            }
	        }
	    }
		return false;
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

}
