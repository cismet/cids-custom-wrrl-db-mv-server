/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * Search for all objects of the given type.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class AllObjectsSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(AllObjectsSearch.class);

    private static final String QUERY = "select %1s, id from %2s ";

    //~ Instance fields --------------------------------------------------------

    private final Integer classId;
    private final String tablename;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  classId    geometry DOCUMENT ME!
     * @param  tablename  DOCUMENT ME!
     */
    public AllObjectsSearch(final Integer classId, final String tablename) {
        this.classId = classId;
        this.tablename = tablename;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final MetaClass MC_SIM_FLAECEHNERWERBSPREIS = ms.getClassByTableName(
                        getUser(),
                        "sim_flaechenerwerbspreis");
                final String query = String.format(
                        QUERY,
                        MC_SIM_FLAECEHNERWERBSPREIS.getID(),
                        MC_SIM_FLAECEHNERWERBSPREIS.getTableName());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + QUERY); // NOI18N
                }
                final MetaObject[] lists = ms.getMetaObject(getUser(), query);
                return Arrays.asList(lists);
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found"); // NOI18N
        }

        return null;
    }
}
