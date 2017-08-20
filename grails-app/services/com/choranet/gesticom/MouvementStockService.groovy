
package com.choranet.gesticom;

import org.springframework.web.context.request.RequestContextHolder
import java.util.Date
import com.choranet.commun.SuperService

/**
 * MouvementStockService Service pour la gestion des opérations
 * transactionnelles pour l'objet MouvementStock
 */
class MouvementStockService extends SuperService {

    static transactional = true

    def securitestockService
        
    def list() throws Exception {
        return super.list(MouvementStock.class)
    }
    
    def ajouterMouvementBlApresLivraison(bl) {
        //        if(!bl.isAttached()) {
        //            logger.warn("bl n est pas attache")
        //            bl.attach()
        //        }
        for(lp in bl.ligneProduits) {
            //            if(!lp.isAttached()) {
            //                logger.warn("ligne produit n est pas attache")
            //                lp.attach()
            //            }
            def m = new MouvementStock();
            m.date = bl.livraison.date
            m.quantite = lp.quantiteLivree
            m.typeMouvement = bl.type
            m.date_preremption = lp.date_preremption
            if(bl.estBonRetour) {
                m.typeMouvement = "RETOUR " + m.typeMouvement
                if(bl.type.equals("VENTE")) {
                    m.empSource = null//bl.emplacementLivraison
                    m.empDestination = lp.entrepot
                } else if (bl.type.equals("ACHAT")) {
                    m.empSource = lp.entrepot
                    m.empDestination = null
                }
            } else {
                if(bl.type.equals("VENTE")) {
                    m.empSource = lp.entrepot                  
                    m.empDestination = null
                } else if (bl.type.equals("ACHAT")) {
                    m.empSource = null
                    m.empDestination = lp.entrepot
                }
            }
            logger.debug("m.produit = lp.produit")
            m.produit = lp.produit
            logger.debug("save(m)")
            save(m)
            logger.debug("securitestockService.mettreAjourSecuriteStockApresMouvement(m)")
            securitestockService.mettreAjourSecuriteStockApresMouvement(m)
        }
    }
    
    def getPrixMvmSuivantMVStock(entrepot, produit){
        /*def prixDeduit = produit.prixAchat
        def mvs = entrepot.modeValorisation
       
        if (mvs.equals("PRIX_VENTE")) prixDeduit = produit.prixVenteStandard
        if (mvs.equals("PRIX_MOYEN_PENDERE")) prixDeduit = produit.prixMoyenPendere 
       */
        return 0
    }
    
    def ajouterMouvementInventaire(LigneInventaire ligne, Inventaire inv) {
        MouvementStock m = new MouvementStock();
        m.date = new Date();
        m.quantite = ligne.quantiteInv;
        m.typeMouvement = "INVENTAIRE"
        m.empDestination = inv.entrepot
        m.empSource = null
        m.produit = ligne.produit;
        
        m.date_preremption = ligne.date_preremption
        save(m)
        securitestockService.mettreAjourSecuriteStockApresMouvement(m)
    }
    
    def ajouterMouvementTransfert(m) {
        save(m)
        securitestockService.mettreAjourSecuriteStockApresMouvement(m)
    }
    
    def ajouterMouvementUtilisation(m) {
        def entrepot = Entrepot.list()[0]
        m.empDestination = entrepot
        m.empSource = entrepot
        save(m)
        securitestockService.mettreAjourSecuriteStockApresMouvement(m)
    }
    
    def save(Object object) throws Exception {
        def session = RequestContextHolder.currentRequestAttributes().getSession()
        super.save(object)
    }
    
    
    //Pour sécuriser l'application de mettre à jour ou supprimer un mouvement de stock
    def update(Object object) {}
    def delete(Object object) {}
}