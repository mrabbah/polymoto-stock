
package com.choranet.gesticom;

import com.choranet.commun.SuperService
/**
 * CategorieProduitService Service pour la gestion des opérations
 * transactionnelles pour l'objet CategorieProduit
 */
class CategorieProduitService extends SuperService {

        static transactional = true

        def list() throws Exception {
            return super.list(CategorieProduit.class)
        }
        
        def getCategorieByLibelle(libelle) {
            return CategorieProduit.findByIntituleIlike(libelle)
        }
        
        def getCategorieByCode(code) {
            return CategorieProduit.findByCodeIlike(code)
        }
        
        def getDefaultCategorie() {
            return CategorieProduit.findByCode("01")
        }
}