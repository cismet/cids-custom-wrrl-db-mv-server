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
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public final class ExcemptionToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return "Ausnahme (" + processCat(cidsBean.getProperty("ex_cat")) + ", "
                    + processTyp(cidsBean.getProperty("ex_typ")) + ")";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String processCat(final Object in) {
        if (in == null) {
            return "keine Kategorie gewählt";
        } else {
            return in.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String processTyp(final Object in) {
        if (in == null) {
            return "kein Typ gewählt";
        } else {
            return in.toString();
        }
    }
}
