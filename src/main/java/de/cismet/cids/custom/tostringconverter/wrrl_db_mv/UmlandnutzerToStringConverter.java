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
public class UmlandnutzerToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String vorname = (String)cidsBean.getProperty("nutzer.vorname");
        final String nachname = (String)cidsBean.getProperty("nutzer.name");

        if ((vorname != null) || (nachname != null)) {
            if (vorname == null) {
                return "Person: " + nachname;
            } else if (nachname == null) {
                return "Person: " + vorname;
            } else {
                return "Person: " + vorname + " " + nachname;
            }
        } else {
            return "Person: unbenannt";
        }
    }
}
