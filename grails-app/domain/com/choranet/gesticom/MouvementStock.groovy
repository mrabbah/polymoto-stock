

package com.choranet.gesticom


import java.io.Serializable;

/**
 * MouvementStock Domain Object 
 */
class MouvementStock implements Serializable {	
    private static final long serialVersionUID = 1L;
    	
    Date date	
    Double quantite	
    String typeMouvement
    Date date_preremption
    
    static belongsTo = [empSource : Entrepot , empDestination : Entrepot , produit : Produit]
    
    static mapping = { 
        date index : "mouvementstock_date"
        quantite index : "mouvementstock_quantite"
        typeMouvement index : "mouvementstock_type_mouvement"
        empSource lazy : false
        empDestination lazy : false
        produit lazy : false
        batchSize: 14 
    }
    static constraints = {
    
        date()
        quantite()
        typeMouvement(inList : ["ACHAT", "VENTE", "TRANSFERT", "RETOUR ACHAT", "RETOUR VENTE", "INVENTAIRE", "AFFECTATION CHANTIER", "RETOUR CHANTIER", "UTILISATION", "RECEPTION"])
        empDestination(nullable : true)
        empSource(nullable : true)
        date_preremption(nullable : true)
    
    }
	
    String toString() {
        return super.toString()
    }
}

