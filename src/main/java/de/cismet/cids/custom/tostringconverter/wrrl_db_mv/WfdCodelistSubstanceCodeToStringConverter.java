/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WfdCodelistSubstanceCodeToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        if (cidsBean.getProperty("value") == null) {
            return cidsBean.getProperty("cas_nr") + " - " + cidsBean.getProperty("name");
        } else {
            return cidsBean.getProperty("value") + " - " + cidsBean.getProperty("name");
        }
    }
}
