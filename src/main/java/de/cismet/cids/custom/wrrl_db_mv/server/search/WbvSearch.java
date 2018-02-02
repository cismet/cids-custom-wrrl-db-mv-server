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
 * Searchs for the wbv the given geometry is contained in.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WbvSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(WbvSearch.class);

    private static final String QUERY =
        "select wbv.name from ogc.wbv wbv, (select line_substring(geo_field, %1$s / st_length(geo_field), %2$s / st_length(geo_field)) as geo from route, geom where route.gwk = %3$s AND route.geom = geom.id) as geo where the_geom && geo.geo AND st_intersects(the_geom, geo.geo);"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String von;
    private String bis;
    private String gwk;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  von  geometry DOCUMENT ME!
     * @param  bis  route the route that contains the given geometry
     * @param  gwk  DOCUMENT ME!
     */
    public WbvSearch(final String von, final String bis, final String gwk) {
        this.von = von;
        this.bis = bis;
        this.gwk = gwk;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, von, bis, gwk);
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
