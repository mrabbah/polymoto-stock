package com.choranet.commun

import org.apache.commons.logging.Log 
import org.apache.commons.logging.LogFactory
import java.lang.reflect.Field
import org.hibernate.Criteria;

abstract class SuperService {

    protected Log logger = LogFactory.getLog(this.getClass())
    
    static transactional = true
    
    def filtrer(clazz, filtre, sortedHeader, sortedDirection, ofs, maxNb) throws Exception {
        try {
            def listeObjets 
            def tailleListe = 0
            def fields = clazz.getDeclaredFields()
            def requete = clazz.createCriteria()
            def attributToSort
            def selectionnerDistinctResultat = false
            listeObjets = requete.list {
                for(field in fields) {
                    def nomAttribut = field.getName()
                    def modifier = field.getModifiers()
                    if(!nomAttribut.startsWith("trans_") && 
                        !(modifier > 2)) { // eviter les attributs transients: qui ne sont pas persister et les attributs static
                        def typeAttribut = field.getType()
                        //def valeurAttribut = field.get(filtre)
                        def valeurAttribut = filtre."$nomAttribut"
                    
                        if(sortedHeader == "h"+nomAttribut) {
                            attributToSort = nomAttribut 
                        }
                        if(valeurAttribut != null) {
                            if(valeurAttribut instanceof String) {
                                if(valeurAttribut != "") {
                                    ilike(nomAttribut, "%"+valeurAttribut+"%")                                
                                }
                            } else if(valeurAttribut instanceof Boolean || valeurAttribut == boolean.class) {
                                if(valeurAttribut) {
                                    eq(nomAttribut, true)
                                } else {
                                    eq(nomAttribut, false)
                                }
                            } else if(valeurAttribut instanceof java.util.Collection) {
                                    "$nomAttribut" {"in"("id",valeurAttribut.id)}
                                    selectionnerDistinctResultat = true
                            } else {
                                eq(nomAttribut, valeurAttribut)
                            }
                        }
                    }
                }
                if(attributToSort != null) {
                    order(attributToSort, sortedDirection)
                }
                firstResult(ofs)
                maxResults(maxNb)
                if(selectionnerDistinctResultat) {
                    resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                    //Cette solution n est pas clean puisque ce travail est fait
                    //cote client et non pas database une deuxieme solution es la
                    //suivante reste a affiner voir: 
                    //https://forum.hibernate.org/viewtopic.php?t=941669
                    //                    projections {
                    //                        clazz.getSimpleName()
                    //                        distinct("id")
                    //                    }
                }
            }
            
            if(listeObjets != null && listeObjets.size() > 0) {
                def criteria = clazz.createCriteria()
                tailleListe = criteria.count{
                    for(field in fields) {
                        def nomAttribut = field.getName()
                        def modifier = field.getModifiers()
                        if(!nomAttribut.startsWith("trans_") && 
                            !(modifier > 2)) { // eviter les attributs transients: qui ne sont pas persister et les attributs static
                            def typeAttribut = field.getType()
                            //def valeurAttribut = field.get(filtre)
                            def valeurAttribut = filtre."$nomAttribut"
                    
                            if(valeurAttribut != null) {
                                if(valeurAttribut instanceof String) {
                                    if(valeurAttribut != "") {
                                        ilike(nomAttribut, "%"+valeurAttribut+"%")                                
                                    }
                                } else if(valeurAttribut instanceof Boolean || valeurAttribut == boolean.class) {
                                    if(valeurAttribut) {
                                        eq(nomAttribut, true)
                                    }
                                } else if(valeurAttribut instanceof java.util.Collection) {
                                    "$nomAttribut" {"in"("id",valeurAttribut.id)}
                                } else {
                                    eq(nomAttribut, valeurAttribut)
                                }
                            }
                        }
                    } 
                    if(selectionnerDistinctResultat) {
                        resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        //Cette solution n est pas clean puisque ce travail est fait
                        //cote client et non pas database une deuxieme solution es la
                        //suivante reste a affiner voir: 
                        //https://forum.hibernate.org/viewtopic.php?t=941669
                        //                    projections {
                        //                        clazz.getSimpleName()
                        //                        distinct("id")
                        //                    }
                    }    
                }
            } else {
                tailleListe = 0
                listeObjets = null
            }
            
            return [tailleListe:tailleListe,listeObjets:listeObjets]
        }
        catch(Exception e) {
            logger.error(e)
            throw e
        }
    }

