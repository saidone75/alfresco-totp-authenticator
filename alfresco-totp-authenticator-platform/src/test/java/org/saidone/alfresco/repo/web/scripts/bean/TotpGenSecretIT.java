package org.saidone.alfresco.repo.web.scripts.bean;

import org.junit.Test;

import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.assertStandardJsonResponse;

public class TotpGenSecretIT extends TotpBaseIT {

    @Test
    public void testGenSecret() throws Exception {
        String response = testWebScriptCall("/s/security/gensecret");
        assertStandardJsonResponse(response);
    }

}