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

        if (stat09 != null) {
            final Double statVal = (Double)stat09.getProperty(LinearReferencingConstants.PROP_STATION_VALUE);
            final String wert = new DecimalFormat("#.#").format(statVal);
            final CidsBean route = (CidsBean)stat09.getProperty(LinearReferencingConstants.PROP_STATION_ROUTE);
            final String gwk = String.valueOf(route.getProperty(LinearReferencingConstants.PROP_ROUTE_GWK));
            return "Querbauwerk " + gwk + "@" + wert;
        } else {
            return "Querbauwerk " + 0 + "@" + 0;
        }
    }
}
