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
public class ProjekteIndikatorenToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final CidsBean indikator = (CidsBean)cidsBean.getProperty("indikator_schluessel");

        if (indikator != null) {
            return indikator.getProperty("indikator_nr").toString() + " - "
                        + String.valueOf(indikator.getProperty("indikator"));
        } else {
            return "Kein Indikatorschlüssel zugewiesen";
        }
    }
}
