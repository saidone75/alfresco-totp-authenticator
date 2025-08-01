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
import org.junit.Test;

import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.assertStandardJsonResponse;

/**
 * Integration test for the {@code /s/security/gensecret} Web Script.
 */
public class TotpGenSecretIT extends TotpBaseIT {

    /**
     * Ensures that generating a new secret returns a valid JSON response.
     */
    @Test
    public void testGenSecret() throws Exception {
        val response = testWebScriptCall("/s/security/gensecret");
        assertStandardJsonResponse(response);
    }

}