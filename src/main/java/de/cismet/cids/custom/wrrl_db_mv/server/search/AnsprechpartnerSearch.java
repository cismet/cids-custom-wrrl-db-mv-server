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

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class AnsprechpartnerSearch extends CidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String QUERY = "select a.id, s.name, a.email, a.name, a.tel, "
                + "   s_von.wert - {1} as startPoint, s_bis.wert - {1} as endPoint "
                + "from gup_ansprechpartner a inner join gup_zust_seite s on (a.seite = s.id)"
                + "  	inner join station_linie sl on (a.linie = sl.id)"
                + "  	inner join station s_von on (sl.von = s_von.id)"
                + " 	inner join station s_bis on (sl.bis = s_bis.id)"
                + " 	inner join route r on (s_bis.route = r.id) "
                + "where r.gwk = {0} and"
                + "        ( (s_von.wert > {1} AND s_von.wert < {2}) OR"
                + "          (s_bis.wert > {1} AND s_bis.wert < {2}) OR"
                + "          (s_von.wert = {1} AND s_bis.wert = {2})    );";

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
    public AnsprechpartnerSearch(final double from, final double to, final String route_gwk) {
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
