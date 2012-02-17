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
public class SwstnToStringConverter extends CustomToStringConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SwstnToStringConverter.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Object name = cidsBean.getProperty("name_stn");

        if (name != null) {
            return name.toString();
        } else {
            return "unbenannt";
        }
    }
}
