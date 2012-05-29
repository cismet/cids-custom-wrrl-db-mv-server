/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Sucht nach den Naturschutzgebieten, die auf der uebergebenen Strecke liegen. Folgende PL/SQL Funktionen muessen der
 * Datenbank bekannt sein:
 *
 * <p>CREATE OR REPLACE FUNCTION determineLine(startVal numeric, endVal numeric, routeGwk bigint) RETURNS Geometry AS
 * $BODY$ declare g Geometry; begin select line_substring(geo_field, startVal / LENGTH(geo_field), endVal /
 * LENGTH(geo_field)) into g from route, geom where route.geom = geom.id and gwk = routeGwk; return g; end $BODY$
 * LANGUAGE 'plpgsql' IMMUTABLE COST 100;</p>
 *
 * <p>CREATE OR REPLACE FUNCTION determineRouteGeom(routeGwk bigint) RETURNS Geometry AS $BODY$ declare g Geometry;
 * begin select geo_field into g from route, geom where route.geom = geom.id and gwk = routeGwk; return g; end $BODY$
 * LANGUAGE 'plpgsql' IMMUTABLE COST 100;</p>
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class NaturschutzgebietSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(NaturschutzgebietSearch.class);

    private static final String QUERY = "select na.name,"
                + " case when geometrytype(ST_intersection(geo_field, sgeom.geo)) = ''MULTILINESTRING'' then "
                + "   ST_Line_Locate_Point(route_geo, st_startPoint( ST_GeometryN(ST_intersection(geo_field, sgeom.geo), "
                + "      generate_series(1, ST_NumGeometries(ST_intersection(geo_field, sgeom.geo)))) )) * length(route_geo)"
                + " else"
                + "   ST_Line_Locate_Point(route_geo, st_startPoint(ST_intersection(geo_field, sgeom.geo))) * length(route_geo)"
                + " end as startPoint, "
                + " case when geometrytype(ST_intersection(geo_field, sgeom.geo)) = ''MULTILINESTRING'' then "
                + "   ST_Line_Locate_Point(route_geo, st_endPoint( ST_GeometryN(ST_intersection(geo_field, sgeom.geo), "
                + "      generate_series(1, ST_NumGeometries(ST_intersection(geo_field, sgeom.geo)))) )) * length(route_geo)"
                + " else"
                + "   ST_Line_Locate_Point(route_geo, st_endPoint(ST_intersection(geo_field, sgeom.geo))) * length(route_geo)"
                + " end as endPoint, "
                + "n.bemerkung, "
                + " case when geometrytype(ST_intersection(geo_field, sgeom.geo)) = ''MULTILINESTRING'' then "
                + "   asText(ST_GeometryN(ST_intersection(geo_field, sgeom.geo), "
                + "     generate_series(1, ST_NumGeometries(ST_intersection(geo_field, sgeom.geo)))))"
                + "  else"
                + "   asText(ST_intersection(geo_field, sgeom.geo)) "
                + " end as geom "
                + "from gup_naturschutz n inner join geom on (n.geom = geom.id) "
                + "inner join gup_naturschutzart na on (n.art = na.id), "
                + "(select determineRouteGeom({0}) as route_geo, determineLine({1}, {2}, {0}) as geo "
                + ") as sgeom "
                + "where geo_field && sgeom.geo AND intersects(geo_field, sgeom.geo); ";

//    private static final String QUERY = "select na.name, "
//                + "ST_Line_Locate_Point(route_geo, st_startPoint(ST_intersection(geo_field, sgeom.geo))) "
//                + "* length(route_geo) as startPoint, "
//                + "ST_Line_Locate_Point(route_geo, st_endPoint(ST_intersection(geo_field, sgeom.geo))) "
//                + "* length(route_geo) as endPoint, "
//                + "n.bemerkung "
//                + "from gup_naturschutz n inner join geom on (n.geom = geom.id) inner join "
//                + "gup_naturschutzart na on (n.art = na.id), "
//                + "(select geo_field as route_geo, (line_substring(geo_field, {1} / length(geo_field), {2} / length(geo_field))) as geo "
//                + "from route inner join geom on (route.geom = geom.id) "
//                + "where route.gwk = {0}) as sgeom "
//                + "where geo_field && sgeom.geo AND intersects(geo_field, sgeom.geo);";

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
        this.from = ((from <= to) ? from : to);
        this.to = ((to >= from) ? to : from);
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
                "966400000000",
                String.format(Locale.US, "%f", 0f),
                String.format(Locale.US, "%f", 73485f)));
    }
}
