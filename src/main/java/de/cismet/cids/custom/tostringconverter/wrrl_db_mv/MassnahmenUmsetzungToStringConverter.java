/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MassnahmenUmsetzungToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Object besch = cidsBean.getProperty("mass_beschreibung");
        final String beschreibung = ((besch == null) ? "" : String.valueOf(besch));

        if (cidsBean.getProperty("id").toString().equals("-1") && beschreibung.equals("")) {
            return "unbenannt";
        }

        return cidsBean.getProperty("id").toString() + " " + beschreibung;
    }
}
