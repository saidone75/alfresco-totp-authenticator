package org.saidone.alfresco.repo.web.scripts.bean;

import org.junit.Test;

import java.util.Locale;

import static org.saidone.alfresco.repo.web.scripts.bean.TotpAssert.*;

public class TotpSetSecretIT extends TotpBaseIT {

    @Test
    public void testSetInvalidSecret() throws Exception {
        String response = testWebScriptCall("/s/security/setsecret?secret=123");
        assertStandardJsonResponse(response);
        assertSecretMatches("", response);
    }

    @Test
    public void testSetValidSecret() throws Exception {
        String secret = "AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBB";
        String response = testWebScriptCall("/s/security/setsecret?secret=" + secret);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret, response);
    }

    @Test
    public void testSetValidSecret2() throws Exception {
        String secret = "aaaaaaaaAAAAAAAA0000000011111111";
        String response = testWebScriptCall("/s/security/setsecret?secret=" + secret);
        assertStandardJsonResponse(response);
        assertSecretMatches(secret.toUpperCase(Locale.ROOT), response);
    }

    @Test
    public void testSetEmptySecret() throws Exception {
        String response = testWebScriptCall("/s/security/setsecret");
        assertStandardJsonResponse(response);
        assertSecretMatches("", response);
    }

}