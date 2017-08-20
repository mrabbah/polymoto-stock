package com.choranet.commun


import org.zkoss.zul.*
import org.zkoss.zkplus.databind.*
import org.apache.commons.logging.Log 
import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import org.zkoss.zk.ui.Executions

//import org.codehaus.groovy.grails.plugins.springsecurity.Secured

/**
 * Super Window Object
 **/
//@Secured(['ROLE_ROOT'])
abstract class SuperWindow extends Window {
    
    /**
     * la classe fille de la super window :)
     * */
    def clazz
    /**
     * Nom de la classe fille
     **/
    def nomClassFille
    /**
     * Objet qui va contenir le filtre a utiliser
     * */
    def filtre
    /**
     * En cas ou on veut appliquer le filtre seulement sur certains attributs
     * */
    def attributsAFiltrer
    /**
     * Logger de la class ArticleWindow
     **/
    private Log logger = LogFactory.getLog(SuperWindow.class)

    /**
     * liste des objets
     **/
    def listeObjets
    /**
     * article selectionnï¿½
     **/
    def objetSelected
    /**
     * un nouveau element objet  
     **/
    def objet

    /**
     * Taille de liste extraite de la BdD
     * */
    def tailleListe
    
    /**
     * offset de la requete
     * */
    def ofs
    
    /**
     * nombre maximum a extraire de la BdD
     * */
    def maxNb
    /**
     * l element sur lequel va s effectuer le trie
     * */
    def sortedHeader = ""
    
    /**
     * direction du trie
     * */
    def sortedDirection = ""
    
    /**
     * session courante
     * */
    def session
    
    /**
     * servelet Application
     * */
    def servletContext
    
    /**
     * Constructeur
     **/
    public SuperWindow (Class clazz, Integer maxNb, List attributsAFiltrer, boolean securise = true, boolean initialise = true) throws Exception {
        initialisation(clazz, maxNb, attributsAFiltrer, securise, initialise)
    }   
    
    /**
     * Constructeur
     **/
    public SuperWindow (Class clazz, Integer maxNb, boolean securise = true, boolean initialise = true) throws Exception {
        initialisation(clazz, maxNb, null, securise, initialise)
    }   
    
    /**
     * Constructeur
     **/
    public SuperWindow (Class clazz, boolean securise = true, boolean initialise = true) throws Exception {
        initialisation(clazz, 12, null, securise, initialise)
    }   
    
    private void initialisation(clazz, maxNb, attributsAFiltrer, securise, initialise) throws Exception {
        logger.debug("Initialisation du super window : " + new Date())
        try {
            session = RequestContextHolder.currentRequestAttributes().getSession()
            servletContext = Executions.getCurrent().getDesktop().getWebApp()
            
            this.clazz = clazz
            this.maxNb = maxNb
            this.attributsAFiltrer = attributsAFiltrer
            this.nomClassFille = clazz.getSimpleName()
            ofs = 0
            objet = clazz.newInstance()
            filtre = clazz.newInstance()
            if(initialise) {
                listeObjets = clazz.list(offset:ofs, max:maxNb)
                def criteria = clazz.createCriteria()
                tailleListe = criteria.count{} 
                objetSelected = null                
                initialiserAssociation()
            }
        } catch(Exception e) {
            logger.error(e)
            throw e
        }
        logger.debug("Fin initialisation du super window : " + new Date())
    }        
    def filtrer() {
        try {
            def map
            if(attributsAFiltrer == null) {
                map = getService().filtrer(clazz, filtre, sortedHeader, sortedDirection, ofs, maxNb)
            } else {
                map = getService().filtrer(clazz, filtre, attributsAFiltrer, sortedHeader, sortedDirection, ofs, maxNb)
            }
            tailleListe = map["tailleListe"]
            listeObjets = map["listeObjets"]
        
            new AnnotateDataBinder(this.getFellow("lstObjet")).loadAll()
            def binder2 = new AnnotateDataBinder(this.getFellow("paging"))
            binder2.loadAll()    
        } catch (Exception ex) {
            logger.error(ex)
        }        
    }
    
    def getObjetsToExport() {
        try {
            if(attributsAFiltrer == null) {
                return getService().filtrer(clazz, filtre, sortedHeader, sortedDirection)
            } else {
                return getService().filtrer(clazz, filtre, attributsAFiltrer, sortedHeader, sortedDirection)
            }
        } catch (Exception ex) {
            logger.error(ex)
        }        
    }
    
    def sort(event) {
        sortedHeader = event.getTarget().getId()
        def sortDirection =  event.getTarget().getSortDirection()
        sortedDirection = "asc"

        if(sortDirection == "natural" || sortDirection == "descending") {
            sortedDirection = "desc"
        }
        
        filtrer()
    }
    
    def getNextElements(event) {
        def pgno = event.getActivePage()
        ofs = pgno * event.getPageable().getPageSize()
        
        filtrer()
    }
    
