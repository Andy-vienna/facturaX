package org.andy.code.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblKunde")
public class Kunde {
    @Id
    @Column(name = "Id")
    private String id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Strasse")
    private String strasse;
    
    @Column(name = "PLZ")
    private String plz;

    @Column(name = "Ort")
    private String ort;
    
    @Column(name = "Land")
    private String land;
    
    @Column(name = "Pronomen")
    private String pronomen;

    @Column(name = "Ansprechpartner")
    private String person;

    @Column(name = "UID")
    private String ustid;
    
    @Column(name = "Steuersatz")
    private String taxvalue;

    @Column(name = "Rabattschluessel")
    private String deposit;

    @Column(name = "Zahlungsziel")
    private String zahlungsziel;
    
    @Column(name = "eBillLeitwegId")
    private String leitwegId;

    @Column(name = "eBillTyp")
    private String eBillTyp;

    @Column(name = "eBillMail")
    private String eBillMail;
    
    @Column(name = "eBillPhone")
    private String eBillPhone;
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getPronomen() {
		return pronomen;
	}

	public void setPronomen(String pronomen) {
		this.pronomen = pronomen;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getUstid() {
		return ustid;
	}

	public void setUstid(String ustid) {
		this.ustid = ustid;
	}

	public String getTaxvalue() {
		return taxvalue;
	}

	public void setTaxvalue(String taxvalue) {
		this.taxvalue = taxvalue;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getZahlungsziel() {
		return zahlungsziel;
	}

	public void setZahlungsziel(String zahlungsziel) {
		this.zahlungsziel = zahlungsziel;
	}

	public String getLeitwegId() {
		return leitwegId;
	}

	public void setLeitwegId(String leitwegId) {
		this.leitwegId = leitwegId;
	}

	public String geteBillTyp() {
		return eBillTyp;
	}

	public void seteBillTyp(String eBillTyp) {
		this.eBillTyp = eBillTyp;
	}

	public String geteBillMail() {
		return eBillMail;
	}

	public void seteBillMail(String eBillMail) {
		this.eBillMail = eBillMail;
	}

	public String geteBillPhone() {
		return eBillPhone;
	}

	public void seteBillPhone(String eBillPhone) {
		this.eBillPhone = eBillPhone;
	}

}
