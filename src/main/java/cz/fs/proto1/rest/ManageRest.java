package cz.fs.proto1.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import cz.fs.proto1.service.WebManager;

@Path("/manage")
public class ManageRest {

	@Inject
	protected WebManager webManager;
	
	@GET
	@Path("/clear")
	public String cleanInfinispan() {
		webManager.clearAll();
		return "OK, cleared";
	}
	
}
