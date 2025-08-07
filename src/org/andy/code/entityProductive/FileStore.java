package org.andy.code.entityProductive;

import jakarta.persistence.*;

@Entity
@Table(name = "tblFiles")
public class FileStore {

    @Id
    @Column(name = "IdNummer", nullable = false)
    private String idNummer;

    @Column(name = "Year", nullable = false)
    private int year;

    @Column(name = "ANFileName")
    private String anFileName;

    @Lob
    @Column(name = "ANpdfFile")
    private byte[] anPdfFile;

    @Column(name = "ABFIleName")
    private String abFileName;

    @Lob
    @Column(name = "ABpdfFIle")
    private byte[] abPdfFile;

    @Column(name = "BEFileName")
    private String beFileName;

    @Lob
    @Column(name = "BEpdfFile")
    private byte[] bePdfFile;

    @Column(name = "REFileName")
    private String reFileName;

    @Lob
    @Column(name = "REpdfFile")
    private byte[] rePdfFile;

    @Column(name = "AddFileName01")
    private String addFileName01;

    @Lob
    @Column(name = "AddFile01")
    private byte[] addFile01;

    @Column(name = "AddFileName02")
    private String addFileName02;

    @Lob
    @Column(name = "AddFile02")
    private byte[] addFile02;

    @Column(name = "AddFileName03")
    private String addFileName03;

    @Lob
    @Column(name = "AddFile03")
    private byte[] addFile03;

	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

    public String getIdNummer() {
        return idNummer;
    }

    public void setIdNummer(String idNummer) {
        this.idNummer = idNummer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAnFileName() {
        return anFileName;
    }

    public void setAnFileName(String anFileName) {
        this.anFileName = anFileName;
    }

    public byte[] getAnPdfFile() {
        return anPdfFile;
    }

    public void setAnPdfFile(byte[] anPdfFile) {
        this.anPdfFile = anPdfFile;
    }

    public String getAbFileName() {
        return abFileName;
    }

    public void setAbFileName(String abFileName) {
        this.abFileName = abFileName;
    }

    public byte[] getAbPdfFile() {
        return abPdfFile;
    }

    public void setAbPdfFile(byte[] abPdfFile) {
        this.abPdfFile = abPdfFile;
    }

    public String getBeFileName() {
        return beFileName;
    }

    public void setBeFileName(String beFileName) {
        this.beFileName = beFileName;
    }

    public byte[] getBePdfFile() {
        return bePdfFile;
    }

    public void setBePdfFile(byte[] bePdfFile) {
        this.bePdfFile = bePdfFile;
    }

    public String getReFileName() {
        return reFileName;
    }

    public void setReFileName(String reFileName) {
        this.reFileName = reFileName;
    }

    public byte[] getRePdfFile() {
        return rePdfFile;
    }

    public void setRePdfFile(byte[] rePdfFile) {
        this.rePdfFile = rePdfFile;
    }

    public String getAddFileName01() {
        return addFileName01;
    }

    public void setAddFileName01(String addFileName01) {
        this.addFileName01 = addFileName01;
    }

    public byte[] getAddFile01() {
        return addFile01;
    }

    public void setAddFile01(byte[] addFile01) {
        this.addFile01 = addFile01;
    }

    public String getAddFileName02() {
        return addFileName02;
    }

    public void setAddFileName02(String addFileName02) {
        this.addFileName02 = addFileName02;
    }

    public byte[] getAddFile02() {
        return addFile02;
    }

    public void setAddFile02(byte[] addFile02) {
        this.addFile02 = addFile02;
    }

    public String getAddFileName03() {
        return addFileName03;
    }

    public void setAddFileName03(String addFileName03) {
        this.addFileName03 = addFileName03;
    }

    public byte[] getAddFile03() {
        return addFile03;
    }

    public void setAddFile03(byte[] addFile03) {
        this.addFile03 = addFile03;
    }
}
