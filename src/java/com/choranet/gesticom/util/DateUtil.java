package com.choranet.gesticom.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author rabbah
 * @description : cette classe comporte des fonctions utiles pour la conversion des dates
 */

public class DateUtil {

    private static Log log = LogFactory.getLog(DateUtil.class);
    private static final String TIME_PATTERN = "HH:mm";
    
    public static final String[] moisAnnee = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private DateUtil() {
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        //org.hibernate.impl.QueryImpl q;
        //q.uniqueResult();
        return "dd/MM/yyyy";
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss";
    }

    /**
     * This method attempts to convert an Oracle-formatted date
     * in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate(Date aDate) {
        if (aDate != null) {
            SimpleDateFormat df = new SimpleDateFormat(DateUtil.getDatePattern());
            return df.format(aDate);
        }

        return "";
    }

    /**
     * This method generates a string representation of a date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @see java.text.SimpleDateFormat
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(String aMask, String strDate)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(aMask);
        try {
            return df.parse(strDate);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(TIME_PATTERN, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     *
     * @return the current date
     * @throws ParseException when String doesn't match the expected format
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(DateUtil.getDatePattern());

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     *
     * @see java.text.SimpleDateFormat
     */
    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date based
     * on the System Property 'dateFormat'
     * in the format you specify on input
     *
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return DateUtil.getDateTime("dd/MM/yyyy", aDate);
    }

    /**
     * Convertir une date en String en donnant un pattern
     * @param pattern
     * @param aDate
     * @return
     */
    public static String convertDateToStringByPattern(String pattern, Date aDate) {
        return DateUtil.getDateTime(pattern, aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     *
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(String strDate)
            throws ParseException {
        try {
            return DateUtil.convertStringToDate(DateUtil.getDatePattern(), strDate);
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate + "' to a date, throwing exception");
            throw new ParseException(pe.getMessage(),
                    pe.getErrorOffset());
        }
    }

    /**
     * Cette fonction convertit une date de la forme jj/mm/aaaa à la forme aaaa-mm-jj
     * @param french ( la date avec le format jj/mm/aaaa)
     * @return la date avec le format aaaa-mm-jj
     */
    public static String frenshAmericanDate(String french) {
        String jj = french.substring(0, 2);
        String mm = french.substring(3, 5);
        String aa = french.substring(6, 10);
        String hh = french.substring(11, 13);
        String min = french.substring(14, 16);
        if (french.length() > 16) {
            String ss = french.substring(17, 19);
            return aa + "-" + jj + "-" + mm + " " + hh + ":" + min + ":" + ss;
        } else {
            return aa + "-" + jj + "-" + mm + " " + hh + ":" + min;
        }
    }

    public static String americanToFrenshDate(String american) {
        String aa = american.substring(0, 4);
        String mm = american.substring(5, 7);
        String jj = american.substring(8, 10);
        return jj + "/" + mm + "/" + aa;
    }

    /**
     * Cette fonction permet d'inverser une string date pour l'inserer dans une base Mysql
     * @param source ( date format jj/mm/aaaa )
     * @return date fomat aaaa/mm/jj
     */
    public static String inverserDate(String source) {

        String jj = source.substring(0, 2);
        String mm = source.substring(3, 5);
        String aa = source.substring(6, 10);
        return aa + "/" + mm + "/" + jj;
    }

    public static String StringDateNow() {

        Calendar calendarNow = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(calendarNow.getTime());

    }

    /**
     * retourner la date courante sous forme de string sous forme  dd + "/" + MM + "/" + yyyy + " " + hh + ":" + mnt
     * @return la chaine de la date courante
     */
    public static String StringDateTimeNow() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar calendarNow = new GregorianCalendar();
        return simpleDateFormat.format(calendarNow.getTime());
    }

    /**
     * convertir string en date
     * @param date le string a convertir
     * @return la date correspondante
     */
    public static Date convertString(String date) {
        int jj = Integer.parseInt(date.substring(0, 2));
        int MM = Integer.parseInt(date.substring(3, 5));
        int yyyy = Integer.parseInt(date.substring(6, 10));
        Calendar cal = new GregorianCalendar(yyyy, MM - 1, jj);

        return cal.getTime();

    }

    /**
     * Méthode pour retourner la date actuelle (date now) pour l'historisation
     * @return
     */
    public static Timestamp getDateHisto() {
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateN = formatter.format(date);
        java.sql.Timestamp timeStampDate = Timestamp.valueOf(dateN);
        return timeStampDate;

    }

    public static List<String> listeAnneee(String debut, String fin) {
        int debu = Integer.parseInt(debut.substring(6, 10));
        int finn = Integer.parseInt(fin.substring(6, 10));
        List list = new ArrayList();
        if (debu == finn) {
            list.add(String.valueOf(debu));
        } else {
            for (int i = debu; i <= finn; i++) {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    public static List<String> listeMois(String debu, String fin, String annee) {
        List liste = new ArrayList();
        String[] mois = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};
        int AD, AF, MD, MF, anne;
        anne = Integer.parseInt(annee);
        AD = Integer.parseInt(debu.substring(6, 10));
        MD = Integer.parseInt(debu.substring(3, 5));
        AF = Integer.parseInt(fin.substring(6, 10));
        MF = Integer.parseInt(fin.substring(3, 5));
        if(anne < AD || anne > AF)
            return liste;
        if (AF == AD) {
            for (int i = MD; i <= MF; i++) {
                liste.add(mois[i]);
            }
            return liste;
        } else if (AF > AD) {
            if (anne == AD) {
                for (int i = MD; i <= 12; i++) {
                    liste.add(mois[i]);
                }
                return liste;
            }
            if (anne == AF) {
                for (int i = 1; i <= MF; i++) {
                    liste.add(mois[i]);
                }
                return liste;
            }
            if (AD < anne && anne < AF) {
                for (int i = 1; i <= 12; i++) {
                    liste.add(mois[i]);
                }
                return liste;
            }
        }
        return liste;
    }

    public static String recupererMoisDate(Date d) {
        //DateFormat df = new SimpleDateFormat("MM");
        int mois = d.getMonth();
        switch (mois) {
            case 0:
                return "Janvier";
            case 1:
                return "Février";
            case 2:
                return "Mars";
            case 3:
                return "Avril";
            case 4:
                return "Mai";
            case 5:
                return "Juin";
            case 6:
                return "Juillet";
            case 7:
                return "Aout";
            case 8:
                return "Septembre";
            case 9:
                return "Octobre";
            case 10:
                return "Novembre";
            case 11:
                return "Décembre";
        }
        return "Janvier";
    }

    public static boolean superieur(String mois1,String mois2) {
        String rangMois1 = DateUtil.recupererOrdreMois(mois1);
        String rangMois2 = DateUtil.recupererOrdreMois(mois2);
        return Integer.valueOf(rangMois1).intValue() >= Integer.valueOf(rangMois2).intValue();
    }
    public static String recupererMoisByOrdre(int mois) {
        switch (mois) {
            case 0:
                return "Janvier";
            case 1:
                return "Février";
            case 2:
                return "Mars";
            case 3:
                return "Avril";
            case 4:
                return "Mai";
            case 5:
                return "Juin";
            case 6:
                return "Juillet";
            case 7:
                return "Aout";
            case 8:
                return "Septembre";
            case 9:
                return "Octobre";
            case 10:
                return "Novembre";
            case 11:
                return "Décembre";
        }
        return "Janvier";
    }
    public static String recupererOrdreMois(String mois) {
        DecimalFormat df = new DecimalFormat("00");
        if(mois.equals("Janvier")) {
            return df.format(1);
        }
        if(mois.equals("Février")) {
            return df.format(2);
        }
        if(mois.equals("Mars")) {
            return df.format(3);
        }
        if(mois.equals("Avril")) {
            return df.format(4);
        }
        if(mois.equals("Mai")) {
            return df.format(5);
        }
        if(mois.equals("Juin")) {
            return df.format(6);
        }
        if(mois.equals("Juillet")) {
            return df.format(7);
        }
        if(mois.equals("Aout")) {
            return df.format(8);
        }
        if(mois.equals("Septembre")) {
            return df.format(9);
        }
        if(mois.equals("Octobre")) {
            return df.format(10);
        }
        if(mois.equals("Novembre")) {
            return df.format(11);
        }
        if(mois.equals("Décembre")) {
            return df.format(12);
        }
        return df.format(1);
    }
    public static String recupererMoisDateActuelle() {
        Date actuelle = new Date();
        DateFormat formatter = new SimpleDateFormat("MM");
        int mois = Integer.parseInt(formatter.format(actuelle));
        switch (mois) {
            case 1:
                return "Janvier";
            case 2:
                return "Février";
            case 3:
                return "Mars";
            case 4:
                return "Avril";
            case 5:
                return "Mai";
            case 6:
                return "Juin";
            case 7:
                return "Juillet";
            case 8:
                return "Aout";
            case 9:
                return "Septembre";
            case 10:
                return "Octobre";
            case 11:
                return "Novembre";
            case 12:
                return "Décembre";
        }

        return "";
    }

    public static String recupererAnneeDateActuelle() {
        Date actuelle = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(actuelle);
    }
    
    public static Integer recupererAnneeDateActuelleInt() {
        Date actuelle = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy");
        String annee = formatter.format(actuelle);
        return Integer.valueOf(annee);
    }


    public static Date[] recupereDateDebutDateFinMois(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int minDayOfMounth = c.getActualMinimum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, minDayOfMounth);
        Date d1 = c.getTime();
        int maxDayInMounth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, maxDayInMounth);
        Date d2 = c.getTime();
        Date result[] = {d1, d2};
        return result;
    }

    public static Date recupererDateMoisPrecedent(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }
    
    public static Date[] recupererDateDebutDateFinDerniersTroisMois(Date d) {
        
        Date d3 = recupererDateMoisPrecedent(d);
        Date d2 = recupererDateMoisPrecedent(d3);
        Date d1 = recupererDateMoisPrecedent(d2);
        Date[] dateDebutFinMois1 = recupereDateDebutDateFinMois(d1);
        Date[] dateDebutFinMois3 = recupereDateDebutDateFinMois(d3);
        

        Date result[] = {dateDebutFinMois1[0],dateDebutFinMois3[1]};
        return result;
    }
    public static String getMonth(int mois) {
 switch (mois) {
            case 1:
                return "Janvier";
            case 2:
                return "Février";
            case 3:
                return "Mars";
            case 4:
                return "Avril";
            case 5:
                return "Mai";
            case 6:
                return "Juin";
            case 7:
                return "Juillet";
            case 8:
                return "Aout";
            case 9:
                return "Septembre";
            case 10:
                return "Octobre";
            case 11:
                return "Novembre";
            case 12:
                return "Décembre";
        }

        return "";
    }
}
