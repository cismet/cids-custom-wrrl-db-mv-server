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
public class FgskIdSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(FgskIdSearch.class);

    private static final String QUERY = "select id from fgsk_kartierabschnitt"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String condition;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  condition  geometry DOCUMENT ME!
     */
    public FgskIdSearch(final String condition) {
        this.condition = condition;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                String query = QUERY;

                if (condition != null) {
                    query += " WHERE wkk ilike '" + condition + "'";
                }
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
