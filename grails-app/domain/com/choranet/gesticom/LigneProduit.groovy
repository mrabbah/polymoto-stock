
package com.choranet.gesticom

import java.io.Serializable;

/**
 * LigneProduit Domain Object 
 */
class LigneProduit implements Serializable {	
    
    private static final long serialVersionUID = 1L;
    	
    Integer sequenceLigne	
    Double quantite = 1	
    Double quantiteLivree = 0d
    Double quantiteRetournee = 0d
    Double remise = 0d
    Double prix
    Double prixDeduit
    String type

    Double trans_sousTotal
    Double trans_sousTotalLivree
    Double trans_sousTotalRetourne
    boolean trans_livree
    boolean trans_retournee
    Double trans_ql
    Double trans_qr
    Date date_preremption
    
    static transients = ['trans_sousTotal', 'trans_livree', 'trans_retournee', 'trans_ql', 'trans_qr', 'trans_sousTotalLivree', 'trans_sousTotalRetourne']
    
    static belongsTo = [produit : Produit, entrepot : Entrepot]
    
    static mapping = { 
        sequenceLigne index : "ligneproduit_sequence_ligne"
        quantite index : "ligneproduit_quantite"
        quantiteLivree index : "ligneproduit_quantite_livree"
        quantiteRetournee index : "ligneproduit_quantite_retournee"
        remise index : "ligneproduit_remise"
        prixDeduit index : "ligneproduit_prix_deduit"
        prix index : "ligneproduit_prix"
        type index : "ligneproduit_type"
        produit fetch : 'join'
        //entrepot lazy : false
    }
    static constraints = {
        sequenceLigne(nullable : false)
        quantite(min : 1d, nullable : false)
        quantiteLivree(min : 0d, nullable : false)
        quantiteRetournee(min : 0d, nullable : false)
        remise(min : 0d, nullable : false)
        prixDeduit(min : 0d, nullable : false)
        type(nullable : false, inList : ["ACHAT", "VENTE"])
        entrepot(nullable : true)
        date_preremption(nullable : true)
    }

    public void mettreAjourEntrepot() {
        entrepot = produit.empReception
    }

    String toString() {
        return (produit.designation + " " + sequenceLigne + " " + quantite)
    }
    
    public Double getTrans_sousTotal() {
        if(prixDeduit == null)
            return 0d;
        return prixDeduit * (1 - remise / 100) * quantite
    }
    
    public Double getTrans_sousTotalLivree() {
        if(prixDeduit == null)
            return 0d;
        //return prixDeduit * quantiteLivree
        return prixDeduit * (1 - remise / 100) * quantiteLivree
    }
    
    public Double getTrans_sousTotalRetourne() {
        if(prixDeduit == null)
            return 0d;
        //return prixDeduit * quantiteRetournee
        return prixDeduit * (1 - remise / 100) * quantiteRetournee
    }
    
    public void setType(String type) {
        this.type = type    
        if(produit != null && prix == null) {
            prix = produit.prixAchat
        }
    }
    
    public void setProduit(Produit p) {
        this.produit = p
        if(type != null && prix == null) {
            prix = produit.prixAchat
        }
    }
    
    public boolean isTrans_livree() {
        return (quantite == quantiteLivree);
    }
    
    public boolean isTrans_retournee() {
        return (quantiteRetournee == quantiteLivree);
    }
}

