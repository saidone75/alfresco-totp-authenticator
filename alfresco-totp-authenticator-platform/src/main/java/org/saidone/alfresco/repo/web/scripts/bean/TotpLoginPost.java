/*
 * #%L
 * Alfresco Remote API
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.saidone.alfresco.repo.web.scripts.bean;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.saidone.alfresco.repo.security.authentication.TotpService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.sync.events.types.Event;
import org.alfresco.sync.events.types.RepositoryEventImpl;
import org.alfresco.sync.repo.events.EventPreparator;
import org.alfresco.sync.repo.events.EventPublisher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
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
public class TotpLoginPost extends org.alfresco.repo.web.scripts.bean.LoginPost
{
    private static final Log logger = LogFactory.getLog(TotpLoginPost.class);
    // dependencies
    private AuthenticationService authenticationService;
    private TotpService totpService;
    protected EventPublisher eventPublisher;

    /**
     * @param authenticationService AuthenticationService
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    /**
     * @param totpService TotpService
     */
    public void setTotpService(TotpService totpService)
    {
        this.totpService = totpService;
    }

    /**
     * @param eventPublisher EventPublisher
     */
    public void setEventPublisher(EventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        // Extract user and password from JSON POST
        Content c = req.getContent();
        if (c == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing POST body.");
        }
        
        // TODO accept xml type.
        
        // extract username and password from JSON object
        JSONObject json;
        try
        {
            json = new JSONObject(c.getContent());
            String username = json.getString("username");
            String password = json.getString("password");
            String token = json.getString("token");

            if (username == null || username.length() == 0)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Username not specified");
            }

            if (password == null)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Password not specified");
            }

            try
            {
                return login(username, password, token);
            }
            catch(WebScriptException e)
            {
                status.setCode(e.getStatus());
                status.setMessage(e.getMessage());
                status.setRedirect(true);
                return null;
            }
        } 
        catch (JSONException jErr)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Unable to parse JSON POST body: " + jErr.getMessage());
        }
        catch (IOException ioErr)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve POST body: " + ioErr.getMessage());
        }
    }

    protected Map<String, Object> login(final String username, String password, String token)
    {
        try
        {
            // check totp
            totpService.authorizeToken(username, token);

            // get ticket
            authenticationService.authenticate(username, password.toCharArray());

            eventPublisher.publishEvent(new EventPreparator(){
                @Override
                public Event prepareEvent(String user, String networkId, String transactionId)
                {
                    // TODO need to fix up to pass correct seqNo and alfrescoClientId
                    return new RepositoryEventImpl(-1l, "login", transactionId, networkId, new Date().getTime(),
                            username, null);
                }
            });

            // add ticket to model for javascript and template access
            Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
            model.put("username", username);
            model.put("ticket",  authenticationService.getCurrentTicket());

            return model;
        }
        catch(AuthenticationException e)
        {
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Login failed");
        }
        finally
        {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }
}