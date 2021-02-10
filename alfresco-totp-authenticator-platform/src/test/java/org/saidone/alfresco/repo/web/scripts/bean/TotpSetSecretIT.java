package org.saidone.alfresco.repo.web.scripts.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.keycloak.authorization.client.util.Http;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TotpSetSecretIT extends TotpBaseIT {

    @Test
    public void testSetInvalidSecret() throws Exception {
        String response = testWebScriptCall("/s/security/setsecret?secret=123");
        assertSecretIsNull(response);
    }

    @Test
    public void testSetValidSecret() throws Exception {
        String response = testWebScriptCall("/s/security/setsecret?secret=AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB");
        assertStandardJsonResponse(response);
    }

    @Test
    public void testSetEmptySecret() throws Exception {
        String response = testWebScriptCall("/s/security/setsecret");
        assertStandardJsonResponse(response);
    }

    private void assertSecretIsNull(String response) throws Exception {
        Gson gson = new Gson();
        JsonObject data = (JsonObject)gson.fromJson(response, JsonObject.class).get("data");
        String secret = data.get("secret").getAsString();
        String dataUri = data.get("dataUri").getAsString();
        assertTrue("Secret is not empty ", "".equals(secret));
        assertTrue("Image data is not empty", "".equals(dataUri));
    }

}