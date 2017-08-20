package com.choranet.commun

import java.io.IOException
import org.zkoss.image.AImage
import org.zkoss.image.Image
import com.choranet.gesticom.util.Utilitaire
import java.io.Serializable;
import org.apache.commons.lang.builder.* 

class ChoraClientInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    String raisonSociale	
   
    String formeJuridique
    
    String telephone
    
    String fax
    
    String email
    
    String patente
    
    String rc
    
    String idF
    
    String cnss
    
    String site

    String repertoirBackup
    
    String adresse
    
    String codePostale
    
    String ville
    
    String pays
    
    byte[] logoData
    byte[] cachetData
    byte[] entetepiedData
    byte[] copieData
    
    Image trans_logo
    Image trans_cachet
    Image trans_entetepied
    Image trans_copie
        
    static transients = ['trans_logo', 'trans_cachet', 'trans_entetepied', 'trans_copie']
    
    
    static constraints = {
        raisonSociale(blank:false, nullable : false)
        email(email:true, nullable: true)
        formeJuridique(nullable: true)
        telephone(nullable: true)
        fax(nullable: true)
        patente(nullable: true)
        rc(nullable: true)
        idF(nullable: true)
        cnss(nullable: true)
        site(nullable: true)
        repertoirBackup(nullable: true)
        adresse (nullable: true)
        codePostale (nullable: true)
        ville (nullable: true)
        pays (nullable: true)
        cachetData(nullable: true)
        entetepiedData(nullable: true)
        copieData(nullable: true)
    }
    
    static mapping = { 
        //modulesActifs lazy : false
        regime fetch : 'join'
        cache usage : 'nonstrict-read-write', include : 'non-lazy'
    }
    
    public void setLogoData(byte[] bd) {
        logoData = bd
        if(logoData != null) {
            //immatriculation = immatriculation1+immatriculation2+immatriculation3
            trans_logo = new AImage("logo", bd);
        }
    }

    public void setTrans_logo(Image img) {        
        if(img == null) {            
            img = Utilitaire.getLogoImage(getClass())
        }
        trans_logo = img
        logoData = trans_logo.getByteData()     
    }
    
    public void setCachetData(byte[] bd) {
        cachetData = bd
        if(cachetData != null) {
            trans_cachet = new AImage("cachet", bd);
        }
    }

    public void setTrans_cachet(Image img) {        
        if(img == null) {            
            img = Utilitaire.getCachetImage(getClass())
        }
        trans_cachet = img
        cachetData = trans_cachet.getByteData() 
    }
    
    public void setEntetepiedData(byte[] bd) {
        entetepiedData = bd
        if(entetepiedData != null) {
            trans_entetepied = new AImage("entetepied", bd);
        }
    }

    public void setTrans_entetepied(Image img) {        
        if(img == null) {            
            img = Utilitaire.getFondImage(getClass())
        }    
        trans_entetepied = img
        entetepiedData = trans_entetepied.getByteData()   
        
    }
    
    public void setCopieData(byte[] bd) {
        copieData = bd
        if(copieData != null) {
            trans_copie = new AImage("copie", bd);
        }
    }

    public void setTrans_copie(Image img) {        
        if(img == null) {            
            img = Utilitaire.getCopieImage(getClass())
        }    
        trans_copie = img
        copieData = trans_copie.getByteData()   

    }
    
    public String toString() {
        return raisonSociale
    }
    
    @Override 
    boolean equals(final Object that) { 
            EqualsBuilder.reflectionEquals(this, that, ["raisonSociale"]) 
    } 

    @Override 
    int hashCode() { 
            HashCodeBuilder.reflectionHashCode(this, ["raisonSociale"]) 
    } 
    
}
