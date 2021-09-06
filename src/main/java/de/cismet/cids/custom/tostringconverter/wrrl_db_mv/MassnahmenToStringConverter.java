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
public class MassnahmenToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Object massn_id = cidsBean.getProperty("massn_id");
        final CidsBean realBean = (CidsBean)cidsBean.getProperty("realisierung");
        final String real = (realBean != null) ? ("_" + String.valueOf(realBean.getProperty("name"))) : "";

        return ((massn_id == null) ? "keine id zugewiesen" : (massn_id.toString() + real));
    }
}
