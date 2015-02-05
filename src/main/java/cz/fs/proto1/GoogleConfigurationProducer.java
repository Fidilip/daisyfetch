package cz.fs.proto1;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.picketlink.social.auth.conf.GoogleConfiguration;

@ApplicationScoped
public class GoogleConfigurationProducer {

	protected String getProperty(String key) {
		return ConfigUtil.getConfig(key);
	}
	
	@Produces
	public GoogleConfiguration produceGoogleConfig() {
		final String scope = getProperty("GOOGLE_SCOPE");
		final String returnUrl = getProperty("GOOGLE_RETURN_URL");
		final String clientID = getProperty("GOOGLE_CLIENT_ID");
		final String clientSecret = getProperty("GOOGLE_CLIENT_SECRET");
		final String accessType = getProperty("GOOGLE_ACCESS_TYPE");
		
		return new GoogleConfiguration() {
			
			@Override
			public String getScope() {
				return scope;
			}
			
			@Override
			public String getReturnURL() {
				return returnUrl;
			}
			
			@Override
			public String getClientSecret() {
				return clientSecret;
			}
			
			@Override
			public String getClientID() {
				return clientID;
			}
			
			@Override
			public String getRandomAlgorithm() {
				return null;
			}
			
			@Override
			public String getApplicationName() {
				return null;// not important
			}
			
			@Override
			public String getAccessType() {
				// online or offline
				return accessType;
			}
		};
	}
	
}
