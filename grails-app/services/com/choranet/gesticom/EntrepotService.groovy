
package com.choranet.gesticom;

import com.choranet.commun.SuperService

/**
 * EntrepotService Service pour la gestion des opérations
 * transactionnelles pour l'objet Entrepot
 */
class EntrepotService extends SuperService {

    static transactional = true

    def list() throws Exception {
        return super.list(Entrepot.class)
    }
    
    def getDefaultEntrepot() {
        return Entrepot.findByCode("01")
    }
    
    def getEntrepotByTitre(er) {
        return Entrepot.findByIntituleIlike(er)
    }
}