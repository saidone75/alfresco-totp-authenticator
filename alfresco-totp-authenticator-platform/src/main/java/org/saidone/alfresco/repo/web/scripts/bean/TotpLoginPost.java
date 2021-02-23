package org.saidone.alfresco.repo.web.scripts.bean;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.sync.events.types.RepositoryEventImpl;
import org.alfresco.sync.repo.events.EventPublisher;
import org.json.JSONException;
import org.json.JSONObject;
import org.saidone.alfresco.repo.security.authentication.TotpService;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Post based login script
 */
public class TotpLoginPost extends org.alfresco.repo.web.scripts.bean.LoginPost {
    // dependencies
    private AuthenticationService authenticationService;
    private TotpService totpService;
    protected EventPublisher eventPublisher;

    /**
     * @param authenticationService AuthenticationService
     */
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * @param totpService TotpService
     */
    public void setTotpService(TotpService totpService) {
        this.totpService = totpService;
    }

    /**
     * @param eventPublisher EventPublisher
     */
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
        // Extract user and password from JSON POST
        Content c = req.getContent();
        if (c == null) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing POST body.");
        }

        // TODO accept xml type.

        // extract username and password from JSON object
        JSONObject json;
        try {
            json = new JSONObject(c.getContent());
            String username = json.getString("username");
            String password = json.getString("password");
            String token = json.getString("token");

            if (username == null || username.length() == 0) {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Username not specified");
            }

            if (password == null) {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Password not specified");
            }

            try {
                return login(username, password, token);
            } catch (WebScriptException e) {
                status.setCode(e.getStatus());
                status.setMessage(e.getMessage());
                status.setRedirect(true);
                return null;
            }
        } catch (JSONException jErr) {
            throw (WebScriptException) new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Unable to parse JSON POST body: " + jErr.getMessage()).initCause(jErr);
        } catch (IOException ioErr) {
            throw (WebScriptException) new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve POST body: " + ioErr.getMessage()).initCause(ioErr);
        }
    }

    protected Map<String, Object> login(final String username, String password, String token) {
        try {
            // check totp
            totpService.authorizeToken(username, token);

            // get ticket
            authenticationService.authenticate(username, password.toCharArray());

            eventPublisher.publishEvent((user, networkId, transactionId) -> {
                // TODO need to fix up to pass correct seqNo and alfrescoClientId
                return new RepositoryEventImpl(-1L, "login", transactionId, networkId, new Date().getTime(),
                        username, null);
            });

            // add ticket to model for javascript and template access
            Map<String, Object> model = new HashMap<>(7, 1.0f);
            model.put("username", username);
            model.put("ticket", authenticationService.getCurrentTicket());

            return model;
        } catch (AuthenticationException e) {
            throw (WebScriptException) new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Login failed").initCause(e);
        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }
}