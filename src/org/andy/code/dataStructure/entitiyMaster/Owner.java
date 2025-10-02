package org.andy.code.dataStructure.entitiyMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblOwner")
public class Owner {
	@Column(name = "Adresse", nullable = false)
    private String adresse;
	
	@Column(name = "Currency", nullable = false)
    private String currency;
	
	@Column(name = "KontaktName", nullable = false)
    private String kontaktName;

    @Column(name = "KontaktMail", nullable = false)
    private String kontaktMail;
    
    @Column(name = "KontaktTel", nullable = false)
    private String kontaktTel;
    
    @Column(name = "Land", nullable = false)
    private String land;
    
    @Id
    @Column(name = "Name", nullable = false)
    private String name;
    
    @Column(name = "Ort", nullable = false)
    private String ort;
    
    @Column(name = "PLZ", nullable = false)
    private String plz;

    @Column(name = "TaxId", nullable = false)
    private String taxid;
    
    @Column(name = "UStId", nullable = false)
    private String ustid;
    
    //###################################################################################################################################################
  	// Getter und Setter für Felder
  	//###################################################################################################################################################

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getKontaktName() {
		return kontaktName;
	}

	public void setKontaktName(String kontaktName) {
		this.kontaktName = kontaktName;
	}

	public String getKontaktMail() {
		return kontaktMail;
	}

	public void setKontaktMail(String kontaktMail) {
		this.kontaktMail = kontaktMail;
	}

	public String getKontaktTel() {
		return kontaktTel;
	}

	public void setKontaktTel(String kontaktTel) {
		this.kontaktTel = kontaktTel;
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

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public String getUstid() {
		return ustid;
	}

	public void setUstid(String ustid) {
		this.ustid = ustid;
	}
    
}
