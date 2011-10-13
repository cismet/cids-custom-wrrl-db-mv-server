/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.fgsk.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCalcCacheSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractCalcCacheSearch.class);
    public static final String WRRL_DOMAIN = "WRRL_DB_MV"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    protected final String tableName;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractCalcCacheSearch object.
     *
     * @param   tableName  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public AbstractCalcCacheSearch(final String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("tablename must not be null"); // NOI18N
        }

        this.tableName = tableName;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        // FIXME: would use CidsBeanSupport.DOMAIN but there is no dependency
        final MetaService ms = (MetaService)getActiveLoaclServers().get(SimpleRatingSearch.WRRL_DOMAIN); // NOI18N

        if (ms == null) {
            final String message = "cannot find metaservice for wrrl domain: " + SimpleRatingSearch.WRRL_DOMAIN; // NOI18N
            LOG.error(message);
            throw new IllegalStateException(message);
        }
        try {
            final Map result = internalPerformSearch(ms);

            final Collection c = new ArrayList(1);
            c.add(Collections.unmodifiableMap(result));

            return Collections.unmodifiableCollection(c);
        } catch (final SearchException ex) {
            final String message = "cannot perform custom search: " + ex.getQuery(); // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ms  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  SearchException  DOCUMENT ME!
     */
    protected abstract Map internalPerformSearch(final MetaService ms) throws SearchException;

    /**
     * DOCUMENT ME!
     *
     * @param   tableName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static String stripSubject(final String tableName) {
        String result = null;

        // we strip the schema first if it exists
        final int begin = tableName.indexOf('.');
        if (begin != -1) {
            try {
                result = tableName.substring(begin + 1);
            } catch (final IndexOutOfBoundsException e) {
                final String message = "illegal tablename, not expecting '.' at last position: " + tableName; // NOI18N
                LOG.error(message, e);
                throw new IllegalArgumentException(message, e);
            }
        }

        // the we strip the fgsk prefix if it exists
        result = result.replace("fgsk_", ""); // NOI18N

        // finally we strip the auswertung suffix
        final int end = result.indexOf("_auswertung");                                                          // NOI18N
        if (end == -1) {
            final String message = "illegal tablename, expecting '_auswertung' withing the name: " + tableName; // NOI18N
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        return result.substring(0, end);
    }
}
