
package com.choranet.gesticom

import org.zkoss.zul.*
import org.zkoss.zkplus.databind.*
import org.apache.commons.logging.Log 
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Executions
import com.choranet.gesticom.util.*

import java.util.Comparator;
import com.choranet.commun.SuperWindow
import com.choranet.commun.SuperService
import com.choranet.commun.ChoraClientInfo

/**
 * BonLivraison Window Object
 **/
class BonLivraisonWindow extends SuperWindow/* implements Comparator */{

    private Log logger = LogFactory.getLog(BonLivraisonWindow.class)
    
    def bonLivraisonService
    def bonCommandeService
    def jasperService
    def parametrageService
 
    def emplacementLivraisons	
    def emplacementLivraisonSelected
    def bonCommandes	
    def bonCommandesFilter	
    def ligneProduitsSelectedBc
    def ligneProduitsSelectedBl
    def livraisonExpresse = true
    def type
    def estBonRetour
    def bonCommandeSelected
    
    def sortedLigneProduitsBc
    def sortedLigneProduitsBl
    
    def produitPerissableManaged = true
    def multiEntrepot = false
    /**
     * Constructeur
     **/
    public BonLivraisonWindow (bonCommandeService, bonLivraisonService, parametrageService) {        
        super(BonLivraison.class, true, false)
        this.type = Executions.getCurrent().getParameter("type")
        this.estBonRetour = Executions.getCurrent().getParameter("estBonRetour").equals("true")
        this.bonCommandeService = bonCommandeService
        this.bonLivraisonService = bonLivraisonService
        this.parametrageService = parametrageService
        specialInitialiserAssociation()
    }  
    
    def specialInitialiserAssociation() {
        multiEntrepot = parametrageService.isMultiEntrepotEnabled()
        produitPerissableManaged = parametrageService.isProduitPerissableManaged() && (this.type.equals('ACHAT') == true)
        
        objet.estBonRetour = estBonRetour
        objet.type = this.type
        
        objet.date = new Date()
        bonCommandes = bonCommandeService.getBonCommandeNonLivree(type)            
        bonCommandesFilter = bonCommandeService.getBonsCommande(type)            
        objet.ligneProduits = new ArrayList<LigneProduit>();
        emplacementLivraisons = Entrepot.list()		
        if(emplacementLivraisons.size() > 0) {
            emplacementLivraisonSelected = emplacementLivraisons.get(0)            
        }        
        sortedLigneProduitsBc = []
        sortedLigneProduitsBl = []
        ligneProduitsSelectedBc = []
        ligneProduitsSelectedBl = []
        
        filtre.type = type
        filtre.estBonRetour = estBonRetour
        def map
        if(attributsAFiltrer == null) {
            map = bonLivraisonService.filtrer(clazz, filtre, sortedHeader, sortedDirection, ofs, maxNb)
        } else {
            map = bonLivraisonService.filtrer(clazz, filtre, attributsAFiltrer, sortedHeader, sortedDirection, ofs, maxNb)
        }
        tailleListe = map["tailleListe"]
        listeObjets = map["listeObjets"]
    }

    protected SuperService getService() {
        return this.bonLivraisonService
    }
    
    def activerBoutons(visible) {
        this.getFellow("btnUpdate").visible = visible
        this.getFellow("btnDocument").visible = visible
        this.getFellow("btnDelete").visible = visible
        this.getFellow("btnPdf").visible = !visible
        this.getFellow("btnExcel").visible = !visible
        this.getFellow("btnSave").visible = false
        this.getFellow("btnNew").visible = !visible
        //this.getFellow("westPanel").open = true        
    }
    
    def fermer() {
        if(bonCommandeSelected) {
            cancel()
        } else {
            activerBoutons(false)
            annulerSelection()
        }
        this.getFellow("westPanel").open = false
    }
    
    def cancel() { 
        objet = clazz.newInstance()
        objet.type = this.type
        objet.date = new Date()
        bonCommandes = bonCommandeService.getBonCommandeNonLivree(type)    
        bonCommandeSelected = null
        objet.ligneProduits = new ArrayList<LigneProduit>();
        
        sortedLigneProduitsBc = []
        sortedLigneProduitsBl = []
        ligneProduitsSelectedBc = []
        ligneProduitsSelectedBl = []
        this.getFellow("btnSave").visible = false
        new AnnotateDataBinder(this.getFellow("lstligneProduitsBl")).loadAll()
        new AnnotateDataBinder(this.getFellow("lstligneProduitsBc")).loadAll()
        def cbc = this.getFellow("cobonCommandes")
        cbc.disabled = false
        new AnnotateDataBinder(cbc).loadAll()
        
        this.getFellow("lstligneProduitsBl").disabled = false
        
        rafraichirField()
        activerBoutons(false)
        annulerSelection()
    }
    
