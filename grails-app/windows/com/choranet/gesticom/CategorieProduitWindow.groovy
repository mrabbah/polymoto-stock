
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
 * CategorieProduit Window Object
 **/
class CategorieProduitWindow extends SuperWindow {
    /**
     * Service pour la gestion de l'objet CategorieProduit
     **/
    def categorieProduitService
    
    /**
     * Service jasper pour la generation des rapports
     **/
    def jasperService
    /**
     * Logger de la class CategorieProduitWindow
     **/
    private Log logger = LogFactory.getLog(CategorieProduitWindow.class)
    
    /**
     * liste de categorieParente
     **/	
    def categorieParentes
    
    def categorieParentesSelected
    /**
     * categorieParente  selectionn�
     **/
    def categorieParenteSelected
                
    def isUpdate
    
    //def basicName = null
    
    def excelImporterService
    
    /**
     * Constructeur
     **/
    public CategorieProduitWindow () {
        super(CategorieProduit.class)
    }  

    protected SuperService getService() {
        return this.categorieProduitService
    }
    
    def importation(media) {
        String resultat = excelImporterService.importerCategoriesProduits(media)
        Messagebox.show(resultat, "Notification" ,  Messagebox.OK, Messagebox.INFORMATION)
        rafraichirList()
    }
    /**
     * Generation du rapport pdf
     **/
    def genererRapportPdf() {
        String nomsociete = session.societe.raisonSociale
        String titrerapport = "Rapport des CategorieProduits"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_CategorieProduits.jrxml',
            fileFormat:JasperExportFormat.PDF_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_CategorieProduits.pdf"
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
        String titrerapport = "Rapport des CategorieProduits"
        def listeObjetsExporter = getObjetsToExport()
        def reportDef = new JasperReportDef(name:'rapport_des_CategorieProduits.jrxml',
            fileFormat:JasperExportFormat.XLS_FORMAT,
            reportData : listeObjetsExporter,
            parameters : [  'nomsociete':nomsociete, 'titrerapport':titrerapport ]
        )
        def bit = jasperService.generateReport(reportDef).toByteArray()
        def nom_fichier = "rapport_des_CategorieProduits.xls"
        Filedownload.save(bit, "application/file", nom_fichier);
        
    }
    
    def add() {
        super.add()
        rechargerListsCategories()
    }
    
    def update() {
        if(!objet.isAttached()) {
            logger.debug("update objet n est pas attache")
            objet.attach()
        }        
        super.update()
        rechargerListsCategories()
    }
    
    def delete() {
        super.delete()
        rechargerListsCategories()
    }
    
    def rechargerListsCategories() {
        //CategorieProduit.withTransaction {
        categorieParenteSelected = null
        categorieParentes = CategorieProduit.list()
        categorieParentesSelected = CategorieProduit.list()
        new AnnotateDataBinder(this.getFellow("filtercocategorieParentes")).loadAll()
        new AnnotateDataBinder(this.getFellow("cocategorieParentes")).loadAll()
        //}
    }
    /**
     * Fonction qui g�re l'initialisation des listes d'associations au niveau du constructeur
     **/
    def initialiserAssociation() {
        categorieParentes = CategorieProduit.list()
        categorieParentesSelected = CategorieProduit.list()
    }
    
    /**
     * Fonction qui copie la valeur de l'association � l'�l�ment courant
     **/
    def actualiserValeurAssociation() {	
        if(!categorieParenteSelected.isAttached()) {
            logger.debug("categorieParenteSelected n est pas attaché")
            try {
                categorieParenteSelected.attach()
            } catch(Exception ex) {
                logger.error(ex)
            }
        }
        objet.categorieParente = categorieParenteSelected        
        categorieParenteSelected = null
    }
    
    /**
     * Fonction qui fait la liaison entre l'association l'�l�ment selectionn� et la liste dans le crud
     **/
    def afficherValeurAssociation() {
        CategorieProduit.withTransaction {
            Combobox cmb = (Combobox) this.getFellow("cocategorieParentes")    
            
            //        def bindercategorieParente = new AnnotateDataBinder(this.getFellow("cocategorieParentes"))  
            //        bindercategorieParente.loadAll()
            if(objet.categorieParente) {
                int index = 0;
                for(c in categorieParentesSelected) {
                    if(c.code.equals(objet.categorieParente.code)) {
                        categorieParenteSelected = c;
                        break;
                    }
                    index++;
                }
                logger.debug("categorie parente retrouvée est " + categorieParenteSelected + " a l index " + index)
                if(!categorieParenteSelected.isAttached()) {
                    logger.debug("categorieParenteSelected n est pas attaché")
                    categorieParenteSelected.attach()
                }
                //categorieParenteSelected = categorieParentesSelected.find { it == objet.categorieParente}
                //int index = categorieParentesSelected.indexOf(categorieParenteSelected)
                cmb.setSelectedIndex(index)
            } else {
                categorieParenteSelected = null  
                cmb.setSelectedItem(null)
            }        
            //bindercategorieParente.loadAll()
        }                    
    }
    
    def afficherValeurAssociationSelected() {
        def bindercategorieParente = new AnnotateDataBinder(this.getFellow("cocategorieParentes"))
        categorieParentesSelected = CategorieProduit.removeAllSon(categorieParentes, objet)
        bindercategorieParente.loadAll()  
    }
    /**
     *  Cette fonction est appeliée lorsque un élement de la liste est selectionné
     **/
    def select() {  
        isUpdate = true
        super.select()
    }
    
    def addOrUpdate(){    
        // CategorieProduit.withTransaction {
        if (isUpdate) {
            //getCategorieFullNameForUpdate()
            this.update()
            isUpdate = false
        } else {
            this.add()
        }
        //     }
        
    }        
    
}

