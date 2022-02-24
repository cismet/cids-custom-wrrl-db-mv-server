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

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * Search for the Meldeinformationen.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LastMassnEnd extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(LastMassnEnd.class);

    private static final String QUERY = "SELECT max(projekte.m_ende)\n"
                + "           FROM massnahmen_umsetzung mu\n"
                + "             LEFT JOIN projekte_massnahmen_umsetzung pmu ON pmu.massnahmen_umsetzung = mu.id\n"
                + "             LEFT JOIN projekte ON projekte.id = pmu.projekte_reference\n"
                + "          WHERE mu.massnahme = %1s\n"
                + "          GROUP BY mu.massnahme";

    //~ Instance fields --------------------------------------------------------

    private String massnId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  massnId  wkk gupId losId geometry DOCUMENT ME!
     */
    public LastMassnEnd(final String massnId) {
        this.massnId = massnId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, massnId);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(
                        query,
                        ConnectionContext.create(AbstractConnectionContext.Category.SEARCH, "LastMassnEnd"));
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
