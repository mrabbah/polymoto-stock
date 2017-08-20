
package com.choranet.gesticom;

import java.util.Date
import com.choranet.commun.SuperService

/**
 * InventaireService Service pour la gestion des opérations
 * transactionnelles pour l'objet Inventaire
 */
class InventaireService extends SuperService {

    static transactional = true

    def securitestockService
    def mouvementStockService
    def parametrageService
    
    def list() throws Exception {
        return super.list(Inventaire.class)
    }
       
    def listeEntrepotsNonInventorier() {
        def c = Inventaire.createCriteria()
        def lstInventaires = c.list {
            or {
                eq("etat" , "SUSPENDU")
                eq("etat" , "EN COURS")
                eq("etat" , "ACHEVE")
            }
        }
        def ids = lstInventaires*.entrepot*.id
        def idds = []
        ids.each {
            idds.addAll(it)
        }
        def criteria = Entrepot.createCriteria()
        def result = criteria.list {
            if(idds.size() > 0) {
                not{"in"("id", idds)}
            }
        }
        return result
    }
    def suspendre(objet) {
        objet.etat = "SUSPENDU"
        return update(objet)
    }
    def achever(objet) {
        objet.etat = "ACHEVE"
        //On va enlever les redandence
        def nouvellesLignesInventaires = []
        def liste = objet.ligneInventaires.toArray(); 
        int tailleListe = liste.length
        
        def dlc, result, produit, ligneProduit, qantiteActuele
        def produitPerissableManaged = parametrageService.isProduitPerissableManaged()
        
        while(tailleListe > 0) {
            ligneProduit = liste[0]
            qantiteActuele = ligneProduit.qantiteActuele
            produit = ligneProduit.produit
            if (produitPerissableManaged){
                dlc = ligneProduit.date_preremption
                result = rassemblerLigneInventaireMemeProduitEtDLC(qantiteActuele, produit, dlc, liste)
            }
            else {
                result = rassemblerLigneInventaireMemeProduit(qantiteActuele, produit, liste)
            }
            nouvellesLignesInventaires.add(result["ligneInventaire"])
            liste = result["liste"]
            tailleListe = liste.size()
        }
        //Etat actuelle de l entrepot
        def ssEntrepot = securitestockService.getSecuritesStockEntrepot(objet.entrepot)
        nouvellesLignesInventaires = ajouterLignesInventaireManquantesDansInventaire(ssEntrepot, nouvellesLignesInventaires);

        objet.ligneInventaires = null
        objet.ligneInventaires = nouvellesLignesInventaires
        return update(objet)
    }
    
    def reprendre(objet) {
        objet.etat = "EN COURS"
        return update(objet)
    }
    
    def valider(objet) {
        if(objet.etat.equals("ACHEVE")) {
            objet.etat = "VALIDE"
            for(ligne in objet.ligneInventaires) {
                mouvementStockService.ajouterMouvementInventaire(ligne, objet)
            }
            return update(objet)
        }
        return objet
    }
    
    def ajouterLignesInventaireManquantesDansInventaire(securitesStock, lignesInventaireCourante) {
        for(ss in securitesStock) {
            if(!existeProduitDansLignesInventaire(ss.produit, lignesInventaireCourante)) {
                LigneInventaire l = new LigneInventaire();
                l.produit = ss.produit;
                l.qantiteActuele = ss.stockReel;
                l.quantiteInv = 0;
                l.date_preremption = null;
                lignesInventaireCourante.add(l)
            }
        }
        return lignesInventaireCourante
    }
    
    def existeProduitDansLignesInventaire(produit, lignesInvenatire) {
        def resultat = false;
        for(ligne in lignesInvenatire) {
            if(ligne.produit.code.equals(produit.code)) {
                resultat = true;
                break;
            }
        }
        return resultat;
    }
    
    def rassemblerLigneInventaireMemeProduit(qantiteActuele, produit, list) {
        LigneInventaire l = new LigneInventaire();
        l.produit = produit;
        l.qantiteActuele = qantiteActuele;
        l.quantiteInv = 0;
        def nouvelleListe = []
        for(ligne in list) {
            if(l.produit.code.equals(ligne.produit.code)) {
                l.quantiteInv += ligne.quantiteInv;
            } else {
                nouvelleListe.add(ligne)
            }
        }
        return [ligneInventaire : l, liste : nouvelleListe];
    }
    
    def rassemblerLigneInventaireMemeProduitEtDLC(qantiteActuele, produit, dlc, list) {
        LigneInventaire l = new LigneInventaire();
        l.produit = produit;
        l.qantiteActuele = qantiteActuele;
        l.quantiteInv = 0;
        l.date_preremption = dlc
        def nouvelleListe = []
        for(ligne in list) {
            if(l.produit.code.equals(ligne.produit.code) && 
              (l.date_preremption == ligne.date_preremption)) {
                l.quantiteInv += ligne.quantiteInv;
            } else {
                nouvelleListe.add(ligne)
            }
        }
        return [ligneInventaire : l, liste : nouvelleListe];
    }
    
}