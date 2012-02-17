/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wrrl_db_mv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class ExDateToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final DateFormat sdf = SimpleDateFormat.getDateInstance();
        final Object date = cidsBean.getProperty("date");
        if (date != null) {
            return String.valueOf(sdf.format(date));
        } else {
            return "";
        }
    }
}
