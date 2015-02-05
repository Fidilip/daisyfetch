package cz.fs.proto1.rest;

import java.io.File;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.fs.proto1.infinispan.CacheContainerProvider;
import cz.fs.proto1.model.Web;
import cz.fs.proto1.service.WebManager;

@RunWith(Arquillian.class)
public class WebServiceTest {
	
	@Inject
	protected WebRest ws;
	
	@Deployment
	public static WebArchive deployment() {		
		
		WebArchive wa = ShrinkWrap.create(WebArchive.class)
				.addClasses(WebRest.class, Web.class, CacheContainerProvider.class)
				.addClass(WebManager.class)
				.addClass(WebServiceTest.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		
		File jars = new File("target/Proto1/WEB-INF/lib");
		Assert.assertTrue(jars.exists() && jars.isDirectory());
		
		for(File jar : jars.listFiles()) {
			wa.addAsLibrary(jar);
		}
		
		return wa;
	}
	
	@Test
	public void testAddGetRemove() {
		Web web = new Web("web-1", "http://example.org");
		ws.add(web);
		
		// retrieving added web
		Web web2 = ws.get(web.getName());
		
		Assert.assertNotNull(web2);
		Assert.assertEquals(true, web == web2);
		
		ws.remove(web.getName());
		try {
			web2 = ws.get(web.getName());
			Assert.fail("NotFoundException was not thrown!");
		}catch(NotFoundException nfe) {
			
		}
	}
	
	// TODO: arquillian proxy exception is thrown :-\
	/*@Test(expected=BadRequestException.class)
	public void testBadRequestEmpty() {
		ws.get("");
	}
	
	@Test(expected=BadRequestException.class)
	public void testBadRequestNull() {
		ws.get(null);
	}*/
	

}
