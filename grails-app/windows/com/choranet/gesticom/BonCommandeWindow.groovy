
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
import java.util.Date;
import org.zkoss.zk.ui.Executions
import com.choranet.gesticom.util.*
import com.choranet.commun.SuperWindow
import com.choranet.commun.SuperService
import com.choranet.commun.ChoraClientInfo

/**
 * BonCommande Window Object
 **/
class BonCommandeWindow extends SuperWindow {
    
    def securitestockService
    def produitService
    def bonCommandeService
    def jasperService
    /**
     * Logger de la class BonCommandeWindow
     **/
    private Log logger = LogFactory.getLog(BonCommandeWindow.class)
    
    /**
     * liste de bonCommandes
     **/
    def bonCommandes	
    /**
     * bonCommandes  selectionn�
     **/
    def bonCommandesSelected
    
    def quantiteProduitEnStock = 0
                
    def ligneProduit
    
    def compteurSequence = 1
    
    def produits
    
    def type
    
    def bonPret
    
    def listeBonPret
    
    def valideLigneProduit = false
    
    def produitsRecherche
    def produitRechercheSelected
    def codeRecherche = ""
    def designationRecherche = ""
    def categorieRecherche
    def categoriesProduitRecherche
    def newPdt 
    def categorieProduits
    def empReceptions
    
    /**
     * Constructeur
     **/
    public BonCommandeWindow (bonCommandeService) {
        super(BonCommande.class, 12, true, false)
        this.type = Executions.getCurrent().getParameter("type")
        this.bonPret = Executions.getCurrent().getParameter("bonPret").equals("true")
        this.bonCommandeService = bonCommandeService
        specialInitialiserAssociation()
    }  

    /**
     * Fonction qui g�re l'initialisation des listes d'associations au niveau du constructeur
     **/
    def specialInitialiserAssociation() {
        produitsRecherche = Produit.list().sort{it.designation}
        categoriesProduitRecherche = CategorieProduit.list()
        categorieProduits = CategorieProduit.list()
        newPdt = new Produit()
        empReceptions = Entrepot.list()
        
        produits = Produit.list().sort{it.designation}
        ligneProduit = new LigneProduit()
        objet.type = this.type
        objet.bonPret = this.bonPret
        
        objet.date = new Date()
        objet.ligneProduits = new ArrayList<LigneProduit>();
        //objet.objet.ligneProduits = objet.ligneProduits
        ligneProduit.type= this.type;
        ligneProduit.sequenceLigne = compteurSequence++;
                    
        filtre.bonPret = bonPret
        filtre.type = type
        filtre.livree = null
        filtre.paye = null
        filtre.retourPaye = null
        filtre.avecRetour = null
        filtre.totalttcRetour = null
        def map
        if(attributsAFiltrer == null) {
            map = getService().filtrer(clazz, filtre, sortedHeader, sortedDirection, ofs, maxNb)
        } else {
            map = getService().filtrer(clazz, filtre, attributsAFiltrer, sortedHeader, sortedDirection, ofs, maxNb)
        }
        tailleListe = map["tailleListe"]
        listeObjets = map["listeObjets"]
    }
    
    
    protected SuperService getService() {
        return this.bonCommandeService
    }
    
