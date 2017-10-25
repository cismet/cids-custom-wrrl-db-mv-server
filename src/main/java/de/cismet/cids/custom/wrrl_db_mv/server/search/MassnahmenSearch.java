/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wrrl_db_mv.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * Search for the gup_unterhaltungsmassnamen, which are contained in the given gup_los.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MassnahmenSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(MassnahmenSearch.class);

    private static final String QUERY =
        "select pvon.wert, pbis.wert, g.name, p.name, a.name, b.ort, von.wert, bis.wert, "
                + "m.randstreifenbreite, m.boeschungsbreite, m.boeschungslaenge, "
                + "m.deichkronenbreite, m.sohlbreite, m.vorlandbreite, m.cbmprom, m.stueck, m.stunden, m.schnitttiefe, m.teillaenge, "
                + "m.m_zwei, m.m_drei, m.arbeitsbreite, a.id, a.leistungstext, "
                + " CASE WHEN a.aufmass_regel is null or a.aufmass_regel = '' THEN gewerk.aufmass_regel "
                + "ELSE a.aufmass_regel END, CASE WHEN a.einheit is null or a.einheit = '' THEN gewerk.einheit "
                + "ELSE a.einheit END, m.id, p.id, g.id, r.gwk, a.massnahmen_id from gup_unterhaltungsmassnahme m "
                + "join gup_los_gup_unterhaltungsmassnahme lgu on (m.id = lgu.gup_unterhaltungsmassnahme)"
                + "left join gup_planungsabschnitt_gup_unterhaltungsmassnahme pgu on (m.id = pgu.gup_unterhaltungsmassnahme)"
                + "left outer join gup_planungsabschnitt p on (pgu.gup_planungsabschnitt_reference = p.id) "
                + "left outer join station_linie psl on (p.linie = psl.id) "
                + "left outer join station pvon on (psl.von = pvon.id) "
                + "left outer join station pbis on (psl.bis = pbis.id) "
                + "left outer join gup_gup g on (p.gup = g.id) "
                + "left outer join gup_massnahmenart a on (m.massnahme = a.id) "
                + "left join gup_massnahmenbezug b on (m.wo = b.id) "
                + "left join gup_gewerk gewerk on (a.gewerk = gewerk.id) "
                + "left join station_linie sl on (m.linie = sl.id) "
                + "left join station von on (sl.von = von.id) "
                + "left join station bis on (sl.bis = bis.id) "
                + "left join route r on (von.route = r.id) "
                + "WHERE lgu.gup_los_reference = %1$s order by a.id";
//    private static final String QUERY =
//        "select pvon.wert, pbis.wert, g.name, p.name, a.name, b.ort, von.wert, bis.wert, "
//                + "m.randstreifenbreite, m.boeschungsbreite, m.boeschungslaenge, "
//                + "m.deichkronenbreite, m.sohlbreite, m.vorlandbreite, m.cbmprom, m.stueck, m.stunden, m.schnitttiefe, a.id, a.leistungstext, "
//                + " CASE WHEN a.aufmass_regel is null or a.aufmass_regel = '' THEN gewerk.aufmass_regel "
//                + "ELSE a.aufmass_regel END, CASE WHEN a.einheit is null or a.einheit = '' THEN gewerk.einheit "
//                + "ELSE a.einheit END, m.id, p.id, g.id from gup_unterhaltungsmassnahme m "
//                + "left outer join gup_planungsabschnitt p on (m.planungsabschnitt = p.id) "
//                + "left outer join station_linie psl on (p.linie = psl.id) "
//                + "left outer join station pvon on (psl.von = pvon.id) "
//                + "left outer join station pbis on (psl.bis = pbis.id) "
//                + "left outer join gup_gup g on (p.gup = g.id) "
//                + "left outer join gup_massnahmenart a on (m.massnahme = a.id) "
//                + "left join gup_massnahmenbezug b on (m.wo = b.id) "
//                + "left join gup_gewerk gewerk on (a.gewerk = gewerk.id) "
//                + "left join station_linie sl on (m.linie = sl.id) "
//                + "left join station von on (sl.von = von.id) "
//                + "left join station bis on (sl.bis = bis.id) "
//                + "left join route r on (von.route = r.id) "
//                + "WHERE m.los = %1$s";

    //~ Instance fields --------------------------------------------------------

    private final String losId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  losId  geometry DOCUMENT ME!
     */
    public MassnahmenSearch(final String losId) {
        this.losId = losId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, losId);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("query: " + query); // NOI18N
                }
                final ArrayList<ArrayList> lists = ms.performCustomSearch(query);
                return lists;
            } catch (RemoteException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            LOG.error("active local server not found"); // NOI18N
        }

        return null;
    }
}
