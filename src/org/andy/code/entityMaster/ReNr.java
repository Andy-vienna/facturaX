package org.andy.code.entityMaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblRE")
public class ReNr {
	@Id
	@Column(name = "Id", length = 12, nullable = false)
	private String reNr;
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public String getReNr() {
		return reNr;
	}

	public void setReNr(String reNr) {
		this.reNr = reNr;
	}
	
	
}
