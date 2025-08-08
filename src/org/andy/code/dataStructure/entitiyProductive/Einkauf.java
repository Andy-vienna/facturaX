package org.andy.code.dataStructure.entitiyProductive;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tblPu")
public class Einkauf {

    @Id
    @Column(name = "Id", nullable = false)
    private String id;

    @Column(name = "re_datum", nullable = false)
    private LocalDate reDatum;

    @Column(name = "Jahr", nullable = false)
    private int jahr;

    @Column(name = "kred_name", nullable = false)
    private String kredName;

    @Column(name = "kred_strasse", nullable = false)
    private String kredStrasse;

    @Column(name = "kred_plz", nullable = false)
    private String kredPlz;

    @Column(name = "kred_ort", nullable = false)
    private String kredOrt;

    @Column(name = "kred_land", nullable = false)
    private String kredLand;

    @Column(name = "kred_uid", nullable = false)
    private String kredUid;

    @Column(name = "waehrung", nullable = false)
    private String waehrung;

    @Column(name = "steuersatz", nullable = false)
    private String steuersatz;

    @Column(name = "netto", nullable = false, precision = 9, scale = 2)
    private BigDecimal netto;

    @Column(name = "ust", nullable = false, precision = 9, scale = 2)
    private BigDecimal ust;

    @Column(name = "brutto", nullable = false, precision = 9, scale = 2)
    private BigDecimal brutto;

    @Column(name = "anzahlung", nullable = false, precision = 9, scale = 2)
    private BigDecimal anzahlung;

    @Column(name = "zahlungsziel", nullable = false)
    private LocalDate zahlungsziel;

    @Column(name = "hinweis", nullable = false)
    private String hinweis;

    @Column(name = "dateiname", nullable = false)
    private String dateiname;

    @Lob
    @Column(name = "datei", nullable = false)
    private byte[] datei;

    @Column(name = "status", nullable = false)
    private int status;
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getReDatum() {
        return reDatum;
    }
    public void setReDatum(LocalDate reDatum) {
        this.reDatum = reDatum;
    }

    public int getJahr() {
        return jahr;
    }
    public void setJahr(int jahr) {
        this.jahr = jahr;
    }

    public String getKredName() {
        return kredName;
    }
    public void setKredName(String kredName) {
        this.kredName = kredName;
    }

    public String getKredStrasse() {
        return kredStrasse;
    }
    public void setKredStrasse(String kredStrasse) {
        this.kredStrasse = kredStrasse;
    }

    public String getKredPlz() {
        return kredPlz;
    }
    public void setKredPlz(String kredPlz) {
        this.kredPlz = kredPlz;
    }

    public String getKredOrt() {
        return kredOrt;
    }
    public void setKredOrt(String kredOrt) {
        this.kredOrt = kredOrt;
    }

    public String getKredLand() {
        return kredLand;
    }
    public void setKredLand(String kredLand) {
        this.kredLand = kredLand;
    }

    public String getKredUid() {
        return kredUid;
    }
    public void setKredUid(String kredUid) {
        this.kredUid = kredUid;
    }

    public String getWaehrung() {
        return waehrung;
    }
    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }

    public String getSteuersatz() {
        return steuersatz;
    }
    public void setSteuersatz(String steuersatz) {
        this.steuersatz = steuersatz;
    }

    public BigDecimal getNetto() {
        return netto;
    }
    public void setNetto(BigDecimal netto) {
        this.netto = netto;
    }

    public BigDecimal getUst() {
        return ust;
    }
    public void setUst(BigDecimal ust) {
        this.ust = ust;
    }

    public BigDecimal getBrutto() {
        return brutto;
    }
    public void setBrutto(BigDecimal brutto) {
        this.brutto = brutto;
    }

    public BigDecimal getAnzahlung() {
        return anzahlung;
    }
    public void setAnzahlung(BigDecimal anzahlung) {
        this.anzahlung = anzahlung;
    }

    public LocalDate getZahlungsziel() {
        return zahlungsziel;
    }
    public void setZahlungsziel(LocalDate zahlungsziel) {
        this.zahlungsziel = zahlungsziel;
    }

    public String getHinweis() {
        return hinweis;
    }
    public void setHinweis(String hinweis) {
        this.hinweis = hinweis;
    }

    public String getDateiname() {
        return dateiname;
    }
    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    public byte[] getDatei() {
        return datei;
    }
    public void setDatei(byte[] datei) {
        this.datei = datei;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}