    def actualiserEntrepotPourLigneProduitBL(elementlpbisCourant){
        elementlpbisCourant.entrepot = emplacementLivraisons.find{it.code == elementlpbisCourant.entrepot.code}
        //println 'elementlpbisCourant.entrepot : ' + elementlpbisCourant.entrepot
    }
    
    def isProduitPerissable(elementlpCourant){
        return elementlpCourant.produit.perissable
    }
    
    def add() {
        try { 
            objet.bonCommande = bonCommandeSelected
            objet = bonLivraisonService.save(objet)
            activerBoutons(true)
            if(objet.bonCommande.livree) {
                this.getFellow("btnUpdate").visible = false
            }            
            Messagebox.show("Enregistrement effectué avec succès")
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Echec lors de la transaction\n" + ex.message, "Erreur", Messagebox.OK, Messagebox.ERROR)
        } finally {
            filtrer()
            try {
                new AnnotateDataBinder(this.getFellow("lstligneProduitsBc")).loadAll()                
            } catch(Exception ex) {
                logger.error(ex)
            }
        }
    }
    
    def select() {    
        try {
            objet = objetSelected
            if(!objet.isAttached()) {
                logger.debug("objet n'est pas attaché")
                objet.attach()
            }
            if(!objet.bonCommande.isAttached()) {
                logger.debug("bc objet n'est pas attaché")
                objet.bonCommande.attach()
            }
            
            bonCommandes = [objetSelected.bonCommande]//.sort{it.libelle}
            bonCommandeSelected = objetSelected.bonCommande
            
            def cbc = this.getFellow("cobonCommandes")
            cbc.disabled = true
            new AnnotateDataBinder(cbc).loadAll()
            
            sortedLigneProduitsBc = objetSelected.bonCommande.ligneProduits.sort{it.produit.designation}
            sortedLigneProduitsBl = objetSelected.ligneProduits.sort{it.produit.designation}
            
            def lstBc = this.getFellow("lstligneProduitsBc")
            def lstBl = this.getFellow("lstligneProduitsBl")
            
            new AnnotateDataBinder(lstBc).loadAll()
            new AnnotateDataBinder(lstBl).loadAll()
            
            rafraichirField()
            activerBoutons(true)
            
            this.getFellow("btnUpdate").visible = false
            this.getFellow("btnDelete").visible = false
            this.getFellow("lstligneProduitsBl").disabled = true
            
            if(objet.bonCommande.livree) {
                this.getFellow("btnUpdate").visible = false
            }
            this.getFellow("westPanel").open = true   
        } catch(Exception ex) {
            logger.error(ex.getMessage())
            logger.error(ex.getCause())
        }        
    }
    
    def update() {
        try {
            objet = bonLivraisonService.update(objet)
            if(objet.bonCommande.livree) {
                this.getFellow("btnUpdate").visible = false
            }
            Messagebox.show("Modification effectuée avec succès")
        }
        catch(Exception e) {
            logger.error "Error: ${e.message}", e
            Messagebox.show("Problème lors de la mise à jour\n" + e.getMessage(), "Erreur", Messagebox.OK, Messagebox.ERROR)
        }
        finally {
            filtrer()
            try {
                new AnnotateDataBinder(this.getFellow("lstligneProduitsBc")).loadAll()                
            } catch(Exception ex) {
                logger.warn(ex)
            }
        }
    }
    
    def delete() {
        bonLivraisonService.delete(objet)
        rafraichirList()          
        cancel()
    }
    
    def commandeChoisie() {        
        if(!bonCommandeSelected.isAttached()) {
            logger.debug("bonCommandeSelected n est pas attaché")
            bonCommandeSelected.attach()
        }
        objet.bonCommande = bonCommandeSelected 
        bonCommandes = [bonCommandeSelected]
        
        new AnnotateDataBinder(this.getFellow("cobonCommandes")).loadAll()
        
        sortedLigneProduitsBc = objet.bonCommande.ligneProduits.sort{it.produit.designation}
        def lstBc = this.getFellow("lstligneProduitsBc")
        new AnnotateDataBinder(lstBc).loadAll()   
    }
    
