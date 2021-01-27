/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.saidone.web.site.servlet;

import org.alfresco.web.site.SlingshotUser;
import org.saidone.web.site.SlingshotUserFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.AbstractLoginController;
import org.springframework.extensions.surf.mvc.LoginController;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This extends the standard {@link LoginController} to store the authenticated user's group membership information
 * as an {@link HttpSession} attribute so that it can be retrieved by the {@link SlingshotUserFactory} when creating
 * {@link SlingshotUser} instances.
 * 
 * @author david
 * @author kevinr
 * @author saidone
 */
public class SlingshotLoginController extends org.alfresco.web.site.servlet.SlingshotLoginController
{
    protected static final String PARAM_TOKEN = "token";

    private SlingshotUserFactory userFactory;
    private WebFrameworkConfigElement webFrameworkConfiguration;

    /**
     * <p>This method is provided to allow the Spring framework to set a <code>UserFactory</code> required for authenticating
     * requests</p>
     *
     * @param userFactory UserFactory
     */
    public void setUserFactory(SlingshotUserFactory userFactory)
    {
        this.userFactory = userFactory;
    }

    public void setWebFrameworkConfiguration(WebFrameworkConfigElement webFrameworkConfiguration)
    {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
    }

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        request.setCharacterEncoding("UTF-8");

        String username = (String) request.getParameter(AbstractLoginController.PARAM_USERNAME);
        String password = (String) request.getParameter(AbstractLoginController.PARAM_PASSWORD);
        String token = (String) request.getParameter(PARAM_TOKEN);

        boolean success = false;
        try
        {
            // check whether there is already a user logged in
            HttpSession session = request.getSession(false);
            // handle SSO which doesn't set a user until later
            if (session != null && request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID) != null)
            {
                // destroy old session and log out the current user
                AuthenticationUtil.logout(request, response);
            }

            // see if we can authenticate the user
            boolean authenticated = this.userFactory.authenticate(request, username, password, token);
            if (authenticated)
            {
                AuthenticationUtil.login(request, response, username, false, webFrameworkConfiguration.isLoginCookiesEnabled());

                // mark the fact that we succeeded
                success = true;
            }
        }
        catch (Throwable err)
        {
            throw new ServletException(err);
        }

        // If they succeeded in logging in, redirect to the success page
        // Otherwise, redirect to the failure page
        if (success)
        {
            onSuccess(request, response);
        }
        else
        {
            onFailure(request, response);
        }

        return null;
    }
}
