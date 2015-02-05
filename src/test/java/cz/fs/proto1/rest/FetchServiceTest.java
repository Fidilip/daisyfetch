package cz.fs.proto1.rest;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FetchServiceTest {

	@Inject
	protected WebRest ws;
	
	@Inject
	protected FetchRest fs;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive wa = WebServiceTest.deployment();
		wa.addClass(FetchRest.class);
		return wa;
	}
	
	// TODO: test

}