    def choisirTout() {
        logger.debug("appel de choisir tout")
        for(lp in objet.bonCommande.ligneProduits) {
            logger.debug("dans la boucle for1")
            if(estBonRetour) {
                if(lp.quantiteRetournee != lp.quantiteLivree) {
                    def lpExisteDeja = false
                    for(nlp in objet.ligneProduits) {
                        logger.debug("dans la boucle for2")
                        if(lp.produit.code.equals(nlp.produit.code)) {
                            lpExisteDeja = true
                            break;
                        }
                    }
                    logger.debug(lpExisteDeja)
                    if(!lpExisteDeja) {
                        def newLp = new LigneProduit()
                        newLp.sequenceLigne = lp.sequenceLigne
                        newLp.quantite = lp.quantite
                        newLp.quantiteLivree = lp.quantiteLivree
                        newLp.quantiteRetournee = lp.quantiteLivree - lp.quantiteRetournee
                        newLp.trans_qr = lp.quantiteRetournee
                        newLp.remise = lp.remise
                        newLp.prix = lp.prix
                        newLp.prixDeduit = lp.prixDeduit
                        newLp.type = lp.type
                        if(!lp.produit.isAttached()) {
                            logger.debug("lp.produit n est pas attache")
                            lp.produit.attach()
                        }
                        newLp.produit = lp.produit
                        if(!lp.entrepot.isAttached()) {
                            logger.debug("lp.entrepot n est pas attache")
                            lp.entrepot.attach()
                        }
                        newLp.entrepot = lp.entrepot
                        objet.addToLigneProduits(newLp)    
                    }
                } 
            } else {
                if(lp.quantite != lp.quantiteLivree) {
                    def lpExisteDeja = false
                    for(nlp in objet.ligneProduits) {
                        if(lp.produit.code.equals(nlp.produit.code)) {
                            lpExisteDeja = true
                            break;
                        }
                    }
                    if(!lpExisteDeja) {
                        def newLp = new LigneProduit()
                        newLp.sequenceLigne = lp.sequenceLigne
                        newLp.quantite = lp.quantite
                        newLp.quantiteLivree = lp.quantite - lp.quantiteLivree
                        newLp.trans_ql = lp.quantiteLivree
                        newLp.remise = lp.remise
                        newLp.prix = lp.prix
                        newLp.prixDeduit = lp.prixDeduit
                        newLp.type = lp.type
                        if(!lp.produit.isAttached()) {
                            logger.debug("lp.produit n est pas attache")
                            lp.produit.attach()
                        }
                        newLp.produit = lp.produit
                        if(!lp.entrepot.isAttached()) {
                            logger.debug("lp.entrepot n est pas attache")
                            lp.entrepot.attach()
                        }
                        newLp.entrepot = lp.entrepot
                        objet.addToLigneProduits(newLp)    
                    }
                } 
            }
        }
        def lstBc = this.getFellow("lstligneProduitsBc")
        sortedLigneProduitsBc = objet.bonCommande.ligneProduits.sort{it.produit.designation}
        lstBc.clearSelection()
        new AnnotateDataBinder(lstBc).loadAll()
        
        def lstBl = this.getFellow("lstligneProduitsBl")
        sortedLigneProduitsBl = objet.ligneProduits.sort{it.produit.designation}
        lstBl.clearSelection()
        new AnnotateDataBinder(lstBl).loadAll()
        
        if(objet.ligneProduits.size() > 0) {
            if(this.getFellow("btnDelete").visible) {
                this.getFellow("btnUpdate").visible = true
            } else {
                this.getFellow("btnSave").visible = true
            }
        } else {
            this.getFellow("btnSave").visible = false
            this.getFellow("btnUpdate").visible = false
        }
    }
    def choisirDesElements() {
        for(lp in ligneProduitsSelectedBc) {
            if(estBonRetour) {
                if(lp.quantiteRetournee != lp.quantiteLivree) {
                    def lpExisteDeja = false
                    for(nlp in objet.ligneProduits) {
                        if(lp.produit.code.equals(nlp.produit.code)) {
                            lpExisteDeja = true
                            break;
                        }
                    }
                    if(!lpExisteDeja) {
                        def newLp = new LigneProduit()
                        newLp.sequenceLigne = lp.sequenceLigne
                        newLp.quantite = lp.quantite
                        newLp.quantiteLivree = lp.quantiteLivree
                        newLp.quantiteRetournee = lp.quantiteLivree - lp.quantiteRetournee
                        newLp.trans_qr = lp.quantiteRetournee
                        newLp.remise = lp.remise
                        newLp.prix = lp.prix
                        newLp.prixDeduit = lp.prixDeduit
                        newLp.type = lp.type
                        newLp.produit = lp.produit
                        newLp.entrepot = lp.entrepot
                        //lp.quantiteLivree = lp.quantite
                        objet.addToLigneProduits(newLp)    
                    }
                }   
            } else {
                if(lp.quantite != lp.quantiteLivree) {
                    def lpExisteDeja = false
                    for(nlp in objet.ligneProduits) {
                        if(lp.produit.code.equals(nlp.produit.code)) {
                            lpExisteDeja = true
                            break;
                        }
                    }
                    if(!lpExisteDeja) {
                        def newLp = new LigneProduit()
                        newLp.sequenceLigne = lp.sequenceLigne
                        newLp.quantite = lp.quantite
                        newLp.quantiteLivree = lp.quantite - lp.quantiteLivree
                        newLp.trans_ql = lp.quantiteLivree
                        newLp.remise = lp.remise
                        newLp.prix = lp.prix
                        newLp.prixDeduit = lp.prixDeduit
                        newLp.type = lp.type
                        newLp.produit = lp.produit
                        newLp.entrepot = lp.entrepot
                        //lp.quantiteLivree = lp.quantite
                        objet.addToLigneProduits(newLp)    
                    }
                }   
            }            
        }
        def lstBc = this.getFellow("lstligneProduitsBc")
        sortedLigneProduitsBc = objet.bonCommande.ligneProduits.sort{it.produit.designation}
        lstBc.clearSelection()
        new AnnotateDataBinder(lstBc).loadAll()
        
        def lstBl = this.getFellow("lstligneProduitsBl")
        sortedLigneProduitsBl = objet.ligneProduits.sort{it.produit.designation}
        lstBl.clearSelection()
        new AnnotateDataBinder(lstBl).loadAll()
        
        if(objet.ligneProduits.size() > 0) {
            if(this.getFellow("btnDelete").visible) {
                this.getFellow("btnUpdate").visible = true
            } else {
                this.getFellow("btnSave").visible = true
            }
        } else {
            this.getFellow("btnSave").visible = false
            this.getFellow("btnUpdate").visible = false
        }
    }
    def enleverDesElements() {
        objet.ligneProduits.removeAll(ligneProduitsSelectedBl)
        
        def lstBc = this.getFellow("lstligneProduitsBc")
        sortedLigneProduitsBc = objet.bonCommande.ligneProduits.sort{it.produit.designation}
        lstBc.clearSelection()
        new AnnotateDataBinder(lstBc).loadAll()
        
        def lstBl = this.getFellow("lstligneProduitsBl")
        sortedLigneProduitsBl = objet.ligneProduits.sort{it.produit.designation}
        lstBl.clearSelection()
        new AnnotateDataBinder(lstBl).loadAll()
        
        if(objet.ligneProduits.size() == 0) {
            if(this.getFellow("btnDelete").visible) {
                this.getFellow("btnUpdate").visible = false
            } else {
                this.getFellow("btnSave").visible = false
            }
        } 
    }
    def enleverTout() {
        objet.ligneProduits = null
        objet.ligneProduits = new ArrayList<LigneProduit>()
        
        def lstBc = this.getFellow("lstligneProduitsBc")
        sortedLigneProduitsBc = objet.bonCommande.ligneProduits.sort{it.produit.designation}
        lstBc.clearSelection()
        new AnnotateDataBinder(lstBc).loadAll()
        
        def lstBl = this.getFellow("lstligneProduitsBl")
        sortedLigneProduitsBl = objet.ligneProduits.sort{it.produit.designation}
        lstBl.clearSelection()
        new AnnotateDataBinder(lstBl).loadAll()
        
        this.getFellow("btnSave").visible = false
        this.getFellow("btnUpdate").visible = false
    }
    
}

