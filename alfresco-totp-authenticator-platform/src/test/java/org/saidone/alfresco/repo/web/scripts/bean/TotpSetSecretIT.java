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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Locale;

import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.assertSecretMatches;
import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.assertStandardJsonResponse;

/**
 * Integration tests for the {@code /s/security/setsecret} Web Script covering
 * various secret values.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TotpSetSecretIT extends TotpBaseIT {

    /**
     * Ensures that providing no secret clears any existing value.
     */
    @Order(1)
    @Test
    public void testSetEmptySecret() throws Exception {
        val response = testWebScriptCall("/s/security/setsecret");
        assertStandardJsonResponse(response);
        assertSecretMatches("", response);
    }

    /**
     * Ensures that an invalid secret does not modify the stored value.
     */
    @Order(2)
    @Test
    public void testSetInvalidSecret() throws Exception {
        val response = testWebScriptCall("/s/security/setsecret?secret=123");
        assertStandardJsonResponse(response);
        assertSecretMatches("", response);
    }

    /**
     * Verifies that a valid secret is stored correctly.
     */
    @Order(3)
    @Test
    public void testSetValidSecret() throws Exception {
        val secret = "AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB";
        val response = testWebScriptCall("/s/security/setsecret?secret=" + secret);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret, response);
    }

    /**
     * Valid secret containing lower-case letters should be normalised to upper
     * case.
     */
    @Order(4)
    @Test
    public void testSetValidSecret2() throws Exception {
        val secret = "aaaaaaaaAAAAAAAA0000000011111111";
        val response = testWebScriptCall("/s/security/setsecret?secret=" + secret);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret.toUpperCase(Locale.ROOT), response);
    }

    /**
     * Admins may set secrets for other users.
     */
    @Order(5)
    @Test
    public void testSetValidSecret3() throws Exception {
        val secret = "aaaaaaaaAAAAAAAA0000000011111111";
        val user = "ciao";
        val response = testWebScriptCall("/s/security/setsecret?secret=" + secret + "&user=" + user);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret.toUpperCase(Locale.ROOT), response);
    }

}