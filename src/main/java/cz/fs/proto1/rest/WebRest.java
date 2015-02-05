package cz.fs.proto1.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cz.fs.proto1.model.Web;
import cz.fs.proto1.service.WebManager;

// TODO: constants for media types in @Produces and @Consumes
// TODO: rename
@Path("/web")
public class WebRest {
	
	@Inject
	protected WebManager webManager;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Web> list() {
			return webManager.getAll();
	}
	
	@GET
	@Path("{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Web get(@PathParam("uuid") String name) {
		try {
			return webManager.get(name);
		}catch(ConstraintViolationException cve) {
			throw new BadRequestException(cve.getMessage());
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Web add(Web web) {
		return webManager.add(web);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(Web web) {
		webManager.update(web);
	}
	 
	// TODO: path param!
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public void remove(@QueryParam("uuid") String uuid) {
		webManager.remove(uuid);
	}
	
}
