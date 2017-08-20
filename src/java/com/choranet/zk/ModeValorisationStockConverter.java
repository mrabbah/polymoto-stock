/*
================= IMPORTANT - READ CAREFULLY BEFORE USING =====================
THE SOFTWARE PRODUCT. The SOFTWARE PRODUCT is owned by CHORA INFORMATIQUE and
is copyrighted and licensed, not sold. The SOFTWARE PRODUCT is protected by
copyright law and international copyright treaties, as well as other
intellectual property laws and treaties. By installing, copying or otherwise
using this SOFTWARE PRODUCT, you agree to be bound by the terms of CHORA
INFORMATIQUE's General Terms and Conditions in relation to the type of license
you have acquired. (See your Contract Administrator for complete details of
your license agreement with CHORA INFORMATIQUE).
If you do not agree to the terms of CHORA INFORMATIQUE's License Agreement,
do not install or use the SOFTWARE PRODUCT. The term "SOFTWARE PRODUCT" means
the original program and all whole or partial copies of it. A Program consists
of machine-readable instructions, its associated media, printed materials, and
"online" or electronic documentation and related licensed materials
("SOFTWARE PRODUCT").
COPYRIGHT. All title and copyrights in and to this SOFTWARE PRODUCT (including
but not limited to any images, photographs, video, audio, text and "applets"
incorporated into the SOFTWARE PRODUCT), the accompanying printed materials,
and any copies of the SOFTWARE PRODUCT are owned by CHORA INFORMATIQUE or its
suppliers. The SOFTWARE PRODUCT is protected by copyright law and international
treaty provisions. Therefore, you must treat the SOFTWARE PRODUCT like any
other copyrighted material except that you may install the SOFTWARE PRODUCT
on a single computer provided you keep the original solely for backup or
archival purposes. Should you have any questions, please contact your local
CHORA INFORMATIQUE Office or Distributor.
 */
package com.choranet.zk;

import org.zkoss.zkplus.databind.TypeConverter;

/**
 *
 * @author RABBAH
 */
public class ModeValorisationStockConverter implements TypeConverter {
    
    public Object coerceToBean(java.lang.Object val,
            org.zkoss.zk.ui.Component comp) {
        return null;
    }
    
    public Object coerceToUi(java.lang.Object val,
            org.zkoss.zk.ui.Component comp) {
        String string = (String) val;
        if(string.equals("PRIX_ACHAT")) {
            string = "Prix d'achat";
        } else if(string.equals("PRIX_VENTE")) {
            string = "Prix de vente";
        } else if(string.equals("PRIX_MOYEN_PENDERE")) {
            string = "Prix moyen pendéré";
        } 
        return string;
    }
}
