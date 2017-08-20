
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
 * Inventaire Window Object
 **/
class InventaireWindow extends SuperWindow {
    
    def securitestockService
    
    def entrepotService
    /**
     * Service pour la gestion de l'objet Inventaire
     **/
    def inventaireService
    
    def parametrageService
    
    /**
     * Service jasper pour la generation des rapports
     **/
    def jasperService
    /**
     * Logger de la class InventaireWindow
     **/
    private Log logger = LogFactory.getLog(InventaireWindow.class)
    
    /**
     * liste de entrepot
     **/	
    def entrepots	
   
    def entrepotSelected
    
    def ligneInventaire
     
    def produits
    
    def entrepotsfilter
    
    def produitPerissableManaged = false
    
    /**
     * Constructeur
     **/
    public InventaireWindow (inventaireService, parametrageService) {
        super(Inventaire.class)                
        this.inventaireService = inventaireService
        this.parametrageService = parametrageService
        specialInitialiserAssociation()
    }  

    protected SuperService getService() {
        return this.inventaireService
    }
    
    /**
     * Generation du rapport pdf
     **/
    def genererRapportPdf() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des Inventaires"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Inventaires.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Inventaires.pdf"
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
        String titrerapport = "Rapport des Inventaires"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Inventaires.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Inventaires.xls"
        Filedownload.save(bit, "application/file", nom_fichier);
        
    }
    
    
    /**
     * Fonction qui g�re l'initialisation des listes d'associations au niveau du constructeur
     **/
    def specialInitialiserAssociation() {
        produitPerissableManaged = parametrageService.isProduitPerissableManaged()
        produits = Produit.list().sort{it.designation}
        entrepots = inventaireService.listeEntrepotsNonInventorier()
        entrepotsfilter = Entrepot.list()
        ligneInventaire = new LigneInventaire()
        objet.etat = "EN COURS"   
        objet.dateDebut = new Date()
        objet.ligneInventaires = new ArrayList<LigneInventaire>();
        
    }
    
    def addLigneInventaire() {
        objet.ligneInventaires.add(ligneInventaire);
        ligneInventaire = new LigneInventaire();        
        def binderligneInventaires = new AnnotateDataBinder(this.getFellow("lstligneInventaires"))
        binderligneInventaires.loadAll()
        new AnnotateDataBinder(this.getFellow("fieldquantitebis")).loadAll()
        new AnnotateDataBinder(this.getFellow("fieldquantiteinventairebis")).loadAll()
        new AnnotateDataBinder(this.getFellow("conewproduits")).loadAll()
        new AnnotateDataBinder(this.getFellow("fielddlcbis")).loadAll()
    }
    
    def deleteLigneInventaire(lignePdt) {
        objet.ligneInventaires.remove(lignePdt)
        def binderligneInventaires = new AnnotateDataBinder(this.getFellow("lstligneInventaires"))
        binderligneInventaires.loadAll()        
    }
        
    def activerBoutons(visible) {
        this.getFellow("btnUpdate").visible = visible
        //this.getFellow("btnDocument").visible = visible
        this.getFellow("btnDelete").visible = visible
        //            this.getFellow("btnCancel").visible = visible
        //this.getFellow("btnPdf").visible = !visible
        //this.getFellow("btnExcel").visible = !visible
        this.getFellow("btnSave").visible = false
        this.getFellow("btnNew").visible = !visible
        
        this.getFellow("btnSuspendre").visible = false
        this.getFellow("btnReprendre").visible = false
        this.getFellow("btnAcheve").visible = false
        this.getFellow("btnValider").visible = false
            
        if(visible) {
            if(objet.etat.equals("EN COURS")) {
                this.getFellow("btnSuspendre").visible = true
                this.getFellow("btnAcheve").visible = true
                this.getFellow("btnUpdate").visible = true
                this.getFellow("btnDelete").visible = false
            } else if(objet.etat.equals("SUSPENDU")) {
                this.getFellow("btnReprendre").visible = true
                this.getFellow("btnUpdate").visible = false
                this.getFellow("btnDelete").visible = true
            } else if(objet.etat.equals("ACHEVE")) {
                this.getFellow("btnValider").visible = visible
                this.getFellow("btnUpdate").visible = false
                this.getFellow("btnDelete").visible = true
                this.getFellow("btnReprendre").visible = true
            } else if(objet.etat.equals("VALIDE")) {
                this.getFellow("btnUpdate").visible = false
                this.getFellow("btnDelete").visible = false
            }
                
        } 
        
        this.getFellow("westPanel").open = visible        
    }
    
    def cancel() {        
        
        objet = clazz.newInstance() 
        objet.etat = "EN COURS"
        objet.dateDebut = new Date()
        entrepots = inventaireService.listeEntrepotsNonInventorier()
        entrepotSelected = null        
        ligneInventaire = new LigneInventaire()  
        new AnnotateDataBinder(this.getFellow("conewproduits")).loadAll()
        objet.ligneInventaires = new ArrayList<LigneInventaire>();
        
        this.getFellow("gridAjout").visible = false
        this.getFellow("btnSave").visible = false
        
        objet.entrepot = null
        new AnnotateDataBinder(this.getFellow("lstligneInventaires")).loadAll()
        def coentrepots = this.getFellow("coentrepots")
        coentrepots.disabled = false
        new AnnotateDataBinder(coentrepots).loadAll()
        
        rafraichirField()
        activerBoutons(false)
        rafraichirList()
    }
    
    def add() {
        try {  
            objet.entrepot = entrepotSelected
            objet = getService().save(objet)
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Echec lors de la transaction\n" + ex.message, "Erreur", Messagebox.OK, Messagebox.ERROR)
        } finally {
            rafraichirList()          
            cancel()
        }
    }
    
    def select() {                    
        objet = objetSelected	
        entrepotSelected = objetSelected.entrepot
        //if(!entrepotSelected.isAttached()) {
            //println 'entrepot not attached'
            //objetSelected.entrepot.attach()
            //entrepotSelected = Entrepot.findById(objet.entrepot.id)
        //}
        //println 'intitule entrepot : ' + entrepotSelected.intitule
        entrepots = [entrepotSelected]
        ligneInventaire = new LigneInventaire()
        this.getFellow("gridAjout").visible = true
        def coentrepots = this.getFellow("coentrepots")       
        coentrepots.disabled = true;
        new AnnotateDataBinder(coentrepots).loadAll()
        new AnnotateDataBinder(this.getFellow("lstligneInventaires")).loadAll()

        rafraichirField()
        activerBoutons(true)
    }
    
    def update() {
        try {
            objet = getService().update(objet)
        }
        catch(Exception e) {
            logger.error "Error: ${e.message}", e
            Messagebox.show("Problème lors de la mise à jour", "Erreur", Messagebox.OK, Messagebox.ERROR)
        }
        finally {
            rafraichirList()          
            cancel()
        }
    }
    
    def delete() {
        getService().delete(objet)
        rafraichirList()          
        cancel()
    }
    
    def changementProduit() {
        ligneInventaire.qantiteActuele = securitestockService.getQuantiteProduitEnEntrepot(ligneInventaire.produit, entrepotSelected)
        new AnnotateDataBinder(this.getFellow("fieldquantitebis")).loadAll()
    }
    
    def changementDlcProduit(dlcSelected) {
        ligneInventaire.qantiteActuele = securitestockService.getQuantiteProduiEnEntrepotPourDatePreremption(ligneInventaire.produit, entrepotSelected, dlcSelected)
        new AnnotateDataBinder(this.getFellow("fieldquantitebis")).loadAll()
    }
    
    def suspendre() {
        objet = inventaireService.suspendre(objet)
        activerBoutons(true)
    }
    def reprendre() {
        objet= inventaireService.reprendre(objet)
        activerBoutons(true)
    }
    def achever() {
        objet = inventaireService.achever(objet)
        new AnnotateDataBinder(this.getFellow("lstligneInventaires")).loadAll()
        activerBoutons(true)
    }
    def valider() {
        objet = inventaireService.valider(objet)
        rafraichirList()
        cancel()
    }
    
        def genererInvenraireDetails() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des Inventaires"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Inventairesdetails.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Inventaires_details.pdf"
        //Filedownload.save(bit, "application/pdf", nom_fichier);
        Media amedia = new AMedia(nom_fichier, "pdf", "application/pdf", bit);
        Map map = new HashMap();
        map.put("content", amedia);
        Executions.createComponents("/zul/util/pdfviewer.zul", null, map).doModal();
    }
    
}

