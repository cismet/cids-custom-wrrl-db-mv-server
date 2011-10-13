/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.mv.fgsk.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.mv.fgsk.server.search.ComplexRatingSearch.Range;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractSubstrateRatingSearch extends AbstractCalcCacheSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractSubstrateRatingSearch.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ComplexRatingSearch object.
     *
     * @param  tableName  DOCUMENT ME!
     */
    public AbstractSubstrateRatingSearch(final String tableName) {
        super(tableName);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Map internalPerformSearch(final MetaService ms) throws SearchException {
        final Map<String, Map<Range, Integer>> result = new HashMap<String, Map<Range, Integer>>();

        final Map<String, Range> rangeCache = new HashMap<String, Range>();

        String query = null;
        try {
            final String subject = getSubject();
            query = MessageFormat.format(getQuery(), subject, tableName);

            if (LOG.isDebugEnabled()) {
                LOG.debug("created query: " + query); // NOI18N
            }

            final ArrayList<ArrayList> rows = ms.performCustomSearch(query);
            for (final ArrayList row : rows) {
                final int wbType = ((BigDecimal)row.get(0)).intValueExact();
                final double rFrom = ((BigDecimal)row.get(1)).doubleValue();
                final double rTo = ((BigDecimal)row.get(2)).doubleValue();
                final int rating = (Integer)row.get(3);
                final String subType = (String)row.get(4);

                final String key = wbType + "-" + subType; // NOI18N
                final Map<Range, Integer> rMap;
                if (result.containsKey(key)) {
                    rMap = result.get(key);
                } else {
                    rMap = new HashMap<Range, Integer>();
                    result.put(key, rMap);
                }

                final Range range;
                final String rangeKey = rFrom + "-" + rTo; // NOI18N

                if (rangeCache.containsKey(rangeKey)) {
                    range = rangeCache.get(rangeKey);
                } else {
                    range = new Range(rFrom, rTo);
                    rangeCache.put(rangeKey, range);
                }

                rMap.put(range, rating);
            }
        } catch (final Exception ex) {
            final String message = "cannot perform complex rating search"; // NOI18N
            LOG.error(message, ex);
            throw new SearchException(message, query, ex);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getQuery();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getSubject();

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class DefaultSubstrateRatingSearch extends AbstractSubstrateRatingSearch {

        //~ Static fields/initializers -----------------------------------------

        private static final String QUERY =
            "SELECT id_gewaessertyp, anz_{0}_von, anz_{0}_bis, bewertung, id_gewaessersubtyp FROM {1}"; // NOI18N

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DefaultSubstrateRatingSearch object.
         *
         * @param  tableName  DOCUMENT ME!
         */
        public DefaultSubstrateRatingSearch(final String tableName) {
            super(tableName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected String getQuery() {
            return QUERY;
        }

        @Override
        protected String getSubject() {
            return stripSubject(tableName);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class PercentSubstrateRatingSearch extends AbstractSubstrateRatingSearch {

        //~ Static fields/initializers -----------------------------------------

        private static final String QUERY =
            "SELECT id_gewaessertyp, pro_{0}_von, pro_{0}_bis, bewertung, id_gewaessersubtyp FROM {1}"; // NOI18N

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PercentSubstrateRatingSearch object.
         *
         * @param  tableName  DOCUMENT ME!
         */
        public PercentSubstrateRatingSearch(final String tableName) {
            super(tableName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected String getQuery() {
            return QUERY;
        }

        @Override
        protected String getSubject() {
            return stripSubject(tableName).replace("prozent_", ""); // NOI18N
        }
    }
}
