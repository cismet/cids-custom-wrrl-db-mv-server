/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.extensionfactories.wrrl_db_mv;

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;

import org.apache.log4j.Logger;

import java.util.ArrayList;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.objectextension.ObjectExtensionFactory;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class BewirtschaftungsendeExtensionFactory extends ObjectExtensionFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ATTRIBUTE_NAME = "name";
    private static final Logger LOG = Logger.getLogger(BewirtschaftungsendeExtensionFactory.class);
    private static final String QUERY = "SELECT wk_fg. wk_k "
                + "FROM wk_fg, wk_fg_teile teile, wk_teil teil, station von, station bis, "
                + "     station_linie linie "
                + "WHERE teile.wk_fg_reference = wk_fg.id AND "
                + "      teil.id = teile.teil AND "
                + "      linie.id = teil.linie AND "
                + "      von.route = %1$s AND "
                + "      von.id = linie.von AND "
                + "      bis.id = linie.bis AND "
                + "      (%2$s >= von.wert AND %2$s < bis.wert) "
                + "ORDER BY wk_fg.id "
                + "LIMIT 1;";

    //~ Methods ----------------------------------------------------------------

    @Override
    public void extend(final CidsBean bean) {
        try {
            final DomainServerImpl server = getDomainServer();

            if (server != null) {
                final String routeId = String.valueOf(bean.getProperty("stat.route.id"));
                final String wert = String.valueOf(bean.getProperty("stat.wert"));

                if (!((routeId == null) || (wert == null) || routeId.equals("null") || wert.equals("null"))) {
                    final String queryString = String.format(QUERY, routeId, wert);
                    final ArrayList<ArrayList> resArray = server.performCustomSearch(queryString);

                    if ((resArray != null) && (resArray.size() == 1)) {
                        final ArrayList resRow = resArray.get(0);

                        if ((resRow != null) && (resRow.size() == 1)) {
                            final Object name = resRow.get(0);

                            if (name instanceof String) {
                                bean.setProperty(ATTRIBUTE_NAME, name);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            LOG.error("Error while determining the name of an object of the type Bewirtschaftungsende", e);
        }
    }
}
