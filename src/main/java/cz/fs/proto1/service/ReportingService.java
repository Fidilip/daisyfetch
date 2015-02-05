package cz.fs.proto1.service;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import cz.fs.proto1.model.Web;


public class ReportingService {

	@Inject
	protected FetchService fetchService;
	
	public Web checkChange(Web web) {
		if(!reportingEnabledAndValid(web)) return null;
		
		String oldContent = web._getCachedValue();
		try {
			String newContent = fetchService.fetch(web);
			if(!oldContent.equals(newContent)) {
				// do report
				reportChange(web);
				return web;
			}else {
				return null;
			}
			
		}catch(IOException ioe) {
			// todo: implement messages for user?
			ioe.printStackTrace();
			return null;
		}
	}
	
	protected void reportChange(Web web) throws IOException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(web.getReportUrl());
		Entity<String> content = Entity.entity(web._getCachedValue(), MediaType.TEXT_HTML);
		target.request().post(content);
		
		web.setLastReported(System.currentTimeMillis());
	}
	
	public boolean isTimeToReport(Web web) {
		long time = System.currentTimeMillis();
		
		// report interval is in hours
		// TODO: magic numbers
		return time > web.getLastReported() + web.getReportInterval() * 3600L * 1000L;	
	} 
	
	protected boolean reportingEnabledAndValid(Web web) {
		return web.getReporting() && (web.getReportUrl() != null) && web.getReportInterval() > 0;
	}
	
}
