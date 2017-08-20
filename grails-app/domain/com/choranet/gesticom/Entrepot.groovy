

package com.choranet.gesticom


import java.io.Serializable;
import org.apache.commons.lang.builder.*

/**
 * Entrepot Domain Object 
 */
class Entrepot implements Serializable {	
    
    private static final long serialVersionUID = 1L;
    	
    String code	
    String intitule	
    
    static mapping = { 
        code index : "entrepot_code"
        intitule index : "entrepot_intitule"
        cache usage : 'nonstrict-read-write'
        //batchSize: 14 
    }
    
    static constraints = {
        code(blank : false, nullable : false, unique : true)
        intitule(blank : false, nullable : false, unique : true)
    }
    
    static def remove(listEntrepots, entrepotASupprime) {
        def newEntrepotList = []
        
        if (listEntrepots == null)
        return null
            
        if (entrepotASupprime == null) 
        return listEntrepots
            
        listEntrepots.each { obj ->
            if ( obj != null && (obj.code != entrepotASupprime.code)) {
                newEntrepotList.add(obj)
            }
        }
        return newEntrepotList
    }
    
	
    String toString() {
        return (code + " : " + intitule)
    }
    
    @Override 
    boolean equals(final Object that) { 
            EqualsBuilder.reflectionEquals(this, that, ["code"]) 
    } 

    @Override 
    int hashCode() { 
            HashCodeBuilder.reflectionHashCode(this, ["code"]) 
    } 
}

