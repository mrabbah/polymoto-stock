
package com.choranet.gesticom;

import com.choranet.commun.SuperService

/**
 * LigneProduitService Service pour la gestion des opérations
 * transactionnelles pour l'objet LigneProduit
 */
class LigneProduitService extends SuperService {

    static transactional = true

    def list() throws Exception {
        return super.list(LigneProduit.class)
    }        
    
}