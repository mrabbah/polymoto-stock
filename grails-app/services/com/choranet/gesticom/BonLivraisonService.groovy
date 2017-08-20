
package com.choranet.gesticom;

import com.choranet.commun.SuperService
/**
 * BonLivraisonService Service pour la gestion des opérations
 * transactionnelles pour l'objet BonLivraison
 */
class BonLivraisonService extends SuperService {

    static transactional = true

    def ligneProduitService
    def bonCommandeService
    def mouvementStockService
    
    def list() throws Exception {
        return super.list(BonLivraison.class)
    }
    
    def getBonsLivraisonPartenaire(type) {
        def criteria = BonLivraison.createCriteria()
        def bls = criteria.list {            
            eq("type", type)
            eq("estBonRetour", false)
        }
        return bls
    }
    
    def getBonsLivraisonBonCommande(bc) {
        def criteria = BonLivraison.createCriteria()
        def bls = criteria.list {
            eq("estBonRetour", false)
            bonCommande {
                eq("numBC", bc.numBC)
            }
        }
        return bls
    }
    
    def save(objet) throws Exception {
        def bc = objet.bonCommande        
        objet = super.save(objet)   
        mettreAjourBc(bl)
        mettreAjourStock(bl)
        return objet
    }
    
    def delete(objet) throws Exception {
        def bc = objet.bonCommande        
        super.delete(objet)                
    }
    
    def update(objet)throws Exception {
        objet = super.update(objet)        
        return objet
    }
    
    private void mettreAjourStock(bl) {
        mouvementStockService.ajouterMouvementBlApresLivraison(bl)
    }
    
    private void mettreAjourBc(bl) {
        def bc = BonCommande.findById(bl.bonCommande.id)
        def bclivree = false
        for(lpBc in bc.ligneProduits) {
            for(lpBl in bl.ligneProduits) {
                if(lpBc.produit.code.equals(lpBl.produit.code)) {
                    lpBc.quantiteLivree += lpBl.quantiteLivree
                    break
                }
            }
        }
        bc.livree = bc.trans_livree
        bonCommandeService.update(bc)
    }
}