    def filtrer(clazz, filtre, attributsAFiltrer, sortedHeader, sortedDirection, ofs, maxNb) throws Exception {
        try {
            def listeObjets 
            def tailleListe = 0
            def requete = clazz.createCriteria()
            def attributToSort
            def selectionnerDistinctResultat = false
            listeObjets = requete.list {
                for(nomAttribut in attributsAFiltrer) {
                    def valeurAttribut = filtre."$nomAttribut"
                    
                    if(sortedHeader == "h"+nomAttribut) {
                        attributToSort = nomAttribut 
                    }
                    if(valeurAttribut != null) {
                        if(valeurAttribut instanceof String) {
                            if(valeurAttribut != "") {
                                ilike(nomAttribut, "%"+valeurAttribut+"%")                                
                            }
                        } else if(valeurAttribut instanceof Boolean || valeurAttribut == boolean.class) {
                            if(valeurAttribut) {
                                eq(nomAttribut, true)
                            } else {
                                eq(nomAttribut, false)
                            }
                        } else if(valeurAttribut instanceof java.util.Collection) {
                            "$nomAttribut" {"in"("id",valeurAttribut.id)}
                            selectionnerDistinctResultat = true
                        } else {
                            eq(nomAttribut, valeurAttribut)
                        }
                    }
                }
                if(attributToSort != null) {
                    order(attributToSort, sortedDirection)
                }
                firstResult(ofs)
                maxResults(maxNb) 
                if(selectionnerDistinctResultat) {
                    resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                    //Cette solution n est pas clean puisque ce travail est fait
                    //cote client et non pas database une deuxieme solution es la
                    //suivante reste a affiner voir: 
                    //https://forum.hibernate.org/viewtopic.php?t=941669
                    //                    projections {
                    //                        clazz.getSimpleName()
                    //                        distinct("id")
                    //                    }
                }
            }
            if(listeObjets != null && listeObjets.size() > 0) {
                def criteria = clazz.createCriteria()
                tailleListe = criteria.count {
                    for(nomAttribut in attributsAFiltrer) {
                        def valeurAttribut = filtre."$nomAttribut"
                        
                        if(valeurAttribut != null) {
                            if(valeurAttribut instanceof String) {
                                if(valeurAttribut != "") {
                                    ilike(nomAttribut, "%"+valeurAttribut+"%")                                
                                }
                            } else if(valeurAttribut instanceof Boolean || valeurAttribut == boolean.class) {
                                if(valeurAttribut) {
                                    eq(nomAttribut, true)
                                }
                            }else if(valeurAttribut instanceof java.util.Collection) {
                                "$nomAttribut" {"in"("id",valeurAttribut.id)}
                            } else {
                                eq(nomAttribut, valeurAttribut)
                            }
                        }
                    }
                    if(selectionnerDistinctResultat) {
                        resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        //Cette solution n est pas clean puisque ce travail est fait
                        //cote client et non pas database une deuxieme solution es la
                        //suivante reste a affiner voir: 
                        //https://forum.hibernate.org/viewtopic.php?t=941669
                        //                    projections {
                        //                        clazz.getSimpleName()
                        //                        distinct("id")
                        //                    }
                    }
                }
            }else {
                tailleListe = 0
                listeObjets = null
            }
            
            return [tailleListe:tailleListe,listeObjets:listeObjets]
            
        }
        catch(Exception e) {
            logger.error(e)
            throw e
        }
    }
    
    def filtrer(clazz, filtre, sortedHeader, sortedDirection) throws Exception {
        try {
            def listeObjets 
            def fields = clazz.getDeclaredFields()
            def requete = clazz.createCriteria()
            def attributToSort
            def selectionnerDistinctResultat = false
            listeObjets = requete.list {
                for(field in fields) {
                    def nomAttribut = field.getName()
                    def modifier = field.getModifiers()
                    if(!nomAttribut.startsWith("trans_") && 
                        !(modifier > 2)) { // eviter les attributs transients: qui ne sont pas persister et les attributs static
                        def typeAttribut = field.getType()
                        //def valeurAttribut = field.get(filtre)
                        def valeurAttribut = filtre."$nomAttribut"
                    
                        if(sortedHeader == "h"+nomAttribut) {
                            attributToSort = nomAttribut 
                        }
                        if(valeurAttribut != null) {
                            if(valeurAttribut instanceof String) {
                                if(valeurAttribut != "") {
                                    ilike(nomAttribut, "%"+valeurAttribut+"%")                                
                                }
                            } else if(valeurAttribut instanceof Boolean || valeurAttribut == boolean.class) {
                                if(valeurAttribut) {
                                    eq(nomAttribut, true)
                                } else {
                                    eq(nomAttribut, false)
                                }
                            } else if(valeurAttribut instanceof java.util.Collection) {
                                    "$nomAttribut" {"in"("id",valeurAttribut.id)}
                                    selectionnerDistinctResultat = true
                            } else {
                                eq(nomAttribut, valeurAttribut)
                            }
                        }
                    }
                }
                if(attributToSort != null) {
                    order(attributToSort, sortedDirection)
                }
                
                if(selectionnerDistinctResultat) {
                    resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                    //Cette solution n est pas clean puisque ce travail est fait
                    //cote client et non pas database une deuxieme solution es la
                    //suivante reste a affiner voir: 
                    //https://forum.hibernate.org/viewtopic.php?t=941669
                    //                    projections {
                    //                        clazz.getSimpleName()
                    //                        distinct("id")
                    //                    }
                }
            }
            
            if(listeObjets == null || listeObjets.size() == 0) {
                listeObjets = null
            }
            
            
            return listeObjets
        }
        catch(Exception e) {
            logger.error(e)
            throw e
        }
    }

