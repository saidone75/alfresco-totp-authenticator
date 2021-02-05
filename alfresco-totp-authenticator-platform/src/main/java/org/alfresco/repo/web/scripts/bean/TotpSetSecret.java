package org.alfresco.repo.web.scripts.bean;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.TotpService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

public class TotpSetSecret extends DeclarativeWebScript {

    private TotpService totpService;

    /**
     * @param totpService TotpService
     */
    public void setTotpService(TotpService totpService)
    {
        this.totpService = totpService;
    }

    protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<String, Object>();
        try {
            String user = AuthenticationUtil.getFullyAuthenticatedUser();
            String secret = req.getParameter("secret");
            totpService.setSecret(user, secret);
            model.put("result", "OK");
        }
        catch (Exception e)
        {
            model.put("result", e.getMessage());
        }
        return model;
    }

}
