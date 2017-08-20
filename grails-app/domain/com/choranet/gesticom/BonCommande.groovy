

package com.choranet.gesticom


import java.io.Serializable;

/**
 * BonCommande Domain Object 
 */
class BonCommande implements Serializable {	
    private static final long serialVersionUID = 1L;
    	
    String numBC	
    String type	
    String reference	
    Date date	
    Double trans_totalht    
    Double totalttcRetour = 0d
    Boolean livree = false
    Boolean paye = false
    Boolean retourPaye = true
    Boolean facture = false
    Boolean bonPret = false  
    Boolean retournee = false
    Boolean avecRetour = false
        
    boolean trans_livree
    boolean trans_retournee
    
    static transients = ['trans_totalht', 'trans_livree', 'trans_retournee']
  
    static hasMany = [ligneProduits : LigneProduit]
    
    static mapping = { 
        numBC index : "boncommande_num_b_c"
        type index : "boncommande_type"
        reference index : "boncommande_reference"
        date index : "boncommande_date"
        livree index : "boncommande_livree"
        retournee index : "boncommande_retournee"
        paye index : "boncommande_paye"
        retourPaye index : "boncommande_retour_paye"
        avecRetour index : "boncommande_avec_retour"
        totalttcRetour index : "boncommande_totalttc_retour"
        bonPret index : "boncommande_bonpret"
        ligneProduits cascade:"all-delete-orphan"
        batchSize : 10 
        lazy : false
    }
    static constraints = {
    
        numBC(blank : false, nullable : false, unique : true)
        type(blank : false, nullable : false, inList : ["ACHAT", "VENTE"])
        reference(nullable : true)
        date(nullable : false)
        livree(nullable : false)
        retournee(nullable : false)
        paye(nullable : false)
        retourPaye(nullable : false)
        avecRetour(nullable : false)
        totalttcRetour(nullable : false)
    
    }
	
    String toString() {
        return numBC
    }
    
    public Double getTrans_totalht() {
        trans_totalht = 0d;
        for(LigneProduit lp : ligneProduits) {
            if(!lp.isAttached() && this.isAttached()) {
                lp.attach()
            }
            trans_totalht += lp.trans_sousTotal ;   
        }
        return trans_totalht;
    }
    
    public boolean isTrans_livree() {
        boolean result = true;
        for(LigneProduit lp : ligneProduits) {
            if(!lp.isAttached() && this.isAttached()) {
                lp.attach()
            }
            result = lp.trans_livree ;  
            if(!result) {
                break;
            }
        }
        return result;
    }
    public boolean isTrans_retournee() {
        boolean result = true;
        for(LigneProduit lp : ligneProduits) {
            if(!lp.isAttached() && this.isAttached()) {
                lp.attach()
            }
            result = lp.trans_retournee ;  
            if(!result) {
                break;
            }
        }
        return result;
    }
    
}
