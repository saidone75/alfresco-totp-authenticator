package org.saidone.alfresco.repo.web.scripts.bean;

import org.saidone.alfresco.repo.security.authentication.TotpService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;

public class TotpWebScript extends DeclarativeWebScript {

    protected TotpService totpService;

    /**
     * @param totpService TotpService
     */
    public void setTotpService(TotpService totpService)
    {
        this.totpService = totpService;
    }

}
