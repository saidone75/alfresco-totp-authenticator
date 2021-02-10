package org.saidone.alfresco.repo.web.scripts.bean;

import org.apache.http.HttpEntity;
import org.junit.Test;

public class TotpGenSecretIT extends TotpBaseIT {

    @Test
    public void testGenSecret() throws Exception {
        String response = testWebScriptCall("/s/security/gensecret");
        assertStandardJsonResponse(response);
    }

}