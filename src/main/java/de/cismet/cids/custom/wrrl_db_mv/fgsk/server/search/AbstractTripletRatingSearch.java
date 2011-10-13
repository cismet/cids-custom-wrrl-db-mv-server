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
public abstract class AbstractTripletRatingSearch extends AbstractCalcCacheSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AbstractTripletRatingSearch.class);

    private static final String QUERY = "SELECT id_gewaessertyp, id_{0}, {1}, bewertung FROM {2}"; // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractTripletRatingSearch object.
     *
     * @param  tableName  DOCUMENT ME!
     */
    public AbstractTripletRatingSearch(final String tableName) {
        super(tableName);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Map internalPerformSearch(final MetaService ms) throws SearchException {
        try {
            final String subject = stripSubject(tableName);
            final String predicate = getPredicate();

            final String query = MessageFormat.format(QUERY, subject, predicate, tableName);

            if (LOG.isDebugEnabled()) {
                LOG.debug("created query: " + query); // NOI18N
            }

            final ArrayList<ArrayList> rows = ms.performCustomSearch(query);

            final Map<String, Number> result = new HashMap<String, Number>();
            for (final ArrayList row : rows) {
                final Number c1 = (Number)row.get(0);
                final Number c2 = (Number)row.get(1);
                final Number c3 = (Number)row.get(2);
                final Number c4 = (Number)row.get(3);

                final String key = c1.intValue() + "-" + c2.intValue() + "-" + c3.intValue(); // NOI18N

                result.put(key, c4);
            }

            return result;
        } catch (final Exception ex) {
            final String message = "cannot perform calc cache search"; // NOI18N
            LOG.error(message, ex);
            throw new SearchException(message, QUERY, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getPredicate();

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class BedFitmentRatingSearch extends AbstractTripletRatingSearch {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BedFitmentRatingSearch object.
         */
        public BedFitmentRatingSearch() {
            super("public.fgsk_sohlenverbau_auswertung"); // NOI18N
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected String getPredicate() {
            return "id_z_sohlenverbau"; // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class BankVegetationRatingSearch extends AbstractTripletRatingSearch {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BankVegetationRatingSearch object.
         */
        public BankVegetationRatingSearch() {
            super("public.fgsk_ufervegetation_auswertung"); // NOI18N
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected String getPredicate() {
            return "ufervegetation_typical"; // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class BankFitmentRatingSearch extends AbstractTripletRatingSearch {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BankFitmentRatingSearch object.
         */
        public BankFitmentRatingSearch() {
            super("public.fgsk_uferverbau_auswertung"); // NOI18N
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected String getPredicate() {
            return "id_z_uferverbau"; // NOI18N
        }
    }
}
