package cz.fs.proto1.rest;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.picketlink.social.standalone.fb.FacebookPrincipal;

import cz.fs.proto1.GooglePrincipal;
import cz.fs.proto1.model.IdentityDTO;
import cz.fs.proto1.model.IdentityDTO.EType;
import cz.fs.proto1.qualifiers.PicketLinkPrincipal;

@RequestScoped
@Path("identity")
public class IdentityRest {
	
	@Inject @PicketLinkPrincipal
	protected Principal principal;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public IdentityDTO getIdentity() {
		IdentityDTO identity = new IdentityDTO();
		
		System.out.printf("Principal: %s%n", principal);
		
		if(principal instanceof FacebookPrincipal) {
			FacebookPrincipal fbPrinc = (FacebookPrincipal)principal;
			identity.setEmail( fbPrinc.getEmail() );
			identity.setType(EType.FACEBOOK);
		}

		if(principal instanceof GooglePrincipal) {
			GooglePrincipal googlePrinc = (GooglePrincipal)principal;
			identity.setEmail( googlePrinc.getEmail() );
			identity.setType(EType.GOOGLE);
		}
		
		return identity;
	}
	
}
