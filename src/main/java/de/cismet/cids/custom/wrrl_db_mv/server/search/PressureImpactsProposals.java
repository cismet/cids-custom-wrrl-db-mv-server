/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * Loads the proposal values for pressure, impact, driver.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class PressureImpactsProposals extends AbstractCidsServerSearch implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(PlanungsabschnittSearch.class);

//    private static final String QUERY = "select pressure, impact from impact_driver";
    private static final String QUERY_DRIVER = "select pressure, impact, driver from impact_driver";

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext cc = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     */
    public PressureImpactsProposals() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + QUERY_DRIVER); // NOI18N
                }
                final ArrayList<ArrayList> list = ms.performCustomSearch(QUERY_DRIVER, cc);

                return list;
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found"); // NOI18N
        }

        return null;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext cc) {
        this.cc = cc;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return cc;
    }
}
