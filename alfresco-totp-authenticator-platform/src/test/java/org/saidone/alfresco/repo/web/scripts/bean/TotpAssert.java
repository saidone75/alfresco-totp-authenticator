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
