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
public class HydrologRouteToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String routenname = String.valueOf(cidsBean.getProperty("linie.von.route.routenname"));
        final String start = String.valueOf(cidsBean.getProperty("linie.von.wert"));
        final String end = String.valueOf(cidsBean.getProperty("linie.bis.wert"));

        if (!isStringNull(routenname) && !isStringNull(start) && !isStringNull(end)) {
            return routenname + " [" + start + "-" + end + "]";
        } else {
            return "unbekannt";
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
