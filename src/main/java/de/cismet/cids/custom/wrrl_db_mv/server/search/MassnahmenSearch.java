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

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

/**
 * Search for the gup_unterhaltungsmassnamen, which are contained in the given gup_los.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MassnahmenSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY =
        "select pvon.wert, pbis.wert, g.name, p.name, a.name, b.ort, von.wert, bis.wert, "
                + "m.randstreifenbreite, m.boeschungsbreite, m.boeschungslaenge, "
                + "m.deichkronenbreite, m.sohlbreite, m.vorlandbreite from "
                + "gup_unterhaltungsmassnahme m "
                + "left outer join gup_planungsabschnitt p on (m.planungsabschnitt = p.id) "
                + "left outer join station_linie psl on (p.linie = psl.id) "
                + "left outer join station pvon on (psl.von = pvon.id) "
                + "left outer join station pbis on (psl.bis = pbis.id) "
                + "left outer join gup_gup g on (p.gup = g.id) "
                + "left outer join gup_massnahmenart a on (m.massnahme = a.id) "
                + "left join gup_massnahmenbezug b on (m.wo = b.id)"
                + "left join station_linie sl on (m.linie = sl.id) "
                + "left join station von on (sl.von = von.id) "
                + "left join station bis on (sl.bis = bis.id) "
                + "left join route r on (von.route = r.id) "
                + "WHERE m.los = %1$s";

    //~ Instance fields --------------------------------------------------------

    private String losId;

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
        final MetaService ms = (MetaService)getActiveLoaclServers().get(WRRLUtil.DOMAIN_NAME);

        if (ms != null) {
            try {
                final String query = String.format(QUERY, losId);
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