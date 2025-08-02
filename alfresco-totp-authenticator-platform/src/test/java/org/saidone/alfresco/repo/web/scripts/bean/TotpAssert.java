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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.val;
import org.junit.Assert;

/**
 * Utility assertions for verifying responses from TOTP related Web Scripts.
 */
public final class TotpAssert extends Assert {

    /**
     * Asserts that the standard JSON response contains a valid secret and QR code
     * data URI.
     *
     * @param jsonResponse JSON string returned by the Web Script
     */
    static void assertStandardJsonResponse(String jsonResponse) {
        val response = parseJsonResponse(jsonResponse);
        assertTrue("Secret length mismatch", (("".equals(response.secret)) || response.secret.matches("^[A-Z0-9]{32}$")));
        assertNotNull("Image data is null", response.dataUri);
    }

    /**
     * Asserts that the provided secret matches the one contained in the JSON
     * response.
     *
     * @param secret       expected secret value
     * @param jsonResponse JSON string returned by the Web Script
     */
    static void assertSecretMatches(String secret, String jsonResponse) {
        val response = parseJsonResponse(jsonResponse);
        assertEquals(secret, response.secret);
    }

    /**
     * Parses the JSON Web Script response into a {@link Response} object.
     *
     * @param response JSON response string
     * @return parsed response instance
     */
    private static Response parseJsonResponse(String response) {
        val gson = new Gson();
        val data = (JsonObject) gson.fromJson(response, JsonObject.class).get("data");
        val secret = data.get("secret").getAsString();
        val dataUri = data.get("dataUri").getAsString();
        return new Response(secret, dataUri);
    }

    @AllArgsConstructor
    private static class Response {
        protected String secret;
        protected String dataUri;
    }

}
