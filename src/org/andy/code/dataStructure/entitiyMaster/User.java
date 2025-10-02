package org.andy.code.dataStructure.entitiyMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblUser")
public class User {
	@Column(name = "Hash")
    private String hash;
	
	@Id
    @Column(name = "Id")
    private String id;

    @Column(name = "Roles")
    private String roles;
    
    @Column(name = "tabConfig")
    private int tabConfig;

	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public int getTabConfig() {
		return tabConfig;
	}

	public void setTabConfig(int tabConfig) {
		this.tabConfig = tabConfig;
	}
   
}
