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
public class PersonToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String vorname = (String)cidsBean.getProperty("vorname");
        final String nachname = (String)cidsBean.getProperty("name");

        if ((vorname != null) || (nachname != null)) {
            if (vorname == null) {
                return nachname;
            } else if (nachname == null) {
                return vorname;
            } else {
                return vorname + " " + nachname;
            }
        } else {
            return "unbenannt";
        }
    }
}
