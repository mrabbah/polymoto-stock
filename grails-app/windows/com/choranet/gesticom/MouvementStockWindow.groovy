
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
import com.choranet.commun.SuperWindow
import com.choranet.commun.SuperService
import com.choranet.commun.ChoraClientInfo

/**
 * MouvementStock Window Object
 **/
class MouvementStockWindow extends SuperWindow {
    /**
     * Service pour la gestion de l'objet MouvementStock
     **/
    def mouvementStockService
    
    def securitestockService
    
    def parametrageService
    
    /**
     * Service jasper pour la generation des rapports
     **/
    def jasperService
    /**
     * Logger de la class MouvementStockWindow
     **/
    private Log logger = LogFactory.getLog(MouvementStockWindow.class)
    
    /**
     * liste de empSource
     **/	
    def empSources	
    /**
     * empSource  selectionn�
     **/
    def empSourceSelected
                
    /**
     * liste de empDestination
     **/	
    def empDestinations	
    /**
     * empDestination  selectionn�
     **/
    def empDestinationSelected
                
    /**
     * liste de produit
     **/	
    def produits	
    /**
     * produit  selectionn�
     **/
    def produitSelected
        
    def multiEntrepot = false
    def produitPerissableManaged = true
    
    /**
     * les dates de preremption pour un produit sélectionné dans un entrepôt donné
     **/
    def dlcs
    def dlcSelected
    
    def prixMvms = null
    def prixMvmSelected = null
    
    
    //def isUpdate
    
    /**
     * Constructeur
     **/
    public MouvementStockWindow (securitestockService, parametrageService) {
        super(MouvementStock.class)
        this.securitestockService = securitestockService
        this.parametrageService = parametrageService
        multiEntrepot = parametrageService.isMultiEntrepotEnabled()
        produitPerissableManaged = parametrageService.isProduitPerissableManaged()
    }  

    protected SuperService getService() {
        return this.mouvementStockService
    }
    