    /**
     *  Cette fonction est appelï¿½e lorsque un ï¿½lï¿½ment de la liste est selectionnï¿½
     **/
    def select() {          
        logger.debug("appel de select")
        objet = objetSelected	
        if(!objet.isAttached()) {
            logger.debug("objet selectionné n eset pas attaché")
            try {
                objet.attach()
            } catch(Exception ex) {
                logger.error("select : " + ex)
            }
        }
        logger.debug("appel d afficher valeur association")
        afficherValeurAssociation()
        //article.lock()  //Ne peut etre utilisï¿½ que pour le base de donnï¿½e qui accepte le veruillage des enregisterments
        logger.debug("rafraichissement des fields")
        rafraichirField()
        logger.debug("activation des boutons")
        activerBoutons(true)
    }
    /**
     * Fonction qui se charge de sauveguarder un nouveau ï¿½lï¿½ment de article
     **/
    def add() {
	actualiserValeurAssociation()
        try {     
            objet = getService().save(objet)            
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Echec lors de la transaction\n" + ex.message, "Erreur", Messagebox.OK, Messagebox.ERROR)
        } finally {
            objet = clazz.newInstance()
            rafraichirField()
            rafraichirList()
            activerBoutons(false)
        }
    }
    
    def addWithNoRefresh() {
	actualiserValeurAssociation()
        try {
            getService().save(objet)
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Echec lors de la transaction\n" + ex.message, "Erreur", Messagebox.OK, Messagebox.ERROR)
        } 
    }
    
    def refresh() {
        objet = clazz.newInstance()
        rafraichirField()
        rafraichirList()
        activerBoutons(false)
    }
    
    /**
     * Pour annuler la modification ou la supression et pour basculer en mode ajout d'un nouveau ï¿½lï¿½ment
     **/
    def cancel() {        
        objet = clazz.newInstance()
	reinitialiserAssociation(false)
        rafraichirField()
        activerBoutons(false)
        annulerSelection()
    }
    /**
     * Fonction qui se charge de la mise à jour
     **/
    def update() {
	actualiserValeurAssociation()
        try {
            objet = getService().update(objet)
        }
        catch(Exception e) {
            logger.error "Error: ${e.message}", e
            Messagebox.show("Problème lors de la mise à jour", "Erreur", Messagebox.OK, Messagebox.ERROR)
        }
        finally {
            objet = clazz.newInstance()
            activerBoutons(false)
            rafraichirField()
            rafraichirList()
        }
    }
    
    def updateWithNoRefresh() {
	actualiserValeurAssociation()
        try {
            getService().update(objet)
        } catch(Exception ex) {
            logger.error "Error: ${ex.message}", ex
            Messagebox.show("Echec lors de la transaction\n" + ex.message, "Erreur", Messagebox.OK, Messagebox.ERROR)
        } 
    }
    
    /**
     * Fonction qui se charge de supprimer un ï¿½lï¿½ment selectinnï¿½ de article
     **/
    def delete() {
        try {
            getService().delete(objet)
            activerBoutons(false)
            objet = clazz.newInstance()
            rafraichirField()
            rafraichirList()
            reinitialiserAssociation(true)
        } catch(Exception ex) {
            logger.error("problème lors de la suppression : " + ex)
            Messagebox.show("Impossible de supprimer l'élément en cours, vérifier qu'il n'est pas attaché à d'autres éléments", "Erreur", Messagebox.OK, Messagebox.ERROR)
        }
        
    }
    /**
     * Permet d'afficher l'anglet d'ajout d'un nouveau article
     **/
    def newRecord(){
        this.getFellow("westPanel").open = visible        
    }
    /**
     * Activer ou dï¿½sactiver les boutons d'ajout, suppression, modfication
     **/
    def activerBoutons(visible) {
        try {
            this.getFellow("btnUpdate").visible = visible
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("btnDelete").visible = visible
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("btnCancel").visible = true
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("btnPdf").visible = !visible
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("btnExcel").visible = !visible
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("btnSave").visible = !visible
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("btnNew").visible = !visible
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
        try {
            this.getFellow("westPanel").open = visible        
        } catch(Exception ex) {/*Exception prévue si l'interface ne contient pas le composant donné*/}
    }
    /**
     * Rï¿½initialiser les champs du formulaire
     **/
    def rafraichirField() {
        this.getFellows().each { co ->
            if(co.getId() != null && co.getId().startsWith("field")) {
                def binder = new AnnotateDataBinder(co)
                binder.loadAll()
            }
        }               
    }
    
    /**
     * Rafrichier la liste des article
     **/
    def rafraichirList() {
        filtrer()		
        annulerSelection()
    }
    /**
     * Basculer en mode saisi d'un nouveau ï¿½lï¿½ment
     **/
    def annulerSelection() {
        this.getFellow("lstObjet").clearSelection()
        objetSelected = null
    }

    protected abstract SuperService getService();
    
    def initialiserAssociation() {}
    def afficherValeurAssociation() {}
    def reinitialiserAssociation(test) {}
    def actualiserValeurAssociation() {}
}

