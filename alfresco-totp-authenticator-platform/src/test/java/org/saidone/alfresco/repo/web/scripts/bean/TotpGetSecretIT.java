package org.saidone.alfresco.repo.web.scripts.bean;

import org.junit.Test;

public class TotpGetSecretIT extends TotpBaseIT {

    @Test
    public void testGetSecret() throws Exception {
        testWebScriptCall("/s/security/getsecret");
    }

}