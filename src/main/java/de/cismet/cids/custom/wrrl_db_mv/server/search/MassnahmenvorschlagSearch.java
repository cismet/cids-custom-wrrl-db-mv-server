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
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;
import de.cismet.cids.custom.wrrl_db_mv.fgsksimulation.FgskSimCalc;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MassnahmenvorschlagSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(MassnahmenvorschlagSearch.class);
    private static final String MO_QUERY = "select distinct %s, %s as id";
    private static final String COSTS_QUERY = "select %1s, id from %2s ";
    private static final String QUERY = "SELECT DISTINCT "
                + "                (SELECT id "
                + "                FROM    cs_class "
                + "                WHERE   name ilike 'sim_massnahmenauswahl_regel' "
                + "                ) "
                + "                , "
                + "                mr.id, "
                + "                mg.name "
                + "FROM            sim_massnahmenauswahl_regel mr "
                + "                JOIN sim_massnahmenauswahl_regel_sim_massnahmen_gruppe smrsmg "
                + "                ON              ( "
                + "                                                mr.kandidaten = smrsmg.sim_massnahmenauswahl_regel_reference "
                + "                                ) "
                + "                JOIN sim_massnahmen_gruppe mg "
                + "                ON              ( "
                + "                                                smrsmg.massnahmengruppe = mg.id "
                + "                                ) "
                + "WHERE           ( "
                + "                                (SELECT COUNT(DISTINCT m1.id) "
                + "                                        FROM    sim_massnahmen_gruppe_sim_massnahmen smgsm1 "
                + "                                                JOIN sim_massnahme m1 "
                + "                                                ON      ( "
                + "                                                                smgsm1.massnahmen = m1.id "
                + "                                                        ) "
                + "                                                JOIN sim_massnahmen_wirkung w "
                + "                                                ON      m1.id = w.massnahme "
                + "                                                JOIN la_lawa_nr l "
                + "                                                ON      gewaessertyp            = l.id "
                + "                                        WHERE   l.code                          = %1$s "
                + "                                        AND     sim_massnahmen_gruppe_reference = mg.id "
                + "                                ) "
                + "                                = "
                + "                                (SELECT COUNT(DISTINCT m2.id) "
                + "                                FROM    sim_massnahmen_gruppe_sim_massnahmen smgsm2 "
                + "                                        JOIN sim_massnahme m2 "
                + "                                        ON      ( "
                + "                                                        smgsm2.massnahmen = m2.id "
                + "                                                ) "
                + "                                WHERE   sim_massnahmen_gruppe_reference = mg.id "
                + "                                ) "
                + "                ) ";

    //~ Instance fields --------------------------------------------------------

    // "select distinct (select id from cs_class where name ilike 'sim_massnahmenauswahl_regel'),"
    // + " mr.id from sim_massnahmenauswahl_regel mr join sim_massnahmenauswahl_regel_sim_massnahmen_gruppe smrsmg"
    // + " on (mr.kandidaten = smrsmg.sim_massnahmenauswahl_regel_reference) "
    // + "join sim_massnahmen_gruppe mg on (smrsmg.massnahmengruppe = mg.id) "
    // + " join sim_massnahmen_gruppe_sim_massnahmen smgsm on "
    // + "(mg.id = smgsm.sim_massnahmen_gruppe_reference)  join sim_massnahme m on (smgsm.massnahmen = m.id) "
    // + "join sim_massnahmen_wirkung w on m.id = w.massnahme join la_lawa_nr l "
    // + "on gewaessertyp = l.id where l.code = %1$s";

    private String json;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  fgsk_mo  fgsk_bean losId geometry DOCUMENT ME!
     * @param  usr      DOCUMENT ME!
     */
    public MassnahmenvorschlagSearch(final MetaObject fgsk_mo, final User usr) {
        this.json = fgsk_mo.getBean().toJSONString(true);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final CidsBean fgskBean;
                try {
                    fgskBean = CidsBean.createNewCidsBeanFromJSON(true, json);
                } catch (Exception e) {
                    LOG.error("Error while creating cids bean from json", e);
                    return null;
                }
                final Object gewTyp = fgskBean.getProperty("gewaessertyp_id.value");
                final String query = String.format(QUERY, String.valueOf(gewTyp));

                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                // Alle Regeln laden die auf Typ passen
                final MetaObject[] rules = ms.getMetaObject(getUser(), query);
                final ArrayList<CidsBean> resultCandidates = new ArrayList<CidsBean>(rules.length);
                final ArrayList<MassnahmenEffizienz> wirkungen = new ArrayList<MassnahmenEffizienz>(rules.length);

                for (final MetaObject rule : rules) {
                    if (FgskSimCalc.getInstance().isRuleFulfilled(fgskBean, rule.getBean())) {
                        final List<CidsBean> candidates = rule.getBean().getBeanCollectionProperty("kandidaten");
                        final String hinweis = (String)rule.getBean().getProperty("hinweis");
                        if (candidates != null) {
                            for (final CidsBean candidate : candidates) {
                                try {
                                    candidate.setProperty("hinweis", hinweis);
                                } catch (Exception e) {
                                    LOG.error("Cannot set property hinweis", e);
                                }
                                if (isGroupAllowed(candidate, gewTyp)) {
                                    resultCandidates.add(candidate);
                                }
                            }
                        }
                    }
                }

                for (final CidsBean massnahmegruppeBean : resultCandidates) {
                    final List<CidsBean> einzelmassnahmen = new ArrayList<CidsBean>(
                            massnahmegruppeBean.getBeanCollectionProperty("massnahmen"));
                    double price = 0.0;
                    int wirkung = 0;

                    for (final CidsBean mn : einzelmassnahmen) {
                        try {
                            price += FgskSimCalc.getInstance().calcCosts(fgskBean, mn, getFlCosts(ms));
                            final List<CidsBean> massnahmeWirkungen = mn.getBeanCollectionProperty("wirkungen");

                            for (final CidsBean wirkungBean : massnahmeWirkungen) {
                                if (wirkungBean.getProperty("gewaessertyp.code").equals(gewTyp)) {
                                    wirkung += FgskSimCalc.getInstance().calcFgskSum(wirkungBean);
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("Cannot calculate the price", e);
                        }
                    }

                    final MassnahmenEffizienz me = new MassnahmenEffizienz(wirkung, price, massnahmegruppeBean);
                    if (!wirkungen.contains(me)) {
                        wirkungen.add(me);
                    }
                }

                Collections.sort(wirkungen, MassnahmenEffizienz.getComparator());

                // rounding up
                final int maxCount = Math.round(((float)wirkungen.size()
                                    * (float)FgskSimCalc.THRESHHOLD
                                    / 100f) + 0.5f);
                int index = 0;
                final ArrayList<Node> result = new ArrayList<Node>(maxCount);

                for (final MassnahmenEffizienz tmpObject : wirkungen) {
                    if (index == maxCount) {
                        break;
                    }
                    result.add(new MetaObjectNode(tmpObject.getMgBean()));
                    ++index;
                }

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
     * Get all objects of type sim_flaechenerwerbspreis, which are required to calculate the costs of massnahmen
     * objects.
     *
     * @param   ms  a reference to the wrrl domain server
     *
     * @return  a list of all sim_flaechenerwerbspreis objects
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private List<MetaObject> getFlCosts(final MetaService ms) throws Exception {
        final MetaClass mc = ms.getClassByTableName(getUser(), "sim_flaechenerwerbspreis");
        final String query = String.format(QUERY, mc.getID(), mc.getTableName());
        if (LOG.isDebugEnabled()) {
            LOG.debug("query: " + query); // NOI18N
        }
        final MetaObject[] lists = ms.getMetaObject(getUser(), query);

        return Arrays.asList(lists);
    }

    /**
     * Prüft, ob alle Mitglieder der Gruppe den gegebenen Gewässsertyp unterstützen.
     *
     * @param   group   DOCUMENT ME!
     * @param   gewTyp  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isGroupAllowed(final CidsBean group, final Object gewTyp) {
        final List<CidsBean> massnList = group.getBeanCollectionProperty("massnahmen");

        for (final CidsBean massn : massnList) {
            boolean allowed = false;
            final List<CidsBean> massnahmeWirkungen = massn.getBeanCollectionProperty("wirkungen");

            for (final CidsBean wirkungBean : massnahmeWirkungen) {
                if (wirkungBean.getProperty("gewaessertyp.code").equals(gewTyp)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                return false;
            }
        }

        return true;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * This class is not comparable, because it cannot be ensured, that (x.compareTo(y)==0) == (x.equals(y)) and this
     * can lead to problems, if a class is comparable. But the static method <code>getComparator()</code> delivers a
     * Comparator class.
     *
     * @version  $Revision$, $Date$
     */
    private static class MassnahmenEffizienz {

        //~ Instance fields ----------------------------------------------------

        private CidsBean mgBean;
        private double price;
        private int wirkung;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MassnahmenEffizienz object.
         *
         * @param  wirkung  DOCUMENT ME!
         * @param  price    DOCUMENT ME!
         * @param  mgBean   DOCUMENT ME!
         */
        public MassnahmenEffizienz(final int wirkung, final double price, final CidsBean mgBean) {
            this.wirkung = wirkung;
            this.price = price;
            this.mgBean = mgBean;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof MassnahmenEffizienz) {
                if (((MassnahmenEffizienz)obj).getMgBean().getMetaObject().getID()
                            == getMgBean().getMetaObject().getID()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = (37 * hash)
                        + ((this.mgBean != null) ? this.mgBean.hashCode() : 0);
            return hash;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  the mgBean
         */
        public CidsBean getMgBean() {
            return mgBean;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  mgBean  the mgBean to set
         */
        public void setMgBean(final CidsBean mgBean) {
            this.mgBean = mgBean;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static Comparator<MassnahmenEffizienz> getComparator() {
            return new Comparator<MassnahmenEffizienz>() {

                    @Override
                    public int compare(final MassnahmenEffizienz o1, final MassnahmenEffizienz o2) {
                        final double eff = o1.price
                                    / (((double)o1.wirkung == 0.0) ? 1.0 : (double)o1.wirkung);
                        final double otherEff = o2.price
                                    / (((double)o2.wirkung == 0.0) ? 1.0 : (double)o2.wirkung);

                        return (int)Math.signum(eff - otherEff);
                    }
                };
        }
    }
}
