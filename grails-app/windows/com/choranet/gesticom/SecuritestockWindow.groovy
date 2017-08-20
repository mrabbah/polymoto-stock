
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

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.choranet.commun.SuperWindow
import com.choranet.commun.SuperService
import com.choranet.commun.ChoraClientInfo

/**
 * Securitestock Window Object
 **/
class SecuritestockWindow extends SuperWindow {
    /**
     * Service pour la gestion de l'objet Securitestock
     **/
    def securitestockService
    
    def parametrageService
    
    /**
     * Service jasper pour la generation des rapports
     **/
    def jasperService
    /**
     * Logger de la class SecuritestockWindow
     **/
    private Log logger = LogFactory.getLog(SecuritestockWindow.class)
    
    /**
     * liste de entrepot
     **/	
    def entrepots	
    /**
     * entrepot  selectionn�
     **/
    def entrepotSelected
                
    /**
     * liste de produit
     **/	
    def produits	
    /**
     * produit  selectionn�
     **/
    def produitSelected
    
    def listeProduits
    
    def isUpdate
    
    def produitPerissableManaged = true
    /**
     * Constructeur
     **/
    public SecuritestockWindow (parametrageService) {
        super(Securitestock.class)  
        this.parametrageService = parametrageService
        produitPerissableManaged = parametrageService.isProduitPerissableManaged()
    }          

    protected SuperService getService() {
        return this.securitestockService
    }
    
    def entrepotChoisi() {
        //getFellow("coentrepots").disabled = true
        produits = securitestockService.getProduitsSansSecuriteStock(entrepotSelected)
        if(produits.size() > 0) {
            produitSelected = produits.get(0)
            //getFellow("btnSave").visible = true
        }
        new AnnotateDataBinder(this.getFellow("coproduits")).loadAll()
        //getFellow("rwproduits").visible = true
    }
    /**
     * Generation du rapport pdf
     **/
    def genererRapportPdf() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des Securitestocks"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Securitestocks.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Securitestocks.pdf"
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
        String titrerapport = "Rapport des Securitestocks"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Securitestocks.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Securitestocks.xls"
        Filedownload.save(bit, "application/file", nom_fichier);
    }
    
    /**
     * Fonction qui g�re l'initialisation des listes d'associations au niveau du constructeur
     **/
    def initialiserAssociation() {
        entrepots = Entrepot.list()		
        filtre.stockMax = null
        filtre.stockMin = null
        filtre.stockReel = null
        listeProduits = Produit.list()
    }
    /**
     * Fonction qui permet de r�-initaliser l'association au niveau de l'interface
     * @param del si c'est une r�initionalisation apr�s une suppression ou non
     **/
    def reinitialiserAssociation(del) {
        entrepotSelected = null
        produits = null	
        produitSelected = null
    }
    /**
     * Fonction qui copie la valeur de l'association � l'�l�ment courant
     **/
    def actualiserValeurAssociation() {
        objet.entrepot = entrepotSelected
        def binderentrepot = new AnnotateDataBinder(this.getFellow("coentrepots"))
        entrepotSelected = null
        binderentrepot.loadAll()
                    		
        objet.produit = produitSelected
        produits = null
        produitSelected = null
        def binderproduit = new AnnotateDataBinder(this.getFellow("coproduits"))
        binderproduit.loadAll()
    }
    /**
     * Fonction qui fait la liaison entre l'association l'�l�ment selectionn� et la liste dans le crud
     **/
    def afficherValeurAssociation() {
        		
        def binderentrepot = new AnnotateDataBinder(this.getFellow("coentrepots"))
        entrepotSelected = entrepots.find{ it.id == Securitestock.findById(objet.id).entrepot.id }
        binderentrepot.loadAll()
                 
        produits = Produit.list()        
        def binderproduit = new AnnotateDataBinder(this.getFellow("coproduits"))
        produitSelected = produits.find{ it.id == Securitestock.findById(objet.id).produit.id }
        binderproduit.loadAll()
                    
    }
    
//    def activerBoutons(visible) {
//        this.getFellow("btnUpdate").visible = visible
//        //this.getFellow("btnDelete").visible = visible
//        this.getFellow("btnCancel").visible = true
//        this.getFellow("btnPdf").visible = !visible
//        this.getFellow("btnExcel").visible = !visible
//        this.getFellow("btnSave").visible = false
//        this.getFellow("btnNew").visible = !visible
//        this.getFellow("westPanel").open = visible    
//        this.getFellow("coentrepots").disabled = visible
//        this.getFellow("rwproduits").visible = visible
//        if(visible) {
//            this.getFellow("coproduits").disabled = visible
//        }
//    }
    
    /**
     *  Cette fonction est appeliée lorsque un élement de la liste est selectionné
     **/
    def select() {  
        isUpdate = true
        objet = objetSelected
        afficherValeurAssociation()
        rafraichirField()
    }
    
    def addOrUpdate(){
        if (isUpdate) {
            this.update()
            isUpdate = false
        } else {
            this.add()
        }
    }
    
    def add() {
        actualiserValeurAssociation()
        try {     
            getService().save(objet)
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Paramètres manquants, ou la nouvelle entré est déjà supportée par le système\n", "Ajout non disponible", Messagebox.OK, Messagebox.ERROR)
        } finally {
            objet = clazz.newInstance()
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
            Messagebox.show("La nouvelle valeur est dèjà mentionnée pour une sécurité de stock existante", "Mise à jour non disponible", Messagebox.OK, Messagebox.ERROR)
        }
        finally {
            objet = clazz.newInstance()
            rafraichirField()
            rafraichirList()
        }
        
    }
    
    def delete() {
         try{
            getService().delete(objet)
        }
        catch(Exception e) {
            Messagebox.show("Cette sécurité stock est déjà associée au moins à un produit, ou à un entrepôt\n", "Suppression non disponible", Messagebox.OK, Messagebox.ERROR)
        }
        objet = clazz.newInstance()
        rafraichirField()
        rafraichirList()
        reinitialiserAssociation(true)
    }
}

