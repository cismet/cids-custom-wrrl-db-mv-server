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
 * Searchs for the LAWA types which are contained within a WK-FG.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WkFgLawaKartierabschnittSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(WkFgLawaKartierabschnittSearch.class);

    private static final String QUERY =
        "select code, description, wk_fg_length, intersection_length, lawagwk, vonWert from select_lawa_types_for_wkfg(%1$s);";

    //~ Instance fields --------------------------------------------------------

    private int wkFgId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LawaTypeNeighbourSearch object.
     *
     * @param  wkFgId  lawaTypeId DOCUMENT ME!
     */
    public WkFgLawaKartierabschnittSearch(final int wkFgId) {
        this.wkFgId = wkFgId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, wkFgId);
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

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println(String.format(QUERY, 476));
    }
}
