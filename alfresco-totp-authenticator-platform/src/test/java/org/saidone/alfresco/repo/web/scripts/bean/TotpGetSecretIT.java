package org.saidone.alfresco.repo.web.scripts.bean;

import org.apache.http.HttpEntity;
import org.junit.Test;

public class TotpGetSecretIT extends TotpBaseIT {

    @Test
    public void testGetSecret() throws Exception {
        String response = testWebScriptCall("/s/security/getsecret");
        assertStandardJsonResponse(response);
    }

}