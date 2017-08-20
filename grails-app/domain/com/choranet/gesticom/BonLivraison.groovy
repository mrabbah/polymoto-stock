

package com.choranet.gesticom


import java.io.Serializable;

/**
 * BonLivraison Domain Object 
 */
class BonLivraison implements Serializable {	
    private static final long serialVersionUID = 1L;
    	
    String numBL	
    String type	
    String reference	
    Date date	
    Boolean estBonRetour = false
    
    static belongsTo = [bonCommande : BonCommande]
    
    static hasMany = [ligneProduits : LigneProduit]
    
    static mapping = { 
        numBL index : "bonlivraison_num_b_l"
        type index : "bonlivraison_type"
        reference index : "bonlivraison_reference"
        date index : "bonlivraison_date"
        estBonRetour index : "bonlivriason_est_br"
        bonCommande fetch : 'join'
        ligneProduits cascade:"all-delete-orphan"
        //batchSize: 14 
    }
    static constraints = {
    
        numBL(blank : false, nullable : false, unique : true)
        type(nullable : false, inList : ["ACHAT", "VENTE"])
        reference(nullable : true)
        date()
        bonCommande(nullable : false)
        estBonRetour(nullable: false)
    
    }
	
    String toString() {
        return numBL
    }    
    
}

