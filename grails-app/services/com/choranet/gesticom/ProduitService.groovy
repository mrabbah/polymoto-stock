
package com.choranet.gesticom;

import com.choranet.commun.SuperService

/**
 * ProduitService Service pour la gestion des opérations
 * transactionnelles pour l'objet Produit
 */
class ProduitService extends SuperService {

    static transactional = true

    def list() throws Exception {
        return super.list(Produit.class)
    }
        
    def save(Object object) throws Exception {
        object.validate()
        if(!object.hasErrors()) {
            try {
                object.save(flush:true)
            }
            catch(Exception e) {
                logger.error(e)
                throw e;
            }
        } else {
            def erreurs = "Les erreur(s) suivante(s) son a corriger :"
            object.errors.each { error ->
                erreurs += error
            }
            logger.error(erreurs)
            throw new Exception(erreurs)
        }
    }
    
    def getProduitByCode(code) {
        return Produit.findByCodeIlike(code)
    }
    
    def getProduitByDesignation(designation) {
        return Produit.findByDesignationIlike(designation)
    }

    def getProduitByCodeDesignationCategorie(code, designation, cat) {
        def c = Produit.createCriteria()
        def r = c.list {
            if(code && !code.equals("")) {
                ilike("code", "%" + code + "%")
            }
            if(designation && !designation.equals("")) {
                ilike("designation", "%" + designation + "%")
            }
            if(cat) {
                eq("categorieProduit", cat)
            }
        }
        return r.sort{it.designation};
    }
}