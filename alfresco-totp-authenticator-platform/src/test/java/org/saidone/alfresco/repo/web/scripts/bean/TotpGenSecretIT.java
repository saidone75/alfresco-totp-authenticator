package org.saidone.alfresco.repo.web.scripts.bean;

import org.junit.Test;

public class TotpGenSecretIT extends TotpBaseIT {

    @Test
    public void testGenSecret() throws Exception {
        testWebScriptCall("/s/security/gensecret");
    }

}