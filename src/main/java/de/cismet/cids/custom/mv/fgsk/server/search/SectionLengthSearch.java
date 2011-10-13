/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.mv.fgsk.server.search;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SectionLengthSearch extends SimpleMappingSearch<Integer, Double> {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SectionLengthSearch object.
     *
     * @param  tablename  DOCUMENT ME!
     */
    public SectionLengthSearch(final String tablename) {
        super(tablename, "id_gewaessertyp", Integer.class, "abschnittslaenge", Double.class); // NOI18N
    }
}