    /**
     * Generation du rapport pdf
     **/
    def genererRapportPdf() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des Bon Commandes"
        if(bonPret) {
            titrerapport = "Rapport des Bons Prêts"
        }
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_BonCommandes.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_BonCommandes.pdf"
        if(bonPret) {
            nom_fichier = "rapport_des_BonsPrets.pdf"
        }
        //Filedownload.save(bit, "application/pdf", nom_fichier);
        Media amedia = new AMedia(nom_fichier, "pdf", "application/pdf", bit);
        Map map = new HashMap();
        map.put("content", amedia);
        Executions.createComponents("/zul/util/pdfviewer.zul", null, map).doModal();
    }
    /**
     * Generation du rapport excel
     **/
    def genererRapportExcel() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des Bon Commandes"
        if(bonPret) {
            titrerapport = "Rapport des Bons Prêts"
        }
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_BonCommandes.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_BonCommandes.xls"
        if(bonPret) {
            nom_fichier = "rapport_des_BonsPrets.xls"
        }
        Filedownload.save(bit, "application/file", nom_fichier);
        
    }
    
    def addLigneProduit() {
        valideLigneProduit = false
        ligneProduit.mettreAjourEntrepot()
        objet.ligneProduits.add(ligneProduit);
        ligneProduit = new LigneProduit();
        ligneProduit.type= this.type;
        //
        ligneProduit.sequenceLigne = compteurSequence++
        def binderligneProduits = new AnnotateDataBinder(this.getFellow("lstligneProduits"))
        //objet.ligneProduits = objet.ligneProduits.sort{it.sequenceLigne}
        binderligneProduits.loadAll()
        new AnnotateDataBinder(this.getFellow("fieldsequenceLignebis")).loadAll()
        new AnnotateDataBinder(this.getFellow("fieldquantitebis")).loadAll()
        new AnnotateDataBinder(this.getFellow("fieldprixDeduitbis")).loadAll()
        new AnnotateDataBinder(this.getFellow("fieldprixbis")).loadAll()
        new AnnotateDataBinder(this.getFellow("fieldsoustotal")).loadAll()
        new AnnotateDataBinder(this.getFellow("conewproduits")).loadAll()
        new AnnotateDataBinder(this.getFellow("fieldTotalHt")).loadAll()
    }
    
    def deleteLigneProduit(lignePdt) {
        objet.ligneProduits.remove(lignePdt)
        def binderligneProduits = new AnnotateDataBinder(this.getFellow("lstligneProduits"))
        binderligneProduits.loadAll()
        new AnnotateDataBinder(this.getFellow("fieldTotalHt")).loadAll()
    }
    
    def updateLigneProduit() {
        new AnnotateDataBinder(this.getFellow("fieldTotalHt")).loadAll()
    }
    
    def calculerQuantiteProduitEnStock() {
        if(ligneProduit.produit != null) {
            quantiteProduitEnStock = securitestockService.getQuantiteProduitEnStock(ligneProduit.produit);            
        } else {
            quantiteProduitEnStock = 0;
        }
        getFellow("lbStock").value = "la quantité actuelle du produit en stock est " + quantiteProduitEnStock;
    }
        
    def activerBoutons(visible) {
        this.getFellow("btnUpdate").visible = visible
        this.getFellow("btnDocument").visible = visible
        this.getFellow("btnDelete").visible = visible
        //            this.getFellow("btnCancel").visible = visible
        this.getFellow("btnPdf").visible = !visible
        this.getFellow("btnExcel").visible = !visible
        this.getFellow("btnSave").visible = false
        this.getFellow("btnNew").visible = !visible
        this.getFellow("westPanel").open = true       
        if(bonPret) {
            this.getFellow("btnValider").visible = visible
        }
    }
    
    def fermer() {
        cancel()
        this.getFellow("westPanel").open = false
    }
    
    def cancel() {        
        objet = clazz.newInstance()
        
        ligneProduit = new LigneProduit()
        objet.type = this.type
        objet.bonPret = this.bonPret
        objet.date = new Date()
        objet.ligneProduits = new ArrayList<LigneProduit>();
        ligneProduit.type= this.type;
        compteurSequence = 1;
        ligneProduit.sequenceLigne = compteurSequence++;
        
        this.getFellow("btnSave").visible = false
        
        new AnnotateDataBinder(this.getFellow("lstligneProduits")).loadAll()
        
        rafraichirField()
        activerBoutons(false)
        annulerSelection()
    }
    
    def add() {
        try {  
            getService().save(objet)
            Messagebox.show("Enregistrement effectué avec succès", "", Messagebox.OK, Messagebox.INFORMATION)
            activerBoutons(true)
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Echec lors de la transaction\n" + ex.message, "Erreur", Messagebox.OK, Messagebox.ERROR)
        } finally {
            rafraichirList()          
            //cancel()
        }
    }
    
    def select() {                    
        objet = objetSelected	
        if(!objet.isAttached()) {
            objet.attach()
        } 
        compteurSequence = bonCommandeService.getSequenceSuivante(objet);
        ligneProduit = new LigneProduit()
        ligneProduit.type= this.type;
        ligneProduit.sequenceLigne = compteurSequence++;
        
        //objet.ligneProduits = objet.ligneProduits.sort{it.sequenceLigne}
        new AnnotateDataBinder(this.getFellow("lstligneProduits")).loadAll()
        
        rafraichirField()
        activerBoutons(true)
    }
    
    def update() {
        try {
            objet.livree = objet.trans_livree
            objet = getService().update(objet)
            if(!this.bonPret) {
                if(type.equals("VENTE")) {
                    objet = paiementService.updateEtatPaiementBonCommande(objet, true)
                } else if(type.equals("ACHAT")) {
                    objet = paiementService.updateEtatPaiementBonCommande(objet, false)
                }
            }
            Messagebox.show("Modification effectuée avec succès", "", Messagebox.OK, Messagebox.INFORMATION)
        }
        catch(Exception e) {
            logger.error "Error: ${e.message}", e
            Messagebox.show("Problème lors de la mise à jour", "Erreur", Messagebox.OK, Messagebox.ERROR)
        }
        finally {
            rafraichirList()          
            //cancel()
        }
    }
    
    def delete() {
        getService().delete(objet)
        rafraichirList()          
        cancel()
    }
    
    def doModalDialogChoix(){
        this.getFellow("modalDialogChoix").visible = true
        this.getFellow("modalDialogChoix").doModal() 
    }
    
    def doModalDialogPdt(){
        this.getFellow("winProduit").visible = true
        this.getFellow("winProduit").doModal() 
    }
    
    def rechercher() {
        produitsRecherche = produitService.getProduitByCodeDesignationCategorie(codeRecherche, designationRecherche, categorieRecherche)
        new AnnotateDataBinder(this.getFellow("winProduit")).loadAll()
    }
    
    def utiliserProduitRecherche() {
      
        if(produitRechercheSelected) {
            this.getFellow("winProduit").visible = false
            ligneProduit.produit = produits.find{ it.id == produitRechercheSelected.id };
            def cpdt = this.getFellow("conewproduits")
            new AnnotateDataBinder(this.getFellow("lblUm")).loadAll()
            new AnnotateDataBinder(this.getFellow("fieldprixbis")).loadAll()
            new AnnotateDataBinder(cpdt).loadAll()
            calculerPrixDeduit();
            calculerQuantiteProduitEnStock();
            this.getFellow("informationsporduit").open(cpdt, "after_start")
            
        } else {
            Messagebox.show("Vous n'avez pas choisi de produit", "Produit non sélectionné", Messagebox.OK, Messagebox.ERROR)
        }
    }
    def ajouterNouveauProduit() {
        try {
            produitService.save(newPdt)
            codeRecherche = newPdt.code
            produits = Produit.list().sort{it.designation}
            new AnnotateDataBinder(this.getFellow("conewproduits")).loadAll()
            newPdt = new Produit()
            rechercher()
            Messagebox.show("Produit ajouté avec succès", "", Messagebox.OK, Messagebox.INFORMATION)
        } catch(Exception e) {
            Messagebox.show("Impossible d'ajouter le nouveau produit, vérifier les données saisies", "", Messagebox.OK, Messagebox.ERROR)
        }
    }
    
}

