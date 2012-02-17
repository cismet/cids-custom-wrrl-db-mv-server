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
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class RohrleitungToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final CidsBean lineBean = (CidsBean)cidsBean.getProperty("linie");
        final CidsBean stationVon = (CidsBean)lineBean.getProperty(LinearReferencingConstants.PROP_STATIONLINIE_FROM);
        final CidsBean stationBis = (CidsBean)lineBean.getProperty(LinearReferencingConstants.PROP_STATIONLINIE_TO);
        final String wertVon =
            new DecimalFormat("#.#").format((Double)stationVon.getProperty(
                    LinearReferencingConstants.PROP_STATION_VALUE));
        final String wertBis =
            new DecimalFormat("#.#").format((Double)stationBis.getProperty(
                    LinearReferencingConstants.PROP_STATION_VALUE));
        final CidsBean route = (CidsBean)stationVon.getProperty(LinearReferencingConstants.PROP_STATION_ROUTE);
        final String gwk = String.valueOf(route.getProperty(LinearReferencingConstants.PROP_ROUTE_GWK));

        return String.valueOf(gwk + "@" + wertVon + "-" + wertBis);
    }
}
