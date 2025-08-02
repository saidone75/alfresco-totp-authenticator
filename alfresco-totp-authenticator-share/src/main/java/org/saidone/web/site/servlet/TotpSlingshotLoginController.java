package org.saidone.web.site.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.saidone.web.site.TotpSlingshotUserFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.AbstractLoginController;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.servlet.ModelAndView;

/**
 * Custom {@link org.alfresco.web.site.servlet.SlingshotLoginController} that adds
 * support for a TOTP token during authentication.
 */
public class TotpSlingshotLoginController extends org.alfresco.web.site.servlet.SlingshotLoginController
{
    protected static final String PARAM_TOKEN = "token";

    private TotpSlingshotUserFactory userFactory;
    private WebFrameworkConfigElement webFrameworkConfiguration;

    /**
     * Sets the user factory used to authenticate the user.
     *
     * @param userFactory factory capable of authenticating with a TOTP token
     */
    public void setUserFactory(TotpSlingshotUserFactory userFactory)
    {
        this.userFactory = userFactory;
    }

    /**
     * Injects the web framework configuration.
     *
     * @param webFrameworkConfiguration Alfresco web framework configuration element
     */
    public void setWebFrameworkConfiguration(WebFrameworkConfigElement webFrameworkConfiguration)
    {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
    }

    /**
     * Handles the login request and performs TOTP based authentication.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return always {@code null} as response handling is performed via redirects
     * @throws Exception on authentication or IO errors
     */
    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter(AbstractLoginController.PARAM_USERNAME);
        String password = request.getParameter(AbstractLoginController.PARAM_PASSWORD);
        String token = request.getParameter(PARAM_TOKEN);

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
