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
public class MaxWBNumberSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(MaxWBNumberSearch.class);

    private static final String QUERY =
        "SELECT max(massn_wk_lfdnr) FROM massnahmen, %1$s WHERE massnahmen.%2$s = %1$s.id AND %1$s.id = %3$s";

    //~ Instance fields --------------------------------------------------------

    private String referencedTable;
    private String massReferenceField;
    private String referencedObjectId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MaxWBNumberSearch object.
     *
     * @param  referencedTable     DOCUMENT ME!
     * @param  referencedObjectId  DOCUMENT ME!
     * @param  massReferenceField  DOCUMENT ME!
     */
    public MaxWBNumberSearch(final String referencedTable,
            final String referencedObjectId,
            final String massReferenceField) {
        this.referencedTable = referencedTable;
        this.referencedObjectId = referencedObjectId;
        this.massReferenceField = massReferenceField;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, referencedTable, massReferenceField, referencedObjectId);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query);
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query);
                return lists;
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found");
        }

        return null;
    }
}
