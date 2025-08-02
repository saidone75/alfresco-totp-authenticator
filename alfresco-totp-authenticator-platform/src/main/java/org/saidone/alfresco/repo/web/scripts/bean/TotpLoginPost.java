/*
 * Alfresco TOTP authenticator - two factor authentication for Alfresco
 * Copyright (C) 2021-2025 Saidone
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.saidone.alfresco.repo.web.scripts.bean;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Web Script handling login requests that include a TOTP token in addition to
 * the standard username and password.
 */
public class TotpLoginPost extends org.alfresco.repo.web.scripts.bean.LoginPost {
    // dependencies
    @Setter
    private AuthenticationService authenticationService;
    @Setter
    protected EventPublisher eventPublisher;

    /**
     * Processes the POST request containing login credentials and delegates to
     * {@link #login(String, String, String)} once the JSON payload has been
     * parsed.
     *
     * @param req    current web script request
     * @param status web script status
     * @return model containing the user name and authentication ticket
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
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Unable to parse JSON POST body: " + jErr.getMessage(), jErr);
        } catch (IOException ioErr) {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve POST body: " + ioErr.getMessage(), ioErr);
        }
    }

    /**
     * Performs the actual authentication using username, password and TOTP token
     * and returns the authentication ticket.
     *
     * @param username user name
     * @param password user password
     * @param token    one-time TOTP token
     * @return model containing the authenticated user name and ticket
     */
    protected Map<String, Object> login(final String username, String password, String token) {
        try {
            // check totp
            TotpService.authorizeToken(username, token);

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
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Login failed", e);
        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }

}