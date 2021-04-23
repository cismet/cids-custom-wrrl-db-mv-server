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

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class QbwInUseSearch extends AbstractCidsServerSearch implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(QbwInUseSearch.class);

    private static final String QUERY = "select dgh.id, dgh.name from \n"
                + "(select id, dgh_reference, querbauwerk from dgh_qbw\n"
                + "union\n"
                + "select id, dgh_reference, querbauwerk from dgh_qbw_gest) as ats\n"
                + "join dgh on (ats.dgh_reference = dgh.id)\n"
                + "where querbauwerk = %1$s;"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final int qbwId;
    private ConnectionContext cc = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  qbwId  geometry DOCUMENT ME!
     */
    public QbwInUseSearch(final int qbwId) {
        this.qbwId = qbwId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, qbwId);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query, getConnectionContext());
                return lists;
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found"); // NOI18N
        }

        return null;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext cc) {
        this.cc = cc;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return this.cc;
    }
}
