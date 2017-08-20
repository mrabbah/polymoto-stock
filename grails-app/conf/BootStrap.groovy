import com.choranet.gesticom.*
import com.choranet.commun.*

import org.codehaus.groovy.grails.commons.*

class BootStrap {

    def categorieProduitService
    
    def init = { servletContext ->
        environments {
            development {
                
                def config = ConfigurationHolder.config

                def chora = new ChoraClientInfo(raisonSociale:'CHORA INFORMATIQUE',
                    formeJuridique : "SARL", telephone : "+212 522 233 343", 
                    fax : "+212 522 233 344", email:"contact@choranet.com",
                    patente:"78/7877",rc:"78965",idF:"45788",cnss:"21/45789",
                    site:"http://www.choranet.com",repertoirBackup:System.getProperty('user.home'),
                    adresse: '11M3 Lot Alhamd Ain Sbaa', codePostale : '20250', ville : 'Casablanca', 
                    pays : 'Maroc', cachetData : null, entetepiedData : null, copieData : null)
                chora.setTrans_logo(null)
                chora.setTrans_cachet(null)
                chora.setTrans_entetepied(null)
                chora.setTrans_copie(null)                
                chora.save()
                
                
                // Catégories des produits
                def c_p = new CategorieProduit(code:'01', intitule : 'Médicament', categorieParente : null);
                categorieProduitService.save(c_p)
                def c_logiciels = new CategorieProduit(code:'02', intitule : 'Consommable', categorieParente : null);
                categorieProduitService.save(c_logiciels)
                
                def entrepot = new Entrepot(code :'01', intitule : 'DEFAUT').save();
                
            }
            test {

            }
            production {
        
            }
        }
                
    }
    def destroy = {
    }
}
