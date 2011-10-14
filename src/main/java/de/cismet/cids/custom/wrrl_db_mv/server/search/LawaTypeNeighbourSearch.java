/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 therter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

/**
 * Searchs for the predecessor and sucessor of a lawa type object. The function getLAWAType(integer, boolean) must exist
 * in the database.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LawaTypeNeighbourSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY = "select getLAWAType(%1$s, %2$s);"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String lawaTypeId;
    private String isPredecessor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LawaTypeNeighbourSearch object.
     *
     * @param  lawaTypeId     DOCUMENT ME!
     * @param  isPredecessor  DOCUMENT ME!
     */
    public LawaTypeNeighbourSearch(final String lawaTypeId,
            final boolean isPredecessor) {
        this.lawaTypeId = lawaTypeId;
        this.isPredecessor = (isPredecessor ? "true" : "false");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLoaclServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, lawaTypeId, isPredecessor);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query);
                return lists;
            } catch (RemoteException ex) {
                getLog().error(ex.getMessage(), ex);
            }
        } else {
            getLog().error("active local server not found"); // NOI18N
        }

        return null;
    }
}
