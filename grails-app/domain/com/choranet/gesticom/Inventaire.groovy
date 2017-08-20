

package com.choranet.gesticom


import java.io.Serializable;
import java.util.Date;

/**
 * Inventaire Domain Object 
 */
class Inventaire implements Serializable {	
    
    private static final long serialVersionUID = 1L;
    	
    Date dateDebut
    Date dateFin
    String details	
    String etat
       		   
    static belongsTo = [entrepot : Entrepot]
    
    static hasMany = [ligneInventaires : LigneInventaire]
    
    static mapping = { 
        date index : "inventaire_date"
        details index : "inventaire_details"
        entrepot fetch : 'join'
        ligneInventaires lazy : false, cascade:"all-delete-orphan", batchSize : 50
        //batchSize: 14 
    }
    static constraints = {
        dateDebut(nullable : false)    
        dateFin(nullable : true)    
        details(nullable : true, maxSize : 1000)
        etat(nullable : false, inList : ["EN COURS", "SUSPENDU", "ACHEVE", "VALIDE"])
        entrepot(nullable : false)
        ligneInventaires(nullable : true)
    }
	
    String toString() {
        String result = "Inventaire effectué entre le " + dateDebut
        if(dateFin != null) {
            result += " et le " + dateFin
        }
        return  result
    }
}

