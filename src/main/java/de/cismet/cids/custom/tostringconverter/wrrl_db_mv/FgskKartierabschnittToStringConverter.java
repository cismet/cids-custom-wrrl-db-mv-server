/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import de.cismet.cids.custom.wrrl_db_mv.commons.linearreferencing.LinearReferencingConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FgskKartierabschnittToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final CidsBean statVonBean = (CidsBean)cidsBean.getProperty("linie."
                        + LinearReferencingConstants.PROP_STATIONLINIE_FROM);
        final CidsBean statBisBean = (CidsBean)cidsBean.getProperty("linie."
                        + LinearReferencingConstants.PROP_STATIONLINIE_TO);
        final Long gwk = (Long)cidsBean.getProperty("linie."
                        + LinearReferencingConstants.PROP_STATIONLINIE_FROM + "."
                        + LinearReferencingConstants.PROP_STATION_ROUTE + "."
                        + LinearReferencingConstants.PROP_ROUTE_GWK);
        final String abschnitt = (String)cidsBean.getProperty("gewaesser_abschnitt");
        final Double von = (Double)statVonBean.getProperty(LinearReferencingConstants.PROP_STATION_VALUE);
        final Double bis = (Double)statBisBean.getProperty(LinearReferencingConstants.PROP_STATION_VALUE);

        final String gwkString = (gwk == null) ? "unbekannt" : String.valueOf(gwk);
        final String abschnittString = (abschnitt == null) ? "" : abschnitt;
        final String vonString = (von == null) ? "unbekannt" : String.format("%06d", von.intValue());
        final String bisString = (bis == null) ? "unbekannt" : String.format("%06d", bis.intValue());

        return gwkString + " " + abschnittString + " [" + vonString + " - " + bisString + "]";
    }
}
