/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.fgsk.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import org.apache.log4j.Logger;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class SimpleRatingSearch<T extends Number> extends AbstractCalcCacheSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY = "SELECT id_{0}, id_gewaessertyp, bewertung FROM {1}"; // NOI18N

    private static final transient Logger LOG = Logger.getLogger(SimpleRatingSearch.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimpleRatingSearch object.
     *
     * @param  tableName  DOCUMENT ME!
     */
    public SimpleRatingSearch(final String tableName) {
        super(tableName);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Map internalPerformSearch(final MetaService ms) throws SearchException {
        final String columnSuffix = stripSubject(tableName);
        final String query = MessageFormat.format(QUERY, columnSuffix, tableName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("created query: " + query); // NOI18N
        }

        final ArrayList<ArrayList> rows;
        try {
            rows = ms.performCustomSearch(query);

            final Map<String, Number> result = new HashMap<String, Number>();
            for (final ArrayList row : rows) {
                final Number c1 = (Number)row.get(0);
                final Number c2 = (Number)row.get(1);
                final Number c3 = (Number)row.get(2);

                final String key = c1.intValue() + "-" + c2.intValue(); // NOI18N

                result.put(key, c3);
            }

            return convert(result);
        } catch (final Exception ex) {
            final String message = "cannot perform calc cache search"; // NOI18N
            LOG.error(message, ex);
            throw new SearchException(message, query, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   result  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Map<String, T> convert(final Map<String, Number> result);

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class DoubleRatingSearch extends SimpleRatingSearch<Double> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DoubleRatingSearch object.
         *
         * @param  tableName  DOCUMENT ME!
         */
        public DoubleRatingSearch(final String tableName) {
            super(tableName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Map<String, Double> convert(final Map<String, Number> result) {
            final Map<String, Double> ret = new HashMap<String, Double>(result.size());
            for (final Map.Entry<String, Number> entry : result.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().doubleValue());
            }

            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class IntegerRatingSearch extends SimpleRatingSearch<Integer> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new IntegerRatingSearch object.
         *
         * @param  tableName  DOCUMENT ME!
         */
        public IntegerRatingSearch(final String tableName) {
            super(tableName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Map<String, Integer> convert(final Map<String, Number> result) {
            final Map<String, Integer> ret = new HashMap<String, Integer>(result.size());
            for (final Map.Entry<String, Number> entry : result.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().intValue());
            }

            return ret;
        }
    }
}
