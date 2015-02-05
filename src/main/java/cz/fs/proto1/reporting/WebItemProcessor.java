package cz.fs.proto1.reporting;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;

import cz.fs.proto1.model.Web;
import cz.fs.proto1.rest.FetchRest;
import cz.fs.proto1.service.ReportingService;

@Named
public class WebItemProcessor implements ItemProcessor {

	@Inject
	protected FetchRest fetchService;
	
	@Inject
	protected ReportingService reportingService;
	
	@Override
	public Object processItem(Object o) throws Exception {
		if(o instanceof Web) {
			Web web = (Web)o;
			if(reportingService.isTimeToReport(web)) {
				return reportingService.checkChange(web);
			}else {
				return null;
			}
		}else {
			throw new RuntimeException("Processed item should be instance of Web!");
		}
	}

}
