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
import org.alfresco.service.cmr.security.AuthorityService;
import org.saidone.alfresco.repo.security.authentication.TotpService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class TotpWebScript extends DeclarativeWebScript {

    protected TotpService totpService;
    protected AuthorityService authorityService;

    /**
     * @param totpService TotpService
     */
    public void setTotpService(TotpService totpService) {
        this.totpService = totpService;
    }

    /**
     * @param authorityService AuthorityService
     */
    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    protected String validateUser(WebScriptRequest req) {
        String user = req.getParameter("user");
        if (user == null) {
            user = AuthenticationUtil.getFullyAuthenticatedUser();
        } else {
            try {
                new UserNameConstraint().evaluate(user);
                if (!authorityService.hasAdminAuthority() &&
                        !user.equals(AuthenticationUtil.getFullyAuthenticatedUser())) {
                    throw new WebScriptException("Only admin can read or modify other user's TOTP secret.");
                }
            } catch (ConstraintException e) {
                throw new WebScriptException(e.getMessage(), e);
            }
        }
        return user;
    }

}
