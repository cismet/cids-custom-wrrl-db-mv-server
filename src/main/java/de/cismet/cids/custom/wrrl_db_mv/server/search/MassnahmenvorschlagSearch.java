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
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

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

    private static final String QUERY =
        "select distinct (select id from cs_class where name ilike 'sim_massnahmen_gruppe') , "
                + "mg.id from sim_massnahmen_gruppe mg join sim_massnahmen_gruppe_sim_massnahmen smgsm on "
                + "(mg.id = smgsm.sim_massnahmen_gruppe_reference)  join sim_massnahme m on (smgsm.massnahmen = m.id) "
                + "join sim_massnahmen_wirkung w on m.id = w.massnahme join la_lawa_nr l "
                + "on gewaessertyp = l.id where l.code = %1$s";

    //~ Instance fields --------------------------------------------------------

    private MetaObject fgsk_mo;
    private User usr;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  fgsk_mo  fgsk_bean losId geometry DOCUMENT ME!
     * @param  usr      DOCUMENT ME!
     */
    public MassnahmenvorschlagSearch(final MetaObject fgsk_mo, final User usr) {
        this.fgsk_mo = fgsk_mo;
        this.usr = usr;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final Object gewTyp = fgsk_mo.getBean().getProperty("gewaessertyp_id.value");
                final String query = String.format(QUERY, String.valueOf(gewTyp));

                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final MetaObject[] list = ms.getMetaObject(usr, query);

                final ArrayList<Node> result = new ArrayList<Node>(list.length);

                for (final MetaObject tmpObject : list) {
                    result.add(new MetaObjectNode(tmpObject.getBean()));
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
}
