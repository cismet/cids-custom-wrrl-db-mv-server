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
public class SchadstoffeSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(SchadstoffeSearch.class);

    private static final String QUERY = "select kurzname, jahr, schadstoff, datum, messwert\n"
                + "from fiswrv_light.fiswrv_light_abwag_mess_behoerdlich m_bh\n"
                + "left join fiswrv_light.fiswrv_light_ka_anlagen ka on ka.nr_einleit = m_bh.nreinlst\n"
                + "where gid = %s\n"
                + "--group by kurzname,jahr\n"
                + "order by kurzname, schadstoff, datum::date"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final int gid;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FgskIdByIntersectionSearch object.
     *
     * @param  gid  routeId DOCUMENT ME!
     */
    public SchadstoffeSearch(final int gid) {
        this.gid = gid;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, gid);

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
