package cz.fs.proto1;

import org.picketlink.authentication.AuthenticationException;
import org.picketlink.idm.model.basic.User;
import org.picketlink.social.auth.AbstractSocialAuthenticator;
import org.picketlink.social.auth.conf.GoogleConfiguration;
import org.picketlink.social.standalone.google.GoogleAccessTokenContext;
import org.picketlink.social.standalone.google.GoogleConstants;
import org.picketlink.social.standalone.google.GoogleProcessor;
import org.picketlink.social.standalone.google.InteractionState;

import com.google.api.services.oauth2.model.Userinfo;

/**
 * An implementation of {@link org.picketlink.authentication.Authenticator} for Google+ login
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CustomGoogleAuthenticator extends AbstractSocialAuthenticator {

    private GoogleConfiguration configuration;
    private GoogleProcessor googleProcessor;

    public void setConfiguration(GoogleConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void authenticate() {
        if(httpServletRequest == null){
            throw new IllegalStateException("http request not available");
        }
        if(httpServletResponse == null){
            throw new IllegalStateException("http response not available");
        }
        if(configuration == null){
            throw new IllegalStateException("configuration not available");
        }

        InteractionState interactionState;
        Userinfo userInfo = null;

        try {
            interactionState = getGoogleProcessor().processOAuthInteraction(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            // Cleanup state of OAuth interaction if error occured
            httpServletRequest.getSession().removeAttribute(GoogleConstants.ATTRIBUTE_AUTH_STATE);

            throw new AuthenticationException("Google+ login failed due to error", e);
        }

        // Authentication is finished. Let's obtain user info
        if (interactionState.getState().equals(InteractionState.State.FINISH)) {
            GoogleAccessTokenContext accessTokenContext = interactionState.getAccessTokenContext();
            userInfo = getGoogleProcessor().obtainUserInfo(accessTokenContext);
            
            GooglePrincipal gp = new GooglePrincipal();
            gp.setEmail(userInfo.getEmail());
            
            httpServletRequest.getSession().setAttribute("PRINCIPAL", gp);
            
            // Establish security context
            setStatus(AuthenticationStatus.SUCCESS);
            setAccount(new User(userInfo.getEmail()));
        }
    }

    protected GoogleProcessor getGoogleProcessor() {
        if (this.googleProcessor == null) {
            this.googleProcessor = new GoogleProcessor(configuration.getClientID(),
                    configuration.getClientSecret(),
                    configuration.getReturnURL(),
                    configuration.getAccessType(),
                    configuration.getApplicationName(),
                    configuration.getRandomAlgorithm(),
                    configuration.getScope());
        }
        return this.googleProcessor;
    }
}
