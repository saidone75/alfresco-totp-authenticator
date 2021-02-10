package org.saidone.web.site.servlet;

import org.saidone.web.site.TotpSlingshotUserFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.AbstractLoginController;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TotpSlingshotLoginController extends org.alfresco.web.site.servlet.SlingshotLoginController
{
    protected static final String PARAM_TOKEN = "token";

    private TotpSlingshotUserFactory userFactory;
    private WebFrameworkConfigElement webFrameworkConfiguration;

    public void setUserFactory(TotpSlingshotUserFactory userFactory)
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
