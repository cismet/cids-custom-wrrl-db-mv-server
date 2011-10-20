/*
 * Copyright (C) 2011 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author therter
 */
public class NaturschutzgebietSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY = "select na.name, "
                + "ST_Line_Locate_Point(route_geo, st_startPoint(ST_intersection(geo_field, sgeom.geo))) "
                + "* length(route_geo) as startPoint, "
                + "ST_Line_Locate_Point(route_geo, st_endPoint(ST_intersection(geo_field, sgeom.geo))) "
                + "* length(route_geo) as endPoint, "
                + "n.bemerkung "
                + "from gup_naturschutz n inner join geom on (n.geom = geom.id) inner join " 
                + "gup_naturschutzart na on (n.art = na.id), "
                + "(select geo_field as route_geo, (line_substring(geo_field, {1} / length(geo_field), {2} / length(geo_field))) as geo "
                + "from route inner join geom on (route.geom = geom.id) "
                + "where route.gwk = {0}) as sgeom "
                + "where geo_field && sgeom.geo AND intersects(geo_field, sgeom.geo);";

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
    public NaturschutzgebietSearch(final double from, final double to, final String route_gwk) {
        this.from = (from <= to ? from : to);
        this.to = (to >= from ? to : from);
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
