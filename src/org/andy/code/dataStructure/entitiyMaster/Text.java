package org.andy.code.dataStructure.entitiyMaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblText")
public class Text {
	@Id
    @Column(name = "Id")
    private int id;
	
	@Column(name = "TextUSt")
    private String textUst;
    
    @Column(name = "TextZahlZiel")
    private String textZahlZiel;
    
    @Column(name = "TextAngebot")
    private String textAngebot;

    @Column(name = "TextZahlErin")
    private String textZahlErin;
    
    @Column(name = "TextOrderConfirm")
    private String textOrderConfirm;

    @Column(name = "TextMahnung")
    private String textMahnung;
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
	
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTextUst() {
		return textUst;
	}

	public void setTextUst(String textUst) {
		this.textUst = textUst;
	}

	public String getTextZahlZiel() {
		return textZahlZiel;
	}

	public void setTextZahlZiel(String textZahlZiel) {
		this.textZahlZiel = textZahlZiel;
	}

	public String getTextAngebot() {
		return textAngebot;
	}

	public void setTextAngebot(String textAngebot) {
		this.textAngebot = textAngebot;
	}

	public String getTextZahlErin() {
		return textZahlErin;
	}

	public void setTextZahlErin(String textZahlErin) {
		this.textZahlErin = textZahlErin;
	}

	public String getTextOrderConfirm() {
		return textOrderConfirm;
	}

	public void setTextOrderConfirm(String textOrderConfirm) {
		this.textOrderConfirm = textOrderConfirm;
	}

	public String getTextMahnung() {
		return textMahnung;
	}

	public void setTextMahnung(String textMahnung) {
		this.textMahnung = textMahnung;
	}

}
