/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.deletionprovider;

import Sirius.server.localserver.object.AbstractCustomDeletionProvider;
import Sirius.server.localserver.object.CustomDeletionProvider;
import Sirius.server.localserver.object.DeletionProviderClientException;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.custom.wrrl_db_mv.commons.WRRLUtil;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CustomDeletionProvider.class)
public class MassnahmenDeletionProvider extends AbstractCustomDeletionProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MassnahmenDeletionProvider.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTableName() {
        return "massnahmen";
    }

    @Override
    public boolean customDeleteMetaObject(final User user, final MetaObject metaObject) throws Exception {
        // darf nicht geloescht werden
        if (user.getUserGroup().getName().toLowerCase().startsWith("stalu")) {
            throw new DeletionProviderClientException(
                "Die Maßnahme darf nicht gelöscht werden.\nStattdessen sollte die Option \"Maßnahme verworfen\" markiert werden.");
        }

        return false;
    }

    @Override
    public String getDomain() {
        return WRRLUtil.DOMAIN_NAME;
    }
}
