/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SimMassnahmenGruppeToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final List<CidsBean> beans = cidsBean.getBeanCollectionProperty("massnahmen");
        String name = (String)cidsBean.getProperty("name");
        boolean first = true;

        if ((beans != null) && (beans.size() > 0)) {
            for (final CidsBean tmp : beans) {
                if (first) {
                    name += " (" + (String)tmp.getProperty("name");
                    first = false;
                } else {
                    name += ", " + (String)tmp.getProperty("name");
                }
            }
            name += ")";

            return name;
        } else {
            return (String)cidsBean.getProperty("name");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   str  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isStringNull(final String str) {
        return (str == null) || str.equals("null");
    }
}
