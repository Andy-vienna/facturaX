package org.andy.code.entityMaster;

import jakarta.persistence.*;

@Entity
@Table(name = "tblBank")
public class Bank {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "BankName")
    private String bankName;
    
    @Column(name = "IBAN")
    private String iban;
    
    @Column(name = "BIC")
    private String bic;

    @Column(name = "Kontoinhaber")
    private String ktoName;

	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
    	this.id = id;
    }

    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban != null ? iban.toUpperCase() : null;
    }
    
    public String getBic() {
        return bic;
    }
    
    public void setBic(String bic) {
        this.bic = bic != null ? bic.toUpperCase() : null;
    }
    
    public String getKtoName() {
        return ktoName;
    }
    
    public void setKtoName(String ktoName) {
        this.ktoName = ktoName;
    }
}
