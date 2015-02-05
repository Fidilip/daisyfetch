package cz.fs.proto1.service;

import java.security.Principal;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.picketlink.social.standalone.fb.FacebookPrincipal;

import cz.fs.proto1.qualifiers.PicketLinkPrincipal;

import java.io.Serializable;

@SessionScoped
public class IdentityService implements Serializable {

	private static final long serialVersionUID = -8055760348195273572L;

	/** Obtains principal from session and returns it */
	@Produces @PicketLinkPrincipal
	public Principal producePrincipal(@Context HttpServletRequest req) {
		Principal p = (Principal) req.getSession().getAttribute("PRINCIPAL");
		
		// getName should always return email
		if(p instanceof FacebookPrincipal) {
			FacebookPrincipal fbPrinc = (FacebookPrincipal)p;
			fbPrinc.setName( fbPrinc.getEmail() );
			return fbPrinc;
		}
		return p;
	}
	
}

