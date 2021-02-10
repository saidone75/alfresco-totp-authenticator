package org.saidone.web.site;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.webscripts.connector.*;

import javax.servlet.http.HttpServletRequest;

public class TotpSlingshotUserFactory extends org.alfresco.web.site.SlingshotUserFactory
{
    public static final String CREDENTIAL_TOKEN = "token";

    private static final Log logger = LogFactory.getLog(AlfrescoUserFactory.class);

    public boolean authenticate(HttpServletRequest request, String username, String password, String token)
    {
        boolean authenticated = false;
        try
        {
            // make sure our credentials are in the vault
            CredentialVault vault = frameworkUtils.getCredentialVault(request.getSession(), username);
            Credentials credentials = vault.newCredentials(ALFRESCO_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
            credentials.setProperty(CREDENTIAL_TOKEN, token);

            // build a connector whose connector session is bound to the current session
            Connector connector = frameworkUtils.getConnector(request.getSession(), username, ALFRESCO_ENDPOINT_ID);
            AuthenticatingConnector authenticatingConnector;
            if (connector instanceof AuthenticatingConnector)
            {
                authenticatingConnector = (AuthenticatingConnector)connector;
            }
            else
            {
                // Manual connector retrieval and authenticator creation required.
                // This code path is followed if an SSO attempt has failed and the
                // login form is shown as a failover once all SSO attempts expire.
                ConnectorService cs = (ConnectorService)getApplicationContext().getBean("connector.service");
                authenticatingConnector = new AuthenticatingConnector(connector, cs.getAuthenticator("alfresco-ticket"));
            }
            authenticated = authenticatingConnector.handshake();
        }
        catch (Throwable ex)
        {
            // many things might have happened
            // an invalid ticket or perhaps a connectivity issue
            // at any rate, we cannot authenticate
            if (logger.isInfoEnabled())
                logger.info("Exception in AlfrescoUserFactory.authenticate()", ex);
        }

        return authenticated;
    }

}
