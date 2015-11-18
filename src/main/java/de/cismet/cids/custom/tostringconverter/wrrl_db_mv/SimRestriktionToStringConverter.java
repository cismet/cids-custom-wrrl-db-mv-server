/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SimRestriktionToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String routenname = String.valueOf(cidsBean.getProperty("ausdehnung.von.route.routenname"));
        final String start = String.valueOf(cidsBean.getProperty("ausdehnung.von.wert"));
        final String end = String.valueOf(cidsBean.getProperty("ausdehnung.bis.wert"));

        if (!isStringNull(routenname) && !isStringNull(start) && !isStringNull(end)) {
            return "Restriktionen: " + routenname + " [" + start + "-" + end + "]";
        } else {
            return "Restriktionen: unbekannt";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   str  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isStringNull(final String str) {
        return (str == null) || str.equals("null");
    }
}
