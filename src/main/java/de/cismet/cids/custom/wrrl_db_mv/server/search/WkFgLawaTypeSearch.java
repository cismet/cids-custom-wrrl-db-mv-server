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
 * Searchs for the LAWA types which are contained within a WK-FG.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WkFgLawaTypeSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY = "SELECT "
                + "    nr.code, "
                + "    nr.description, "
                + "    wk_fg_total_length.length AS wk_fg_length, "
                + "    length(st_intersection(wk_fg_total_geom, lg.geo_field)) AS intersection_length "
                + "FROM "
                + "    wk_fg AS fg, "
                + "    lawa AS l, "
                + "    station_linie AS linie, "
                + "    geom AS lg, "
                + "    la_lawa_nr AS nr, "
                + "        ( "
                + "            SELECT "
                + "                sum(length(geo_field)) AS length, "
                + "                geomunion(geo_field) AS wk_fg_total_geom, "
                + "                extent(geo_field) AS wk_fg_total_geom_ext "
                + "            FROM "
                + "                wk_fg AS fg, "
                + "                wk_fg_teile AS teile, "
                + "                wk_teil AS teil, "
                + "                station_linie AS linie, "
                + "                geom AS g "
                + "            WHERE "
                + "                fg.id = %1$s "
                + "                AND teil.id = teile.teil "
                + "                AND teile.wk_fg_reference = fg.id "
                + "                AND linie.id = teil.linie "
                + "                AND g.id = linie.geom "
                + "        ) wk_fg_total_length "
                + "WHERE "
                + "    fg.id = %1$s "
                + "    AND linie.id = l.linie "
                + "    AND linie.geom = lg.id "
                + "    AND l.lawa_nr = nr.id "
                + "    AND fast_intersects(lg.geo_field, wk_fg_total_geom_ext, wk_fg_total_geom)                               "
                + "UNION                                                                                                     "
                + "SELECT "
                + "    -1, "
                + "    'kein Typ',                                                                                    "
                + "    coalesce( st_numGeometries( st_LineMerge( st_Difference( wk_fg_total_geom, st_union(lg.geo_field) ) ) ), 1), "
                + "    st_length( st_LineMerge( st_Difference( wk_fg_total_geom, st_union(lg.geo_field) ) ) ) "
                + "FROM "
                + "    wk_fg AS fg, "
                + "    lawa AS l, "
                + "    station_linie AS linie, "
                + "    geom AS lg, "
                + "    la_lawa_nr AS nr,                                                            "
                + "        ( "
                + "            SELECT "
                + "                sum(length(geo_field)) AS length, "
                + "                geomunion(geo_field) AS wk_fg_total_geom, "
                + "                extent(geo_field) AS wk_fg_total_geom_ext                                                             "
                + "            FROM "
                + "                wk_fg AS fg, "
                + "                wk_fg_teile AS teile, "
                + "                wk_teil AS teil, "
                + "                station_linie AS linie, "
                + "                geom g "
                + "            WHERE "
                + "                fg.id = %1$s "
                + "                AND teil.id = teile.teil "
                + "                AND teile.wk_fg_reference = fg.id "
                + "                AND linie.id = teil.linie "
                + "                AND g.id = linie.geom "
                + "        ) wk_fg_total_length                                                        "
                + "WHERE "
                + "    fg.id = %1$s "
                + "    AND linie.id = l.linie "
                + "    AND linie.geom = lg.id "
                + "    AND l.lawa_nr = nr.id "
                + "    AND fast_intersects( lg.geo_field, wk_fg_total_geom_ext, wk_fg_total_geom ) "
                + "GROUP BY "
                + "    wk_fg_total_geom"
                + ";"; // NOI18N

    private static final String QUERY_WITHOUT_LAWA = "SELECT "
                + "    -1, "
                + "    'kein Typ', "
                + "    sum( length( geo_field ) ) AS wk_fg_length, "
                + "    sum( length( geo_field ) ) AS intersection_length "
                + "FROM "
                + "    wk_fg AS fg, "
                + "    wk_fg_teile AS teile, "
                + "    wk_teil AS teil, "
                + "    station_linie AS linie, "
                + "    geom AS g "
                + "WHERE "
                + "    fg.id = %1$s "
                + "    AND teile.wk_fg_reference = fg.id "
                + "    AND teil.id = teile.teil "
                + "    AND linie.id = teil.linie "
                + "    AND g.id = linie.geom;"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private String wkFgId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LawaTypeNeighbourSearch object.
     *
     * @param  wkFgId  lawaTypeId DOCUMENT ME!
     */
    public WkFgLawaTypeSearch(final String wkFgId) {
        this.wkFgId = wkFgId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLoaclServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                String query = String.format(QUERY, wkFgId);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("query: " + query); // NOI18N
                }
                ArrayList<ArrayList> lists = ms.performCustomSearch(query);

                if (!lists.isEmpty()) {
                    return lists;
                } else {
                    query = String.format(QUERY_WITHOUT_LAWA, wkFgId);
                    if (getLog().isDebugEnabled()) {
                        getLog().debug("query: " + query); // NOI18N
                    }
                    lists = ms.performCustomSearch(query);

                    return lists;
                }
            } catch (RemoteException ex) {
                getLog().error(ex.getMessage(), ex);
            }
        } else {
            getLog().error("active local server not found"); // NOI18N
        }

        return null;
    }
}
