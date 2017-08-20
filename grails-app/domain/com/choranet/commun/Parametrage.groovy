package com.choranet.commun

import java.io.Serializable;


class Parametrage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    Boolean afficherRemiseFacture
    Boolean afficherTvaFacture
    Boolean produitPerissableManaged
    Boolean multiEntrepotEnabled = false
    
    static constraints = {
    }
        
    public boolean isProduitPerissableManaged() {
        return produitPerissableManaged;
    }
    
    public boolean isAfficherRemiseFacture() {
        return afficherRemiseFacture;
    }
    
    public boolean isAfficherTvaFacture() {
        return afficherTvaFacture;
    }
    
    public boolean isMultiEntrepotEnabled() {
        return multiEntrepotEnabled;
    }
}
