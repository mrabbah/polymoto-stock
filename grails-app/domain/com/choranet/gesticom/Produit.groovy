

package com.choranet.gesticom


import java.io.Serializable;

/**
 * Produit Domain Object 
 */
class Produit implements Serializable {	
    
    private static final long serialVersionUID = 1L;
    	
    String code	
    String conditionnement	
    String designation	
    Double prixAchat
    Boolean perissable
    
    static belongsTo = [empReception : Entrepot , categorieProduit : CategorieProduit]
    
    static mapping = { 
        code index : "produit_code"
        conditionnement index : "produit_conditionnement"
        designation index : "produit_designation"
        prixAchat index : "produit_prix_achat"
        prixMoyenPendere index : "produit_prix_moyen_pendere"
        empReception lazy : false
        categorieProduit lazy : false
        batchSize: 100 
        sort designation: "asc"
    }
    static constraints = {
    
        code(blank : false, nullable : false, unique : false)
        conditionnement(nullable : true, unique : false)
        designation(nullable : false)
        prixAchat(min : 0d, nullable : false)
        empReception(nullable : false)
        categorieProduit(nullable : false)
        perissable()
    }
	
//    @Override
//    public boolean equals(Object o) {
//        return this.code == o.code
//    }
    
    // function pour retourner la difference entre deux listes d'objets Produit
    static def removeAll(listResult, listObject) {
        if (listResult == null || listResult.size()<1 || listObject == null || listObject.size()<1 )
            return false
            
        def listResultfinal = listResult.toList()
        listResultfinal.each { ProdBasic ->
            listObject.each {
                if (ProdBasic.code == it.code) {
                    listResult.remove(ProdBasic)
                }
            }
        }
        if (listResultfinal.size() > listResult.size()) {
            listResult = listResultfinal
            return true
        }
    }
    
    
    String toString() {
        return (designation + " : " + code + " : " + conditionnement)
    }
}

