package org.andy.code.dataStructure.entitiyMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblLieferant")
public class Lieferant {
    @Id
    @Column(name = "Id", nullable = false)
    private String id;
    
    @Column(name = "kd-nr")
    private String kdnr;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Strasse", nullable = false)
    private String strasse;
    
    @Column(name = "PLZ", nullable = false)
    private String plz;

    @Column(name = "Ort", nullable = false)
    private String ort;
    
    @Column(name = "Land", nullable = false)
    private String land;
    
    @Column(name = "UID", nullable = false)
    private String ustid;
    
    @Column(name = "Steuersatz", nullable = false)
    private String taxvalue;
    
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

	public String getKdnr() {
		return kdnr;
	}

	public void setKdnr(String kdnr) {
		this.kdnr = kdnr;
	}

}
