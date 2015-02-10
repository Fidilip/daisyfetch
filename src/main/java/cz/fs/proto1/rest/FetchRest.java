package cz.fs.proto1.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jsoup.HttpStatusException;

import cz.fs.proto1.model.Web;
import cz.fs.proto1.service.FetchService;
import cz.fs.proto1.service.WebManager;

@Path("/fetch")
@Named
public class FetchRest {

	protected static final String WEB_NOT_FOUND = "Web with name '%s' of user '%s' not found!";
	protected static final String WEB_HTTP_ERROR = "Given page '%s' have returned error: %d.";
	
	@Inject
	protected WebManager webManager;
	
	@Inject
	protected FetchService fetchService;
	
	@GET
	@Path("{user}/{web}")
	public Response fetch(@PathParam("user") String user, @PathParam("web") String name) {
		
		Web web;
		try {
			web = webManager.get(user, name);
		}catch(NotFoundException nfe) {
			return Response.status(400).entity(String.format(WEB_NOT_FOUND, name, user)).build();
		}
		
		try {
			return fetchContent(web);
		}catch(HttpStatusException httpStatus) {
			String msg = String.format(WEB_HTTP_ERROR, httpStatus.getUrl(), httpStatus.getStatusCode());
			return Response.status(httpStatus.getStatusCode()).entity(msg).build();
		}catch (IOException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		
	}	
	
	protected Response fetchContent(Web web) throws IOException {
		if(web.getSnippetsEnabled()) {
			String json = fetchService.fetchSnippets(web);
			ResponseBuilder rb = Response.ok(json, MediaType.APPLICATION_JSON);
			addHeaders(web, rb);
			return rb.build();
		}else {
			String content = fetchService.getContent(web);
			ResponseBuilder rb = Response.ok(content, MediaType.TEXT_HTML);
			addHeaders(web, rb);
			return rb.build();
		}
	} 
	
	/** Adds helpfull headers and enables CORS (for all domains)
	 * X-Source = original source of parsed content
	 * X-Last-Cache-Time = time when the content was fetch
	 * X-Cache-Expires-At = time when content will be refetched from source */
	protected void addHeaders(Web web, ResponseBuilder rb) {
		//rb.header("X-Cached", String.valueOf(cached));
		
		rb.header("X-Source", web.getUrl());
		rb.header("X-Last-Cache-Time", String.valueOf(web.getLastCacheTime()));
		rb.header("X-Cache-Expires-At", String.valueOf(web.cacheExpirationTime()));
		
		// Allow CORS
		rb.header("Access-Control-Allow-Origin", "*");
	}
	
	
}
