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
public class WkFgMeldeInfosSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(WkFgMeldeInfosSearch.class);

    private static final String QUERY = "SELECT\n"
                + "    st.katalog_nr,\n"
                + "    st.name,\n"
                + "    sn.sn_nr,\n"
                + "    e.ef,\n"
                + "    e.bezug,\n"
                + "    e.zusammenfassung,\n"
                + "    e.bewertung,\n"
                + "    e.bemerkung\n"
                + "   FROM anhoerung_sn_bp3 sn\n"
                + "     LEFT JOIN anhoerung_stammdaten_einwender_bp3 st ON sn.katalog_nr = st.katalog_nr\n"
                + "     LEFT JOIN anhoerung_sn_ef_bp3 e ON sn.sid = e.sid\n"
                + "  WHERE e.hwrm IS NOT TRUE AND e.hwrm_mp IS NOT TRUE AND e.hwrm_ub IS NOT TRUE\n"
                + "  and bezug ilike '%1s'\n"
                + "  ORDER BY sn.sid, st.katalog_nr, e.ef;";

    //~ Instance fields --------------------------------------------------------

    private final String wkk;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  wkk  gupId losId geometry DOCUMENT ME!
     */
    public WkFgMeldeInfosSearch(final String wkk) {
        this.wkk = wkk;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, "%" + wkk + "%");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(
                        query,
                        ConnectionContext.create(AbstractConnectionContext.Category.SEARCH, "WkFgMeldeInfosSearch"));
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
