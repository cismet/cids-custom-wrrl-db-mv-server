/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class MassnahmenartSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(MassnahmenSearch.class);

    private static final String QUERY = "select count(*) from gup_massnahmenart"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String kompartiment;
    private String intervall;
    private String einsatzvariante;
    private String geraet;
    private String ausfuehrungszeitpunkt;
    private String zweiter_ausfuehrungszeitpunkt;
    private String gewerk;
    private String verbleib;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  intervall                      DOCUMENT ME!
     * @param  einsatzvariante                DOCUMENT ME!
     * @param  geraet                         DOCUMENT ME!
     * @param  ausfuehrungszeitpunkt          DOCUMENT ME!
     * @param  zweiter_ausfuehrungszeitpunkt  DOCUMENT ME!
     * @param  gewerk                         DOCUMENT ME!
     * @param  verbleib                       DOCUMENT ME!
     */
    public MassnahmenartSearch(final String intervall,
            final String einsatzvariante,
            final String geraet,
            final String ausfuehrungszeitpunkt,
            final String zweiter_ausfuehrungszeitpunkt,
            final String gewerk,
            final String verbleib) {
        this.intervall = intervall;
        this.einsatzvariante = einsatzvariante;
        this.geraet = geraet;
        this.ausfuehrungszeitpunkt = ausfuehrungszeitpunkt;
        this.zweiter_ausfuehrungszeitpunkt = zweiter_ausfuehrungszeitpunkt;
        this.gewerk = gewerk;
        this.verbleib = verbleib;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                String newQuery = QUERY;
                int conditions = 0;

                if ((intervall != null) && !intervall.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE intervall = " + intervall;
                    } else {
                        newQuery += " AND intervall = " + intervall;
                    }
                    ++conditions;
                }

                if ((einsatzvariante != null) && !einsatzvariante.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE einsatzvariante = " + einsatzvariante;
                    } else {
                        newQuery += " AND einsatzvariante = " + einsatzvariante;
                    }
                    ++conditions;
                }

                if ((geraet != null) && !geraet.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE geraet = " + geraet;
                    } else {
                        newQuery += " AND geraet = " + geraet;
                    }
                    ++conditions;
                }

                if ((ausfuehrungszeitpunkt != null) && !ausfuehrungszeitpunkt.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE ausfuehrungszeitpunkt = " + ausfuehrungszeitpunkt;
                    } else {
                        newQuery += " AND ausfuehrungszeitpunkt = " + ausfuehrungszeitpunkt;
                    }
                    ++conditions;
                }

                if ((zweiter_ausfuehrungszeitpunkt != null) && !zweiter_ausfuehrungszeitpunkt.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE zweiter_ausfuehrungszeitpunkt = " + zweiter_ausfuehrungszeitpunkt;
                    } else {
                        newQuery += " AND zweiter_ausfuehrungszeitpunkt = " + zweiter_ausfuehrungszeitpunkt;
                    }
                    ++conditions;
                }

                if ((gewerk != null) && !gewerk.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE gewerk = " + gewerk;
                    } else {
                        newQuery += " AND gewerk = " + gewerk;
                    }
                    ++conditions;
                }

                if ((verbleib != null) && !verbleib.equals("null")) {
                    if (conditions == 0) {
                        newQuery += " WHERE verbleib = " + verbleib;
                    } else {
                        newQuery += " AND verbleib = " + verbleib;
                    }
                    ++conditions;
                }

//                final String query = String.format(
//                        QUERY,
//                        intervall,
//                        einsatzvariante,
//                        geraet,
//                        ausfuehrungszeitpunkt,
//                        gewerk,
//                        zweiter_ausfuehrungszeitpunkt,
//                        verbleib);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + newQuery); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(newQuery);
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
