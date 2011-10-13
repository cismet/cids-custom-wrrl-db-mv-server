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
public final class SearchException extends Exception {

    //~ Instance fields --------------------------------------------------------

    private final String query;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>SearchException</code> without detail message.
     */
    public SearchException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>SearchException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public SearchException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Creates a new SearchException object.
     *
     * @param  msg    DOCUMENT ME!
     * @param  query  DOCUMENT ME!
     */
    public SearchException(final String msg, final String query) {
        this(msg, query, null);
    }

    /**
     * Constructs an instance of <code>SearchException</code> with the specified detail message and the specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public SearchException(final String msg, final Throwable cause) {
        this(msg, null, cause);
    }

    /**
     * Creates a new SearchException object.
     *
     * @param  msg    DOCUMENT ME!
     * @param  query  DOCUMENT ME!
     * @param  cause  DOCUMENT ME!
     */
    public SearchException(final String msg, final String query, final Throwable cause) {
        super(msg, cause);

        this.query = query;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getQuery() {
        return query;
    }
}
