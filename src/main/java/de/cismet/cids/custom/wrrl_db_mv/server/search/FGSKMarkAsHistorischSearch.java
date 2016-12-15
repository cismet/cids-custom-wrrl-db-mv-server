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
import java.util.List;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FGSKMarkAsHistorischSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(FGSKMarkAsHistorischSearch.class);

    private static final String QUERY = "update fgsk_kartierabschnitt set historisch = true "
                + "where id = any(Array[%1$s]);";

    //~ Instance fields --------------------------------------------------------

    private final Integer[] fgskIds;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FGSKMarkAsHistorischSearch object.
     *
     * @param  fgskIds  DOCUMENT ME!
     */
    public FGSKMarkAsHistorischSearch(final Integer[] fgskIds) {
        this.fgskIds = fgskIds;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, asStringList(fgskIds));

                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final int consideredRows = ms.update(getUser(), query);

                final List<Integer> result = new ArrayList<Integer>();
                result.add(consideredRows);

                return result;
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
     * @param   ids  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String asStringList(final Integer[] ids) {
        StringBuilder stringList = null;

        for (final int i : ids) {
            if (stringList == null) {
                stringList = new StringBuilder(i);
            } else {
                stringList.append(",").append(i);
            }
        }

        return ((stringList != null) ? stringList.toString() : "");
    }
}
