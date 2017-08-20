
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
import java.text.SimpleDateFormat;

/**
 * Home Window Object
 **/
class HomeWindow extends SuperWindow {
    
    /**
     * Service pour la gestion de l'objet Home
     **/
    def securitestockService
    
    def parametrageService
    
    /**
     * Service jasper pour la generation des rapports
     **/
    def jasperService
    /**
     * Logger de la class HomeWindow
     **/
    private Log logger = LogFactory.getLog(HomeWindow.class)
    
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
    
    def modelpie = new SimpleCategoryModel();
    def modelpiePeremp = new SimpleCategoryModel();


    /**
     * Constructeur
     **/
    public HomeWindow (parametrageService, securitestockService) {
        super(Securitestock.class, false, false)  
        this.parametrageService = parametrageService
        this.securitestockService = securitestockService
        produitPerissableManaged = parametrageService.isProduitPerissableManaged()
        def criteria = Securitestock.createCriteria()
        Date d = new Date() + 90
        //println d
        listeObjets = criteria.list {
            gt("stockReel", 0d)
            lt("date_preremption", d)
        }
        def criteriabis = Securitestock.createCriteria()
        tailleListe = criteriabis.count{
            gt("stockReel", 0d)
            lt("date_preremption", d)
        } 
        objetSelected = null                
        
        def criteria2 = Securitestock.createCriteria()
        
        def listPie = criteria2.list {            
            createAlias('produit', 'pdt')            
            projections{
                groupProperty('pdt.code')
                count("stockReel")
            }
            gt("stockReel", 0d)
        }
        
        for(elementPie in listPie) {
            modelpie.setValue("MED", elementPie[0], elementPie[1]);            
        }
        
        def criteria3 = Securitestock.createCriteria()
        
        def listPie2 = criteria3.list {
            gt("stockReel", 0d)            
        }
        SimpleDateFormat df = new SimpleDateFormat("MM/yyyy");
        for(elementPie2 in listPie2) {
            if(elementPie2.date_preremption == null) {
                modelpiePeremp.setValue("consommable", elementPie2.produit.designation, elementPie2.stockReel);
            } else {
                
                modelpiePeremp.setValue(df.format(elementPie2.date_preremption), elementPie2.produit.designation , elementPie2.stockReel);
            }
            
        }
        
        initialiserAssociation()
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
        String titrerapport = "Rapport des Homes"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Homes.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Homes.pdf"
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
        String titrerapport = "Rapport des Homes"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Homes.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Homes.xls"
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
        entrepotSelected = entrepots.find{ it.id == Home.findById(objet.id).entrepot.id }
        binderentrepot.loadAll()
                 
        produits = Produit.list()        
        def binderproduit = new AnnotateDataBinder(this.getFellow("coproduits"))
        produitSelected = produits.find{ it.id == Home.findById(objet.id).produit.id }
        binderproduit.loadAll()
                    
    }
 
}

