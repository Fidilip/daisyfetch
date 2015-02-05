package cz.fs.proto1;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.Authenticator;
import org.picketlink.idm.model.Account;
import org.picketlink.social.auth.FacebookAuthenticator;
import org.picketlink.social.auth.conf.FacebookConfiguration;
import org.picketlink.social.auth.conf.GoogleConfiguration;

@RequestScoped
@PicketLink
public class FbAuth implements Authenticator {

	@Inject
	protected FacebookConfiguration fbConf;
	
	@Inject
	protected GoogleConfiguration googleConf;
	
	protected Authenticator auth;
	
	protected Authenticator makeAuth() {
		HttpServletRequest httpServletRequest = (HttpServletRequest) ThreadLocalUtils.currentRequest.get();
        HttpServletResponse httpServletResponse = (HttpServletResponse) ThreadLocalUtils.currentResponse.get();

        String login = httpServletRequest.getParameter("login");
        Authenticator authenticator = null;

        if(login == null || login.equals("facebook")){
            FacebookAuthenticator facebookAuthenticator = new FacebookAuthenticator();
            facebookAuthenticator.setHttpServletRequest(httpServletRequest);
            facebookAuthenticator.setHttpServletResponse(httpServletResponse);
            facebookAuthenticator.setConfiguration(fbConf);
            authenticator = facebookAuthenticator;
        }else if(login.equals("google")) {
        	CustomGoogleAuthenticator googleAuth = new CustomGoogleAuthenticator();
        	googleAuth.setHttpServletRequest(httpServletRequest);
        	googleAuth.setHttpServletResponse(httpServletResponse);
        	googleAuth.setConfiguration(googleConf);
        	authenticator = googleAuth;
        }
        return authenticator;
	}
	
	protected Authenticator getAuth() {
		if(auth == null) {
			auth = makeAuth();
		}
		return auth;
	}
	
	@Override
	public void authenticate() {
		getAuth().authenticate();
	}

	@Override
	public Account getAccount() {
		return getAuth().getAccount();
	}

	@Override
	public AuthenticationStatus getStatus() {
		return getAuth().getStatus();
	}

	@Override
	public void postAuthenticate() {
		getAuth().postAuthenticate();
	}

	
	
}
