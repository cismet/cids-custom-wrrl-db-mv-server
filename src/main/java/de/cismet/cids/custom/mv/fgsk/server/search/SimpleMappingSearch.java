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

import de.cismet.tools.Equals;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class SimpleMappingSearch<K, V> extends AbstractCalcCacheSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SimpleMappingSearch.class);

    private static final String SIMPLE_QUERY = "SELECT {0}, {1} FROM {2}"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String keyColumn;
    private final Class<K> keyClass;
    private final String valueColumn;
    private final Class<V> valueClass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimpleMappingSearch object.
     *
     * @param   tablename    DOCUMENT ME!
     * @param   keyColumn    DOCUMENT ME!
     * @param   keyClass     DOCUMENT ME!
     * @param   valueColumn  DOCUMENT ME!
     * @param   valueClass   DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public SimpleMappingSearch(final String tablename,
            final String keyColumn,
            final Class<K> keyClass,
            final String valueColumn,
            final Class<V> valueClass) {
        super(tablename);

        this.keyColumn = keyColumn;
        this.keyClass = keyClass;
        this.valueColumn = valueColumn;
        this.valueClass = valueClass;

        if (!Equals.nonNull(tablename, keyColumn, keyClass, valueColumn, valueClass)) {
            throw new IllegalArgumentException("none of the arguments must be null"); // NOI18N
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Map internalPerformSearch(final MetaService ms) throws SearchException {
        final String query = MessageFormat.format(SIMPLE_QUERY, keyColumn, valueColumn, tableName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("created query: " + query); // NOI18N
        }

        try {
            final ArrayList<ArrayList> rows = ms.performCustomSearch(query);

            final Map<K, V> result = new HashMap<K, V>();
            for (final ArrayList row : rows) {
                final K key = get(row.get(0), keyClass);
                final V value = get(row.get(1), valueClass);

                result.put(key, value);
            }

            return result;
        } catch (final Exception ex) {
            final String message = "cannot perform simple mapping search search "; // NOI18N
            LOG.error(message, ex);
            throw new SearchException(message, query, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>    DOCUMENT ME!
     * @param   o      DOCUMENT ME!
     * @param   clazz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private <T> T get(final Object o, final Class<T> clazz) {
        if (Number.class.isAssignableFrom(clazz) && (o instanceof Number)) {
            if (Integer.class.isAssignableFrom(clazz)) {
                return (T)(Integer)((Number)o).intValue();
            } else if (Double.class.isAssignableFrom(clazz)) {
                return (T)(Double)((BigDecimal)o).doubleValue();
            } else {
                throw new IllegalStateException("type mismatch: " + o.getClass() + " vs. " + clazz); // NOI18N
            }
        } else if (String.class.isAssignableFrom(clazz) && (o instanceof String)) {
            return (T)o;
        } else {
            throw new IllegalStateException("type mismatch: " + o.getClass() + " vs. " + clazz);     // NOI18N
        }
    }
}
