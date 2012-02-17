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
public class AggregierterMassnahmenTypToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String pick = (String)cidsBean.getProperty("massnahme.value");
        final String bez = (String)cidsBean.getProperty("bezeichnung");

        if (pick == null) {
            return "unb";
        } else {
            if (bez != null) {
                return pick + " - " + bez;
            } else {
                return pick + " - " + bez;
            }
        }
    }
}
