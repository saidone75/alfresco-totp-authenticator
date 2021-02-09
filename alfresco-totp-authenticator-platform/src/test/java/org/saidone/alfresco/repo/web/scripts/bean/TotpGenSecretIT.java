package org.saidone.alfresco.repo.web.scripts.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TotpGenSecretIT {

    private static final String ACS_ENDPOINT_PROP = "acs.endpoint.path";
    private static final String ACS_DEFAULT_ENDPOINT = "http://localhost:8080/alfresco";

    @Test
    public void testWebScriptCall() throws Exception {
        String webscriptURL = getPlatformEndpoint() + "/s/security/gensecret";

        // Login credentials for Alfresco Repo
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);

        // Create HTTP Client with credentials
        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        // Execute Web Script call
        try {
            HttpGet httpget = new HttpGet(webscriptURL);
            HttpResponse httpResponse = httpclient.execute(httpget);
            assertEquals("Incorrect HTTP Response Status",
                    HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
            HttpEntity entity = httpResponse.getEntity();
            assertNotNull("Response from Web Script is null", entity);
            Gson gson = new Gson();
            JsonObject data = (JsonObject)gson.fromJson(EntityUtils.toString(entity), JsonObject.class).get("data");
            String secret = data.get("secret").getAsString();
            String dataUri = data.get("dataUri").getAsString();
            assertTrue("Secret length mismatch", secret.matches("^[A-Z0-9]{32}$"));
            assertNotNull("Image data is null", dataUri);
        } finally {
            httpclient.close();
        }
    }

    private String getPlatformEndpoint() {
        final String platformEndpoint = System.getProperty(ACS_ENDPOINT_PROP);
        return StringUtils.isNotBlank(platformEndpoint) ? platformEndpoint : ACS_DEFAULT_ENDPOINT;
    }
}