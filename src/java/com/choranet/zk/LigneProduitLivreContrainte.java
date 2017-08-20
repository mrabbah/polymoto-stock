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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Listitem;
import com.choranet.gesticom.LigneProduit;
//import org.zkoss.zul.CustomConstraint;
//import org.zkoss.zul.Label;
//import org.zkoss.zul.Popup;

/**
 *
 * @author RABBAH
 */
public class LigneProduitLivreContrainte implements Constraint/*, CustomConstraint */ {

    public void validate(Component comp, Object value) throws WrongValueException {
        if (comp != null && comp.getParent() != null && comp.getParent().getParent() != null) {
            Listitem li = (Listitem) comp.getParent().getParent();
            LigneProduit lp = (LigneProduit) li.getValue();
            Double quantiteMaxPermit = /*lp.getQuantite() -*/ lp.getQuantiteLivree();
            if (value == null || ((Double) value) < 1 || ((Double) value) > quantiteMaxPermit) {
                throw new WrongValueException(comp, "Veuillez indiquez une valeur positive inférieur ou égal à " + quantiteMaxPermit);
//                if (quantiteMaxPermit != 1) {
//                    throw new WrongValueException(comp, "Veuillez indiquez une valeur entre 1 et " + quantiteMaxPermit);
//                } else {
//                    throw new WrongValueException(comp, "La seule valeur permise est 1");
//                }
            }
        }
    }
//    public void showCustomError(Component comp, WrongValueException ex) {
//        Label l = new Label(ex != null ? ex.getMessage():"zzzzzzz");
//        Popup pp = new Popup();
//        pp.setStyle("background : #00FF00");
//        pp.setWidth("200px");
//        pp.appendChild(l);
//        pp.open(comp, "start_before");
//    }
}
