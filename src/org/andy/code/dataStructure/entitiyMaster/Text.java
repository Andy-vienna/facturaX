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
	
	@Column(name = "VarTextAngebot")
    private String varTextAngebot;
	
	@Column(name = "TextAngebot")
    private String textAngebot;
	
	@Column(name = "VarTextAngebotRev")
    private String varTextAngebotRev;
	
	@Column(name = "TextAngebotRev")
    private String textAngebotRev;
	
	@Column(name = "VarTextBestellung")
    private String varTextBestellung;
	
	@Column(name = "TextBestellung")
    private String textBestellung;
	
	@Column(name = "VarTextLieferschein")
    private String varTextLieferschein;
	
	@Column(name = "TextLieferschein")
    private String textLieferschein;
	
	@Column(name = "VarTextMahnungStufe1")
    private String varTextMahnungStufe1;
	
	@Column(name = "TextMahnungStufe1")
    private String textMahnungStufe1;
	
	@Column(name = "VarTextMahnungStufe2")
    private String varTextMahnungStufe2;
	
	@Column(name = "TextMahnungStufe2")
    private String textMahnungStufe2;
	
	@Column(name = "VarTextOrderConfirm")
    private String varTextOrderConfirm;
	
	@Column(name = "TextOrderConfirm")
    private String textOrderConfirm;
	
	@Column(name = "VarTextRechnung")
    private String varTextRechnung;
    
    @Column(name = "TextRechnung")
    private String textRechnung;
	
	@Column(name = "VarTextZahlErin")
    private String varTextZahlErin;
	
	@Column(name = "TextZahlErin")
    private String textZahlErin;
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVarTextAngebot() {
		return varTextAngebot;
	}

	public void setVarTextAngebot(String varTextAngebot) {
		this.varTextAngebot = varTextAngebot;
	}

	public String getTextAngebot() {
		return textAngebot;
	}

	public void setTextAngebot(String textAngebot) {
		this.textAngebot = textAngebot;
	}

	public String getVarTextAngebotRev() {
		return varTextAngebotRev;
	}

	public void setVarTextAngebotRev(String varTextAngebotRev) {
		this.varTextAngebotRev = varTextAngebotRev;
	}

	public String getTextAngebotRev() {
		return textAngebotRev;
	}

	public void setTextAngebotRev(String textAngebotRev) {
		this.textAngebotRev = textAngebotRev;
	}

	public String getVarTextBestellung() {
		return varTextBestellung;
	}

	public void setVarTextBestellung(String varTextBestellung) {
		this.varTextBestellung = varTextBestellung;
	}

	public String getTextBestellung() {
		return textBestellung;
	}

	public void setTextBestellung(String textBestellung) {
		this.textBestellung = textBestellung;
	}

	public String getVarTextLieferschein() {
		return varTextLieferschein;
	}

	public void setVarTextLieferschein(String varTextLieferschein) {
		this.varTextLieferschein = varTextLieferschein;
	}

	public String getTextLieferschein() {
		return textLieferschein;
	}

	public void setTextLieferschein(String textLieferschein) {
		this.textLieferschein = textLieferschein;
	}

	public String getVarTextMahnungStufe1() {
		return varTextMahnungStufe1;
	}

	public void setVarTextMahnungStufe1(String varTextMahnungStufe1) {
		this.varTextMahnungStufe1 = varTextMahnungStufe1;
	}

	public String getTextMahnungStufe1() {
		return textMahnungStufe1;
	}

	public void setTextMahnungStufe1(String textMahnungStufe1) {
		this.textMahnungStufe1 = textMahnungStufe1;
	}
	
	public String getVarTextMahnungStufe2() {
		return varTextMahnungStufe2;
	}

	public void setVarTextMahnungStufe2(String varTextMahnungStufe2) {
		this.varTextMahnungStufe2 = varTextMahnungStufe2;
	}

	public String getTextMahnungStufe2() {
		return textMahnungStufe2;
	}

	public void setTextMahnungStufe2(String textMahnungStufe2) {
		this.textMahnungStufe2 = textMahnungStufe2;
	}

	public String getVarTextOrderConfirm() {
		return varTextOrderConfirm;
	}

	public void setVarTextOrderConfirm(String varTextOrderConfirm) {
		this.varTextOrderConfirm = varTextOrderConfirm;
	}

	public String getTextOrderConfirm() {
		return textOrderConfirm;
	}

	public void setTextOrderConfirm(String textOrderConfirm) {
		this.textOrderConfirm = textOrderConfirm;
	}

	public String getVarTextRechnung() {
		return varTextRechnung;
	}

	public void setVarTextRechnung(String varTextRechnung) {
		this.varTextRechnung = varTextRechnung;
	}

	public String getTextRechnung() {
		return textRechnung;
	}

	public void setTextRechnung(String textRechnung) {
		this.textRechnung = textRechnung;
	}

	public String getVarTextZahlErin() {
		return varTextZahlErin;
	}

	public void setVarTextZahlErin(String varTextZahlErin) {
		this.varTextZahlErin = varTextZahlErin;
	}

	public String getTextZahlErin() {
		return textZahlErin;
	}

	public void setTextZahlErin(String textZahlErin) {
		this.textZahlErin = textZahlErin;
	}
	
}
