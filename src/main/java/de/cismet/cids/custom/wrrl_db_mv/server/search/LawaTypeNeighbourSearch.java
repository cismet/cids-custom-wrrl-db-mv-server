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
 * Searchs for the predecessor and sucessor of a lawa type object. The function getLAWAType(integer, boolean) must exist
 * in the database.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LawaTypeNeighbourSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(LawaTypeNeighbourSearch.class);

    private static final String QUERY = "select getLAWAType(%1$s, %2$s);"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String lawaTypeId;
    private final String isPredecessor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LawaTypeNeighbourSearch object.
     *
     * @param  lawaTypeId     DOCUMENT ME!
     * @param  isPredecessor  DOCUMENT ME!
     */
    public LawaTypeNeighbourSearch(final String lawaTypeId,
            final boolean isPredecessor) {
        this.lawaTypeId = lawaTypeId;
        this.isPredecessor = (isPredecessor ? "true" : "false");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, Long.parseLong(lawaTypeId), isPredecessor);
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
