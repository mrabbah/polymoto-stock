
package com.choranet.gesticom;

import com.choranet.commun.SuperService

/**
 * LigneInventaireService Service pour la gestion des opérations
 * transactionnelles pour l'objet LigneInventaire
 */
class LigneInventaireService extends SuperService {

        static transactional = true

        def list() throws Exception {
            return super.list(LigneInventaire.class)
        }
}