    /**
     * Generation du rapport pdf
     **/
    def genererRapportPdf() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des MouvementStocks"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_MouvementStocks.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_MouvementStocks.pdf"
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
        String titrerapport = "Rapport des MouvementStocks"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_MouvementStocks.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_MouvementStocks.xls"
        Filedownload.save(bit, "application/file", nom_fichier);
        
    }
    
    
    /**
     * Fonction qui g�re l'initialisation des listes d'associations au niveau du constructeur
     **/
    def initialiserAssociation() {

        empSources = Entrepot.list()		
        if(empSources.size() > 0)
                empSourceSelected = empSources.get(0)
                else
        empSourceSelected = null
                    
        empDestinations = Entrepot.list()		
        if(empDestinations.size() > 0)
        //        empDestinationSelected = empDestinations.get(0)
        //        else
        empDestinationSelected = null
                    
        produits = Produit.list()		
        if(produits.size() > 0)
        //        produitSelected = produits.get(0)
        //        else
        produitSelected = null
        
        dlcs = null
        dlcSelected = null
        
        prixMvms = new ArrayList()
        prixMvmSelected = null
                    
    }
    /**
     * Fonction qui permet de r�-initaliser l'association au niveau de l'interface
     * @param del si c'est une r�initionalisation apr�s une suppression ou non
     **/
    def reinitialiserAssociation(del) {
        	
        if(del) {
            empSources = Entrepot.list()
        }	
        if(empSources.size() > 0)
               empSourceSelected = empSources.get(0)
               else
        empSourceSelected = null
                    	
        if(del) {
            empDestinations = Entrepot.list()
        }	
        if(empDestinations.size() > 0)
        //        empDestinationSelected = empDestinations.get(0)
        //        else
        empDestinationSelected = null
                    	
        if(del) {
            produits = Produit.list()
        }	
        if(produits.size() > 0)
        //        produitSelected = produits.get(0)
        //        else
        produitSelected = null
        
        if(del) {
            dlcs = null
        }
        dlcSelected = null  
        
//        if(del) {
//            prixMvms = new ArrayList()
//        }
//        prixMvmSelected = null
    }
    /**
     * Fonction qui copie la valeur de l'association � l'�l�ment courant
     **/
    def actualiserValeurAssociation() {
        		
        objet.empSource = empSourceSelected
        //def binderempSource = new AnnotateDataBinder(this.getFellow("coempSources"))
        if(empSources.size() <= 0) {
            empSourceSelected = empSources.get(0)
            //empSourceSelected = null
        }
        //binderempSource.loadAll()
        //        else
        //        empSourceSelected = null

        objet.produit = produitSelected
        produits = Produit.list()//securitestockService.getProduitsEnStock(objet.empSource)
        def binderproduit = new AnnotateDataBinder(this.getFellow("coproduits"))
        if(produits.size() <= 0) 
        produitSelected = null
        //produitSelected = produits.get(0)
        binderproduit.loadAll()
                    		
//        objet.empDestination = empDestinationSelected
//        empDestinations = Entrepot.remove(empDestinations, objet.empSource)
//        def binderempDestination = new AnnotateDataBinder(this.getFellow("coempDestinations"))
//        if(empDestinations.size() <= 0) {
//            empDestinationSelected = null
//            //empDestinationSelected = empDestinations.get(0)
//        }
//        binderempDestination.loadAll()
        //        else
        //        empDestinationSelected = null
        
        objet.date_preremption = null
        if (!dlcSelected.toString().equals("Non Précisée")){
            objet.date_preremption = dlcSelected
        }
        
        def binderdlc = new AnnotateDataBinder(this.getFellow("codlcs"))
        if(dlcs == null || dlcs.size() <= 0) {
            //empSourceSelected = empSources.get(0)
            dlcSelected = null
        }
        binderdlc.loadAll()
               
    }
    
    def getDLCs(){
        def binderDlcs = new AnnotateDataBinder(this.getFellow("codlcs"))
        dlcs = securitestockService.getDLCs(produitSelected, empSourceSelected)
        binderDlcs.loadAll()
    }
    
    def getPrixMvmsInvoker(){
        def binderprixmvms = new AnnotateDataBinder(this.getFellow("coprixMvms"))
        prixMvms = mouvementStockService.getPrixMvms(produitSelected, empSourceSelected, dlcSelected)
        binderprixmvms.loadAll()
    }
    
    def updateProduitListAndEmpDestination(empSourceSelected){
        
        produits = Produit.list()//securitestockService.getProduitsEnStock(empSourceSelected)
        def binderproduit = new AnnotateDataBinder(this.getFellow("coproduits"))
        binderproduit.loadAll()
        
//        empDestinations = Entrepot.remove(empDestinations, empSourceSelected)
//        def binderempDestination = new AnnotateDataBinder(this.getFellow("coempDestinations"))
//        binderempDestination.loadAll()
    }
    
    //    def updateEmpDestinationList(empSourceSelected){
    //        def binderempDestination = new AnnotateDataBinder(this.getFellow("coempDestinations"))
    //        //Entrepot.remove(empDestinations, empSourceSelected)
    //        empDestinations.remove(empSourceSelected)
    //        binderempDestination.loadAll()
    //    }
    /**
     * Fonction qui fait la liaison entre l'association l'�l�ment selectionn� et la liste dans le crud
     **/
    def afficherValeurAssociation() {
        		
        /*def binderempSource = new AnnotateDataBinder(this.getFellow("coempSources"))
        empSourceSelected = empSources.find{ it.id == MouvementStock.findById(objet.id).empSource.id }
        binderempSource.loadAll()*/
            
        def binderproduit = new AnnotateDataBinder(this.getFellow("coproduits"))
        produitSelected = produits.find{ it.id == MouvementStock.findById(objet.id).produit.id }
        binderproduit.loadAll()
        
        /*def binderempDestination = new AnnotateDataBinder(this.getFellow("coempDestinations")) 
        empDestinationSelected = empDestinations.find{ it.id == MouvementStock.findById(objet.id).empDestination.id }
        binderempDestination.loadAll()*/
                    
    }
    
    def getQuantiteProduitEnEntrepot(){
        if(empSourceSelected != null){
            objet.quantite = securitestockService.getQuantiteProduitEnEntrepot(produitSelected, empSourceSelected)
        }else {
            objet.quantite = securitestockService.getQuantiteProduitEnStock(produitSelected)
        }
        new AnnotateDataBinder(this.getFellow("fieldquantitebis")).loadAll()               
//        def bindercoprixMvms = new AnnotateDataBinder(this.getFellow("coprixMvms"))
//        prixMvmSelected = null
//        bindercoprixMvms.loadAll()
        def binderdlcs = new AnnotateDataBinder(this.getFellow("codlcs"))
        dlcSelected = null
        binderdlcs.loadAll()                        
    }
    
    def getQuantiteProduiEnEntrepotPourDatePreremption(){
        return securitestockService.getQuantiteProduiEnEntrepotPourDatePreremption(produitSelected, empSourceSelected, dlcSelected)
    }
    
    def getQuantiteProduiEnEntrepotPourDlcEtPrixMvmInvoker(){      
        def qty = mouvementStockService.getQuantiteProduiEnEntrepotPourDlcEtPrixMvm(produitSelected, empSourceSelected, dlcSelected, prixMvmSelected)
        if (/*!prixMvmSelected.toString().equals("Non Précisé") ||*/ (qty <= 0)){
            this.getFellow("fieldquantitebis").readonly = true
        }else 
        this.getFellow("fieldquantitebis").readonly = false
        return qty
    }
    
    /**
     *  Cette fonction est appeliée lorsque un élement de la liste est selectionné
     **/
    def select() {  
        //        isUpdate = true
        //        objet = objetSelected
        //        afficherValeurAssociation()
        //        rafraichirField()
    }
    
    //    def addOrUpdate(){
    //        if (isUpdate) {
    //            this.update()
    //            isUpdate = false
    //        } else {
    //            this.add()
    //        }
    //    }
    
    def add() {
        actualiserValeurAssociation()
        def datetmp = objet.date
        def typeMouvementtmp = objet.typeMouvement
        try {     
            getService().ajouterMouvementUtilisation(objet)
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Paramètres manquants, ou la nouvelle entré est déjà supportée par le système\n", "Ajout non disponible", Messagebox.OK, Messagebox.ERROR)
        } finally {
            objet = clazz.newInstance()
            objet.date = datetmp
            objet.typeMouvement = typeMouvementtmp
            rafraichirField()
            rafraichirList()
        }
        
    }
    
    def update() {
        actualiserValeurAssociation()
        try {
            getService().update(objet)
        }
        catch(Exception e) {
            logger.error "Error: ${e.message}", e
            Messagebox.show("La nouvelle valeur est dèjà mentionnée pour une mouvement de stock antécédente", "Mise à jour non disponible", Messagebox.OK, Messagebox.ERROR)
        }
        finally {
            objet = clazz.newInstance()
            rafraichirField()
            rafraichirList()
        }
        
    }
}

