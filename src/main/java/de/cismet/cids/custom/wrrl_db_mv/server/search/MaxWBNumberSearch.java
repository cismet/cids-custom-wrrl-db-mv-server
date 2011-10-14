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
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MaxWBNumberSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY =
        "SELECT max(massn_wk_lfdnr) FROM massnahmen, %1$s WHERE massnahmen.%2$s = %1$s.id AND %1$s.id = %3$s";

    //~ Instance fields --------------------------------------------------------

    private String referencedTable;
    private String massReferenceField;
    private String referencedObjectId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MaxWBNumberSearch object.
     *
     * @param  referencedTable     DOCUMENT ME!
     * @param  referencedObjectId  DOCUMENT ME!
     * @param  massReferenceField  DOCUMENT ME!
     */
    public MaxWBNumberSearch(final String referencedTable,
            final String referencedObjectId,
            final String massReferenceField) {
        this.referencedTable = referencedTable;
        this.referencedObjectId = referencedObjectId;
        this.massReferenceField = massReferenceField;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLoaclServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, referencedTable, massReferenceField, referencedObjectId);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("query: " + query);
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query);
                return lists;
            } catch (RemoteException ex) {
                getLog().error(ex.getMessage(), ex);
            }
        } else {
            getLog().error("active local server not found");
        }

        return null;
    }
}
