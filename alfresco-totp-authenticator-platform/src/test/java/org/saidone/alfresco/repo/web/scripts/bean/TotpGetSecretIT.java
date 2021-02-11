package org.saidone.alfresco.repo.web.scripts.bean;

import org.junit.Test;

import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.assertStandardJsonResponse;

public class TotpGetSecretIT extends TotpBaseIT {

    @Test
    public void testGetSecret() throws Exception {
        String response = testWebScriptCall("/s/security/getsecret");
        assertStandardJsonResponse(response);
    }

}