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
package de.cismet.cids.custom.wrrl_db_mv.fgsk.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;

import java.rmi.RemoteException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Searchs for the wk_k the given geometry is contained in. The pgsql function getWk_k(integer, geometry) must exist in
 * the database.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WkSearchByStations extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY = "SELECT   wk_fg.wk_k   , "
                + "         MIN(von.wert), "
                + "         MAX(bis.wert) "
                + "FROM     wk_fg        , "
                + "         wk_fg_teile  , "
                + "         wk_teil      , "
                + "         station_linie, "
                + "         station von  , "
                + "         station bis  , "
                + "         route "
                + "WHERE    wk_fg.teile      =wk_fg_teile.wk_fg_reference "
                + "AND      wk_fg_teile.teil =wk_teil.id "
                + "AND      wk_teil.linie    =station_linie.id "
                + "AND      station_linie.von=von.id "
                + "AND      station_linie.bis=bis.id "
                + "AND      von.route        =route.id "
                + "AND      route.gwk        ={0} "
                + "GROUP BY wk_fg.wk_k "
                + "HAVING   ( "
                + "                  ( "
                + "                           {1}  >=MIN(von.wert) "
                + "                  AND      {2}  >=MAX(bis.wert) "
                + "                  ) "
                + "         OR "
                + "                  ( "
                + "                           {1}  <=MIN(von.wert) "
                + "                  AND      {2}  >=MAX(bis.wert) "
                + "                  ) "
                + "         OR "
                + "                  ( "
                + "                           {1}  <=MIN(von.wert) "
                + "                  AND      {2}  >=MIN(von.wert) "
                + "                  ) "
                + "         ) "
                + "ORDER BY MIN(von.wert)";
    private static final String WRRL_DOMAIN = "WRRL_DB_MV"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final double from;
    private final double to;
    private final String route_gwk;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkSearchByStations object.
     *
     * @param  from       DOCUMENT ME!
     * @param  to         DOCUMENT ME!
     * @param  route_gwk  DOCUMENT ME!
     */
    public WkSearchByStations(final double from, final double to, final String route_gwk) {
        this.from = from;
        this.to = to;
        this.route_gwk = route_gwk;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLoaclServers().get(WRRL_DOMAIN);

        if (ms != null) {
            try {
                final String query = MessageFormat.format(
                        QUERY,
                        route_gwk,
                        String.format(Locale.US, "%f", from),
                        String.format(Locale.US, "%f", to));
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

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println(MessageFormat.format(
                QUERY,
                "4711",
                String.format(Locale.US, "%f", 0.67),
                String.format(Locale.US, "%f", 859457f)));
    }
}
