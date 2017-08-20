package com.choranet.gesticom.util;

import java.io.File;
import java.io.InputStream;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;

/**
 *
 * @author RABBAH
 */
public class Utilitaire {

    private static String pathToLogoImage = "com/choranet/gesticom/images/logo.png";
    private static String pathToCachetImage = "com/choranet/gesticom/images/NeantCachet.png";
    private static String pathToCopieImage = "com/choranet/gesticom/images/NeantCopie.png";
    private static String pathToFondImage = "com/choranet/gesticom/images/NeantFonA4.png";
    private static String pathToDocumentSubReport = "com/choranet/gesticom/subreports/SubReport_LigneProduits.jasper";
    private static String pathToDocumentSubReportBL = "com/choranet/gesticom/subreports/SubReport_LigneProduitsBLWPRICE.jasper";
    private static String pathToDocumentSubReportBR = "com/choranet/gesticom/subreports/SubReport_LigneProduitsBRWPRICE.jasper";
    private static String pathToDocumentSubReportWD = "com/choranet/gesticom/subreports/SubReport_LigneProduitsWD.jasper";
    private static String pathToSubReportBonCommandesventesachatsDetails = "com/choranet/gesticom/subreports/SubReport_BonCommandes_ventesachatsDetails.jasper";
    private static String pathToDocumentImage = "com/choranet/gesticom/images/logo.png";
    private static String pathToPaiementSubReport = "com/choranet/gesticom/subreports/subReport_des_Paiements.jasper";
    private static String pathToBCSubReport = "com/choranet/gesticom/subreports/subReport_des_BonCommandes.jasper";
    private static String pathToDocumentSubReportWRT = "com/choranet/gesticom/subreports/SubReport_LigneProduitsWRT.jasper";

    public static Image getLogoImage(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        InputStream is;
        try {
            is = cl.getResourceAsStream(pathToLogoImage);
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir l image logo", e);
        }
        Image img = new AImage("logo", is);
        return img;
    }

    public static Image getCachetImage(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        InputStream is;
        try {
            is = cl.getResourceAsStream(pathToCachetImage);
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir l image ", e);
        }
        Image img = new AImage("logo", is);
        return img;
    }

    public static Image getCopieImage(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        InputStream is;
        try {
            is = cl.getResourceAsStream(pathToCopieImage);
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir l image ", e);
        }
        Image img = new AImage("logo", is);
        return img;
    }

    public static Image getFondImage(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        InputStream is;
        try {
            is = cl.getResourceAsStream(pathToFondImage);
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir l image ", e);
        }
        Image img = new AImage("logo", is);
        return img;
    }

    public static File getDocumentSubReport(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToDocumentSubReport).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport", e);
        }
        return is;
    }

    public static File getDocumentSubReportWRT(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToDocumentSubReportWRT).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport", e);
        }
        return is;
    }

    public static File getDocumentSubReportBL(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToDocumentSubReportBL).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport", e);
        }
        return is;
    }

    public static File getDocumentSubReportBR(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToDocumentSubReportBR).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport", e);
        }
        return is;
    }

    public static File getPaiementSubReport(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToPaiementSubReport).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport paiement", e);
        }
        return is;
    }

    public static File getBCSubReport(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToBCSubReport).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport BC", e);
        }
        return is;
    }

    public static File getDocumentSubReportWD(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToDocumentSubReportWD).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport", e);
        }
        return is;
    }

    public static File getSubReportBonCommandesventesachatsDetails(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        File is;
        try {
            is = new File(cl.getResource(pathToSubReportBonCommandesventesachatsDetails).getPath());
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir le rapport", e);
        }
        return is;
    }

    public static InputStream getDocumentImage(Class clazz) throws Exception {
        ClassLoader cl = clazz.getClassLoader();
        InputStream is;
        try {
            is = cl.getResourceAsStream(pathToDocumentImage);
        } catch (Exception e) {
            throw new Exception("Impossible d obtenir l image", e);
        }
        return is;
    }

    public static int getNbApresLaVergule(double nombre) {

        int intNb = (int) nombre;
        nombre = nombre - intNb;
        double res = nombre * 100;
        int resultat = (int) res;
        return resultat;

    }
}