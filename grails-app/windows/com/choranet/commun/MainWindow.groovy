
package com.choranet.commun

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.*
import org.zkoss.zkplus.databind.*
import org.apache.commons.logging.Log 
import org.apache.commons.logging.LogFactory
import org.zkoss.zksandbox.Category
import org.springframework.web.context.request.RequestContextHolder
import org.zkoss.zk.ui.Executions

/**
 *
 * @author RABBAH
 */
class MainWindow extends Window {
    
    /**
     * Logger de la class ArticleWindow
     **/
    private Log logger = LogFactory.getLog(MainWindow.class)
    
    def session
    def nomsociete
    def site
    def trans_logo
    
    public MainWindow() {                
        session = RequestContextHolder.currentRequestAttributes().getSession()
        if(!session.societe) {
            session.societe = ChoraClientInfo.list()[0]
        }
        nomsociete = session.societe.raisonSociale
        site = session.societe.site
        trans_logo  = session.societe.trans_logo
    }
    
}

