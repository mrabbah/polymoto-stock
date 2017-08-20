
package com.choranet.gesticom;

import org.springframework.web.context.request.RequestContextHolder
import com.choranet.commun.SuperService
/**
 * BonCommandeService Service pour la gestion des opérations
 * transactionnelles pour l'objet BonCommande
 */
class BonCommandeService extends SuperService {

    static transactional = true

    def ligneProduitService
    
    def list() throws Exception {
        return super.list(BonCommande.class)
    }
        
    def save(objet) throws Exception {
             
        def session = RequestContextHolder.currentRequestAttributes().getSession()
        super.save(objet)
    }
        
    def getSequenceSuivante(objet) {
        def resultat = 0;
        for(LigneProduit lp : objet.ligneProduits) {
            if(lp.sequenceLigne > resultat) {
                resultat = lp.sequenceLigne;
            }   
        }
        resultat++;
        return resultat;
    }
    
    def getBonCommandeNonLivree(type) {
        def c = BonCommande.createCriteria()
        def result = c.list {
            eq("type", type)
            eq("livree", false)
        }
        return result
    }
    def getBonsCommande(type) {
        def c = BonCommande.createCriteria()
        def result = c.list {
            eq("type", type)
            eq("bonPret", false)
        }
        return result
    }
    
    
    def getBonCommandeBetwen(type,date1,date2) {
        def criteria = BonCommande.createCriteria()
        def result = criteria.list {
            between("date", date1, date2)
            eq("type",type)
        }
        return result
    }
    
}
