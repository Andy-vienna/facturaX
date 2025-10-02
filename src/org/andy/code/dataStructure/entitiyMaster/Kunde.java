package org.andy.code.dataStructure.entitiyMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblKunde")
public class Kunde {
	@Column(name = "Ansprechpartner", nullable = false)
    private String person;
	
	@Column(name = "eBillLeitwegId", nullable = false)
    private String leitwegId;
	
	@Column(name = "eBillMail", nullable = false)
    private String eBillMail;
    
    @Column(name = "eBillPhone", nullable = false)
    private String eBillPhone;
    
    @Column(name = "eBillTyp", nullable = false)
    private String eBillTyp;
	
    @Id
    @Column(name = "Id", nullable = false)
    private String id;
    
    @Column(name = "Land", nullable = false)
    private String land;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Ort", nullable = false)
    private String ort;
    
    @Column(name = "PLZ", nullable = false)
    private String plz;

    @Column(name = "Pronomen", nullable = false)
    private String pronomen;
    
    @Column(name = "Rabattschluessel", nullable = false)
    private String deposit;
    
    @Column(name = "Steuersatz", nullable = false)
    private String taxvalue;

    @Column(name = "Strasse", nullable = false)
    private String strasse;

    @Column(name = "UID", nullable = false)
    private String ustid;
    
    @Column(name = "Zahlungsziel", nullable = false)
    private String zahlungsziel;
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getLeitwegId() {
		return leitwegId;
	}

	public void setLeitwegId(String leitwegId) {
		this.leitwegId = leitwegId;
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

	public String geteBillTyp() {
		return eBillTyp;
	}

	public void seteBillTyp(String eBillTyp) {
		this.eBillTyp = eBillTyp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getPronomen() {
		return pronomen;
	}

	public void setPronomen(String pronomen) {
		this.pronomen = pronomen;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getTaxvalue() {
		return taxvalue;
	}

	public void setTaxvalue(String taxvalue) {
		this.taxvalue = taxvalue;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getUstid() {
		return ustid;
	}

	public void setUstid(String ustid) {
		this.ustid = ustid;
	}

	public String getZahlungsziel() {
		return zahlungsziel;
	}

	public void setZahlungsziel(String zahlungsziel) {
		this.zahlungsziel = zahlungsziel;
	}
    
}
