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

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * Searchs for the wk_k the given geometry is contained in. The pgsql function getWk_k(integer, geometry) must exist in
 * the database.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class QuerbautenSearchByStations extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(QuerbautenSearchByStations.class);
    private static final String QUERY = "SELECT querbauwerke.id     , "
                + "querbauwerke.bauwerk, "
                + "querbauwerke.anlagename, "
                + "station.wert, "
                + "asText(geo_field) "
                + "FROM querbauwerke, "
                + "station, "
                + "route,"
                + "geom "
                + "WHERE  stat09=station.id "
                + "AND station.route=route.id "
                + "AND route.gwk ={0} "
                + "AND station.real_point = geom.id "
                + "AND station.wert >={1} "
                + "AND station.wert <={2};";

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
    public QuerbautenSearchByStations(final double from, final double to, final String route_gwk) {
        this.from = from;
        this.to = to;
        this.route_gwk = route_gwk;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRL_DOMAIN);

        if (ms != null) {
            try {
                final String query = MessageFormat.format(
                        QUERY,
                        route_gwk,
                        String.format(Locale.US, "%f", from),
                        String.format(Locale.US, "%f", to));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query);

                for (final ArrayList tmp : lists) {
                    try {
                        final String tmpString = (String)tmp.get(4);
                        tmp.set(4, new WKTReader(new GeometryFactory()).read(tmpString));
                    } catch (final ParseException e) {
                        LOG.error("Error while parsing geometry.", e);
                    }
                }
                return lists;
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found"); // NOI18N
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
