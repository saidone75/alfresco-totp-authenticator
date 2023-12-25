/*
 * Alfresco TOTP authenticator - two factor authentication for Alfresco
 * Copyright (C) 2021-2022 Saidone
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

import org.junit.Test;

import java.util.Locale;

import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.*;

public class TotpSetSecretIT extends TotpBaseIT {

    @Test
    public void testSetInvalidSecret() throws Exception {
        var response = testWebScriptCall("/s/security/setsecret?secret=123");
        assertStandardJsonResponse(response);
        assertSecretMatches("", response);
    }

    @Test
    public void testSetValidSecret() throws Exception {
        var secret = "AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB";
        var response = testWebScriptCall("/s/security/setsecret?secret=" + secret);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret, response);
    }

    @Test
    public void testSetValidSecret2() throws Exception {
        var secret = "aaaaaaaaAAAAAAAA0000000011111111";
        var response = testWebScriptCall("/s/security/setsecret?secret=" + secret);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret.toUpperCase(Locale.ROOT), response);
    }

    @Test
    public void testSetValidSecret3() throws Exception {
        var secret = "aaaaaaaaAAAAAAAA0000000011111111";
        var user = "ciao";
        var response = testWebScriptCall("/s/security/setsecret?secret=" + secret + "&user=" + user);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret.toUpperCase(Locale.ROOT), response);
    }

    @Test
    public void testSetEmptySecret() throws Exception {
        var response = testWebScriptCall("/s/security/setsecret");
        assertStandardJsonResponse(response);
        assertSecretMatches("", response);
    }

}