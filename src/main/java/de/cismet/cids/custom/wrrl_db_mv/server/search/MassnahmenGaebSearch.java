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
public class MassnahmenGaebSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(MassnahmenGaebSearch.class);
    private static final String QUERY = "select m.id, "
                + "CASE WHEN a.boeschungslaenge then "
                + "(((case when von.wert > bis.wert then von.wert else bis.wert end) - (case when bis.wert < von.wert then bis.wert else von.wert end)) "
                + "* coalesce(m.boeschungslaenge, 0)) "
                + "WHEN a.cbmprom then "
                + "(((case when von.wert > bis.wert then von.wert else bis.wert end) - (case when bis.wert < von.wert then bis.wert else von.wert end)) "
                + "* coalesce(m.cbmprom, 0)) "
                + "WHEN a.stueck then "
                + "coalesce(m.stueck, 0) "
                + "WHEN a.sohlbreite then "
                + "(((case when von.wert > bis.wert then von.wert else bis.wert end) - (case when bis.wert < von.wert then bis.wert else von.wert end)) "
                + "* coalesce(m.sohlbreite, 0)) "
                + "WHEN a.deichkronenbreite then "
                + "(((case when von.wert > bis.wert then von.wert else bis.wert end) - (case when bis.wert < von.wert then bis.wert else von.wert end)) "
                + "* coalesce(m.deichkronenbreite, 0)) "
                + "WHEN a.vorlandbreite then "
                + "(((case when von.wert > bis.wert then von.wert else bis.wert end) - (case when bis.wert < von.wert then bis.wert else von.wert end)) "
                + "* coalesce(m.vorlandbreite, 0)) "
                + "WHEN a.randstreifenbreite then "
                + "(((case when von.wert > bis.wert then von.wert else bis.wert end) - (case when bis.wert < von.wert then bis.wert else von.wert end)) "
                + "* coalesce(m.randstreifenbreite, 0)) "
                + "ELSE  "
                + "1 "
                + "END as menge "
                + ", "
                + "CASE WHEN a.boeschungslaenge or a.sohlbreite or a.deichkronenbreite or a.vorlandbreite or a.randstreifenbreite then "
                + "'m²' "
                + "WHEN a.cbmprom then "
                + "'m³' "
                + "WHEN a.stueck then "
                + "' ' "
                + "ELSE "
                + "' ' "
                + "END as einheit, "
                + "a.leistungstext, "
                + "a.name "
                + "from gup_unterhaltungsmassnahme m "
                + "left outer join gup_planungsabschnitt p on (m.planungsabschnitt = p.id) "
                + "left outer join station_linie psl on (p.linie = psl.id) "
                + "left outer join station pvon on (psl.von = pvon.id) "
                + "left outer join station pbis on (psl.bis = pbis.id) "
                + "left outer join gup_gup g on (p.gup = g.id) "
                + "left outer join gup_massnahmenart a on (m.massnahme = a.id) "
                + "left join gup_massnahmenbezug b on (m.wo = b.id) "
                + "left join station_linie sl on (m.linie = sl.id) "
                + "left join station von on (sl.von = von.id) "
                + "left join station bis on (sl.bis = bis.id) "
                + "left join route r on (von.route = r.id) "
                + "WHERE m.los =  %1$s";

    //~ Instance fields --------------------------------------------------------

    private String losId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WkkSearch object.
     *
     * @param  losId  geometry DOCUMENT ME!
     */
    public MassnahmenGaebSearch(final String losId) {
        this.losId = losId;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, Long.parseLong(losId));
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
