package org.saidone.alfresco.repo.web.scripts.bean;

import org.junit.Test;

public class TotpSetSecretIT extends TotpBaseIT {

    @Test
    public void testSetValidSecret() throws Exception {
        testWebScriptCall("/s/security/setsecret?secret=AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB");
    }

    @Test
    public void testSetEmptySecret() throws Exception {
        testWebScriptCall("/s/security/setsecret");
    }

}