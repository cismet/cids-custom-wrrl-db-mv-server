/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.sql.PreparableStatement;

import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.server.search.builtin.DefaultGeoSearch;
import de.cismet.cids.server.search.builtin.GeoSearch;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
//@ServiceProvider(
//    service = GeoSearch.class,
//    position = 1
//)
public class CustomGeoSearch { // extends DefaultGeoSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CustomGeoSearch.class);

//    @Override
//    public PreparableStatement getSearchSql(final String domainKey) {
//        final String sql = ""                                                                                                // NOI18N
//                    + "SELECT DISTINCT i.class_id , "                                                                        // NOI18N
//                    + "                i.object_id, "                                                                        // NOI18N
//                    + "                s.stringrep "                                                                         // NOI18N
//                    + "FROM            geom g, "                                                                             // NOI18N
//                    + "                cs_attr_object_derived i "                                                            // NOI18N
//                    + "                LEFT OUTER JOIN cs_cache s "                                                          // NOI18N
//                    + "                ON              ( "                                                                   // NOI18N
//                    + "                                                s.class_id =i.class_id "                              // NOI18N
//                    + "                                AND             s.object_id=i.object_id "                             // NOI18N
//                    + "                                ) "                                                                   // NOI18N
//                    + "WHERE           i.attr_class_id = "                                                                   // NOI18N
//                    + "                ( SELECT cs_class.id "                                                                // NOI18N
//                    + "                FROM    cs_class "                                                                    // NOI18N
//                    + "                WHERE   cs_class.table_name::text = 'GEOM'::text "                                    // NOI18N
//                    + "                ) "                                                                                   // NOI18N
//                    + "AND             i.attr_object_id = g.id "                                                             // NOI18N
//                    + "AND i.class_id IN <cidsClassesInStatement> "                                                          // NOI18N
//                    + "AND geo_field && GeometryFromText('SRID=<cidsSearchGeometrySRID>;<cidsSearchGeometryWKT>') "          // NOI18N
//                    + "AND intersects(geo_field,GeometryFromText('SRID=<cidsSearchGeometrySRID>;<cidsSearchGeometryWKT>')) " // NOI18N
//                    + "ORDER BY        1,2,3";
//
//        String cidsSearchGeometryWKT = getGeometry().toText();
//        final String sridString = Integer.toString(getGeometry().getSRID());
//        final String classesInStatement = getClassesInSnippetsPerDomain().get(domainKey);
//
//        if (getGeometry().getCoordinates().length > 1000) {
//            LOG.warn("length of the geometry: " + getGeometry().getCoordinates().length + " "
//                        + " Geometry will be simplified to a length of: "
//                        + TopologyPreservingSimplifier.simplify(getGeometry(), 30).getCoordinates().length);
//            cidsSearchGeometryWKT = TopologyPreservingSimplifier.simplify(getGeometry(), 30).toText();
//        }
//
//        if ((cidsSearchGeometryWKT == null) || (cidsSearchGeometryWKT.trim().length() == 0)
//                    || (sridString == null)
//                    || (sridString.trim().length() == 0)) {
//            // TODO: Notify user?
//            LOG.error(
//                "Search geometry or srid is not given. Can't perform a search without those information."); // NOI18N
//
//            return null;
//        }
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("cidsClassesInStatement=" + classesInStatement);   // NOI18N
//            LOG.debug("cidsSearchGeometryWKT=" + cidsSearchGeometryWKT); // NOI18N
//            LOG.debug("cidsSearchGeometrySRID=" + sridString);           // NOI18N
//        }
//
//        if ((classesInStatement == null) || (classesInStatement.trim().length() == 0)) {
//            LOG.warn("There are no search classes defined for domain '" + domainKey // NOI18N
//                        + "'. This domain will be skipped."); // NOI18N
//            return null;
//        }
//
//        final PreparableStatement ps = new PreparableStatement(
//                sql.replaceAll("<cidsClassesInStatement>", classesInStatement) // NOI18N
//                .replaceAll("<cidsSearchGeometryWKT>", cidsSearchGeometryWKT)  // NOI18N
//                .replaceAll("<cidsSearchGeometrySRID>", sridString),
//                new int[0]);                                                   // NOI18N
//
//        ps.setObjects(new Object[0]);
//
//        return ps;
//    }
}
