/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FgskIdByIntersectionSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(FgskIdByIntersectionSearch.class);

    private static final String QUERY = "select k.id from fgsk_kartierabschnitt k "
                + "join station_linie sl on (linie = sl.id) "
                + "join station von on (von.id = sl.von) "
                + "join station bis on (bis.id = sl.bis) "
                + "join route on (route.id = von.route) "
                + "where (historisch is null or not historisch) and von.route = %1s and "
                + "((least(greatest(%2$s, %3$s), greatest(von.wert, bis.wert)) - greatest(least(%2$s, %3$s), least(von.wert, bis.wert))) > 0.01) "
                + "order by least(von.wert, bis.wert)"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final int routeId;
    private final double from;
    private final double till;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FgskIdByIntersectionSearch object.
     *
     * @param  routeId  DOCUMENT ME!
     * @param  from     DOCUMENT ME!
     * @param  till     DOCUMENT ME!
     */
    public FgskIdByIntersectionSearch(final int routeId, final double from, final double till) {
        this.routeId = routeId;
        this.from = from;
        this.till = till;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, routeId, from, till);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query);
                return lists;
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found"); // NOI18N
        }

        return null;
    }
}
