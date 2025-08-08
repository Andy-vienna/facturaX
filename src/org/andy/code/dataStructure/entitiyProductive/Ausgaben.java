package org.andy.code.dataStructure.entitiyProductive;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tblEx")
public class Ausgaben {

    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Jahr", nullable = false)
    private Integer jahr;

    @Column(name = "Datum", nullable = false)
    private LocalDate datum;

    @Column(name = "Art", nullable = false)
    private String art;

    @Column(name = "netto", precision = 9, scale = 2, nullable = false)
    private BigDecimal netto;

    @Column(name = "Steuersatz", nullable = false)
    private String steuersatz;

    @Column(name = "steuer", precision = 9, scale = 2)
    private BigDecimal steuer;

    @Column(name = "brutto", precision = 9, scale = 2)
    private BigDecimal brutto;

    @Column(name = "dateiname", nullable = false)
    private String dateiname;

    @Lob
    @Column(name = "datei", nullable = false)
    private byte[] datei;

	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getJahr() { return jahr; }
    public void setJahr(Integer jahr) { this.jahr = jahr; }

    public LocalDate getDatum() { return datum; }
    public void setDatum(LocalDate datum) { this.datum = datum; }

    public String getArt() { return art; }
    public void setArt(String art) { this.art = art; }

    public BigDecimal getNetto() { return netto; }
    public void setNetto(BigDecimal netto) { this.netto = netto; }

    public String getSteuersatz() { return steuersatz; }
    public void setSteuersatz(String steuersatz) { this.steuersatz = steuersatz; }

    public BigDecimal getSteuer() { return steuer; }
    public void setSteuer(BigDecimal steuer) { this.steuer = steuer; }

    public BigDecimal getBrutto() { return brutto; }
    public void setBrutto(BigDecimal brutto) { this.brutto = brutto; }

    public String getDateiname() { return dateiname; }
    public void setDateiname(String dateiname) { this.dateiname = dateiname; }

    public byte[] getDatei() { return datei; }
    public void setDatei(byte[] datei) { this.datei = datei; }
}

