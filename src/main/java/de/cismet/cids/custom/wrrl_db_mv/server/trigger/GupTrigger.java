/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.trigger;

import Sirius.server.localserver.DBServer;
import Sirius.server.newuser.User;
import Sirius.server.search.Query;
import Sirius.server.search.QueryIdentifier;

import org.openide.util.lookup.ServiceProvider;

import java.sql.Statement;

import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.trigger.AbstractDBAwareCidsTrigger;
import de.cismet.cids.trigger.CidsTrigger;
import de.cismet.cids.trigger.CidsTriggerKey;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsTrigger.class)
public class GupTrigger extends AbstractDBAwareCidsTrigger {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            GupTrigger.class);
    private static final String GUP_LOS_CLASS_NAME = "de.cismet.cids.dynamics.Gup_los";
    private static final String GUP_GUP_CLASS_NAME = "de.cismet.cids.dynamics.Gup_gup";
    private static final String GUP_PLANUNGSABSCHNITT_CLASS_NAME = "de.cismet.cids.dynamics.Gup_planungsabschnitt";
    private static final String GUP_UNTERHALTUNGSMASSNAHME_CLASS_NAME =
        "de.cismet.cids.dynamics.Gup_unterhaltungsmassnahme";
    private static final String GUP_LOS_TABLE_NAME = "gup_los";
    private static final String LOS_QUERY =
        "delete from gup_los_gup_unterhaltungsmassnahme where gup_unterhaltungsmassnahme = %1$s and gup_los_reference <> %2$s";
    private static final String LOS_QUERY_INSERT =
        "delete from gup_los_gup_unterhaltungsmassnahme where gup_unterhaltungsmassnahme = %1$s and gup_los_reference <> (select max(gup_los_reference) from gup_los_gup_unterhaltungsmassnahme where gup_unterhaltungsmassnahme = %1$s)";
    private static final String DELETE_UNASSIGNED_MASSN_QUERY =
        "delete from gup_unterhaltungsmassnahme where id not in (select distinct gup_unterhaltungsmassnahme from gup_planungsabschnitt_gup_unterhaltungsmassnahme)";
    private static final String CLEAR_LOS_ARRAY_QUERY =
        "delete from gup_los_gup_unterhaltungsmassnahme where gup_unterhaltungsmassnahme not in (select distinct id from gup_unterhaltungsmassnahme)";
    private static final String CLEAR_PLANUNGSABSCHNITT_ARRAY_QUERY =
        "delete from gup_planungsabschnitt_gup_unterhaltungsmassnahme where gup_unterhaltungsmassnahme not in (select distinct id from gup_unterhaltungsmassnahme)";
    private static final String FREEZE_PLANUNGSABSCHNITT_QUERY =
        "update gup_planungsabschnitt set einfrieren = true where gup = %1$s";

    //~ Methods ----------------------------------------------------------------

    @Override
    public void afterDelete(final CidsBean cidsBean, final User user) {
    }

    @Override
    public void afterInsert(final CidsBean cidsBean, final User user) {
    }

    @Override
    public void afterUpdate(final CidsBean cidsBean, final User user) {
    }

    @Override
    public void beforeDelete(final CidsBean cidsBean, final User user) {
    }

    @Override
    public void beforeInsert(final CidsBean cidsBean, final User user) {
    }

    @Override
    public void beforeUpdate(final CidsBean cidsBean, final User user) {
    }

    @Override
    public CidsTriggerKey getTriggerKey() {
        return new CidsTriggerKey(CidsTriggerKey.ALL, CidsTriggerKey.ALL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public int compareTo(final CidsTrigger o) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isGupGup(final CidsBean cidsBean) {
        return (cidsBean.getClass().getName().equals(GUP_GUP_CLASS_NAME));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isGupLos(final CidsBean cidsBean) {
        return (cidsBean.getClass().getName().equals(GUP_LOS_CLASS_NAME));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isGupPlanungsabschnitt(final CidsBean cidsBean) {
        return (cidsBean.getClass().getName().equals(GUP_PLANUNGSABSCHNITT_CLASS_NAME));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isGupUnterhaltungsmassnahme(final CidsBean cidsBean) {
        return (cidsBean.getClass().getName().equals(GUP_UNTERHALTUNGSMASSNAHME_CLASS_NAME));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  user      DOCUMENT ME!
     */
    private void unassignMassnFromOtherLos(final CidsBean cidsBean, final User user) {
        // ensures that an object of the type gup_unterhaltungsmassnahme is only assigned to one gup_los
        try {
            final List<CidsBean> beans = cidsBean.getBeanCollectionProperty("massnahmen");
            final Integer losId = (Integer)cidsBean.getProperty("id");
            final DBServer server = getDbServer();
            final Statement st = server.getActiveDBConnection().getConnection().createStatement();

            if (beans != null) {
                for (final CidsBean bean : beans) {
                    final Integer massnId = (Integer)bean.getProperty("id");

                    if (losId == -1) {
                        st.executeUpdate(String.format(LOS_QUERY_INSERT, massnId.toString()));
                    } else {
                        st.executeUpdate(String.format(LOS_QUERY, massnId.toString(), losId.toString()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while executing los trigger.", e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteUnassignedMassnObjects() {
        // ensures that there are no gup_unterhaltungsmassnahme objects without reference to a planungsabschnitt
        try {
            final DBServer server = getDbServer();
            final Statement st = server.getActiveDBConnection().getConnection().createStatement();

            st.executeUpdate(DELETE_UNASSIGNED_MASSN_QUERY);
            st.executeUpdate(CLEAR_LOS_ARRAY_QUERY);
        } catch (Exception e) {
            log.error("Error while executing los trigger.", e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteUnassignedMassnObjectReferences() {
        // ensures that there are no references to the deleted gup_unterhaltungsmassnahme object
        try {
            final DBServer server = getDbServer();
            final Statement st = server.getActiveDBConnection().getConnection().createStatement();

            st.executeUpdate(CLEAR_PLANUNGSABSCHNITT_ARRAY_QUERY);
            st.executeUpdate(CLEAR_LOS_ARRAY_QUERY);
        } catch (Exception e) {
            log.error("Error while executing los trigger.", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gupId  DOCUMENT ME!
     */
    private void freezePlanungsabschnitte(final String gupId) {
        try {
            final DBServer server = getDbServer();
            final Statement st = server.getActiveDBConnection().getConnection().createStatement();

            st.executeUpdate(String.format(FREEZE_PLANUNGSABSCHNITT_QUERY, gupId));
        } catch (Exception e) {
            log.error("Error while executing los trigger.", e);
        }
    }

    @Override
    public void afterCommittedInsert(final CidsBean cidsBean, final User user) {
        if (isGupLos(cidsBean)) {
            unassignMassnFromOtherLos(cidsBean, user);
        }
        if (isGupGup(cidsBean)) {
            final Boolean freezed = (Boolean)cidsBean.getProperty("eingefroren");

            if ((freezed != null) && freezed.booleanValue()) {
                freezePlanungsabschnitte(cidsBean.getProperty("id").toString());
            }
        }
    }

    @Override
    public void afterCommittedUpdate(final CidsBean cidsBean, final User user) {
        if (isGupLos(cidsBean)) {
            unassignMassnFromOtherLos(cidsBean, user);
        }
        if (isGupPlanungsabschnitt(cidsBean)) {
            deleteUnassignedMassnObjects();
        }

        if (isGupGup(cidsBean)) {
            final Boolean freezed = (Boolean)cidsBean.getProperty("eingefroren");

            if ((freezed != null) && freezed.booleanValue()) {
                freezePlanungsabschnitte(cidsBean.getProperty("id").toString());
            }
        }
    }

    @Override
    public void afterCommittedDelete(final CidsBean cidsBean, final User user) {
        if (isGupPlanungsabschnitt(cidsBean)) {
            deleteUnassignedMassnObjects();
        }
        if (isGupUnterhaltungsmassnahme(cidsBean)) {
            deleteUnassignedMassnObjectReferences();
        }
    }
}
