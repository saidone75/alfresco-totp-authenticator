package org.saidone.alfresco.repo.web.scripts.bean;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TotpSetSecret extends TotpWebScript {

    protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {

        Map<String, Object> model = new HashMap<>();

        String user = AuthenticationUtil.getFullyAuthenticatedUser();

        String secret = req.getParameter("secret");
        if (null != secret) secret = secret.toUpperCase(Locale.ROOT);
        if (null != secret && !secret.matches("^[A-Z0-9]{32}$")) {
            secret = "";
        }

        totpService.setSecret(user, secret);

        model.put("secret", totpService.getSecret(user));
        model.put("dataUri", totpService.getDataUri(user));

        return model;
    }

}
