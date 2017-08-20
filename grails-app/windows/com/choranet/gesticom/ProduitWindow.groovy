
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
 * Produit Window Object
 **/
class ProduitWindow extends SuperWindow {
    /**
     * Service pour la gestion de l'objet Produit
     **/
    def produitService
    /**
     * Service jasper pour la generation des rapports
     **/
    def jasperService
    /**
     * Logger de la class ProduitWindow
     **/
    private Log logger = LogFactory.getLog(ProduitWindow.class)
    /**
     * liste de empReception
     **/	
    def empReceptions	
    /**
     * empReception  selectionné
     **/
    def empReceptionSelected
    /**
     * liste de categorieProduit
     **/	
    def categorieProduits	
    /**
     * categorieProduit  selectionné
     **/
    def categorieProduitSelected

    def excelImporterService
    /**
     * Constructeur
     **/
    public ProduitWindow () {
        super(Produit.class)
    }  

    protected SuperService getService() {
        return this.produitService
    }
    
    def importation(media) {
        String resultat = excelImporterService.importerProduits(media)
        Messagebox.show(resultat, "Notification" ,  Messagebox.OK, Messagebox.INFORMATION)
        rafraichirList()
    }
    
    /**
     * Generation du rapport pdf
     **/
    def genererRapportPdf() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des Produits"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Produits.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Produits.pdf"
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
        String titrerapport = "Rapport des Produits"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_Produits.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_Produits.xls"
        Filedownload.save(bit, "application/file", nom_fichier);
        
    }
    /**
     * Fonction qui gére l'initialisation des listes d'associations au niveau du constructeur
     **/
    def initialiserAssociation() {
        
        empReceptions = Entrepot.list()		
        if(empReceptions.size() > 0)
        empReceptionSelected = empReceptions.get(0)
        else
        empReceptionSelected = null
                    
        categorieProduits = CategorieProduit.list()		
        if(categorieProduits.size() > 0)
        categorieProduitSelected = categorieProduits.get(0)
        else
        categorieProduitSelected = null
    }
    /**
     * Fonction qui permet de ré-initaliser l'association au niveau de l'interface
     * @param del si c'est une réinitionalisation aprés une suppression ou non
     **/
    def reinitialiserAssociation(del) {
        	
        if(del) {
            empReceptions = Entrepot.list()
        }	
        if(empReceptions.size() > 0)
        empReceptionSelected = empReceptions.get(0)
        else
        empReceptionSelected = null
                    	
        if(del) {
            categorieProduits = CategorieProduit.list()
        }	
        if(categorieProduits.size() > 0)
        categorieProduitSelected = categorieProduits.get(0)
        else
        categorieProduitSelected = null
                    	
    }
    /**
     * Fonction qui copie la valeur de l'association é l'élément courant
     **/
    def actualiserValeurAssociation() {
        		
        objet.empReception = empReceptionSelected
        if(empReceptions.size() > 0) {
            def binderempReception = new AnnotateDataBinder(this.getFellow("coempReceptions"))
            empReceptionSelected = empReceptions.get(0)
            binderempReception.loadAll()
        }
        else
        empReceptionSelected = null
                    		
        objet.categorieProduit = categorieProduitSelected
        if(categorieProduits.size() > 0) {
            def bindercategorieProduit = new AnnotateDataBinder(this.getFellow("cocategorieProduits"))
            categorieProduitSelected = categorieProduits.get(0)
            bindercategorieProduit.loadAll()
        }
        else
        categorieProduitSelected = null
    }
    /**
     * Fonction qui fait la liaison entre l'association l'élément selectionné et la liste dans le crud
     **/
    def afficherValeurAssociation() {
        		
        def binderempReception = new AnnotateDataBinder(this.getFellow("coempReceptions"))
        empReceptionSelected = empReceptions.find{ it.id == Produit.findById(objet.id).empReception.id }
        binderempReception.loadAll()
                    		
        def bindercategorieProduit = new AnnotateDataBinder(this.getFellow("cocategorieProduits"))
        categorieProduitSelected = categorieProduits.find{ it.id == Produit.findById(objet.id).categorieProduit.id }
        bindercategorieProduit.loadAll()
                    
    }
}

