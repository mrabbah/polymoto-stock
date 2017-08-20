

package com.choranet.gesticom


import java.io.Serializable;

/**
 * Securitestock Domain Object 
 */
class Securitestock implements Serializable {	
    private static final long serialVersionUID = 1L;
    	
    Double stockMin   = 1d	
    Double stockMax   = 1000d	
    Double stockReel  = 0d
    Date date_preremption
    
    static belongsTo = [entrepot : Entrepot , produit : Produit]
    
    static mapping = { 
        stockMin index : "securitestock_stock_min"
        stockMax index : "securitestock_stock_max"
        stockReel index : "securitestock_stock_reel"
        entrepot lazy : false
        produit lazy : false
        batchSize: 14 
    }
    
    static constraints = {
    
        stockMin(nullable : false, min : 0d)
        stockMax(nullable : false, min : 0d)
        stockReel(nullable : false)
        date_preremption(nullable : true)
    
    }
	
    String toString() {
        return super.toString()
    }
}

