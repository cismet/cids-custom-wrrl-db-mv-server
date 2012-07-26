/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.trigger;

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.newuser.User;
import Sirius.server.search.Query;
import Sirius.server.sql.DBConnection;
import Sirius.server.sql.SystemStatement;

import org.openide.util.lookup.ServiceProvider;

import java.sql.SQLException;
import java.sql.Statement;

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
public class FgskKartierabschnittTrigger extends AbstractDBAwareCidsTrigger {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            FgskKartierabschnittTrigger.class);
    private static final String FGSK_KARTIERABSCHNITT_CLASS_NAME = "de.cismet.cids.dynamics.Fgsk_kartierabschnitt";
    private static final String FGSK_KARTIERABSCHNITT_TABLE_NAME = "fgsk_kartierabschnitt";

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
        return new CidsTriggerKey(CidsTriggerKey.ALL, FGSK_KARTIERABSCHNITT_TABLE_NAME);
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
    private boolean isKartierabschnittObject(final CidsBean cidsBean) {
        return (cidsBean.getClass().getName().equals(FGSK_KARTIERABSCHNITT_CLASS_NAME));
    }

    @Override
    public void afterCommittedInsert(final CidsBean cidsBean, final User user) {
        renumber(cidsBean);
    }

    @Override
    public void afterCommittedUpdate(final CidsBean cidsBean, final User user) {
        renumber(cidsBean);
    }

    @Override
    public void afterCommittedDelete(final CidsBean cidsBean, final User user) {
        renumber(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void renumber(final CidsBean cidsBean) {
        if (isKartierabschnittObject(cidsBean)) {
            try {
                final Object gwk = cidsBean.getProperty("linie.von.route.gwk");
                final Statement s = getDbServer().getActiveDBConnection().getConnection().createStatement();
                s.execute("select fgsk_abschnitte_nummerieren(" + gwk.toString() + ", null)");
            } catch (Exception e) {
                log.error("Error while executing fgsk trigger.", e);
            }
        }
    }
}