    def filtrer(clazz, filtre, attributsAFiltrer, sortedHeader, sortedDirection) throws Exception {
        try {
            def listeObjets 
            def requete = clazz.createCriteria()
            def attributToSort
            def selectionnerDistinctResultat = false
            listeObjets = requete.list {
                for(nomAttribut in attributsAFiltrer) {
                    def valeurAttribut = filtre."$nomAttribut"
                    
                    if(sortedHeader == "h"+nomAttribut) {
                        attributToSort = nomAttribut 
                    }
                    if(valeurAttribut != null) {
                        if(valeurAttribut instanceof String) {
                            if(valeurAttribut != "") {
                                ilike(nomAttribut, "%"+valeurAttribut+"%")                                
                            }
                        } else if(valeurAttribut instanceof Boolean || valeurAttribut == boolean.class) {
                            if(valeurAttribut) {
                                eq(nomAttribut, true)
                            } else {
                                eq(nomAttribut, false)
                            }
                        } else if(valeurAttribut instanceof java.util.Collection) {
                            "$nomAttribut" {"in"("id",valeurAttribut.id)}
                            selectionnerDistinctResultat = true
                        } else {
                            eq(nomAttribut, valeurAttribut)
                        }
                    }
                }
                if(attributToSort != null) {
                    order(attributToSort, sortedDirection)
                }                
                if(selectionnerDistinctResultat) {
                    resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                    //Cette solution n est pas clean puisque ce travail est fait
                    //cote client et non pas database une deuxieme solution es la
                    //suivante reste a affiner voir: 
                    //https://forum.hibernate.org/viewtopic.php?t=941669
                    //                    projections {
                    //                        clazz.getSimpleName()
                    //                        distinct("id")
                    //                    }
                }
            }
            if(listeObjets == null || listeObjets.size() == 0) {
                listeObjets = null
            }
            
            
            return listeObjets
            
        }
        catch(Exception e) {
            logger.error(e)
            throw e
        }
    }
    
    def list(Class clazz) throws Exception {
        try {
            return clazz.list()
        }
        catch(Exception e) {
            logger.error(e)
            throw e
        }
    }
    def update(Object object) throws Exception {       
        object.validate()
        if(!object.hasErrors()) {
            try {                
                return object.merge(flush:true)
            }
            catch(Exception e) {
                logger.error(e)
                throw e;
            }
        } else {
            def erreurs = "Les erreur(s) suivante(s) sont a corriger :"
            object.errors.each { error ->
                erreurs += error
            }
            logger.error(erreurs)
            throw new Exception(erreurs)
        }        
    }

    def save(Object object) throws Exception {
        object.validate()
        if(!object.hasErrors()) {
            try {
                return object.save(flush:true)
            }
            catch(Exception e) {
                logger.error(e)
                throw e;
            }
        } else {
            def erreurs = "Les erreur(s) suivante(s) sont a corriger :"
            object.errors.each { error ->
                erreurs += error
            }
            logger.error(erreurs)
            throw new Exception(erreurs)
        }
    }

    def delete(Object object) throws Exception {
        try {
            object.delete(flush:true)
        } catch(org.springframework.orm.hibernate3.HibernateSystemException hex) {
            logger.warn("HibernateSystemException soulevé lors de la suppression")
        } catch(Exception e) {
            logger.error(e)
            throw new Exception("Impossible de supprimer l'objet en cours tant qu'il a d'atures liaisons");
        }
    }
}
