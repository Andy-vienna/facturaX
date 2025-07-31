package org.andy.code.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tblOwner")
public class Owner {
	@Id
    @Column(name = "Name")
    private String name;
    
    @Column(name = "Adresse")
    private String adresse;
    
    @Column(name = "PLZ")
    private String plz;

    @Column(name = "Ort")
    private String ort;
    
    @Column(name = "Land")
    private String land;
    
    @Column(name = "UStId")
    private String ustid;
    
    @Column(name = "KontaktName")
    private String kontaktName;

    @Column(name = "KontaktTel")
    private String kontaktTel;
    
    @Column(name = "KontaktMail")
    private String kontaktMail;
    
    @Column(name = "Currency")
    private String currency;
    
    @Column(name = "TaxId")
    private String taxid;
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
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

	public String getUstid() {
		return ustid;
	}

	public void setUstid(String ustid) {
		this.ustid = ustid;
	}

	public String getKontaktName() {
		return kontaktName;
	}

	public void setKontaktName(String kontaktName) {
		this.kontaktName = kontaktName;
	}

	public String getKontaktTel() {
		return kontaktTel;
	}

	public void setKontaktTel(String kontaktTel) {
		this.kontaktTel = kontaktTel;
	}

	public String getKontaktMail() {
		return kontaktMail;
	}

	public void setKontaktMail(String kontaktMail) {
		this.kontaktMail = kontaktMail;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}
}
