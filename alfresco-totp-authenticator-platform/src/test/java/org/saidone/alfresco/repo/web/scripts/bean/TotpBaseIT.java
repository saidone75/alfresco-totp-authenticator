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

import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TotpBaseIT {

    private static final String ACS_ENDPOINT_PROP = "acs.endpoint.path";
    private static final String ACS_DEFAULT_ENDPOINT = "http://localhost:8080/alfresco";

    protected String testWebScriptCall(String url) throws Exception {
        var webscriptUrl = String.format("%s%s", getPlatformEndpoint(), url);

        /* Login credentials for Alfresco repo */
        var provider = new BasicCredentialsProvider();
        var credentials = new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);

        /* Create HTTP client with credentials */
        @Cleanup var httpclient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        /* Execute webscript call */
        var httpget = new HttpGet(webscriptUrl);
        var httpResponse = httpclient.execute(httpget);
        assertEquals("Incorrect HTTP Response Status",
                HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
        var entity = httpResponse.getEntity();
        assertNotNull("Response from Web Script is null", entity);
        return EntityUtils.toString(entity);
    }

    private String getPlatformEndpoint() {
        final String platformEndpoint = System.getProperty(ACS_ENDPOINT_PROP);
        return StringUtils.isNotBlank(platformEndpoint) ? platformEndpoint : ACS_DEFAULT_ENDPOINT;
    }

}
