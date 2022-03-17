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
public class WkFromEzgSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(WkFromEzgSearch.class);

    private static final String QUERY = "select k.gid, k.wk_k, ezg.wk_k as ezg_wk,\n"
                + "case when k.wk_k=ezg.wk_k::text then true else false end as check\n"
                + "FROM fiswrv_light.fiswrv_light_ka_anlagen k\n"
                + "join wk_ezg_2018 ezg on st_intersects(st_setsrid(ezg.geom,5650),\n"
                + "st_setsrid(k.the_geom,5650))\n"
                + "where gid = %s"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final int gid;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FgskIdByIntersectionSearch object.
     *
     * @param  gid  routeId DOCUMENT ME!
     */
    public WkFromEzgSearch(final int gid) {
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
