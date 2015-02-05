package cz.fs.proto1;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.picketlink.social.auth.conf.FacebookConfiguration;

/**
 * Class to obtain the configuration
 */
@ApplicationScoped
public class FacebookConfigurationProducer {
    @Produces
    public FacebookConfiguration configure(){
        final String clientID  = ConfigUtil.getConfig("FB_CLIENT_ID");
        final String clientSecret = ConfigUtil.getConfig("FB_CLIENT_SECRET");
        final String returnURL = ConfigUtil.getConfig("FB_RETURN_URL");
        final String scope = "email";
        
        if(clientID == null || clientSecret == null || returnURL == null) {
        	throw new RuntimeException("Facebook configuration is not set! Use CLI script please.");
        }

        FacebookConfiguration facebookConfiguration = new FacebookConfiguration() {
            @Override
            public String getClientID() {
                return clientID;
            }

            @Override
            public String getClientSecret() {
                return clientSecret;
            }

            @Override
            public String getScope() {
                return scope;
            }

            @Override
            public String getReturnURL() {
                return returnURL;
            }
        };
        return facebookConfiguration;
    }
}