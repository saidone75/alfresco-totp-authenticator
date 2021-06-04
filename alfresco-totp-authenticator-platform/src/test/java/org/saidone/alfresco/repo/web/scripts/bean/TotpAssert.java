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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;

public class TotpAssert extends Assert {

    protected static void assertStandardJsonResponse(String jsonResponse) {
        Response response = parseJsonResponse(jsonResponse);
        assertTrue("Secret length mismatch", (("".equals(response.secret)) || response.secret.matches("^[A-Z0-9]{32}$")));
        assertNotNull("Image data is null", response.dataUri);
    }

    protected static void assertSecretMatches(String secret, String jsonResponse) {
        Response response = parseJsonResponse(jsonResponse);
        assertEquals(secret, response.secret);
    }

    private static Response parseJsonResponse(String response) {
        Gson gson = new Gson();
        JsonObject data = (JsonObject) gson.fromJson(response, JsonObject.class).get("data");
        String secret = data.get("secret").getAsString();
        String dataUri = data.get("dataUri").getAsString();
        return new Response(secret, dataUri);
    }

    private static class Response {
        protected String secret;
        protected String dataUri;

        Response(String secret, String dataUri) {
            this.secret = secret;
            this.dataUri = dataUri;
        }
    }

}
