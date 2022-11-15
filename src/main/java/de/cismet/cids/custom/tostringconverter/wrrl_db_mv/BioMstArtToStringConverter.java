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
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BioMstArtToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

// private static final Logger LOG = Logger.getLogger(BewirtschaftungsendeToStringConverter.class);
// private static Map<String, String> map = new Hashtable<String, String>();

    @Override
    public String createString() {
        String name = (String)cidsBean.getProperty("art");

        if (name == null) {
            name = "unbekannt";
        }

        return name;
    }
}
