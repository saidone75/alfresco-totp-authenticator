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

import lombok.val;
import org.saidone.alfresco.repo.security.authentication.TotpService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Web Script returning the current TOTP secret for a user together with the QR
 * code data URI.
 */
public class TotpGetSecret extends TotpWebScript {

    /**
     * Retrieves the TOTP secret and QR code for the requested user.
     *
     * @param req    current web script request
     * @param status web script status
     * @param cache  response cache
     * @return model containing the secret and QR code URI
     */
    protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {

        val model = new HashMap<String, Object>();

        val user = validateUser(req);

        model.put("secret", TotpService.getSecret(user));
        model.put("dataUri", TotpService.getDataUri(user));

        return model;
    }

}
