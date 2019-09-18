/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.studiomascia.gestionale.models;


import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author luigi
 */
@Entity
@Table(name = "xmlfatturabase")
@NamedQueries({
    @NamedQuery(name = "XmlFatturaBase.findAllPassive", query = "SELECT x FROM XmlFatturaBase x WHERE x.attiva IS FALSE"),
    @NamedQuery(name = "XmlFatturaBase.findPassiveNotRegistered", query = "SELECT x FROM XmlFatturaBase x WHERE (x.attiva IS FALSE) AND (x.numeroRegistrazione IS NULL) "),
    @NamedQuery(name = "XmlFatturaBase.findAttiveNotRegistered", query = "SELECT x FROM XmlFatturaBase x WHERE (x.attiva IS TRUE) AND (x.numeroRegistrazione IS NULL) "),
    @NamedQuery(name = "XmlFatturaBase.findAllAttive", query = "SELECT x FROM XmlFatturaBase x WHERE x.attiva IS TRUE")})

public class XmlFatturaBase {

    

 
    
    
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Size(max = 255)
    @Column(name = "file_name")
    private String fileName;
    
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "data_inserimento")
    private Date dataInserimento;
    
    @Size(max = 255)
    @Column(name = "numero_registrazione")
    private String numeroRegistrazione;
    
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "data_registrazione")
    private Date dataRegistrazione;
    
    @Lob
    private String xmlString;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Pagamento> pagamenti;
    
     
    /**
     * @return the pagamenti
     */
    public Set<Pagamento> getPagamenti() {
        return pagamenti;
    }
    /**
     * @param pagamenti the pagamenti to set
     */
    public void setPagamenti(Set<Pagamento> listaPagamenti) {
        this.pagamenti = listaPagamenti;
    }
     
    
    
    
    private boolean attiva;
    
    /**
     * @return the attiva status
     */
    public boolean isAttiva() {
        return this.attiva;
    }

    /**
     * @param attiva the id to set
     */
    public void setAttiva(boolean x ) {
        this.attiva = x;
    }
    
    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the dataInserimento
     */
    public Date getDataInserimento() {
        return dataInserimento;
    }

    /**
     * @param dataInserimento the dataInserimento to set
     */
    public void setDataInserimento(Date dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    /**
     * @return the numeroRegistrazione
     */
    public String getNumeroRegistrazione() {
        return this.numeroRegistrazione;
    }

    /**
     * @param protocolloFattura the numeroRegistrazione to set
     */
    public void setNumeroRegistrazione(String protocolloFattura) {
        this.numeroRegistrazione = protocolloFattura;
    }

    /**
     * @return the dataRegistrazione
     */
    public Date getDataRegistrazione() {
        return this.dataRegistrazione;
    }

    /**
     * @param dataRegistrazione the dataRegistrazione to set
     */
    public void setDataRegistrazione(Date dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    /**
     * @return the stringaXml
     */
    public String getXmlData() {
        return this.xmlString;
    }

    /**
     * @param data the data to set
     */
    public void setXmlData(String data) {
        this.xmlString = data;
    }
    
    public XmlFatturaBase(Integer id) {
        this.id = id;
        this.attiva=false;
    }

    public XmlFatturaBase() {
        this.attiva=false;
    }
    
     public XmlFatturaBase(boolean isAttiva) {
        this.attiva=isAttiva;
    }
     
   
}
