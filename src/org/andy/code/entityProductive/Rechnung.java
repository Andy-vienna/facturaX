package org.andy.code.entityProductive;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tblRe")
public class Rechnung {

    @Id
    @Column(name = "IdNummer", length = 12, nullable = false)
    private String idNummer;
    
    @Column(name = "Jahr", nullable = false)
	private int jahr;

    @Column(name = "activeState", nullable = false)
    private char activeState;

    @Column(name = "printState", nullable = false)
    private char printState;

    @Column(name = "moneyState", nullable = false)
    private char moneyState;

    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @Column(name = "Datum", nullable = false)
    private LocalDate datum;

    @Column(name = "LZeitr", nullable = false, length = 50)
    private String lZeitr;

    @Column(name = "Ref", nullable = false, columnDefinition = "varchar(max)")
    private String ref;

    @Column(name = "IdKunde", nullable = false, length = 50)
    private String idKunde;

    @Column(name = "RevCharge", length = 50)
    private String revCharge;

    @Column(name = "IdBank", nullable = false, length = 50)
    private String idBank;

    @Column(name = "Netto", nullable = false, length = 50)
    private BigDecimal netto;

    @Column(name = "USt", nullable = false, length = 50)
    private BigDecimal ust;

    @Column(name = "Brutto", nullable = false, length = 50)
    private BigDecimal brutto;

    @Column(name = "AnzPos", nullable = false, length = 50)
    private String anzPos;

    @Column(name = "IdArt01", nullable = false, columnDefinition = "varchar(max)")
    private String idArt01;
    @Column(name = "Menge01", nullable = false, length = 50)
    private BigDecimal menge01;
    @Column(name = "EPreis01", nullable = false, length = 50)
    private BigDecimal ePreis01;

    @Column(name = "IdArt02", columnDefinition = "varchar(max)")
    private String idArt02;
    @Column(name = "Menge02", length = 50)
    private BigDecimal menge02;
    @Column(name = "EPreis02", length = 50)
    private BigDecimal ePreis02;

    @Column(name = "IdArt03", columnDefinition = "varchar(max)")
    private String idArt03;
    @Column(name = "Menge03", length = 50)
    private BigDecimal menge03;
    @Column(name = "EPreis03", length = 50)
    private BigDecimal ePreis03;

    @Column(name = "IdArt04", columnDefinition = "varchar(max)")
    private String idArt04;
    @Column(name = "Menge04", length = 50)
    private BigDecimal menge04;
    @Column(name = "EPreis04", length = 50)
    private BigDecimal ePreis04;

    @Column(name = "IdArt05", columnDefinition = "varchar(max)")
    private String idArt05;
    @Column(name = "Menge05", length = 50)
    private BigDecimal menge05;
    @Column(name = "EPreis05", length = 50)
    private BigDecimal ePreis05;

    @Column(name = "IdArt06", columnDefinition = "varchar(max)")
    private String idArt06;
    @Column(name = "Menge06", length = 50)
    private BigDecimal menge06;
    @Column(name = "EPreis06", length = 50)
    private BigDecimal ePreis06;

    @Column(name = "IdArt07", columnDefinition = "varchar(max)")
    private String idArt07;
    @Column(name = "Menge07", length = 50)
    private BigDecimal menge07;
    @Column(name = "EPreis07", length = 50)
    private BigDecimal ePreis07;

    @Column(name = "IdArt08", columnDefinition = "varchar(max)")
    private String idArt08;
    @Column(name = "Menge08", length = 50)
    private BigDecimal menge08;
    @Column(name = "EPreis08", length = 50)
    private BigDecimal ePreis08;

    @Column(name = "IdArt09", columnDefinition = "varchar(max)")
    private String idArt09;
    @Column(name = "Menge09", length = 50)
    private BigDecimal menge09;
    @Column(name = "EPreis09", length = 50)
    private BigDecimal ePreis09;

    @Column(name = "IdArt10", columnDefinition = "varchar(max)")
    private String idArt10;
    @Column(name = "Menge10", length = 50)
    private BigDecimal menge10;
    @Column(name = "EPreis10", length = 50)
    private BigDecimal ePreis10;

    @Column(name = "IdArt11", columnDefinition = "varchar(max)")
    private String idArt11;
    @Column(name = "Menge11", length = 50)
    private BigDecimal menge11;
    @Column(name = "EPreis11", length = 50)
    private BigDecimal ePreis11;

    @Column(name = "IdArt12", columnDefinition = "varchar(max)")
    private String idArt12;
    @Column(name = "Menge12", length = 50)
    private BigDecimal menge12;
    @Column(name = "EPreis12", length = 50)
    private BigDecimal ePreis12;

    // Getter & Setter (kann deine IDE generieren)
}

