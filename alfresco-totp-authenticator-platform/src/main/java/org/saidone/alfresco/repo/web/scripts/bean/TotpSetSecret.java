/*
 * Alfresco TOTP authenticator - two factor authentication for Alfresco
 * Copyright (C) 2021 Saidone
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.saidone.alfresco.repo.web.scripts.bean;

import org.alfresco.repo.dictionary.constraint.UserNameConstraint;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TotpSetSecret extends TotpWebScript {

    protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {

        Map<String, Object> model = new HashMap<>();

        String user = req.getParameter("user");

        if (user == null) {
            user = AuthenticationUtil.getFullyAuthenticatedUser();
        } else {
            try {
                new UserNameConstraint().evaluate(user);
                if (!authorityService.hasAdminAuthority() &&
                        !user.equals(AuthenticationUtil.getFullyAuthenticatedUser())) {
                    throw new WebScriptException("Only admin can change other user's TOTP secret.");
                }
            } catch (ConstraintException e) {
                throw new WebScriptException(e.getMessage(), e);
            }
        }

        String secret = req.getParameter("secret");
        if (null != secret) secret = secret.toUpperCase(Locale.ROOT);
        if (null != secret && !secret.matches("^[A-Z0-9]{32}$")) {
            secret = "";
        }

        totpService.setSecret(user, secret);

        model.put("secret", totpService.getSecret(user));
        model.put("dataUri", totpService.getDataUri(user));

        return model;
    }

}
