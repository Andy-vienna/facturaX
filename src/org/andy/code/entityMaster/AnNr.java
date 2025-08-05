package org.andy.code.entityMaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblAN")
public class AnNr {
	@Id
    @Column(name = "Id", length = 12, nullable = false)
    private String anNr;
		
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getAnNr() {
		return anNr;
	}
	
	public void setAnNr(String anNr) {
		this.anNr = anNr;
	}
	
}
