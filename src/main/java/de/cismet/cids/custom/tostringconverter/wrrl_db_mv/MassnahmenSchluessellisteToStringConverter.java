/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MassnahmenSchluessellisteToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        String nr = (String)cidsBean.getProperty("nr");
        String kat = (String)cidsBean.getProperty("kat");
        String beschreibung = (String)cidsBean.getProperty("beschreibung");

        if (nr == null) {
            nr = "";
        }

        if (kat == null) {
            kat = "";
        }

        if (beschreibung == null) {
            beschreibung = "";
        }

        final StringBuilder sb = new StringBuilder(nr);
        sb.append(" | ").append(kat).append(" | ").append(beschreibung);

        return sb.toString();
    }
}
