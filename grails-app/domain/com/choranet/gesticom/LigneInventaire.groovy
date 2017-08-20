

package com.choranet.gesticom


import java.io.Serializable;

/**
 * LigneInventaire Domain Object 
 */
class LigneInventaire implements Serializable {	
   
    private static final long serialVersionUID = 1L;
    	
    Double quantiteInv	
    Double qantiteActuele
    Date date_preremption
    
    String trans_style
    
    static transients = ['trans_style']
    
    static belongsTo = [produit : Produit]
    
    static mapping = { 
        quantiteInv index : "ligneinventaire_quantite_inv"
        qantiteActuele index : "ligneinventaire_qantite_actuele"
        produit fetch : 'join'
        //batchSize: 14 
    }
    static constraints = {
        quantiteInv(min : 0d, nullable : false)
        qantiteActuele(min : 0d, nullable : false)
        produit(nullable : false)
        date_preremption(nullable : true)
    }
	
    String toString() {
        if(!produit.isAttached()) {
            produit.attach()
        }
        return produit.toString()
    }
    
    public String getTrans_style() {
        def resultat = "font-weight: bold;color: #060;"
        if(quantiteInv != null && qantiteActuele != null && qantiteActuele != quantiteInv) {
            resultat = "font-weight: bold;color: #F81105;"
        }
        return resultat
    }
}

