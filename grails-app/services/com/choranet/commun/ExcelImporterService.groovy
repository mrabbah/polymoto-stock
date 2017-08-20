package com.choranet.commun

import org.apache.commons.logging.Log 
import org.apache.commons.logging.LogFactory

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.ByteArrayOutputStream
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.*;

import java.text.SimpleDateFormat;

import com.choranet.gesticom.*


class ExcelImporterService {

    private Log logger = LogFactory.getLog(ExcelImporterService.class)    
    
    static transactional = true   
    
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
    
    static String fs = System.getProperty('file.separator');
    static String ls = System.getProperty('line.separator');
    
    def categorieProduitService
    def produitService
    def entrepotService
    
    def creerFichierLogImport(String nomFichier) {
        File filelog = new File(System.getProperty('user.home')+fs+nomFichier+dateFormat.format(new Date()))
        filelog.createNewFile();
        loggerInformations(filelog, System.getProperty('os.name'))
        loggerInformations(filelog, System.getProperty('os.arch'))
        loggerInformations(filelog, System.getProperty('user.dir'))
        loggerInformations(filelog, System.getProperty('user.home'))
        loggerInformations(filelog, System.getProperty('user.language'))
        loggerInformations(filelog, System.getProperty('java.version'))
        loggerInformations(filelog, System.getProperty('java.vendor'))
        return filelog;
    }
    
    
    def loggerInformations(File filelog, Object info) {
        filelog << info
        filelog << ls
    }
    
    def chargerDonneesLigne(Row row, types) {
        int taille = types.size()
        Map map = [:]        
        def cles = types.keySet().toArray();
        for(int i = 0 ; i < taille ; i++ )  {
            Cell cell = row.getCell(i)
            def cle = cles[i]
            if(cell != null) {
                if(types[cle].equals("String")) {
                    switch(cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING : 
                         map[cle] = cell.getStringCellValue(); 
                        break;
                        case Cell.CELL_TYPE_NUMERIC : 
                         map[cle] = String.valueOf(cell.getNumericCellValue());
                        break;
                        case Cell.CELL_TYPE_BOOLEAN : 
                         map[cle] = String.valueOf(cell.getBooleanCellValue());
                        break;
                    }
                    if(map[cle] != null) {
                        map[cle] = map[cle].trim()
                        if(map[cle].equals("")) {
                            map[cle] = null
                        }
                    }
                } else if (types[cle].equals("Numeric")) {
                    map[cle] = cell.getNumericCellValue();
                } else if (types[cle].equals("Date")) {
                    map[cle] = cell.getDateCellValue();
                } else if (types[cle].equals("Boolean")) {
                    map[cle] = cell.getBooleanCellValue();
                } else {
                    map[cle] = null
                }
            } else {
                map[cle] = null
            }
        }
        return map
    }
    
    def chargerDonneesParDefaut(Map objet, Map valeursParDefaut) {
        valeursParDefaut.entrySet().each {
            if(objet[it.key] == null) {
                objet[it.key] = it.value
            }
        }
    } 
    
    def validerChampsRequis(Map objet, champsrequis) {
        for(champs in champsrequis) {
            if(objet[champs] == null) {
                return champs
            }
        }
        return null
    }
    /**
     *  Import des des produits
    Le fichier doit être sous la forme :
    code     designation        prixAchat       prixRevient        prixVente        Emplacement         UniteMesure     RegimeTVA       Categorie       codeBarre           poids           Nature        Produit parent        prixMoyenpendre
    001      produit 1          120.3           150                160              Default             Kg              20%             C1              4556455545          200             Fragile
    .
    .
    .
     **/
    
    def importerProduits(Object media) {
        File fileLog = creerFichierLogImport("log_import_produits")
        try {
            Workbook bk = new HSSFWorkbook(media.getStreamData())
            Sheet sheet = bk.getSheetAt(0)
            def first = true
            def compteur = 1;
            def nbImport = 0;
            def nbEchec = 0;
            
    
            def types = ["code": "String", "designation" : "String","conditionnement" : "String"
                , "prixAchat" : "Numeric"
                , "categorieProduit" : "String","empReception" : "String", "perissable" : "String"];
        
            def defaultCategorie = categorieProduitService.getDefaultCategorie();
            def empReceptionDefaut = entrepotService.getDefaultEntrepot()
            
            def valeursParDefaut = ["categorieProduit" : defaultCategorie,
            "empReception" : empReceptionDefaut]
            
            def valeursObligatoire = ["code", "designation", "prixAchat", "categorieProduit"]
            
            loggerInformations(fileLog, "Début import des produits : " + new Date())
            for (Row row : sheet) {
                try {
                    if(first) {
                        first = false
                        compteur++;
                        continue
                    }
                    
                    def map = chargerDonneesLigne(row, types);
                                        
                    if(map.categorieProduit != null && !map.categorieProduit.equals("")) {
                        map.categorieProduit = categorieProduitService.getCategorieByLibelle(map.categorieProduit)
                        if(map.categorieProduit == null) {
                            loggerInformations(fileLog, "Echec import ligne " + compteur + " categorie parente inexistante")
                            nbEchec++
                            compteur++
                            continue;
                        }
                    }
                    
                    if(map.empReception != null && !map.empReception.equals("")) {
                        map.empReception = entrepotService.getEntrepotByTitre(map.empReception)
                    }
                    
                    if(map.prixAchat != null && map.prixRevient == null) {
                        map.prixRevient = map.prixAchat
                    }
                    
                    chargerDonneesParDefaut(map, valeursParDefaut)
                    
                    def champ = validerChampsRequis(map, valeursObligatoire)
                    
                    if(champ != null) {
                        loggerInformations(fileLog, "Echec import ligne " + compteur + " le champ : " + champ + " est requis")
                        nbEchec++
                    } else {
                        def oldPdt1 = produitService.getProduitByCode(map.code);
                        def oldPdt2 = produitService.getProduitByDesignation(map.designation);
                        if(oldPdt1 != null || oldPdt2 != null) {
                            loggerInformations(fileLog, "Echec import ligne " + compteur + " le produit : " + map.code + "-" + map.designation + " existe déjà")
                            nbEchec++
                        } else {
                            Boolean estPerissable
                            
                            if (map.perissable == 'oui'){
                               estPerissable = true 
                            }
                            else if (map.perissable == 'non'){
                                estPerissable = false
                            }
                            
                            def newPdt = new Produit(code: map.code, designation : map.designation, prixAchat : map.prixAchat
                                , empReception : map.empReception, categorieProduit : map.categorieProduit
                                , conditionnement : map.conditionnement, perissable : estPerissable );
                                    
                            produitService.save(newPdt)
                            nbImport++
                        }
                    }
                    
                    compteur++;
                } catch(Exception ex) {
                    loggerInformations(fileLog, "Echec import ligne " + compteur + " " + ex)
                    nbEchec++
                    compteur++;
                }
                
            }
            loggerInformations(fileLog, "Fin import catégorie produit : " + new Date())
            loggerInformations(fileLog, "Nombre de lignes importées : " + nbImport)
            loggerInformations(fileLog, "Nombre de lignes échouées : " + nbEchec)
        
            
        } catch(Exception ex) {
            loggerInformations(fileLog, "Fichier invalide : " + ls + ex)
        }
        return "Import terminé voir le fichier de log : " + fileLog.getAbsolutePath();
    }
    
}
