/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import java.text.DecimalFormat;

import de.cismet.cids.custom.wrrl_db_mv.commons.linearreferencing.LinearReferencingConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class QuerbauwerkeToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final CidsBean stat09 = (CidsBean)cidsBean.getProperty("stat09");
        final CidsBean hb = (CidsBean)cidsBean.getProperty("hb");
        final CidsBean bauwerk = (CidsBean)cidsBean.getProperty("bauwerk");
        final String bauwerkString = ((bauwerk == null) ? "nicht gesetzt" : bauwerk.toString());

        if (stat09 != null) {
            final Double statVal = (Double)stat09.getProperty(LinearReferencingConstants.PROP_STATION_VALUE);
            final String wert = new DecimalFormat("#.#").format(statVal);
            final CidsBean route = (CidsBean)stat09.getProperty(LinearReferencingConstants.PROP_STATION_ROUTE);
            String gwk = String.valueOf(route.getProperty(LinearReferencingConstants.PROP_ROUTE_GWK));
            gwk = rtrim(gwk, "0");

            return "Querbauwerk " + gwk + " " + bauwerkString + "@" + wert + ((hb != null) ? hb : "");
        } else {
            return "Querbauwerk " + 0 + " " + bauwerkString + "@" + 0 + ((hb != null) ? hb : "");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s            DOCUMENT ME!
     * @param   replacement  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String rtrim(String s, final String replacement) {
        while ((s.length() > 0) && s.endsWith(replacement)) {
            s = s.substring(0, s.length() - 1);
        }

        return s;
    }
}
