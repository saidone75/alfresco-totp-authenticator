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

import static org.junit.Assert.*;

public class TotpBaseIT {

    private static final String ACS_ENDPOINT_PROP = "acs.endpoint.path";
    private static final String ACS_DEFAULT_ENDPOINT = "http://localhost:8080/alfresco";

    protected String testWebScriptCall(String Url) throws Exception {
        String webscriptURL = getPlatformEndpoint() + Url;

        // Login credentials for Alfresco Repo
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);

        // Create HTTP Client with credentials
        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        // Execute Web Script call
        String response;
        try {
            HttpGet httpget = new HttpGet(webscriptURL);
            HttpResponse httpResponse = httpclient.execute(httpget);
            assertEquals("Incorrect HTTP Response Status",
                    HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
            HttpEntity entity = httpResponse.getEntity();
            assertNotNull("Response from Web Script is null", entity);
            response = EntityUtils.toString(entity);

        } finally {
            httpclient.close();
        }
        return response;
    }

    private String getPlatformEndpoint() {
        final String platformEndpoint = System.getProperty(ACS_ENDPOINT_PROP);
        return StringUtils.isNotBlank(platformEndpoint) ? platformEndpoint : ACS_DEFAULT_ENDPOINT;
    }

}
