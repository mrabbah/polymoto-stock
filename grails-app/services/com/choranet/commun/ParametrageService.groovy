package com.choranet.commun

import com.choranet.commun.SuperService


class ParametrageService extends SuperService {

        static transactional = true

        def list() throws Exception {
            return super.list(Parametrage.class)
        }
        
        def getConfig() throws Exception {
            def config = this.list()
            def objet
            if (config == null){
                objet = new Parametrage()
                objet.afficherRemiseFacture = false
                objet.afficherTvaFacture = false
                objet.produitPerissableManaged = true
                objet = this.save(objet)
            }else {
                objet = config.list()[0]
            }
            return objet
        }
        
        def isProduitPerissableManaged() throws Exception {
            return true
        }
        
        def isMultiEntrepotEnabled() throws Exception {
            return false
        }
}