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
public class GupPlanungsabschnittToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String name = ((cidsBean == null) ? "unbenannt" : String.valueOf(cidsBean.getProperty("name")));

        return name;
    }
}
