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
public class BioMstFibsToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        String mst = (String)cidsBean.getProperty("bio_mst.messstelle");
        final Number gwk = (Number)cidsBean.getProperty("linie.von.route.gwk");
        final Double von = (Double)cidsBean.getProperty("linie.von.wert");
        final Double bis = (Double)cidsBean.getProperty("linie.bis.wert");

        if (mst == null) {
            mst = "unbekannt";
        }

        String stat;

        if (gwk == null) {
            stat = "<nicht stationiert>";
        } else {
            stat = gwk.longValue() + "@" + ((von == null) ? "" : von) + "-" + ((bis == null) ? "" : bis);
        }

        return mst + "/" + stat;
    }
}
